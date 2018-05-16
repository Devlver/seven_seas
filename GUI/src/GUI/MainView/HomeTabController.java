package GUI.MainView;

import Common.Code;
import Common.Excursion;
import Common.ExcursionListResponse;
import GUI.ExceptionDialog;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.time.LocalDate;

class HomeTabController {
	private final MainViewController main;
	
	HomeTabController(MainViewController controller) {
		main = controller;
	}
	
	/**
	 * Populates the Home tab table
	 */
	@FXML
	void PopulateExcursionList() {
		main.SetLoading(true);
		Task<ExcursionListResponse> task = new Task<ExcursionListResponse>() {
			@Override
			protected ExcursionListResponse call() throws IOException, ClassNotFoundException {
				return ExcursionData.GetExcursions();
			}
		};
		
		task.setOnSucceeded(event ->
		{
			ObservableList<Excursion> excursions = FXCollections.observableArrayList();
			excursions.addAll(task.getValue().getExcursions());
			
			JFXTreeTableColumn<Excursion, String> portIdCol = new JFXTreeTableColumn<>("Port ID");
			portIdCol.setSortable(false);
			
			JFXTreeTableColumn<Excursion, String> nameCol = new JFXTreeTableColumn<>("Excursion name");
			nameCol.setSortable(false);
			
			portIdCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPortId()));
			nameCol.setCellValueFactory(param -> new SimpleStringProperty(WordUtils.capitalizeFully(param.getValue().getValue().getName())));
			
			main.getExcursionTable().setRowFactory(param -> {
				TreeTableRow<Excursion> row = new TreeTableRow<>();
				row.setOnMouseClicked(event1 -> {
					if (!row.isEmpty() && event1.getButton() == MouseButton.PRIMARY && event1.getClickCount() == 2)
						Book();
				});
				return row;
			});
			
			final TreeItem<Excursion> root = new RecursiveTreeItem<>(excursions, RecursiveTreeObject::getChildren);
			
			//noinspection unchecked
			main.getExcursionTable().getColumns().setAll(nameCol, portIdCol);
			
			main.getExcursionTable().setRoot(root);
			
			main.getExcursionTable().sort();
			
			main.SetLoading(false);
		});
		
		task.setOnFailed(event -> {
			new ExceptionDialog(main.getParent(), (Exception) event.getSource().getException()).show();
			main.SetLoading(false);
			main.getErrorLabelHome().setText("Failed to retrieve excursions. Try again");
		});
		
		new Thread(task).start();
	}
	
	/**
	 * Books a selected excursion
	 */
	@FXML
	void Book() {
		main.getErrorLabelHome().setText("");
		
		TreeItem<Excursion> ex = main.getExcursionTable().getSelectionModel().getSelectedItem();
		
		int selectedId;
		
		if (ex == null) {
			main.getErrorLabelHome().setText("Select an excursion first");
		} else {
			selectedId = ex.getValue().getId();
			
			BookingDialog dialog = new BookingDialog(main.getParent(), selectedId);
			dialog.show();
			
			JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
			JFXDialog resultDialog = new JFXDialog(main.getParent(), resultDialogLayout, JFXDialog.DialogTransition.CENTER);
			
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws IOException, ClassNotFoundException {
					return dialog.GetResult();
				}
			};
			
			task.setOnSucceeded(event -> {
				switch (task.getValue()) {
					case BOOKED:
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Your booking has been confirmed"));
						break;
					case BOOKED_WAIT_LIST:
						resultDialogLayout.setHeading(new Text("The number of bookings for this date" +
								" has exceeded a maximum number of bookings"));
						resultDialogLayout.setBody(new Text("You will get a notification by e-mail once there are places available"));
						break;
					case BOOK_REMOVE_PASSENGERS:
						resultDialogLayout.setHeading(new Text("Too many passengers"));
						resultDialogLayout.setBody(new Text("Number of passengers you specified exceeds a number of available places"));
						break;
					case BOOKED_ALREADY:
						resultDialogLayout.setHeading(new Text("Not available"));
						resultDialogLayout.setBody(new Text("You have already booked this excursion for selected date"));
						break;
					case FAIL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
						break;
				}
				
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
				main.SetLoading(false);
			});
			
			task.setOnFailed(event -> {
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
				main.SetLoading(false);
			});
			
			dialog.getConfirmButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				if (dialog.getComboBox().getValue() == null) {
					dialog.getErrorLabel().setText("Please select number of passengers");
				} else if (dialog.getDatePicker().getValue() == null) {
					dialog.getErrorLabel().setText("Please select a date");
				} else if (dialog.getDatePicker().getValue().isBefore(LocalDate.now())) {
					dialog.getErrorLabel().setText("Please select a valid date");
				} else {
					dialog.close();
					main.SetLoading(true);
					new Thread(task).start();
				}
			});
		}
	}
}
