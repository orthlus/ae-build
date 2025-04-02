package art.aelaort.utils.system;

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import static art.aelaort.utils.Utils.log;

@Component
public class SystemProcess {
	private final Set<String> knownCommands = Set.of("mvn", "yarn", "docker");
	private final Set<String> cmdCommands = Set.of("mvn", "yarn");

	private String fixCmdCall(String command) {
		String first = StringUtils.split(command)[0];
		if (cmdCommands.contains(first)) {
			return "cmd /c " + command;
		}
		if (knownCommands.contains(first)) {
			return command;
		}

		Response response = callProcess("where /f " + first);
		if (response.exitCode() == 0) {
			String[] split = response.stdout().split("\"");
			if (split.length >= 1) {
				if (matchExt(split, ".exe")) {
					return command;
				} else if (matchExt(split, ".cmd")) {
					return "cmd /c " + command;
				}
			}
		}
		return command;
	}

	private boolean matchExt(String[] paths, String extension) {
		return Stream.of(paths).anyMatch(s -> s.endsWith(extension));
	}

	public void callProcessForBuild(String command, Path dir) {
		try {
			String[] commandArray = StringUtils.split(fixCmdCall(command));
			ProcessBuilder pb = new ProcessBuilder(commandArray)
					.inheritIO();

			if (dir != null) {
				pb.directory(dir.toFile());
			}

			int code = pb.start().waitFor();
			if (code != 0) {
				log("java system process exit code " + code);
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("java system process call error: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	public void callProcessInheritIO(String command, Path dir) {
		try {
			String[] commandArray = StringUtils.split(command);
			Process p = new ProcessBuilder(commandArray)
					.inheritIO()
					.directory(dir.toFile())
					.start();
			p.waitFor();
		} catch (Exception e) {
			System.err.println("java system process call error: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	public void callProcessInheritIO(String command) {
		try {
			String[] commandArray = StringUtils.split(command);
			Process p = new ProcessBuilder(commandArray)
					.inheritIO()
					.start();
			p.waitFor();
		} catch (Exception e) {
			System.err.println("java system process call error: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	public Response callProcess(String... command) {
		return callProcess(null, command);
	}

	public Response callProcessThrows(Path dir, String... command) {
		Response response = callProcess(dir, command);
		if (response.exitCode() != 0) {
			throw new RuntimeException(response.stderr());
		}
		return response;
	}

	public Response callProcess(Path dir, String... command) {
		try {
			ProcessBuilder pb;
			if (command.length == 1) {
				String[] split = StringUtils.split(command[0]);
				pb = new ProcessBuilder(split);
			} else {
				pb = new ProcessBuilder(command);
			}

			if (dir != null) {
				pb.directory(dir.toFile());
			}

			Process p = pb.start();

			Response.ResponseBuilder builder = Response.builder();

			try (Reader reader = new InputStreamReader(p.getInputStream())) {
				builder.stdout(CharStreams.toString(reader));
			}

			try (Reader reader = new InputStreamReader(p.getErrorStream())) {
				builder.stderr(CharStreams.toString(reader));
			}

			p.waitFor(30, TimeUnit.MINUTES);

			return builder.exitCode(p.exitValue()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(1, "thrown exception in java", e.getMessage());
		}
	}

	public Response callProcessInheritFilteredStdout(Function<String, Boolean> filter, String... command) {
		try {
			ProcessBuilder pb;
			if (command.length == 1) {
				String[] split = StringUtils.split(command[0]);
				pb = new ProcessBuilder(split);
			} else {
				pb = new ProcessBuilder(command);
			}

			Process p = pb.start();

			Response.ResponseBuilder builder = Response.builder();

			StringBuilder stdout = new StringBuilder();
			try (BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line;
				while ((line = b.readLine()) != null) {
					if (filter.apply(line)) {
						log(line);
					}
					stdout.append(line);
				}
				builder.stdout(stdout.toString());
			}

			try (Reader reader = new InputStreamReader(p.getErrorStream())) {
				builder.stderr(CharStreams.toString(reader));
			}

			p.waitFor(30, TimeUnit.MINUTES);

			return builder.exitCode(p.exitValue()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(1, "thrown exception in java", e.getMessage());
		}
	}
}
