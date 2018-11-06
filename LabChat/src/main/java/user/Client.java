package user;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends User {

	public Client(String name, Socket socket, ObjectInputStream inStream, ObjectOutputStream outStream) {
		super(name, socket, inStream, outStream);
	}

}
