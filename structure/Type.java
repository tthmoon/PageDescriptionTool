package descriptiontool.structure;


public enum Type {
    NUMBER("number"),
    TEXT("text"),
    CHKBOX("checkbox"),
    RADIO("radio"),
    FILE("file"),
    BUTTON("fbutton"),
    IGNORE("ignore"),
    UNDEF("");

    private String type;

    Type(String type) {
        this.type = type;
    }

    public static Type getType(String type) {
        switch (type) {
            case "number":
                return NUMBER;
            case "text":
                return TEXT;
            case "checkbox":
                return CHKBOX;
            case "radio":
                return RADIO;
            case "fbutton":
                return BUTTON;
            case "file":
                return FILE;
            case "ignore":
                return IGNORE;
            default:
                System.out.println("Undefined element type: " + type);
                return UNDEF;
        }
    }

    public String getTypeName() {
        return type;
    }
}
