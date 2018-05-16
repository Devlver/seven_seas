package GUI.MainView;

import Common.Code;
import GUI.Network.ExcursionData;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class BookingEditDialog extends BookingDialog {
	public BookingEditDialog(StackPane parent, int selectedId) {
		super(parent, selectedId);
	}
	
	@Override
	public Code GetResult() throws IOException, ClassNotFoundException {
		return ExcursionData.EditBooking(selected, this.getComboBox().getValue(), this.getDatePicker().getValue());
	}
}
