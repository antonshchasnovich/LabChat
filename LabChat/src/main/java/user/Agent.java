package user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Agent extends User {

	public Agent(String name, Socket socket, ObjectInputStream inStream, ObjectOutputStream outStream) throws IOException {
		super(name, socket, inStream, outStream);
	}

}
