package me.kalin.container.context;

import me.kalin.container.config.ServerConfig;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public final class ServerContext {
	private final static ServerContext serverContext = new ServerContext();

	private ServerContext() {
	}

	public static ServerContext getInstance() {
		return serverContext;
	}

	public void start(ServerConfig serverConfig) throws IOException {
		ServerSocket serverSocket = new ServerSocket(serverConfig.getPort());
		while (true) {
			new ServerHandler(serverSocket.accept()).start();
		}
	}

	private static class ServerHandler extends Thread {
		private Socket client;

		public ServerHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try (
				var bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				var printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			) {
				StringBuffer stringBuffer = new StringBuffer();
				String input;
				while ((input = bufferedReader.readLine()) != null && input.length() != 0) {
					stringBuffer.append(input).append(System.getProperty("line.separator"));
					if (hasRequestBody(input)) {
						var postDataI = Integer.parseInt(input.substring("Content-Length: ".length()));
						var charArray = new char[postDataI];
						bufferedReader.readLine();
						bufferedReader.read(charArray, 0, postDataI);
						var postData = new String(charArray);
						stringBuffer.append(postData);
						break;
					}
				}

				System.out.println(stringBuffer.toString());

				printWriter.println("HTTP/1.1 200 OK\r\n");
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		private boolean hasRequestBody(String input) {
			return input.contains("Content-Length:");
		}
	}
}
