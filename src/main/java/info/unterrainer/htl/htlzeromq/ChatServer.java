package info.unterrainer.htl.htlzeromq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import info.unterrainer.htl.htlzeromq.chatserver.Room;

public class ChatServer {

	private Map<String, Room> rooms = new HashMap<>();

	public void run() {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket socket = context.createSocket(SocketType.REP);
			socket.bind("tcp://*:5555");

			while (!Thread.currentThread().isInterrupted()) {
				ZMsg msg = ZMsg.recvMsg(socket);
				String userId = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				String roomId = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				String message = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				msg.destroy();
				System.out.println(
						"Message received " + ": [" + userId + "]" + " [" + roomId + "]" + " [" + message + "]");
				String response = "ACK";
				socket.send(response.getBytes(ZMQ.CHARSET), 0);
			}
		}
	}
}
