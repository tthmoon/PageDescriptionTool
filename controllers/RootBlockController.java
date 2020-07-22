package descriptiontool.controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import descriptiontool.structure.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RootBlockController {

    private File filePageDescription;
    private JSONObject jsonPageDescription;

    private Map<String, Page> pages;
    private Page selectedPage;

    private boolean changing;

    @FXML
    private TextBlockController textBlockController;
    @FXML
    private ElementBlockController elementBlockController;

    @FXML
    private Text jsonFilePath;
    @FXML
    private Text warning;
    @FXML
    private Text saveStatus;

    @FXML
    private TextField deviceType;
    @FXML
    private TextField pageNameTextField;

    @FXML
    private ChoiceBox<String> pageList;

    @FXML
    private FlowPane accessCheckBoxes;

    @FXML
    private HBox pageAccessBlock;

    @FXML
    private void initialize() {
        changing = false;
        elementBlockController.injectRootBlock(this);
        elementBlockController.initialize();
        textBlockController.injectRootBlock(this);
        textBlockController.initialize();
        initializeListenerForPageList();
        initializeTextFieldListener(pageNameTextField, warning, "Change page settings");
        initializeListenerForAccessCheckBoxes(accessCheckBoxes, warning, "Change page settings");
    }

    static void initializeListenerForAccessCheckBoxes(FlowPane accessCheckBoxes, Text warning, String buttonName) {
        for (Node node : accessCheckBoxes.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != oldValue) {
                    warning.setText(String.format("Click at \"%s\" button -->", buttonName));
                }
            });
        }
    }

    static void initializeTextFieldListener(TextField field, Text warning, String buttonName) {
        field.textProperty().addListener((observable, oldval, newval) -> {
            if (!oldval.isEmpty() && !newval.isEmpty() && !newval.equals(oldval)) {
                warning.setText(String.format("Click at \"%s\" button -->", buttonName));
            }
        });
    }

    private void initializeListenerForPageList() {
        pageList.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue <? extends Number> ov, Number old_val, Number new_val) -> {
            if (isItemInArray(new_val, pageList)) {
                String page = pageList.getItems().get(new_val.intValue());
                pageNameTextField.setText(page);
                selectedPage = pages.get(page);
                onOffAccessCheckBoxes(accessCheckBoxes, selectedPage.getAccess());
                clearWarning(warning);
                pageAccessBlock.setDisable(false);
                elementBlockController.updateCountersOfElements();
                if (!changing) {
                    elementBlockController.clearAllBlock();
                    elementBlockController.offAllBlock();
                    elementBlockController.onTypeBlock();
                    clearWarning(elementBlockController.getWarning());

                    textBlockController.clearAllBlock();
                    textBlockController.offAllBlock();
                    textBlockController.updateChoiceList();
                    textBlockController.onChoiceList();
                    clearWarning(textBlockController.getWarning());
                }
            } else if (!changing) {
                pageNameTextField.setText("");
                onOffAccessCheckBoxes(accessCheckBoxes, EnumSet.noneOf(Access.class));
                clearWarning(warning);
                pageAccessBlock.setDisable(true);

                elementBlockController.clearAllBlock();
                elementBlockController.offAllBlock();
                clearWarning(elementBlockController.getWarning());

                textBlockController.clearAllBlock();
                textBlockController.offAllBlock();
                clearWarning(textBlockController.getWarning());
            }
            saveStatus.setText("");
        });
    }

    static void clearWarning(Text warning) {
        warning.setText("");
    }

    static boolean isItemInArray(Number item, ChoiceBox box) {
        return item.intValue() < box.getItems().size() && item.intValue() >= 0;
    }

    static void onOffAccessCheckBoxes(FlowPane checkBoxes, Set<Access> access) {
        for (Node node : checkBoxes.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            String id = node.getId();
            if (id.contains("Root")) {
                checkBox.setSelected(access.contains(Access.ROOT));
                continue;
            }
            if (id.contains("Admin")) {
                checkBox.setSelected(access.contains(Access.ADMIN));
                continue;
            }
            if (id.contains("Tadmin")) {
                checkBox.setSelected(access.contains(Access.TADMIN));
                continue;
            }
            if (id.contains("User")) {
                checkBox.setSelected(access.contains(Access.USER));
                continue;
            }
            if (id.contains("Dbuser")) {
                checkBox.setSelected(access.contains(Access.DBUSER));
                continue;
            }
            if (id.contains("Support")) {
                checkBox.setSelected(access.contains(Access.SUPPORT));
                continue;
            }
            if (id.contains("Tester")) {
                checkBox.setSelected(access.contains(Access.TESTER));
            }
        }
    }

    static Set<Access> getAccessFromCheckBoxes(FlowPane checkBoxes) {
        Set<Access> access = EnumSet.noneOf(Access.class);
        for (Node node : checkBoxes.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            if (checkBox.isSelected()) {
                access.add(Access.getAccessLevel(node.getId()));
            }
        }
        return access;
    }

    Page getSelectedPage() {
        return selectedPage;
    }

    private void updatePageChoiceList() {
        saveStatus.setText("");
        pageList.getItems().removeAll();
        pageList.setItems(FXCollections.observableArrayList(pages.keySet()));
    }

    private File selectFileInFileChooser() {
        FileChooser chooser = new FileChooser();
        if (!jsonFilePath.getText().isEmpty()) {
            chooser.setInitialDirectory(new File(jsonFilePath.getText()).getParentFile());
        }
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            jsonFilePath.setText(selectedFile.getAbsolutePath());
        } else {
            return null;
        }
        try {
            jsonPageDescription = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(selectedFile),"UTF-8"));
        } catch (ParseException e) {
            jsonFilePath.setText("error in file format");
            return null;
        } catch (FileNotFoundException e) {
            jsonFilePath.setText("file not found");
            return null;
        } catch (IOException e) {
            jsonFilePath.setText("file read error");
            return null;
        }
        return selectedFile;
    }

    public int getAmountOfElement(Type type) {
        return selectedPage.getElementsType(type).keySet().size();
    }

    public void clickAtSelectFileButton() {
        filePageDescription = selectFileInFileChooser();
        if (filePageDescription == null) {
            return;
        }
        deviceType.setText((String) jsonPageDescription.get("deviceType"));
        JSONArray jsonPages = (JSONArray) jsonPageDescription.get("pages");
        pages = new LinkedHashMap<>();
        if (jsonPages != null) {
            for (Object objPage : jsonPages) {
                JSONObject jsonPage = (JSONObject) objPage;
                String pageName = (String) jsonPage.get("page");
                pages.put(pageName, new Page(jsonPage));
            }
        }
        updatePageChoiceList();
    }

    public void clickAtChangePageSettingsButton() {
        if (selectedPage != null) {
            changing = true;
            String oldName = selectedPage.getName();
            selectedPage.setName(pageNameTextField.getText());
            selectedPage.setAccess(getAccessFromCheckBoxes(accessCheckBoxes));
            pages.remove(oldName);
            pages.put(selectedPage.getName(), selectedPage);
            updatePageChoiceList();
            pageList.setValue(selectedPage.getName());
            changing = false;
            warning.setText("");
        }
    }

    public void clickAtAddNewPageButton() {
        if (pages == null) {
            pages = new LinkedHashMap<>();
        }
        Set<String> pagesNames = pages.keySet();
        String newPageName = "New page";
        int number = 1;
        while (pagesNames.contains(newPageName + number)) {
            number++;
        }
        Page newPage = new Page(newPageName + number);
        pages.put(newPage.getName(), newPage);
        pageNameTextField.setText(newPage.getName());
        updatePageChoiceList();
        pageList.setValue(newPage.getName());
    }

    public void clickAtDeletePageButton() {
        if (selectedPage != null) {
            pages.remove(selectedPage.getName());
            updatePageChoiceList();

            elementBlockController.clearAllBlock();
            elementBlockController.offAllBlock();

            textBlockController.clearDescription();
        }
    }

    public void clickAtSaveFileButton() {
        JsonPageDescription jsonPageDescription = JsonPageDescription.createJsonPageDescription(deviceType.getText(), pages);
        if (filePageDescription != null) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePageDescription), "UTF-8")) {
//            try (FileWriter writer = new FileWriter(filePageDescription)) {
                writer.write(jsonPageDescription.getDescription().toJSONString());
                saveStatus.setText("saved");
            } catch (IOException e) {
                saveStatus.setText("file not saved");
            }
        } else {
            clickAtSaveAsButton();
        }
    }

    public void clickAtSaveAsButton() {
        JsonPageDescription jsonPageDescription = JsonPageDescription.createJsonPageDescription(deviceType.getText(), pages);
        FileChooser chooser = new FileChooser();
        if (filePageDescription != null) {
            chooser.setInitialDirectory(filePageDescription);
        }
        File fileToSave = chooser.showSaveDialog(null);
        if (fileToSave != null && fileToSave.getPath().contains(".json")) {
            filePageDescription = fileToSave;
            jsonFilePath.setText(filePageDescription.getAbsolutePath());
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePageDescription), "UTF-8")) {
//            try (FileWriter writer = new FileWriter(filePageDescription)) {
                writer.write(jsonPageDescription.getDescription().toJSONString());
                saveStatus.setText("saved");
            } catch (IOException e) {
                saveStatus.setText("file not saved");
            }
        } else {
            saveStatus.setText("wrong file extension (need .json)");
        }
    }
}
