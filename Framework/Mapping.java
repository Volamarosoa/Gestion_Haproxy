package etu2068.framework;
public class Mapping {
    String className;
    String method;

    public Mapping(){}

    public Mapping(String className, String method){
        this.setClassName(className);
        this.setMethod(method);
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return this.method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    
}
