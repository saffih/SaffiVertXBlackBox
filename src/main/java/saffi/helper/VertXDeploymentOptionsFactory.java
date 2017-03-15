package saffi.helper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VertXDeploymentOptionsFactory {
	public static DeploymentOptions getOptions(AbstractVerticle verticle) {
		JsonObject config = verticle.config();
		if (config.isEmpty()) {
			config = readConfig();
		}

		final DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		return options;
	}

	public static DeploymentOptions getTestOptions() {
		JsonObject config = readConfig();

		final DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		return options;
	}

	private static JsonObject readConfig() {
		String runDir = System.getProperty("user.dir");
		final String path = "src/main/conf/blackbox.json";
		String jsonConf = readFile(path, StandardCharsets.UTF_8);
		return new JsonObject(jsonConf);
	}

	private static String readFile(String path, Charset encoding) {
		try {
            byte[] encoded = new byte[0];
            encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (IOException e) {
            throw new RuntimeException("can't find config at  " + path, e);
        }
	}
}
