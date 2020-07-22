package descriptiontool.structure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum Access {

    ROOT("Root"),
    ADMIN("Администратор"),
    TADMIN("Тех. Администратор"),
    USER("Пользователь"),
    DBUSER("Оператор баз розыска"),
    SUPPORT("Тех. поддержка"),
    TESTER("Гос. поверитель"),
    UNDEF("");

    private String access;

    Access(String access) {
        this.access = access;
    }

    public static Set<Access> getAccessSet(JSONObject jsonObject) {
        List<Access> access = new ArrayList<>();
        if (jsonObject.containsKey("access") && !((JSONArray) jsonObject.get("access")).isEmpty()) {
            JSONArray jsonAccess = (JSONArray) jsonObject.get("access");
            for (Object objAccess : jsonAccess) {
                access.add(Access.getAccessLevel((String) objAccess));
            }
        } else {
            access.add(ROOT);
            access.add(ADMIN);
            access.add(TADMIN);
            access.add(USER);
            access.add(DBUSER);
            access.add(SUPPORT);
        }
        return EnumSet.copyOf(access);
    }

    public String getAccessName() {
        return access;
    }

    public static Access getAccessLevel(String access) {
        switch (access) {
            case "Root":
                return ROOT;
            case "Администратор":
            case "Admin":
                return ADMIN;
            case "Тех. поддержка":
            case "Support":
                return SUPPORT;
            case "Оператор баз розыска":
            case "Dbuser":
                return DBUSER;
            case "Тех. Администратор":
            case "Tadmin":
                return TADMIN;
            case "Пользователь":
            case "User":
                return USER;
            case "Гос. поверитель":
            case "Tester":
                return TESTER;
            default:
                System.out.println("Undefined access level: " + access);
                return UNDEF;
        }
    }

    public static JSONArray getJsonAccessList(Set<Access> accesses) {
        JSONArray jsonAccess = new JSONArray();
        for (Access access : accesses) {
            jsonAccess.add(access.getAccessName());
        }
        return jsonAccess;
    }
}
