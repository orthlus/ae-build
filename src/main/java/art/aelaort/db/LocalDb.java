package art.aelaort.db;

import art.aelaort.build.BuildProperties;
import art.aelaort.build.JobsProvider;
import art.aelaort.exceptions.LocalDbMigrationsFailedException;
import art.aelaort.models.build.Job;
import art.aelaort.utils.DbUtils;
import art.aelaort.utils.system.Response;
import art.aelaort.utils.system.SystemProcess;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static art.aelaort.utils.ColoredConsoleTextUtils.wrapRed;
import static art.aelaort.utils.Utils.log;
import static java.lang.Integer.parseInt;

@Component
@RequiredArgsConstructor
public class LocalDb {
	private final SystemProcess systemProcess;

	private final String dbContainerName = "pg-tmp-dev";
	private final DbUtils dbUtils;
	private final LiquibaseService liquibaseService;
	private final JobsProvider jobsProvider;
	private final BuildProperties buildProperties;

	public boolean isLocalRunning() {
		String command = "docker ps -q --filter name=" + dbContainerName;
		Response response = systemProcess.callProcess(command);
		if (response.exitCode() == 0) {
			return !response.stdout().trim().isEmpty();
		}

		throw new RuntimeException(response.stderr());
	}

	public void localUpFromEntry(String[] args) {
		try {
			localUp(args);
		} catch (LocalDbMigrationsFailedException e) {
			log(wrapRed("миграции упали(("));
		}
	}

	public void localUp() {
		localUp(null);
	}

	private void localUp(String[] args) {
		String command = "docker run -d -p 5433:5432 --env POSTGRES_PASSWORD=postgres --name %s postgres:16".formatted(dbContainerName);
		systemProcess.callProcessInheritIO(command);
		log("PostgreSQL (5433) - started");

		sleep();
		Path dir = dbUtils.getDbDir(dbUtils.getName(args), "local");
		String url = dbUtils.readDbUrl(dir);
		for (String changeSetFile : dbUtils.getChangeSetsFiles(dir)) {
			Path changeSet = dir.resolve(changeSetFile);
			boolean updated = liquibaseService.updateCli(changeSet, url);
			if (!updated) {
				throw new LocalDbMigrationsFailedException();
			}
		}
		log("Migrations - executed");
	}

	public void localDown() {
		String command = "docker rm -f -v " + dbContainerName;
		systemProcess.callProcessInheritIO(command);
		log("PostgreSQL (5433) - stopped");
	}

	@SneakyThrows
	private void sleep() {
		TimeUnit.SECONDS.sleep(1);
	}

	public void localRerunAndGenJooq(String[] args) {
		try {
			localDown();
			localUp();

			if (args.length > 0) {
				Job job = jobsProvider.getJobById(parseInt(args[1]));
				Path srcDir = job.resolveSrcDir(buildProperties.srcRootDir());
				String jooqCommand = "mvn clean jooq-codegen:generate";
				systemProcess.callProcessForBuild(jooqCommand, srcDir);
			}
		} catch (LocalDbMigrationsFailedException e) {
			log(wrapRed("миграции упали(("));
		}
	}
}
