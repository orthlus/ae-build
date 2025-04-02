package art.aelaort.db;

import art.aelaort.properties.DbManageProperties;
import art.aelaort.utils.DbUtils;
import art.aelaort.utils.system.SystemProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static art.aelaort.utils.ColoredConsoleTextUtils.wrapRed;
import static art.aelaort.utils.Utils.log;

@Component
@RequiredArgsConstructor
public class RemoteDb {
	private final SystemProcess systemProcess;
	private final DbManageProperties props;
	private final DbUtils dbUtils;
	private final LiquibaseService liquibaseService;

	public void remoteStatus(String[] args) {
		Path dir = dbUtils.getDbDir(dbUtils.getName(args), "prod");
		String url = dbUtils.readDbUrl(dir);
		sshUp(dir);
		log("\n\n");
		for (String changeSetFile : dbUtils.getChangeSetsFiles(dir)) {
			if (!liquibaseService.statusCli(dir.resolve(changeSetFile), url)) {
				log(wrapRed("миграции упали(((((((("));
				break;
			}
		}
		log("\n\n");
		sshDown(dir);
	}

	public void remoteRun(String[] args) {
		Path dir = dbUtils.getDbDir(dbUtils.getName(args), "prod");
		String url = dbUtils.readDbUrl(dir);
		sshUp(dir);
		log("\n\n");
		for (String changeSetFile : dbUtils.getChangeSetsFiles(dir)) {
			if (!liquibaseService.updateCli(dir.resolve(changeSetFile), url)) {
				log(wrapRed("миграции упали(((((((("));
				break;
			}
		}
		log("\n\n");
		sshDown(dir);
	}

	private void sshUp(Path dir) {
		String command = "docker compose -f %s up -d --build"
				.formatted(dir.getParent().resolve(props.getRemoteSshDockerComposeFilename()));
		systemProcess.callProcessInheritIO(command);
		log("SSH tunnel - started");
	}

	private void sshDown(Path dir) {
		String command = "docker compose -f %s down"
				.formatted(dir.getParent().resolve(props.getRemoteSshDockerComposeFilename()));
		systemProcess.callProcessInheritIO(command);
		log("SSH tunnel - stopped");
	}
}
