package GUI.MainView;

import Common.BookedExcursion;
import Common.BookedExcursionListResponse;
import Common.Code;
import GUI.Network.Account;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

class BookingTabController {
	private final MainViewController main;
	
	BookingTabController(MainViewController controller) {
		main = controller;
	}
	
	/**
	 * Populates the Booking tab table
	 */
	@FXML
	void PopulateBookedList() {
		main.SetLoading(true);
		Task<BookedExcursionListResponse> task = new Task<BookedExcursionListResponse>() {
			@Override
			protected BookedExcursionListResponse call() throws Exception {
				return ExcursionData.GetUserExcursions(Account.getCurrentUser());
			}
		};
		
		task.setOnSucceeded((WorkerStateEvent event) ->
		{
			ObservableList<BookedExcursion> excursions = FXCollections.observableArrayList();
			excursions.addAll(task.getValue().getExcursions());
			
			JFXTreeTableColumn<BookedExcursion, String> nameCol = new JFXTreeTableColumn<>("Name");
			JFXTreeTableColumn<BookedExcursion, String> portCol = new JFXTreeTableColumn<>("Port");
			JFXTreeTableColumn<BookedExcursion, String> passengerCol = new JFXTreeTableColumn<>("Passenger number");
			JFXTreeTableColumn<BookedExcursion, String> dateCol = new JFXTreeTableColumn<>("Date");
			JFXTreeTableColumn<BookedExcursion, String> statusCol = new JFXTreeTableColumn<>("Status");
			
			nameCol.setSortable(false);
			portCol.setSortable(false);
			passengerCol.setSortable(false);
			dateCol.setSortable(false);
			
			nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));
			portCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPort()));
			passengerCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengerNumber()));
			dateCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getDate().toString()));
			statusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getStatus()));
			
			main.getBookedTable().setRowFactory(tv -> {
				TreeTableRow<BookedExcursion> row = new TreeTableRow<>();
				row.setOnMouseClicked(event1 -> {
					if (!row.isEmpty() && event1.getButton() == MouseButton.PRIMARY && event1.getClickCount() == 2) {
						main.EditBooking();
					}
					
				});
				return row;
			});
			
			final TreeItem<BookedExcursion> root = new RecursiveTreeItem<>(excursions, RecursiveTreeObject::getChildren);
			
			//noinspection unchecked
			main.getBookedTable().getColumns().setAll(nameCol, portCol, passengerCol, dateCol, statusCol);
			
			main.getBookedTable().setRoot(root);
			
			main.SetLoading(false);
		});
		
		task.setOnFailed(event ->
		{
			main.SetLoading(false);
			main.getErrorLabelBookings().setText("Failed to update bookings");
		});
		
		task.setOnCancelled(event -> {
			main.SetLoading(false);
			main.getErrorLabelBookings().setText("Failed to update bookings");
		});
		
		new Thread(task).start();
	}
	
	/**
	 * Cancels a booking
	 */
	@FXML
	void CancelBooking() {
		main.getErrorLabelBookings().setText("");
		TreeItem<BookedExcursion> selected = main.getBookedTable().getSelectionModel().getSelectedItem();
		
		if (selected == null) {
			main.getErrorLabelBookings().setText("Select a booking first");
			return;
		}
		
		int selectedBooking = selected.getValue().getId();
		
		
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXDialog dialog = new JFXDialog(main.getParent(), dialogLayout, JFXDialog.DialogTransition.CENTER);
		
		JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
		JFXDialog resultDialog = new JFXDialog(main.getParent(), resultDialogLayout, JFXDialog.DialogTransition.CENTER);
		
		dialogLayout.setHeading(new Text("Cancel booking"));
		dialogLayout.setBody(new Text("Are you sure you want to cancel this booking?"));
		
		JFXButton confirmButton = new JFXButton("Confirm");
		JFXButton cancelButton = new JFXButton("Cancel");
		
		HBox hbox = new HBox();
		hbox.getChildren().setAll(confirmButton, cancelButton);
		
		dialogLayout.setActions(hbox);
		
		
		confirmButton.setOnAction(event -> {
			dialog.close();
			main.SetLoading(true);
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws Exception {
					return ExcursionData.CancelBooking(selectedBooking);
				}
			};
			
			task.setOnSucceeded(event1 -> {
				switch (task.getValue()) {
					case SUCCESS:
						PopulateBookedList();
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Your booking has been cancelled"));
						resultDialog.show();
						main.SetLoading(false);
						break;
					case FAIL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
						resultDialog.show();
						main.SetLoading(false);
						break;
				}
			});
			
			task.setOnFailed(event1 -> {
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.show();
				main.SetLoading(false);
			});
			
			new Thread(task).start();
		});
		
		cancelButton.setOnAction(event -> dialog.close());
		
		dialog.show();
	}
	
	/**
	 * Edits a selected booking
	 */
	@FXML
	void EditBooking() {
		main.getErrorLabelBookings().setText("");
		
		TreeItem<BookedExcursion> selected = main.getBookedTable().getSelectionModel().getSelectedItem();
		
		if (selected == null) {
			main.getErrorLabelBookings().setText("Select a booking first");
		} else {
			BookingEditDialog dialog = new BookingEditDialog(main.getParent(), selected.getValue().getId());
			dialog.show();
			
			JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
			JFXDialog resultDialog = new JFXDialog(main.getParent(), resultDialogLayout, JFXDialog.DialogTransition.CENTER);
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws Exception {
					main.SetLoading(true);
					return dialog.GetResult();
				}
			};
			
			task.setOnSucceeded(event -> {
				switch (task.getValue()) {
					case EDIT_SUCCESS:
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Booking has been edited"));
						break;
					case BOOK_FULL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("There are no spaces left for selected date"));
						break;
					case FAIL:
						resultDialogLayout.setHeading(new Text("Sorry"));
						resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
						break;
				}
				
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
				main.SetLoading(false);
				PopulateBookedList();
			});
			
			task.setOnFailed(event -> {
				main.SetLoading(false);
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
			});
			
			dialog.getConfirmButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				main.SetLoading(true);
				new Thread(task).start();
			});
		}
	}
}
