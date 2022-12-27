package info.unterrainer.htl.htlzeromq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import info.unterrainer.htl.htlzeromq.chatserver.Room;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a chat-server that also doubles as a greeting-server as in the first
 * example. If only a single string is sent, then it works as a greeing-server.
 */
@Slf4j
public class ChatServer {

	private Map<String, Room> rooms = new HashMap<>();

	public void run() {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket inSocket = context.createSocket(SocketType.REP);
			inSocket.bind("tcp://*:5556");
			ZMQ.Socket pubSocket = context.createSocket(SocketType.PUB);
			pubSocket.bind("tcp://*:5557");

			while (!Thread.currentThread().isInterrupted()) {
				ZMsg msg = ZMsg.recvMsg(inSocket);

				if (msg.size() == 1) {
					// Chat-Server-Mode.
					String message = new String(msg.pop().getData(), StandardCharsets.UTF_8);
					log.info("Received " + ": [" + message + "]");
					String response = "world";
					inSocket.send(response.getBytes(ZMQ.CHARSET), 0);
					continue;
				}

				String type = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				String userId = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				String roomId = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				String message = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				msg.destroy();
				System.out.println(
						"Message received " + ": [" + userId + "]" + " [" + roomId + "]" + " [" + message + "]");
				String response = "ACK";
				inSocket.send(response.getBytes(ZMQ.CHARSET), 0);
			}
		}
	}
}
