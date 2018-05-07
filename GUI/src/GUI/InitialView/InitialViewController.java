package GUI.InitialView;

import GUI.Animation;
import GUI.ExceptionDialog;
import GUI.Network.Account;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controls UI and behaviour of login and registration screens
 */
public final class InitialViewController implements Initializable {
	// Duration of fade animation between login and registration views in milliseconds
	private final double fadeDuration = 300.0;
	// Labels
	@FXML
	private Label errorLabelLogin;
	@FXML
	private Label errorLabelRegistration;
	// Text fields
	@FXML
	private TextField fieldLoginUsername;
	@FXML
	private PasswordField fieldLoginPassword;
	@FXML
	private TextField fieldRegistrationEmail;
	@FXML
	private TextField fieldRegistrationUsername;
	@FXML
	private TextField fieldRegistrationName;
	@FXML
	private TextField fieldRegistrationCabin;
	@FXML
	private TextField fieldRegistrationPassword;
	// Indicators
	@FXML
	private ProgressIndicator progressIndicator;
	// Panes
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane root2;
	@FXML
	private AnchorPane root3;
	@FXML
	private AnchorPane root4;
	@FXML
	private StackPane rootNode;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rootNode.setOpacity(0.0);
		
		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.millis(500));
		
		fade.setNode(rootNode);
		
		fade.setFromValue(rootNode.getOpacity());
		fade.setToValue(1.0);
		
		fade.play();
	}
	
	/**
	 * Authenticates user on the system
	 */
	@FXML
	private void Authenticate() {
		SetLoading(true);
		errorLabelLogin.setText("");
		
		if (fieldLoginUsername.getText().isEmpty()) {
			errorLabelLogin.setText("Username cannot be empty");
			SetLoading(false);
		} else if (fieldLoginPassword.getText().isEmpty()) {
			errorLabelLogin.setText("Password cannot be empty");
			SetLoading(false);
		} else {
			Task<Boolean> task = new Task<Boolean>() {
				@Override
				protected Boolean call() throws IOException, ClassNotFoundException {
					SetLoading(true);
					return Account.Authorize(fieldLoginUsername.getText(), fieldLoginPassword.getText());
				}
			};
			
			task.setOnSucceeded(e -> {
				boolean success = task.getValue();
				
				if (success) {
					Task<Integer> task2 = new Task<Integer>() {
						@Override
						protected Integer call() throws IOException, ClassNotFoundException {
							return Account.GetUserId(fieldLoginUsername.getText());
						}
					};
					
					task2.setOnSucceeded(event -> {
						Account.setCurrentUser(task2.getValue());
						SwitchScene();
					});
					
					task2.setOnFailed(event -> {
						errorLabelLogin.setText("Server error has occurred");
						new ExceptionDialog(rootNode, (Exception) e.getSource().getException()).show();
					});
					
					new Thread(task2).start();
				} else {
					errorLabelLogin.setText("Username or password is wrong");
				}
				
				SetLoading(false);
			});
			
			task.setOnFailed(e -> {
				errorLabelLogin.setText("Could not connect to the server");
				SetLoading(false);
				new ExceptionDialog(rootNode, (Exception) e.getSource().getException()).show();
			});
			
			new Thread(task).start();
		}
	}
	
	private void SwitchScene() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../Resources/fxml/mainView.fxml"));
			
			FadeTransition transition = new FadeTransition();
			transition.setDuration(Duration.millis(350));
			
			transition.setNode(rootNode);
			
			transition.setFromValue(rootNode.getOpacity());
			transition.setToValue(0.0);
			
			transition.setOnFinished((ActionEvent event) -> rootNode.getScene().setRoot(root));
			
			transition.play();
		} catch (IOException e) {
			new ExceptionDialog(rootNode, e).show();
		}
	}
	
	/**
	 * Registers user on the system
	 */
	@FXML
	private void Register() {
		SetLoading(true);
		errorLabelRegistration.setText("");
		
		if (fieldRegistrationUsername.getText().isEmpty()) {
			errorLabelRegistration.setText("Username cannot be empty!");
			SetLoading(false);
		} else if (fieldRegistrationEmail.getText().isEmpty()) {
			errorLabelRegistration.setText("E-mail cannot be empty!");
			SetLoading(false);
		} else if (fieldRegistrationName.getText().isEmpty()) {
			errorLabelRegistration.setText("Name cannot be empty!");
			SetLoading(false);
		} else if (fieldRegistrationCabin.getText().isEmpty()) {
			errorLabelRegistration.setText("Cabin number cannot be empty");
			SetLoading(false);
		} else if (fieldRegistrationCabin.getText().length() > 10 || fieldRegistrationCabin.getText().length() < 5) {
			errorLabelRegistration.setText("Check your cabin number");
			SetLoading(false);
		} else if (fieldRegistrationPassword.getText().isEmpty()) {
			errorLabelRegistration.setText("Password cannot be empty!");
			SetLoading(false);
		}
		// We create a new thread using a Task class from javafx api to avoid UI blocking during a time consuming operation
		else {
			Task<Boolean> task = new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					return Account.CreateAccount(fieldRegistrationEmail.getText(),
							fieldRegistrationUsername.getText(),
							fieldRegistrationCabin.getText(),
							fieldRegistrationName.getText(),
							fieldRegistrationPassword.getText());
				}
			};
			
			task.setOnSucceeded(e -> {
				boolean success = task.getValue();
				
				if (success) {
					FadeToLogin();
					errorLabelLogin.setText("Registration complete. Now log in");
				} else {
					errorLabelRegistration.setText("This username or email already exists");
				}
				
				SetLoading(false);
			});
			
			task.setOnFailed(e ->
			{
				SetLoading(false);
				errorLabelRegistration.setText("Could not connect to the server");
				new ExceptionDialog(rootNode, (Exception) e.getSource().getException()).show();
			});
			
			new Thread(task).start();
		}
	}
	
	/**
	 * Triggered when a buttonTransitionLogin is pressed
	 */
	@FXML
	private void FadeToRegistration() {
		ResetRegistration();
		
		Animation.Fade(root, fadeDuration, 0.0);
		Animation.Fade(root2, fadeDuration, 0.0);
		Animation.Fade(root3, fadeDuration, 1.0);
		Animation.Fade(root4, fadeDuration, 1.0);
		
		root3.toFront();
		root4.toFront();
	}
	
	/**
	 * Triggered when a buttonTransitionRegistration is pressed
	 */
	@FXML
	private void FadeToLogin() {
		ResetLogin();
		
		Animation.Fade(root, fadeDuration, 1.0);
		Animation.Fade(root2, fadeDuration, 1.0);
		Animation.Fade(root3, fadeDuration, 0.0);
		Animation.Fade(root4, fadeDuration, 0.0);
		
		root.toFront();
		root2.toFront();
	}
	
	private void SetLoading(boolean x) {
		root.setDisable(x);
		root2.setDisable(x);
		root3.setDisable(x);
		root4.setDisable(x);
		
		progressIndicator.setVisible(x);
	}
	
	/**
	 * Resets all nodes at login screen to their initial state
	 */
	private void ResetLogin() {
		fieldLoginUsername.setText("");
		fieldLoginPassword.setText("");
		
		errorLabelLogin.setText("");
	}
	
	/**
	 * Resets all nodes at registration screen to their initial state
	 */
	private void ResetRegistration() {
		fieldRegistrationCabin.setText("");
		fieldRegistrationEmail.setText("");
		fieldRegistrationName.setText("");
		fieldRegistrationPassword.setText("");
		fieldRegistrationUsername.setText("");
		
		errorLabelRegistration.setText("");
	}
}