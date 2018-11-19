package user;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(name, user.name) &&
				Objects.equals(socket, user.socket) &&
				Objects.equals(inStream, user.inStream) &&
				Objects.equals(outStream, user.outStream);
	}

	@Override
	public int hashCode() {

		return Objects.hash(name, socket, inStream, outStream);
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
