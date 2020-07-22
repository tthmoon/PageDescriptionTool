package descriptiontool.controllers;

import descriptiontool.structure.Access;
import descriptiontool.structure.Element;
import descriptiontool.structure.Type;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ElementBlockController {

    private RootBlockController rootBlock;
    private Element selectedElement;
    private Type selectedType;

    @FXML
    private TextField nameField;
    @FXML
    private TextField pathField;

    @FXML
    private ChoiceBox<String> elementList;

    @FXML
    private Text warning;

    @FXML
    private FlowPane accessCheckBoxes;

    @FXML
    private ToggleGroup typeGroup;

    @FXML
    private HBox typeBlock;
    @FXML
    private HBox choiceListBlock;
    @FXML
    private VBox descriptionBlock;

    void initialize() {
        initializeListenerForElementList();
        initializeListenerForTypeRadioButtons();
        RootBlockController.initializeTextFieldListener(nameField, warning, "Change element");
        RootBlockController.initializeTextFieldListener(pathField, warning, "Change element");
        RootBlockController.initializeListenerForAccessCheckBoxes(accessCheckBoxes, warning, "Change element");
    }

    private void initializeListenerForElementList() {
        elementList.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    if (RootBlockController.isItemInArray(new_val, elementList)) {
                        String element = elementList.getItems().get(new_val.intValue());
                        selectedElement = rootBlock.getSelectedPage().getElementsType(selectedType).get(element);
                        onDescription();
                        RootBlockController.onOffAccessCheckBoxes(accessCheckBoxes, selectedElement.getAccess());
                        nameField.setText(element);
                        pathField.setText(selectedElement.getPath());
                        RootBlockController.clearWarning(warning);
                    }
                }
        );
    }

    private void initializeListenerForTypeRadioButtons() {
        typeGroup.selectedToggleProperty().addListener(event -> {
            if (typeGroup.getSelectedToggle() != null) {
                offChoiceBlock();
                selectedType = Type.getType(((RadioButton) typeGroup.getSelectedToggle()).getId());
                updateElementList();
                onChoiceBlock();
                clearDescription();
                offDescription();
            }
        });
    }

    private void updateElementList() {
        Set<String> elements = rootBlock.getSelectedPage().getElementsType(selectedType).keySet();
        if (!elements.isEmpty()) {
            elementList.setItems(FXCollections.observableArrayList(elements));
        } else {
            elementList.getItems().clear();
        }
        this.updateCountersOfElements();
    }


    private void calculateAmountOfElement(RadioButton radioButton) {
        Type type = Type.getType(radioButton.getId());
        for (Node node : radioButton.getParent().getChildrenUnmodifiable()) {
            if (node.getTypeSelector().equals("Text")) {
                ((Text)node).setText(String.valueOf(rootBlock.getAmountOfElement(type)));
            }
        }
    }

    void clearAllBlock() {
        clearTypeBlock();
        clearChoiceBlock();
        clearDescription();
    }

    void offAllBlock() {
        offTypeBlock();
        offChoiceBlock();
        offDescription();
    }

    void clearDescription() {
        selectedElement = null;
        RootBlockController.onOffAccessCheckBoxes(accessCheckBoxes, EnumSet.noneOf(Access.class));
        nameField.setText("");
        pathField.setText("");
        RootBlockController.clearWarning(warning);
    }

    void injectRootBlock(RootBlockController rootBlock) {
        this.rootBlock = rootBlock;
    }

    void onChoiceBlock() {
        choiceListBlock.setDisable(false);
    }

    void offChoiceBlock() {
        choiceListBlock.setDisable(true);
    }

    void clearChoiceBlock() {
        updateElementList();
    }

    void onTypeBlock() {
        typeBlock.setDisable(false);
    }

    void offTypeBlock() {
        typeBlock.setDisable(true);
    }

    void clearTypeBlock() {
        selectedType = null;
        if (typeGroup.getSelectedToggle() != null) {
            typeGroup.getSelectedToggle().setSelected(false);
        }
    }

    void onDescription() {
        descriptionBlock.setDisable(false);
    }

    void offDescription() {
        descriptionBlock.setDisable(true);
    }

    public void clickAtAddElementButton() {
        if (selectedType != null) {
            String newElementName = "New element";
            int number = 1;
            Set<String> elements = rootBlock.getSelectedPage().getElementsType(selectedType).keySet();
            while (elements.contains(newElementName + number)) {
                number++;
            }
            Element newElement = rootBlock.getSelectedPage().addNewElement(selectedType, new Element(newElementName + number));
            updateElementList();
            elementList.setValue(newElement.getName());
        }
    }

    public void clickAtDeleteElementButton() {
        if (selectedType != null && selectedElement != null) {
            rootBlock.getSelectedPage().getElementsType(selectedType).remove(selectedElement.getName());
            updateElementList();
            clearDescription();
            offDescription();
        }
    }

    public void clickAtChangeElementButton() {
        if (selectedElement != null) {
            String oldName = selectedElement.getName();
            selectedElement.setName(nameField.getText());
            selectedElement.setAccess(RootBlockController.getAccessFromCheckBoxes(accessCheckBoxes));
            selectedElement.setPath(pathField.getText());
            Map<String, Element> elements = rootBlock.getSelectedPage().getElementsType(selectedType);
            elements.remove(oldName);
            elements.put(selectedElement.getName(), selectedElement);
            updateElementList();
            elementList.setValue(selectedElement.getName());
            warning.setText("");
        }
    }

    public void clickAtCopyElementButton() {
        if (selectedElement != null) {
            Element element = new Element(selectedElement);
            int number = 1;
            final String str = "_copy";
            Set<String> elementsList = rootBlock.getSelectedPage().getElementsType(selectedType).keySet();
            while (elementsList.contains(element.getName())) {
                if (!element.getName().contains(str)) {
                    element.setName(element.getName() + str);
                }
                int endIdx = element.getName().indexOf(str) + str.length();
                String name = element.getName().substring(0, endIdx);
                element.setName(name + number);
                number++;
            }
            rootBlock.getSelectedPage().addNewElement(selectedType, element);
            updateElementList();
            elementList.setValue(element.getName());
        }
    }

    public Text getWarning() {
        return warning;
    }

    public void updateCountersOfElements() {
        for (Toggle toggle : typeGroup.getToggles()) {
            this.calculateAmountOfElement((RadioButton)toggle);
        }
    }
}
