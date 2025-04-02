package art.aelaort.build;

import art.aelaort.S3Params;
import art.aelaort.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

import static art.aelaort.S3ClientProvider.client;

@Component
@RequiredArgsConstructor
public class BuildFunctionsS3 {
	private final S3Params buildS3Params;
	private final S3Properties s3Properties;

	public void uploadZip(Path file) {
		try (S3Client client = client(buildS3Params)) {
			client.putObject(PutObjectRequest.builder()
					.bucket(s3Properties.getBuild().getBucket())
					.key(file.getFileName().toString())
					.build(), file);
		}
	}
}
