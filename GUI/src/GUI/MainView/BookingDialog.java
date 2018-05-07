package GUI.MainView;

import Common.Code;
import GUI.Network.Account;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;

class BookingDialog extends JFXDialog {
	final int selected;
	
	JFXDatePicker datePicker;
	JFXComboBox<Integer> comboBox;
	String header;
	
	private JFXButton confirmButton;
	private JFXButton cancelButton;
	private Label errorLabel;
	
	BookingDialog(StackPane parent, int selectedId) {
		selected = selectedId;
		setDialogContainer(parent);
		setTransitionType(DialogTransition.CENTER);
		setContent(GetLayout());
		SetActions();
	}
	
	private JFXDialogLayout GetLayout() {
		header = "Confirm your booking";
		JFXDialogLayout dialogBoxLayout = new JFXDialogLayout();
		dialogBoxLayout.setHeading(new Text(header));
		
		comboBox = new JFXComboBox<>();
		comboBox.getItems().setAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		comboBox.setPromptText("Select number of passengers");
		
		datePicker = new JFXDatePicker();
		datePicker.setPromptText("Select date");
		datePicker.setDefaultColor(Paint.valueOf("BLUEVIOLET"));
		
		errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red");
		
		VBox dialogVBox = new VBox();
		dialogVBox.setSpacing(20);
		dialogVBox.getChildren().setAll(comboBox, datePicker, errorLabel);
		
		dialogBoxLayout.setBody(dialogVBox);
		
		confirmButton = new JFXButton("Confirm");
		cancelButton = new JFXButton("Cancel");
		
		dialogBoxLayout.setActions(confirmButton, cancelButton);
		
		return dialogBoxLayout;
	}
	
	private void SetActions() {
		
		cancelButton.setOnAction(event -> close());
		
		confirmButton.setOnAction(event -> {
			if (comboBox.getValue() == null) {
				errorLabel.setText("Please select number of passengers");
			} else if (datePicker.getValue() == null) {
				errorLabel.setText("Please select a date");
			} else if (datePicker.getValue().isBefore(LocalDate.now())) {
				errorLabel.setText("Please select a valid date");
			} else {
				close();
			}
		});
	}
	
	public Code GetResult() throws IOException, ClassNotFoundException {
		return ExcursionData.BookExcursion(Account.getCurrentUser(), selected, datePicker.getValue(),
				comboBox.getValue());
	}

	public JFXButton getConfirmButton() {
		return confirmButton;
	}
}
