package etu2068.modelView;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ModelView {
    String view;
    boolean isJson;
    boolean invalidateSession ;
    HashMap<String, Object> data;
    HashMap<String, Object> session;
    List<String> removeSession;    
    
    public ModelView() {
        this.setData(new HashMap<String,Object>());
    }
    
    public ModelView(String view) {
        this.setView(view);
        this.setData(new HashMap<String,Object>());
        this.setSession(new HashMap<String,Object>());
        this.setRemoveSession(new ArrayList<String>());
    }
    
    public String getView() {
        return this.view;
    }
    
    public void setView(String view) {
        this.view = view;
    }
    
    public HashMap<String, Object> getData() {
        return this.data;
    }
    
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
    
    public void addItem(String key, Object value) {
        this.getData().put(key, value);
    }
    
    public HashMap<String, Object> getSession() {
        return this.session;
    }
    
    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    
    public void addSession(String key, Object value) {
        this.getSession().put(key, value);
    }
    
    public boolean isJson() {
        return this.isJson;
    }
    
    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }

    public List<String> getRemoveSession() {
        return this.removeSession;
    }

    public void setRemoveSession(List<String> removeSession) {
        this.removeSession = removeSession;
    }
    
    public boolean isInvalidateSession() {
        return this.invalidateSession;
    }

    public void setInvalidateSession(boolean invalidateSession) {
        this.invalidateSession = invalidateSession;
    }

    public void addRemoveSession(String nom) {
        this.getRemoveSession().add(nom);
    }

}