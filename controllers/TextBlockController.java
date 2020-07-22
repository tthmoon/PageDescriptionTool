package descriptiontool.controllers;

import descriptiontool.structure.Access;
import descriptiontool.structure.Page;
import descriptiontool.structure.TextList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class TextBlockController {

    private RootBlockController rootBlock;
    private TextList selectedTextList;

    @FXML
    private ChoiceBox<String> textChoiceBox;

    @FXML
    private Text warning;

    @FXML
    private FlowPane accessCheckBoxes;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private HBox choiceListBlock;
    @FXML
    private VBox descriptionBLock;

    void initialize() {
        initializeListenerForTextChoiceBox();
        RootBlockController.initializeListenerForAccessCheckBoxes(accessCheckBoxes, warning, "Change text");
        initializeTextFieldListener(descriptionArea, warning, "Change text");
    }

    private static void initializeTextFieldListener(TextArea field, Text warning, String buttonName) {
        field.textProperty().addListener((observable, oldval, newval) -> {
            if (!oldval.isEmpty() && !newval.isEmpty() && !newval.equals(oldval)) {
                warning.setText(String.format("Click at \"%s\" button -->", buttonName));
            }
        });
    }

    private void initializeListenerForTextChoiceBox() {
        textChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    if (RootBlockController.isItemInArray(new_val, textChoiceBox)) {
                        clearDescription();
                        selectedTextList = rootBlock.getSelectedPage().getTextList(new_val.intValue());
                        RootBlockController.onOffAccessCheckBoxes(accessCheckBoxes, selectedTextList.getAccess());
                        for (String text : selectedTextList.getTextList()) {
                            descriptionArea.appendText(text + "\n");
                        }
                        onDescription();
                        RootBlockController.clearWarning(warning);
                    } else {
                        clearDescription();
                        RootBlockController.clearWarning(warning);
                    }
                }
        );
    }

    void injectRootBlock(RootBlockController rootBlock) {
        this.rootBlock = rootBlock;
    }

    void clearAllBlock() {
        clearChoiceList();
        clearDescription();
    }

    void offAllBlock() {
        offChoiceList();
        offDescription();
    }

    void onDescription() {
        descriptionBLock.setDisable(false);
    }

    void offDescription() {
        descriptionBLock.setDisable(true);
    }

    void clearDescription() {
        selectedTextList = null;
        RootBlockController.onOffAccessCheckBoxes(accessCheckBoxes, EnumSet.noneOf(Access.class));
        descriptionArea.setText("");
        RootBlockController.clearWarning(warning);
    }

    void onChoiceList() {
        choiceListBlock.setDisable(false);
    }

    void offChoiceList() {
        choiceListBlock.setDisable(true);
    }

    void clearChoiceList() {
        updateChoiceList();
    }

    void updateChoiceList() {
        if (rootBlock.getSelectedPage() != null) {
            List<String> list = rootBlock.getSelectedPage().getItemsForTextChoiceList();
            textChoiceBox.setItems(FXCollections.observableArrayList(list));
        } else {
            textChoiceBox.getItems().clear();
        }
    }

    public void clickAtAddTextListButton() {
        Page selectedPage = rootBlock.getSelectedPage();
        if (selectedPage != null) {
            selectedTextList = new TextList();
            selectedPage.getTextLists().add(selectedTextList);
            updateChoiceList();
            List<String> listsNames = selectedPage.getItemsForTextChoiceList();
            textChoiceBox.setValue(listsNames.get(listsNames.size() - 1));
        }
    }

    public void clickAtDeleteTextListButton() {
        if (selectedTextList != null) {
            rootBlock.getSelectedPage().getTextLists().remove(selectedTextList);
            updateChoiceList();
            clearDescription();
            offDescription();
        }
    }

    public void clickAtChangeTextButton() {
        if (selectedTextList != null) {
            selectedTextList.setAccess(RootBlockController.getAccessFromCheckBoxes(accessCheckBoxes));
            List<String> textList = new ArrayList<>();
            textList.addAll(Arrays.asList(descriptionArea.getText().split("\n")));
            selectedTextList.setTextList(textList);
            warning.setText("");
        }
    }

    public Text getWarning() {
        return warning;
    }
}
