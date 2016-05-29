/**
 * Created by Ксения on 28.05.2016.
 */

public class FileHolder {
    private String name;
    private boolean isDirectory;

    public FileHolder(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}