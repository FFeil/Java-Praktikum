package de.feil.view.panel;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class StatePanel extends VBox {

    private final List<ColorPicker> colorPickers;
    private final ToggleGroup toggleGroup;

    public StatePanel(int numberOfStates) {
        setPadding(new Insets(5, 15, 5 ,5));
        setSpacing(18);

        colorPickers = new ArrayList<>();
        toggleGroup = new ToggleGroup();

        for (int i = 0; i < numberOfStates; i++) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);

            RadioButton radioButton = new RadioButton("" + i);
            radioButton.setMinWidth(47);
            radioButton.setMaxWidth(47);
            HBox.setMargin(radioButton, new Insets(3, 0, 0, 0));
            ColorPicker colorPicker = new ColorPicker(new Color(Math.random(), Math.random(), Math.random(), Math.random()));

            hBox.getChildren().addAll(radioButton, colorPicker);
            getChildren().add(hBox);

            radioButton.setToggleGroup(toggleGroup);
            colorPickers.add(colorPicker);
        }
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
    }

    public List<ColorPicker> getColorPickers() {
        return colorPickers;
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }
}
