package GUI.MainView;

import Common.BookedExcursion;
import Common.Excursion;
import GUI.ExceptionDialog;
import GUI.Network.Account;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
	
	@FXML
	private JFXTextField searchField;
	
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
	private Label profileUsernameLabel;
	
	@FXML
	private Label profileNameLabel;
	
	@FXML
	private Label profileEmailLabel;
	
	@FXML
	private Label profileCabinLabel;
	
	@FXML
	private JFXTreeTableView<Excursion> excursionTable;
	
	@FXML
	private JFXTreeTableView<BookedExcursion> bookedTable;
	
	private HomeTabController homeTabController;
	private BookingTabController bookingTabController;
	
	/**
	 * Called when a main scene is created
	 *
	 * @param location  /
	 * @param resources /
	 */
	public void initialize(URL location, ResourceBundle resources) {
		homeTabController = new HomeTabController(this);
		bookingTabController = new BookingTabController(this);
		ProfileTabController profileTabController = new ProfileTabController(this);
		
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
		
		getSearchField().textProperty().addListener((observable, oldValue, newValue) ->
				getExcursionTable().setPredicate(excursionTreeItem ->
						excursionTreeItem.getValue().getName().contains(newValue.toUpperCase()) ||
								excursionTreeItem.getValue().getPortId().contains(newValue.toUpperCase())));
		
		fade.play();
		excursionTable.setPlaceholder(new Text("No excursions available at the moment"));
		bookedTable.setPlaceholder(new Text("You have no bookings"));
		profileTabController.PopulateTab();
		PopulateExcursionList();
		PopulateBookedList();
	}
	
	/**
	 * Populates the Home tab table
	 */
	@FXML
	private void PopulateExcursionList() {
		homeTabController.PopulateExcursionList();
	}
	
	/**
	 * Populates the Booking tab table
	 */
	@FXML
	private void PopulateBookedList() {
		bookingTabController.PopulateBookedList();
	}
	
	/**
	 * Books a selected excursion
	 */
	@FXML
	private void Book() {
		homeTabController.Book();
		bookingTabController.PopulateBookedList();
	}
	
	/**
	 * Edits a selected booking
	 */
	@FXML
	public void EditBooking() {
		bookingTabController.EditBooking();
	}
	
	/**
	 * Cancels a booking
	 */
	@FXML
	private void CancelBooking() {
		bookingTabController.CancelBooking();
	}

	
	/**
	 * Set loading status
	 *
	 * @param x Status
	 */
	void SetLoading(boolean x) {
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
	
	/**
	 * @return JFXTreeTableView for booked excursions
	 */
	JFXTreeTableView<BookedExcursion> getBookedTable() {
		return bookedTable;
	}
	
	/**
	 * @return Error label for bookings tab
	 */
	Label getErrorLabelBookings() {
		return errorLabelBookings;
	}
	
	/**
	 * @return Root StackPane
	 */
	StackPane getParent() {
		return parent;
	}
	
	/**
	 * @return JFXTreeTableView for excursions
	 */
	JFXTreeTableView<Excursion> getExcursionTable() {
		return excursionTable;
	}
	
	/**
	 * @return Error label for home tab
	 */
	Label getErrorLabelHome() {
		return errorLabelHome;
	}
	
	/**
	 * @return Search JFXTextField
	 */
	private JFXTextField getSearchField() {
		return searchField;
	}
	
	Label getProfileUsernameLabel() {
		return profileUsernameLabel;
	}
	
	Label getProfileNameLabel() {
		return profileNameLabel;
	}
	
	Label getProfileEmailLabel() {
		return profileEmailLabel;
	}
	
	Label getProfileCabinLabel() {
		return profileCabinLabel;
	}
}
