package art.aelaort;

import art.aelaort.no_spring.BuildListEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static art.aelaort.utils.Utils.slice;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		if (args.length >= 1 && args[0].equals("build")) {
			new BuildListEntry().run(slice(args, 1));
		} else {
			SpringApplication.run(Main.class, args);
		}
	}
}
