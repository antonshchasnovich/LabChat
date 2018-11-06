package message;

public enum MessageType {
	TEXT_MESSAGE, // простое текстовое сообщение
	AGENT_REG_MESSAGE, // посылается на сервер при вводе команды "/register agent nickname"
	CLIENT_REG_MESSAGE, // посылается на сервер при вводе команды "/register client nickname"
	EXIT_MESSAGE, // посылается на сервер при вводе команды "/exit"
	LEAVE_MESSAGE, // посылается на сервер при вводе команды "/leave", а так же с сервера агенту
					// если его клиент покинул чат
	TEST_MESSAGE; // посылается сервером агенту прежде чем забрать его из очереди для проверки не
					// закрыто ли соединение
}
