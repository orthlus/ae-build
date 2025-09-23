package art.aelaort.build;

import art.aelaort.models.build.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static art.aelaort.utils.Utils.log;

@Component
@RequiredArgsConstructor
public class BuildConfigService {
	private final JobsProvider jobsProvider;
	private final JobsTextTable jobsTextTable;

	public void printConfig(String typeAlias) {
		List<Job> jobs = jobsProvider.readBuildConfig();
		jobs = jobs.stream()
				.filter(job -> buildTypeAlias(job.getBuildType()).equals(typeAlias))
				.filter(job -> !job.isDeprecated())
				.toList();
		log(jobsTextTable.getJobsTableString(jobs));
	}

	private String buildTypeAlias(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		return Arrays.stream(input.split("_"))
				.filter(part -> !part.isEmpty())
				.map(part -> part.substring(0, 1))
				.collect(Collectors.joining());
	}

	public void printConfigNoDeprecated() {
		List<Job> jobs = jobsProvider.readBuildConfig();
		jobs = jobs.stream()
				.filter(job -> !job.isDeprecated())
				.toList();
		log(jobsTextTable.getJobsTableString(jobs));
	}

	public void printConfigWithDeprecated() {
		log(jobsTextTable.getJobsTableString(jobsProvider.readBuildConfig()));
	}
}
