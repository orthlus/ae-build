package art.aelaort.no_spring;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
	private static final String PROPERTIES_FILE = "application.properties";
	private final Properties properties = new Properties();

	public PropertiesReader() {
		loadProperties();
	}

	@SneakyThrows
	private void loadProperties() {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
			if (input == null) {
				System.out.println("Sorry, unable to find " + PROPERTIES_FILE);
				return;
			}
			properties.load(input);
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public int getIntProperty(String key, int defaultValue) {
		String value = properties.getProperty(key);
		return value != null ? Integer.parseInt(value) : defaultValue;
	}

	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = properties.getProperty(key);
		return value != null ? Boolean.parseBoolean(value) : defaultValue;
	}
}
