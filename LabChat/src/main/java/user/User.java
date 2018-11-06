package user;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class User {
	private final String name;
	private final Socket socket;
	private final ObjectInputStream inStream;
	private final ObjectOutputStream outStream;

	public User(String name, Socket socket, ObjectInputStream inStream, ObjectOutputStream outStream) {
		this.name = name;
		this.socket = socket;
		this.outStream = outStream;
		this.inStream = inStream;
	}

	public String getName() {
		return name;
	}

	public ObjectOutputStream getOutStream() {
		return outStream;
	}

	public ObjectInputStream getInStream() {
		return inStream;
	}

	public Socket getSocket() {
		return socket;
	}
}
