package info.unterrainer.htl.htlzeromq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GreetingServer {

	public static void main(String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket socket = context.createSocket(SocketType.REP);
			socket.bind("tcp://*:5556");
			log.info("Starting Server.");
			while (!Thread.currentThread().isInterrupted()) {
				byte[] reply = socket.recv(0);
				log.info("Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]");
				String response = "world";
				socket.send(response.getBytes(ZMQ.CHARSET), 0);
				Thread.sleep(1); // Do some 'work'
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		log.info("Stopping Server.");
	}
}