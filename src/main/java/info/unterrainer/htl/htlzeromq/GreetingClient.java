package info.unterrainer.htl.htlzeromq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GreetingClient {

	public static void main(final String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			log.info("Connecting to hello world server");

			// Socket to talk to server
			ZMQ.Socket socket = context.createSocket(SocketType.REQ);
			socket.connect("tcp://localhost:5556");

			for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
				String request = "Hello";
				log.info("Sending Hello " + requestNbr);
				socket.send(request.getBytes(ZMQ.CHARSET), 0);

				byte[] reply = socket.recv(0);
				log.info("Received " + new String(reply, ZMQ.CHARSET) + " " + requestNbr);
			}
		}
	}
}