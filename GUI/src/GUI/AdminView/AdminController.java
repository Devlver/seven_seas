package GUI.AdminView;

import Common.AdminBooking;
import Common.Code;
import GUI.ExceptionDialog;
import GUI.MainView.BookingEditDialog;
import GUI.Network.Account;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
	
	@FXML
	private StackPane root;
	
	@FXML
	private JFXTreeTableView<AdminBooking> bookingTable;
	
	@FXML
	private JFXSpinner progressIndicator;
	
	@FXML
	private Label errorLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookingTable.setPlaceholder(new Text("No bookings yet"));
		PopulateBookingsTable();
	}
	
	@FXML
	private void PopulateBookingsTable() {
		SetLoading(true);
		errorLabel.setText("");
		
		Task<ArrayList<AdminBooking>> task = new Task<ArrayList<AdminBooking>>() {
			@Override
			protected ArrayList<AdminBooking> call() throws Exception {
				return ExcursionData.GetAdminBookings(Account.getCurrentUser());
			}
		};
		
		task.setOnSucceeded(event -> {
			if (task.getValue() == null || task.getValue().isEmpty()) {
				SetLoading(false);
				return;
			}
			
			JFXTreeTableColumn<AdminBooking, String> nameColumn = new JFXTreeTableColumn<>("Passenger name");
			JFXTreeTableColumn<AdminBooking, String> dateColumn = new JFXTreeTableColumn<>("Date");
			JFXTreeTableColumn<AdminBooking, String> passengersColumn = new JFXTreeTableColumn<>("Number of passengers");
			JFXTreeTableColumn<AdminBooking, String> statusColumn = new JFXTreeTableColumn<>("Status");
			
			nameColumn.setSortable(false);
			dateColumn.setSortable(false);
			passengersColumn.setSortable(false);
			statusColumn.setSortable(false);
			
			ObservableList<AdminBooking> bookings = FXCollections.observableArrayList();
			bookings.addAll(task.getValue());
			
			nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengerName()));
			dateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getBookingDate()));
			passengersColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengersNumber()));
			statusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getStatus()));
			
			bookingTable.setRowFactory(param -> {
				TreeTableRow<AdminBooking> row = new TreeTableRow<>();
				row.setOnMouseClicked(event1 -> {
					if (!row.isEmpty() && event1.getButton() == MouseButton.PRIMARY && event1.getClickCount() == 2) {
						Edit();
					}
				});
				return row;
			});
			
			final TreeItem<AdminBooking> root = new RecursiveTreeItem<>(bookings, RecursiveTreeObject::getChildren);
			
			//noinspection unchecked
			bookingTable.getColumns().setAll(nameColumn, dateColumn, passengersColumn, statusColumn);
			
			bookingTable.setRoot(root);
			
			bookingTable.sort();
			
			SetLoading(false);
		});
		
		task.setOnFailed(event -> {
			new ExceptionDialog(root, (Exception) event.getSource().getException()).show();
			SetLoading(false);
			errorLabel.setText("Failed to retrieve bookings. Try again later");
		});
		
		new Thread(task).start();
	}
	
	@FXML
	private void Edit() {
		errorLabel.setText("");
		
		AdminBooking selected = bookingTable.getSelectionModel().getSelectedItem().getValue();
		
		if (selected == null) {
			errorLabel.setText("Select an excursion first");
		} else {
			BookingEditDialog dialog = new BookingEditDialog(root, selected.getBookingId());
			dialog.SetHeader("Edit booking");
			dialog.show();
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws Exception {
					SetLoading(true);
					return dialog.GetResult();
				}
			};
			
			JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
			JFXDialog resultDialog = new JFXDialog(root, resultDialogLayout, JFXDialog.DialogTransition.CENTER);
			
			task.setOnSucceeded(event -> {
				switch (task.getValue()) {
					case EDIT_SUCCESS:
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Booking number " + selected.getBookingId() + " has been edited"));
						break;
					case BOOK_FULL:
						resultDialogLayout.setHeading(new Text("Failed to edit booking number " + selected.getBookingId()));
						resultDialogLayout.setBody(new Text("There are no spaces left for selected date"));
						break;
					case FAIL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
						break;
				}
				
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
				SetLoading(false);
				PopulateBookingsTable();
			});
			
			task.setOnFailed(event -> {
				SetLoading(false);
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable. Try again later"));
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
			});
			
			dialog.getConfirmButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				SetLoading(true);
				dialog.close();
				new Thread(task).start();
			});
			
		}
	}
	
	@FXML
	private void Cancel() {
		errorLabel.setText("");
		TreeItem<AdminBooking> selected = bookingTable.getSelectionModel().getSelectedItem();
		
		if (selected == null) {
			errorLabel.setText("Select a booking first");
			return;
		}
		
		int selectedBooking = selected.getValue().getBookingId();
		
		
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.CENTER);
		
		JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
		JFXDialog resultDialog = new JFXDialog(root, resultDialogLayout, JFXDialog.DialogTransition.CENTER);
		
		dialogLayout.setHeading(new Text("Cancel booking"));
		dialogLayout.setBody(new Text("Are you sure you want to cancel this booking?"));
		
		JFXButton confirmButton = new JFXButton("Confirm");
		JFXButton cancelButton = new JFXButton("Cancel");
		
		HBox hbox = new HBox();
		hbox.getChildren().setAll(confirmButton, cancelButton);
		
		dialogLayout.setActions(hbox);
		
		
		confirmButton.setOnAction(event -> {
			dialog.close();
			SetLoading(true);
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws Exception {
					return ExcursionData.CancelBooking(selectedBooking);
				}
			};
			
			task.setOnSucceeded(event1 -> {
				switch (task.getValue()) {
					case SUCCESS:
						PopulateBookingsTable();
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Booking number " + selected.getValue().getBookingId() + " has been cancelled"));
						resultDialog.show();
						SetLoading(false);
						break;
					case FAIL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
						resultDialog.show();
						SetLoading(false);
						break;
				}
			});
			
			task.setOnFailed(event1 -> {
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable. Try again later"));
				resultDialog.show();
				SetLoading(false);
			});
			
			new Thread(task).start();
		});
		
		cancelButton.setOnAction(event -> dialog.close());
		
		dialog.show();
	}
	
	private void SetLoading(boolean x) {
		root.setDisable(x);
		progressIndicator.setVisible(x);
	}
}
