package art.aelaort.no_spring;

import art.aelaort.build.BuildConfigService;
import art.aelaort.build.BuildLaunchUtils;
import art.aelaort.build.JobsProvider;
import art.aelaort.build.JobsTextTable;
import art.aelaort.utils.system.SystemProcess;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class BuildListEntry {
	public void run(String[] args) {
		JobsProvider jobsProvider = new JobsProvider(new ExternalUtilitiesNoSpring(new SystemProcess()), new JsonMapper());
		JobsTextTable jobsTextTable = new JobsTextTable();
		BuildConfigService buildConfigService = new BuildConfigService(jobsProvider, jobsTextTable);

		switch (BuildLaunchUtils.build(args)) {
			case printConfig -> buildConfigService.printConfig(args[0]);
			case printConfigNoDeprecated -> buildConfigService.printConfigNoDeprecated();
			case printConfigWithDeprecated -> buildConfigService.printConfigWithDeprecated();
			case build -> throw new IllegalArgumentException("build");
		}

	}
}
