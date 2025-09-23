package art.aelaort.build;

import lombok.experimental.UtilityClass;

import static art.aelaort.build.BuildLaunchType.*;
import static java.lang.Integer.parseInt;

@UtilityClass
public class BuildLaunchUtils {
	public BuildLaunchType build(String[] args) {
		if (args.length == 0) {
			return printConfigNoDeprecated;
		} else {
			try {
				parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				String probablyType = args[0];
				if (probablyType.equals("-a")) {
					return printConfigWithDeprecated;
				} else {
					return printConfig;
				}
			}
			return build;
		}
	}
}
