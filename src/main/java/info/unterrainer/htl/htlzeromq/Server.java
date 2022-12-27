package info.unterrainer.htl.htlzeromq;

import lombok.extern.slf4j.Slf4j;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

	public static void main(String[] args) throws Exception {
		log.info("Starting ChatServer.");
		ChatServer chatServer = new ChatServer();
		chatServer.run();
		log.info("Stopping ChatServer.");
	}
}