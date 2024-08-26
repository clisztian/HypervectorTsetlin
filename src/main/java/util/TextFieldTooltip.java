package util;

import de.gsi.chart.plugins.DataPointTooltip;
import javafx.scene.control.TextField;

public class TextFieldTooltip extends DataPointTooltip {

    private final TextField textField;

    public TextFieldTooltip(TextField textField) {
        super();
        this.textField = textField;
    }

    protected String formatLabel(DataPoint dataPoint) {
        textField.setText(dataPoint.label);
        return "";
    }

    public TextField getTextField() {
        return textField;
    }
}
