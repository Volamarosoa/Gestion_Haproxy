package etu2068.fileUpload;
public class FileUpload {
    String path = "";
    String name = "";
    byte[] bytes;

    public FileUpload() {}

    public FileUpload(byte[] bytes) {
        this.setBytes(bytes);
    }

    public FileUpload(String path, String name, byte[] bytes) {
        this.setPath(path);
        this.setName(name);
        this.setBytes(bytes);
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
