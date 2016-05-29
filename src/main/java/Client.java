import org.apache.commons.io.input.BoundedInputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ксения on 28.05.2016.
 */

public class Client  {
    public static final int CLOSE_QUERY = 0;
    public static final int GET_QUERY = 1;
    public static final int LIST_QUERY = 2;

    private String hostName;
    private int portNumber;

    public Client(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public BoundedInputStream get(String path) throws IOException {
        Socket clientSocket = new Socket(hostName, portNumber);
        Connection curConnection = new Connection(clientSocket);
        curConnection.ask(GET_QUERY, path);
        return curConnection.readGet();
    }

    public ArrayList<FileHolder> list(String path) throws IOException {
        ArrayList<FileHolder> result;
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                Connection curConnection = new Connection(clientSocket)) {
            curConnection.ask(LIST_QUERY, path);
            result = curConnection.readList();
        }
        return result;
    }
}