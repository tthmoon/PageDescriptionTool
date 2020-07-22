package descriptiontool.structure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class Page {

    private String name;
    private Set<Access> access;
    private Map<Type, Map<String, Element>> elementsTypes;
    private List<TextList> textLists;

    public Page(String name) {
        this.name = name;
        this.access = EnumSet.noneOf(Access.class);
        this.elementsTypes = new EnumMap<>(Type.class);
        this.textLists = new ArrayList<>();
    }

    public Page(String name, Set<Access> access, Map<Type, Map<String, Element>> elementsTypes, List<TextList> textLists) {
        this.name = name;
        this.access = access;
        this.elementsTypes = elementsTypes;
        this.textLists = textLists;
    }

    public Page(JSONObject jsonPage) {
        Set<Access> pageAccess = EnumSet.noneOf(Access.class);
        Map<Type, Map<String, Element>> elementsTypes = new EnumMap<>(Type.class);
        if (jsonPage.containsKey("elements_types")) {
            for (Object objElementsType : (JSONArray) jsonPage.get("elements_types")) {
                JSONObject jsonElementsType = (JSONObject) objElementsType;
                Type type = Type.getType((String) jsonElementsType.get("type"));
                Map<String, Element> elements = getElementsList(jsonElementsType, pageAccess);
                elementsTypes.put(type, elements);
            }
        }
        List<TextList> textLists = new ArrayList<>();
        if (jsonPage.containsKey("texts")) {
            for (Object objText : (JSONArray) jsonPage.get("texts")) {
                TextList textList = new TextList((JSONObject) objText);
                pageAccess.addAll(textList.getAccess());
                textLists.add(textList);
            }
        }
        if (elementsTypes.isEmpty() && textLists.isEmpty()) {
            pageAccess = Access.getAccessSet(jsonPage);
        }
        this.name = (String) jsonPage.get("page");
        this.access = pageAccess;
        this.elementsTypes = elementsTypes;
        this.textLists = textLists;
    }

    public JSONObject convertToJsonObject() {
        JSONObject jsonPage = new JSONObject();
        jsonPage.put("texts", getJsonTextsLists());
        jsonPage.put("elements_types", getJsonElementsTypes());
        jsonPage.put("access", Access.getJsonAccessList(access));
        jsonPage.put("page", name);
        return jsonPage;
    }

    private JSONArray getJsonElementsTypes() {
        JSONArray jsonElementsTypes = new JSONArray();
        for (Type type : elementsTypes.keySet()) {
            JSONObject jsonType = new JSONObject();
            jsonType.put("elements", getJsonElementsForType(type));
            jsonType.put("type", type.getTypeName());
            jsonElementsTypes.add(jsonType);
        }
        return jsonElementsTypes;
    }

    private JSONArray getJsonElementsForType(Type type) {
        JSONArray jsonElements = new JSONArray();
        Map<String, Element> elements = elementsTypes.get(type);
        for (String elementName : elements.keySet()) {
            jsonElements.add(elements.get(elementName).convertToJsonObject());
        }
        return jsonElements;
    }

    private JSONArray getJsonTextsLists() {
        JSONArray jsonTextsLists = new JSONArray();
        for (TextList texts : textLists) {
            jsonTextsLists.add(texts.convertToJsonObject());
        }
        return jsonTextsLists;
    }

    public void setName(String name) {
        if (!name.isEmpty()) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public Set<Access> getAccess() {
        return access;
    }

    public void setAccess(Set<Access> access) {
        this.access = access;
    }

    public Map<String, Element> getElementsType(Type type) {
        if (elementsTypes.containsKey(type)) {
            return elementsTypes.get(type);
        }
        return new HashMap<>();
    }

    public Element addNewElement(Type type, Element newElement) {
        Map<String, Element> elements = getElementsType(type);
        elements.put(newElement.getName(), newElement);
        elementsTypes.put(type, elements);
        return newElement;
    }

    public List<String> getItemsForTextChoiceList() {
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= textLists.size(); i++) {
            items.add("List " + i);
        }
        return items;
    }

    public List<TextList> getTextLists() {
        return textLists;
    }

    public TextList getTextList(int index) {
        return textLists.get(index);
    }

    private Map<String, Element> getElementsList(JSONObject jsonElementsTypes, Set<Access> pageAccess) {
        Map<String, Element> elements = new LinkedHashMap<>();
        for (Object objElement : (JSONArray) jsonElementsTypes.get("elements")) {
            JSONObject jsonElement = (JSONObject) objElement;
            String element = (String) jsonElement.get("element");
            Element el = new Element(jsonElement);
            pageAccess.addAll(el.getAccess());
            elements.put(element, el);
        }
        return elements;
    }
}
