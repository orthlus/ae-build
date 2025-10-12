package art.aelaort.db;

import art.aelaort.properties.DbManageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static art.aelaort.utils.ColoredConsoleTextUtils.wrapGreen;
import static art.aelaort.utils.Utils.log;

@Component
@RequiredArgsConstructor
public class DbListService {
	private final DbManageProperties dbManageProperties;

	public void printDbList() {
		try (Stream<Path> list = Files.list(dbManageProperties.getMigrationsDir())) {
			List<String> names = list
					.filter(Files::isDirectory)
					.map(p -> p.getFileName().toString())
					.toList();
			if (!names.isEmpty()) {
				log("db list:");
				names.forEach(s->log(wrapGreen(s)));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
