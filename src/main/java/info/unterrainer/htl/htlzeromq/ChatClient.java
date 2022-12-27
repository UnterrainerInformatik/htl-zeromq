package info.unterrainer.htl.htlzeromq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import info.unterrainer.htl.htlzeromq.chatserver.MessageType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

	public static void main(final String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			log.info("Connecting to chat server");

			// Socket to talk to server
			Socket ctrlSocket = context.createSocket(SocketType.REQ);
			ctrlSocket.connect("tcp://localhost:5556");
			Socket subSocket = context.createSocket(SocketType.SUB);
			subSocket.connect("tcp://localhost:5557");
			subSocket.subscribe("topic");

			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.execute(() -> {
				log.info("Starting subscription listener...");
				while (!Thread.currentThread().isInterrupted()) {
					ZMsg msg = ZMsg.recvMsg(subSocket);
					log.info("Chat message received: [{}]", ChatServer.messageAsString(msg));
					msg.destroy();
				}
			});

			Thread.sleep(3);

			ZMsg m = null;

			log.info("Setting username.");
			m = new ZMsg();
			m.append(MessageType.SET_USERNAME.name());
			m.append("Gluppi");
			m.send(ctrlSocket, true);
			waitForReply(ctrlSocket);

			log.info("Posting something.");
			m = new ZMsg();
			m.append(MessageType.POST.name());
			m.append("topic");
			m.append("Testmessage glubbi blubbi.");
			m.send(ctrlSocket, true);
			waitForReply(ctrlSocket);

			Thread.sleep(5000);
			executor.shutdownNow();
			log.info("Stopped.");
		}
	}

	private static void waitForReply(final Socket socket) {
		byte[] reply = socket.recv(0);
		log.info("Received: " + new String(reply, ZMQ.CHARSET));
	}
}