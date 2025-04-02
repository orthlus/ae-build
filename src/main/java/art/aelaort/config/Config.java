package art.aelaort.config;

import art.aelaort.DefaultS3Params;
import art.aelaort.S3Params;
import art.aelaort.properties.S3Properties;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class Config {
	@Autowired
	private S3Properties properties;

	@Bean
	public XmlMapper xmlMapper() {
		return new XmlMapper();
	}

	@Bean
	public YAMLMapper yamlMapper() {
		return new YAMLMapper();
	}

	@Bean
	@Primary
	public JsonMapper jsonMapper() {
		return new JsonMapper();
	}

	@Bean
	public S3Params buildS3Params() {
		return new DefaultS3Params(
				properties.getBuild().getId(),
				properties.getBuild().getKey(),
				properties.getEndpoint(),
				properties.getRegion());
	}
}
