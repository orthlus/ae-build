package art.aelaort.build;

import art.aelaort.models.build.Job;
import dnl.utils.text.table.TextTable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static art.aelaort.utils.TablePrintingUtils.*;

@Component
public class JobsTextTable {
	public String getJobsTableString(List<Job> jobs) {
		String[] columnNames = {
				"id",
				"name",
				"build_type",
				"sub_directory",
				"project_dir",
				"secrets_directory",
				"db",
				"deprecated"
		};
		Object[][] data = convertServersToArrays(jobs);
		TextTable tt = new TextTable(columnNames, data);
		return getTableString(tt);
	}

	private Object[][] convertServersToArrays(List<Job> jobs) {
		List<Job> sorted = jobs.stream().sorted(Comparator.comparing(Job::getId)).toList();

		Object[][] result = new Object[sorted.size()][8];
		for (int i = 0; i < sorted.size(); i++) {
			Job job = sorted.get(i);
			result[i][0] = job.getId();
			result[i][1] = job.getName();
			result[i][2] = job.getBuildType();
			result[i][3] = job.getSubDirectory();
			result[i][4] = nullable(job.getProjectDir());
			result[i][5] = nullable(job.getSecretsDirectory());
			result[i][6] = String.valueOf(job.isDb());
			result[i][7] = String.valueOf(job.isDeprecated());
		}

		appendSpaceToRight(result);

		return result;
	}
}
