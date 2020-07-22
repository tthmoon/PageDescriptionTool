package descriptiontool.structure;

import org.json.simple.JSONObject;

import java.util.EnumSet;
import java.util.Set;

public class Element {

    private String name;
    private Set<Access> access;
    private String path;

    public Element(String name) {
        this.name = name;
        this.access = EnumSet.noneOf(Access.class);
        this.path = "";
    }

    public Element(Element element) {
        this.name = element.getName();
        this.access = EnumSet.copyOf(element.getAccess());
        this.path = element.getPath();
    }

    public Element(String name, Set<Access> access, String path) {
        this.name = name;
        this.access = access;
        this.path = path;
    }

    public Element(JSONObject jsonElement) {
        String name = (String) jsonElement.get("element");
        Set<Access> access = Access.getAccessSet(jsonElement);
        String path = (String) jsonElement.get("path");
        this.name = name;
        this.access = access;
        this.path = path;
    }

    public JSONObject convertToJsonObject() {
        JSONObject jsonElement = new JSONObject();
        jsonElement.put("path", path);
        jsonElement.put("access", Access.getJsonAccessList(access));
        jsonElement.put("element", name);
        return jsonElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Access> getAccess() {
        return access;
    }

    public void setAccess(Set<Access> access) {
        this.access = access;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
