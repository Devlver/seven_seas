package GUI.AdminView;

import Common.AdminBooking;
import GUI.Network.Account;
import GUI.Network.ExcursionData;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
	
	@FXML
	private StackPane root;
	
	@FXML
	private JFXTreeTableView excursionTable;
	
	@FXML
	private JFXSpinner progressIndicator;
	
	@FXML
	private Label errorLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		excursionTable.setPlaceholder(new Text("No bookings yet"));
		PopulateExcursionsTable();
	}
	
	@FXML
	private void PopulateExcursionsTable() {
		SetLoading(true);
		
		Task<ArrayList<AdminBooking>> task = new Task<ArrayList<AdminBooking>>() {
			@Override
			protected ArrayList<AdminBooking> call() throws Exception {
				return ExcursionData.GetAdminBookings(Account.getCurrentUser());
			}
		};
		
		task.setOnSucceeded(event -> {
			ArrayList<AdminBooking> list = task.getValue();
			
			if (list == null) {
				errorLabel.setText("Failed to retrieve bookings");
				SetLoading(false);
				return;
			}
			
			ObservableList<AdminBooking> bookings = FXCollections.observableArrayList();
			bookings.addAll(list);
			
			JFXTreeTableColumn<AdminBooking, String> nameColumn = new JFXTreeTableColumn<>("Passenger name");
			JFXTreeTableColumn<AdminBooking, String> dateColumn = new JFXTreeTableColumn<>("Date");
			JFXTreeTableColumn<AdminBooking, String> passengersColumn = new JFXTreeTableColumn<>("Number of passengers");
			JFXTreeTableColumn<AdminBooking, String> statusColumn = new JFXTreeTableColumn<>("Status");
			
			nameColumn.setSortable(false);
			dateColumn.setSortable(false);
			passengersColumn.setSortable(false);
			statusColumn.setSortable(false);
			
			nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengerName()));
			dateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getBookingDate().toString()));
			passengersColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPassengersNumber()));
			statusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getStatus()));
			
			final TreeItem<AdminBooking> root = new RecursiveTreeItem<>(bookings, RecursiveTreeObject::getChildren);
			
			//noinspection unchecked
			excursionTable.getColumns().setAll(nameColumn, dateColumn, passengersColumn, statusColumn);
			
			excursionTable.setRoot(root);
			
			SetLoading(false);
		});
		
		task.setOnFailed(event -> {
			SetLoading(false);
			errorLabel.setText("Failed to retrieve bookings. Try again later");
		});
		
		new Thread(task).start();
	}
	
	private void SetLoading(boolean x) {
		root.setDisable(x);
		progressIndicator.setVisible(x);
	}
}
