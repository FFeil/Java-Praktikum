package de.feil.view.panel;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class StatePanel extends VBox {

    private final List<HBox> hBoxes;
    private final List<ColorPicker> colorPickers;
    private final ToggleGroup toggleGroup;

    public StatePanel(int numberOfStates) {
        setPadding(new Insets(5, 15, 5, 5));
        setSpacing(18);

        hBoxes = new ArrayList<>();
        colorPickers = new ArrayList<>();
        toggleGroup = new ToggleGroup();

        for (int i = 0; i < numberOfStates; i++) {
            addHBox(i);
        }
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
    }

    public void updatePanel(int numberOfStates) {
        Toggle lastToggle = toggleGroup.getSelectedToggle();
        int start = Math.min(colorPickers.size(), numberOfStates);
        int end = Math.max(colorPickers.size(), numberOfStates);

        for (int i = start; i < end; i++) {
            if (colorPickers.size() > numberOfStates) {
                removeHBox();
            } else {
                addHBox(i);
            }
        }

        if (!toggleGroup.getToggles().contains(lastToggle)) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        }
    }

    public List<ColorPicker> getColorPickers() {
        return colorPickers;
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    private void removeHBox() {
        int last = colorPickers.size() - 1;

        getChildren().remove(hBoxes.get(last));
        hBoxes.remove(last);
        toggleGroup.getToggles().remove(last);
        colorPickers.remove(last);
    }

    private void addHBox(int index) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBoxes.add(hBox);

        RadioButton radioButton = new RadioButton("" + index);
        radioButton.setMinWidth(47);
        radioButton.setMaxWidth(47);
        HBox.setMargin(radioButton, new Insets(3, 0, 0, 0));
        ColorPicker colorPicker = new ColorPicker(new Color(Math.random(), Math.random(), Math.random(), Math.random()));

        radioButton.setToggleGroup(toggleGroup);
        colorPickers.add(colorPicker);

        hBox.getChildren().addAll(radioButton, colorPicker);
        getChildren().add(hBox);
    }
}
