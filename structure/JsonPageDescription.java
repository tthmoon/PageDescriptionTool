package descriptiontool.structure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class JsonPageDescription {

    private JSONObject description;

    private JsonPageDescription() {}

    public static JsonPageDescription createJsonPageDescription (String deviceType, Map<String, Page> pages) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceType", deviceType);
        jsonObject.put("pages", getJsonPages(pages));
        JsonPageDescription jsonPageDescription = new JsonPageDescription();
        jsonPageDescription.description = jsonObject;
        return jsonPageDescription;
    }

    private static JSONArray getJsonPages(Map<String, Page> pages) {
        JSONArray jsonPages = new JSONArray();
        if (pages != null) {
            for (String pageName : pages.keySet()) {
                Page page = pages.get(pageName);
                jsonPages.add(page.convertToJsonObject());
            }
        }
        return jsonPages;
    }

    public JSONObject getDescription() {
        return description;
    }
}
