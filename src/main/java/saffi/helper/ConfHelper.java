package saffi.helper;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfHelper {
	public static DeploymentOptions getDeploymentOptions() {
		final JsonObject config = readConfig();

		final DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		return options;
	}

	public static JsonObject readConfig() {
		String runDir = System.getProperty("user.dir");
		System.out.println("running dir :"+runDir);
		final String path = "src/main/conf/json-blackbox.json";
		String jsonConf = readFile(path, StandardCharsets.UTF_8);
		return new JsonObject(jsonConf);
	}

	private static String readFile(String path, Charset encoding)
	{
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException("can't find config at  "+path,e);
		}
		return new String(encoded, encoding);
	}
}
