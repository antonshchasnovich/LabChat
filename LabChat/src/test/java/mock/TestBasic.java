package mock;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TestBasic implements RemoteEndpoint.Basic {
    ArrayList<Object> sendedObjects = new ArrayList<>();

    @Override
    public void sendObject(Object o) throws IOException, EncodeException {
        sendedObjects.add(o);
    }

    public ArrayList<Object> getSendedObjects() {
        return sendedObjects;
    }

    @Override
    public void sendText(String s) throws IOException {

    }

    @Override
    public void sendBinary(ByteBuffer byteBuffer) throws IOException {

    }

    @Override
    public void sendText(String s, boolean b) throws IOException {

    }

    @Override
    public void sendBinary(ByteBuffer byteBuffer, boolean b) throws IOException {

    }

    @Override
    public OutputStream getSendStream() throws IOException {
        return null;
    }

    @Override
    public Writer getSendWriter() throws IOException {
        return null;
    }

    @Override
    public void setBatchingAllowed(boolean b) throws IOException {

    }

    @Override
    public boolean getBatchingAllowed() {
        return false;
    }

    @Override
    public void flushBatch() throws IOException {

    }

    @Override
    public void sendPing(ByteBuffer byteBuffer) throws IOException, IllegalArgumentException {

    }

    @Override
    public void sendPong(ByteBuffer byteBuffer) throws IOException, IllegalArgumentException {

    }
}
