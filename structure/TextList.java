package descriptiontool.structure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TextList {

    private Set<Access> access;
    private List<String> textList;

    public TextList() {
        this.access = EnumSet.noneOf(Access.class);
        this.textList = new ArrayList<>();
    }

    public TextList(EnumSet<Access> access, List<String> textList) {
        this.access = access;
        this.textList = textList;
    }

    public TextList(JSONObject jsonText) {
        List<String> textList = new ArrayList<>();
        for (Object objText : (JSONArray) jsonText.get("text")) {
            textList.add((String) objText);
        }
        this.access = Access.getAccessSet(jsonText);
        this.textList = textList;
    }

    public Set<Access> getAccess() {
        return access;
    }

    public void setAccess(Set<Access> access) {
        this.access = access;
    }

    public List<String> getTextList() {
        return textList;
    }

    public void setTextList(List<String> textList) {
        this.textList = textList;
    }

    public JSONObject convertToJsonObject() {
        JSONObject jsonTextList = new JSONObject();
        jsonTextList.put("text", getJsonTextList());
        jsonTextList.put("access", Access.getJsonAccessList(access));
        return jsonTextList;
    }

    private JSONArray getJsonTextList() {
        JSONArray jsonText = new JSONArray();
        for (String text : textList) {
            jsonText.add(text);
        }
        return jsonText;
    }
}
