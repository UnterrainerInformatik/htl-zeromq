package info.unterrainer.htl.htlzeromq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import info.unterrainer.htl.htlzeromq.chatserver.MessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a chat-server that also doubles as a greeting-server as in the first
 * example. If only a single string is sent, then it works as a greeting-server.
 */
@Slf4j
public class ChatServer {

	private Map<byte[], String> users = new HashMap<>();

	public void run() {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			Socket ctrlSocket = context.createSocket(SocketType.ROUTER);
			ctrlSocket.bind("tcp://*:5556");
			Socket pushSocket = context.createSocket(SocketType.PUB);
			pushSocket.bind("tcp://*:5557");

			while (!Thread.currentThread().isInterrupted()) {
				ZMsg msg = ZMsg.recvMsg(ctrlSocket);
				String receivedMessage = messageAsString(msg);

				log.info("Message received: [{}]", receivedMessage);

				if (msg.size() < 3) {
					error(ctrlSocket, msg, "Message size was [%d], which is not supported.", msg.size());
					continue;
				}

				byte[] userId = msg.pop().getData();
				msg.pop(); // Pop the null-frame that's here because our REQ-ROUTER combo.

				String typeString = new String(msg.pop().getData(), StandardCharsets.UTF_8);
				MessageType type = null;
				try {
					type = MessageType.valueOf(typeString);
				} catch (IllegalArgumentException e) {
					error(ctrlSocket, msg, "Got invalid type [%s] in message [%s].", typeString, receivedMessage);
					if (msg.size() == 0) {
						log.info("Assuming it's a greeting message.");
						handleGreetingServerMessage(ctrlSocket, userId, typeString);
						continue;
					}
					continue;
				}

				switch (type) {
				case SET_USERNAME:
					handleSetUsername(ctrlSocket, userId, msg);
					continue;
				case POST:
					handlePost(ctrlSocket, pushSocket, userId, msg);
					continue;
				default:
					msg.destroy();
					continue;
				}
			}
		}
	}

	private void handleSetUsername(final Socket socket, final byte[] userId, final ZMsg msg) {
		if (msg.size() < 1) {
			error(socket, msg, "SET_USERNAME must be of form [..., USERNAME].");
			return;
		}
		String name = new String(msg.pop().getData(), StandardCharsets.UTF_8);
		users.put(userId, name);
		msg.destroy();

		ZMsg m = new ZMsg();
		m.append(userId);
		m.append(new byte[0]);
		m.append("New username set.");
		m.send(socket, true);
	}

	private void handlePost(final Socket socket, final Socket pushSocket, final byte[] userId, final ZMsg msg) {
		if (msg.size() < 2) {
			error(socket, msg, "POST must be of form [..., ROOM, MESSAGE].");
			return;
		}
		String room = new String(msg.pop().getData(), StandardCharsets.UTF_8);
		String message = new String(msg.pop().getData(), StandardCharsets.UTF_8);
		msg.destroy();

		ZMsg m = new ZMsg();
		m.append(room);
		m.append(getUserName(userId));
		m.append(message);
		m.send(pushSocket, true);

		m = new ZMsg();
		m.append(userId);
		m.append(new byte[0]);
		m.append("Message was posted.");
		m.send(socket, true);
	}

	private String getUserName(byte[] userId) {
		String name = users.get(userId);
		if (name == null)
			return userId.toString();
		return name;
	}

	private void error(final Socket socket, final ZMsg msg, final String errorMessage, final Object... args) {
		String errMsg = String.format(errorMessage, args);
		log.warn(errMsg);
		ZMsg m = ZMsg.newStringMsg(MessageType.ERROR.name(), errMsg,
				"Your original message follows as it has been received:", messageAsString(msg));
		msg.destroy();
		m.send(socket, true);
	}

	public static String messageAsString(final ZMsg msg) {
		StringBuilder sb = new StringBuilder();
		for (ZFrame frame : msg) {
			sb.append(frame.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private void handleGreetingServerMessage(final Socket socket, final byte[] userId, final String message) {
		// Chat-Server-Mode.
		log.info("Received " + ": [" + message + "]");
		String response = message + " world";

		ZMsg m = new ZMsg();
		m.append(userId);
		m.append(new byte[0]);
		m.append(response);
		m.send(socket, true);
	}
}
