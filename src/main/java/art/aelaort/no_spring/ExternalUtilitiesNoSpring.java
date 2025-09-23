package art.aelaort.no_spring;

import art.aelaort.utils.ExternalUtilities;
import art.aelaort.utils.system.Response;
import art.aelaort.utils.system.SystemProcess;

public class ExternalUtilitiesNoSpring extends ExternalUtilities {
	private final SystemProcess systemProcess;

	public ExternalUtilitiesNoSpring(SystemProcess systemProcess) {
		super(systemProcess);
		this.systemProcess = systemProcess;
	}

	@Override
	public String readBuildConfig() {
		PropertiesReader props = new PropertiesReader();
		String buildConfigBin = props.getProperty("build.data.config.bin");
		String buildConfigConverterPath = props.getProperty("build.data.config.converter.path");
		return readBuildConfig(buildConfigBin, buildConfigConverterPath);
	}

	private String readBuildConfig(String buildConfigBin, String buildConfigConverterPath) {
		Response response = systemProcess.callProcess(buildConfigBin, buildConfigConverterPath);
		if (response.exitCode() == 0) {
			return response.stdout();
		}
		throw new RuntimeException(response.stderr());
	}
}
