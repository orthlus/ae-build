package art.aelaort.utils;

import art.aelaort.properties.DbManageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbUtils {
	private final DbManageProperties props;

	public String getName(String[] args) {
		try {
			if (args == null) {
				return Files.readString(props.getDefaultNameFile());
			}
			if (args.length > 1) {
				return args[1];
			}
			return Files.readString(props.getDefaultNameFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String readDbUrl(Path dbDir) {
		try {
			return Files.readString(dbDir.resolve(props.getUrlFilename()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getChangeSetsFiles(Path dir) {
		try {
			return Files.readAllLines(dir.resolve(props.getFilesListFilename()))
					.stream()
					.filter(s -> !s.startsWith("#"))
					.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Path getDbDir(String name, String stand) {
		return props.getMigrationsDir()
				.resolve(name)
				.resolve(stand);
	}
}
