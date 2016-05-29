import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ксения on 28.05.2016.
 */

public class Connection implements AutoCloseable {
    private Socket socket;
    private Boolean isClosed;
    private DataOutputStream out;
    private DataInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        isClosed = false;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public synchronized void close() {
        if (isClosed) {
            return;
        }
        try {
            socket.close();
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) { }
        isClosed = true;
    }

    public void waitUntilClosed() {
        final int delay = 100;
        try {
            while (in != null && in.available() > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException err) {
                    System.out.println("Unexpected interrupt while waiting closing connection");
                    break;
                }
            }
        } catch (IOException ignored) { }
        close();
    }

    public void ask(int id, String path) throws IOException {
        out.writeInt(id);
        out.flush();
        out.writeUTF(path);
        out.flush();
    }

    public int readAskType() {
        try {
            return in.readInt();
        } catch (IOException e) {
            isClosed = true;
            return 0;
        }
    }

    public String readPath() throws IOException {
        return in.readUTF();
    }

    public void writeGet(int size, InputStream file) throws IOException {
        out.writeInt(size);
        out.flush();
        IOUtils.copyLarge(file, out);
        out.flush();
    }

    public BoundedInputStream readGet() throws IOException {
        int size = in.readInt();
        BoundedInputStream result = new BoundedInputStream(in, size);
        (new Thread(this::waitUntilClosed)).start();
        return result;
    }

    public void writeList(int size, ArrayList<FileHolder> files) throws IOException {
        out.writeInt(size);
        out.flush();
        for (FileHolder file: files) {
            out.writeUTF(file.getName());
            out.writeBoolean(file.isDirectory());
            out.flush();
        }
    }

    public ArrayList<FileHolder> readList() throws IOException {
        int size = in.readInt();
        ArrayList<FileHolder> files = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String name = in.readUTF();
            Boolean isDirectory = in.readBoolean();
            files.add(new FileHolder(name, isDirectory));
        }
        close();
        return files;
    }
}