package art.aelaort;

import art.aelaort.build.BuildService;
import art.aelaort.build.JobsProvider;
import art.aelaort.db.LocalDb;
import art.aelaort.db.RemoteDb;
import art.aelaort.exceptions.BuildJobNotFoundException;
import art.aelaort.exceptions.TooManyDockerFilesException;
import art.aelaort.models.build.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static art.aelaort.utils.ColoredConsoleTextUtils.wrapRed;
import static art.aelaort.utils.Utils.log;
import static art.aelaort.utils.Utils.slice;
import static java.lang.Integer.parseInt;

@Component
@RequiredArgsConstructor
public class Entrypoint implements CommandLineRunner {
	private final BuildService buildService;
	private final JobsProvider jobsProvider;
	private final LocalDb localDb;
	private final RemoteDb remoteDb;

	@Override
	public void run(String... args) {
		if (args.length < 1) {
			log(wrapRed("at least one arg required"));
			log(usage());
			System.exit(1);
		} else {
			switch (args[0]) {
				case "build" -> 			 build(slice(args, 1));
				case "db" -> {
					if (args.length < 2) {
						log(wrapRed("with 'db' need another arg"));
						log(usage());
						System.exit(1);
					}
					switch (args[1]) {
						case "l" -> 				   localDb.localUpFromEntry(args);
						case "l-down", "ld" ->		   localDb.localDown();
						case "l-rerun-jooq", "lrrj" -> localDb.localRerunAndGenJooq(args);
						case "pr-status", "prs" ->   remoteDb.remoteStatus(args);
						case "pr-run", "prr" -> 	   remoteDb.remoteRun(args);
						default -> log("unknown args\n" + usage());
					}
				}
				default -> log("unknown args\n" + usage());
			}
		}
	}

	private String usage() {
		return """
				usage:
					build - build and deploy apps
					        number of app (required for run)
					        without args - printing apps list
					\s
					Databases (optional 1 arg - db name):
					db:
					l                 - start local postgres and run migrations
					l-down, ld      - down local postgres
					l-rerun-jooq,     - local down and up, if passed app id - run jooq
					lrrj
					pr-status, prs    - prod migrations status
					pr-run, prr       - execute prod migrations""";
	}

	private void build(String[] args) {
		if (args.length == 0) {
			buildService.printConfigWithDeprecated();
		} else {
			int id;

			try {
				id = parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				String type = args[0];
				buildService.printConfig(type);
				return;
			}

			try {
				Job job = jobsProvider.getJobById(id);
				boolean isBuildDockerNoCache = buildService.isBuildDockerNoCache(args);
				buildService.run(job, isBuildDockerNoCache);
			} catch (TooManyDockerFilesException e) {
				log("too many docker-files");
			} catch (BuildJobNotFoundException e) {
				log("job %s not found\n", args[1]);
			}
		}
	}
}
