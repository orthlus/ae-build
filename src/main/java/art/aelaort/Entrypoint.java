package art.aelaort;

import art.aelaort.build.BuildConfigService;
import art.aelaort.build.BuildLaunchUtils;
import art.aelaort.build.BuildService;
import art.aelaort.build.JobsProvider;
import art.aelaort.db.DbListService;
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
	private final BuildConfigService buildConfigService;
	private final DbListService dbListService;

	@Override
	public void run(String... args) {
		if (args.length < 1) {
			log(wrapRed("at least one arg required"));
			log(usage());
			System.exit(1);
		} else {
			switch (args[0]) {
				case "build" -> build(slice(args, 1));
				case "db" -> {
					if (args.length < 2) {
						log(wrapRed("with 'db' need another arg"));
						dbListService.printDbList();
						log(usage());
						System.exit(1);
					}
					switch (args[1]) {
						case "l" ->						localDb.localUp(slice(args, 2));
						case "l-down", "ld" ->			localDb.localDown();
						case "l-rerun-jooq", "lrrj" ->	localDb.localRerunAndGenJooq(slice(args, 2));
						case "prod-status", "prods" ->	remoteDb.remoteStatus(slice(args, 2));
						case "prod-run" ->				remoteDb.remoteRun(slice(args, 2));
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
					build - build and deploy apps:
					        number of app (required for run)
					        number and -d - build without approve
					        with -a - print all
					        no args - print all without deprecated
					\s
					Databases (optional 1 arg - db name):
					db:
					l                    - start local postgres and run migrations
					l-down, ld           - down local postgres
					l-rerun-jooq, lrrj   - local down and up, if passed app id - run jooq
					prod-status, prods   - prod migrations status
					prod-run        - execute prod migrations""";
	}

	@SuppressWarnings("RedundantIfStatement")
	private void build(String[] args) {
		switch (BuildLaunchUtils.build(args)) {
			case printConfig -> buildConfigService.printConfig(args[0]);
			case printConfigNoDeprecated -> buildConfigService.printConfigNoDeprecated();
			case printConfigWithDeprecated -> buildConfigService.printConfigWithDeprecated();
			case build -> {
				try {
					Job job = jobsProvider.getJobById(parseInt(args[0]));
					boolean isBuildDockerNoCache = buildService.isBuildDockerNoCache(args);
					if (args.length > 1 && args[1].equals("-d")) {
						buildService.run(job, isBuildDockerNoCache, false);
					} else {
						buildService.run(job, isBuildDockerNoCache, true);
					}
				} catch (TooManyDockerFilesException e) {
					log("too many docker-files");
				} catch (BuildJobNotFoundException e) {
					log("job %s not found\n", args[1]);
				}
			}
		}
	}
}
