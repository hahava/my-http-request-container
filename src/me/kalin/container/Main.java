package me.kalin.container;

import me.kalin.container.config.ServerConfig;
import me.kalin.container.context.ServerContext;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setPort(8080);

		try {
			ServerContext.getInstance().start(serverConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
