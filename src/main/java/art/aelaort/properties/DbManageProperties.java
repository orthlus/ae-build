package art.aelaort.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties("db")
@Getter
@Setter
public class DbManageProperties {
	private Path migrationsDir;
	private Path defaultNameFile;
	private String remoteSshDockerComposeFilename;
	private String filesListFilename;
	private String urlFilename;
}
