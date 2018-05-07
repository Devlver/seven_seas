package GUI.MainView;

import Common.*;
import GUI.ExceptionDialog;
import GUI.Network.Account;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

public class MainViewController implements Initializable {
	
	@FXML
	private JFXTextField searchField;
	
	@FXML
	private JFXButton bookButton;
	
	@FXML
	private JFXHamburger hamburger;
	
	@FXML
	private JFXDrawer drawer;
	
	@FXML
	private VBox drawerSidePane;
	
	@FXML
	private AnchorPane rootAnchor;
	
	@FXML
	private StackPane parent;
	
	@FXML
	private JFXSpinner progressIndicator;
	
	@FXML
	private Label errorLabelHome;
	
	@FXML
	private Label errorLabelBookings;
	
	@FXML
	private JFXTreeTableView<Excursion> excursionTable;
	
	@FXML
	private JFXTreeTableView<BookedExcursion> bookedTable;
	
	/**
	 * Called when a main scene is created
	 *
	 * @param location  /
	 * @param resources /
	 */
	public void initialize(URL location, ResourceBundle resources) {
		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.millis(800));
		
		fade.setNode(parent);
		
		fade.setFromValue(parent.getOpacity());
		fade.setToValue(1.0);
		
		fade.setOnFinished((ActionEvent event) -> {
			drawer.setSidePane(drawerSidePane);
			
			HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(hamburger);
			
			transition.setRate(-1);
			
			hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				transition.setRate(transition.getRate() * -1);
				transition.play();
				
				if (drawer.isOpened())
					drawer.close();
				else
					drawer.open();
			});
		});
		
		fade.play();
		excursionTable.setPlaceholder(new Text("No excursions available at the moment"));
		bookedTable.setPlaceholder(new Text("You have no bookings"));
		PopulateExcursionList();
		PopulateBookedList();
	}
	
	/**
	 * Populates the Home tab table
	 */
	@FXML
	private void PopulateExcursionList() {
		SetLoading(true);
		bookButton.setDisable(true);
		Task<ExcursionListResponse> task = new Task<ExcursionListResponse>() {
			@Override
			protected ExcursionListResponse call() throws IOException, ClassNotFoundException {
				return ExcursionData.GetExcursions();
			}
		};
		
		task.setOnSucceeded((WorkerStateEvent event) ->
		{
			ObservableList<Excursion> excursions = FXCollections.observableArrayList();
			excursions.addAll(task.getValue().getExcursions());
			JFXTreeTableColumn<Excursion, String> portIdCol = new JFXTreeTableColumn<>("Port ID");
			portIdCol.setSortable(false);
			
			JFXTreeTableColumn<Excursion, String> nameCol = new JFXTreeTableColumn<>("Excursion name");
			nameCol.setSortable(false);
			
			portIdCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPortId()));
			nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));
			
			final TreeItem<Excursion> root = new RecursiveTreeItem<>(excursions, RecursiveTreeObject::getChildren);
			
			//noinspection unchecked
			excursionTable.getColumns().setAll(nameCol, portIdCol);
			
			excursionTable.setRoot(root);
			excursionTable.setShowRoot(false);
			
			excursionTable.sort();
			
			bookButton.setDisable(false);
			SetLoading(false);
		});
		
		task.setOnFailed(event -> {
			SetLoading(false);
			errorLabelHome.setText("Failed to retrieve excursions. Try again");
		});
		
		new Thread(task).start();
	}
	
	/**
	 * Populates the Booking tab table
	 */
	@FXML
	private void PopulateBookedList() {
		SetLoading(true);
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
			passengerCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengers()));
			dateCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getDate().toString()));
			statusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getStatus()));
			
			bookedTable.setRowFactory(tv -> {
				TreeTableRow<BookedExcursion> row = new TreeTableRow<>();
				row.setOnMouseClicked(event1 -> {
					if(!row.isEmpty() && event1.getButton() == MouseButton.PRIMARY && event1.getClickCount() == 2)
					{
						System.out.println("Double-clicked");
						EditBooking();
					}
					
				});
				return row;
			});
			
			final TreeItem<BookedExcursion> root = new RecursiveTreeItem<>(excursions, RecursiveTreeObject::getChildren);
			
			bookedTable.getColumns().setAll(nameCol, portCol, passengerCol, dateCol, statusCol);
			
			bookedTable.setRoot(root);
			
			SetLoading(false);
		});
		
		task.setOnFailed(event ->
		{
			SetLoading(false);
			errorLabelBookings.setText("Failed to update bookings");
		});
		
		task.setOnCancelled(event -> {
			SetLoading(false);
			errorLabelBookings.setText("Failed to update bookings");
		});
		
		new Thread(task).start();
	}
	
	/**
	 * Books a selected excursion
	 */
	@FXML
	private void Book() {
		errorLabelHome.setText("");
		
		TreeItem<Excursion> ex = excursionTable.getSelectionModel().getSelectedItem();
		
		int selectedId;
		
		if (ex == null) {
			errorLabelHome.setText("Select an excursion first");
		} else {
			selectedId = ex.getValue().getId();
			
			BookingDialog dialog = new BookingDialog(parent, selectedId);
			dialog.show();
			
			JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
			JFXDialog resultDialog = new JFXDialog(parent, resultDialogLayout, JFXDialog.DialogTransition.CENTER);
			
			
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
				SetLoading(false);
				PopulateBookedList();
			});
			
			task.setOnFailed(event -> {
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
				SetLoading(false);
			});
			
			dialog.getConfirmButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				SetLoading(true);
				new Thread(task).start();
			});
		}
	}
	
	/**
	 * Edits a selected booking
	 */
	@FXML
	private void EditBooking() {
		errorLabelBookings.setText("");
		
		TreeItem<BookedExcursion> selected = bookedTable.getSelectionModel().getSelectedItem();
		
		if(selected == null) {
			errorLabelBookings.setText("Select a booking first");
		}
		else {
			BookingEditDialog dialog = new BookingEditDialog(parent, selected.getValue().getId());
			dialog.show();
			
			JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
			JFXDialog resultDialog = new JFXDialog(parent, resultDialogLayout, JFXDialog.DialogTransition.CENTER);
			
			Task<Code> task = new Task<Code>() {
				@Override
				protected Code call() throws Exception {
					SetLoading(true);
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
				SetLoading(false);
				PopulateBookedList();
			});
			
			task.setOnFailed(event -> {
				SetLoading(false);
				resultDialogLayout.setHeading(new Text("Sorry"));
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.setContent(resultDialogLayout);
				resultDialog.show();
			});
			
			dialog.getConfirmButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				SetLoading(true);
				new Thread(task).start();
			});
		}
	}
	
	@FXML
	private void CancelBooking() {
		errorLabelBookings.setText("");
		TreeItem<BookedExcursion> selected = bookedTable.getSelectionModel().getSelectedItem();
		
		if(selected == null) {
			errorLabelBookings.setText("Select a booking first");
			return;
		}
		
		int selectedBooking = selected.getValue().getId();
		
		
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXDialog dialog = new JFXDialog(parent, dialogLayout, JFXDialog.DialogTransition.CENTER);
		
		JFXDialogLayout resultDialogLayout = new JFXDialogLayout();
		JFXDialog resultDialog = new JFXDialog(parent, resultDialogLayout, JFXDialog.DialogTransition.CENTER);
		
		dialogLayout.setHeading(new Text("Cancel booking"));
		dialogLayout.setBody(new Text("Are you sure you want to cancel this booking?"));
		
		JFXButton confirmButton = new JFXButton("Confirm");
		JFXButton cancelButon = new JFXButton("Cancel");
		
		HBox hbox = new HBox();
		hbox.getChildren().setAll(confirmButton, cancelButon);
		
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
						PopulateBookedList();
						resultDialogLayout.setHeading(new Text("Success"));
						resultDialogLayout.setBody(new Text("Your booking has been cancelled"));
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
				resultDialogLayout.setBody(new Text("The service is temporarily unavailable"));
				resultDialog.show();
				SetLoading(false);
			});
			
			new Thread(task).start();
		});
		
		cancelButon.setOnAction(event -> dialog.close());
		
		dialog.show();
	}
	
	/**
	 * Searches through excursions list
	 */
	//TODO: implement
	@FXML
	private void Search() {
		errorLabelHome.setText("");
		
		String searchQuery = searchField.getText();
		if(searchQuery.isEmpty()) {
			errorLabelHome.setText("Please enter a search query first");
			return;
		}
		
		ArrayList<TreeItem<Excursion>> excursions = new ArrayList<>();
		
		for(int i = 0; i < excursionTable.getCurrentItemsCount(); i ++) {
			excursions.add(excursionTable.getTreeItem(i));
		}
		
		boolean found = false;
		for (int i = 0; i < excursions.size(); i++) {
			if(excursions.get(i).getValue().getName().toLowerCase().contains(searchQuery.toLowerCase())) {
				excursionTable.getSelectionModel().select(i);
				excursionTable.getSelectionModel().focus(i);
				excursionTable.scrollTo(i);
				found = true;
			}
		}
		
		if(!found) {
			for (int i = 0; i < excursions.size(); i++) {
				if(excursions.get(i).getValue().getPortId().toLowerCase().contains(searchQuery.toLowerCase())) {
					excursionTable.getSelectionModel().select(i);
					excursionTable.getSelectionModel().focus(i);
					excursionTable.scrollTo(i);
				}
			}
		}
	}
	
	/**
	 * Set loading status
	 *
	 * @param x Status
	 */
	private void SetLoading(boolean x) {
		rootAnchor.setDisable(x);
		progressIndicator.setVisible(x);
	}
	
	/**
	 * Terminates app completely
	 */
	@FXML
	private void Terminate() {
		Platform.exit();
		System.exit(0);
	}
	
	/**
	 * Switches scene back to login
	 */
	@FXML
	private void SwitchScene() {
		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.millis(500));
		
		fade.setNode(parent);
		
		fade.setFromValue(parent.getOpacity());
		fade.setToValue(0.0);
		
		fade.setOnFinished(event -> {
			try {
				Parent root = FXMLLoader.load(getClass().getResource("../Resources/fxml/startupView.fxml"));
				parent.getScene().setRoot(root);
				Account.setCurrentUser(-1);
			} catch (IOException e) {
				new ExceptionDialog(parent, e).show();
			}
		});
		
		fade.play();
	}
}
