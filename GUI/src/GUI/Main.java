package GUI;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Preloader {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		// Loading fonts from GUI.GUI.Resources
		Font.loadFont(getClass().getResourceAsStream("Resources/fonts/Orkney-Light.ttf"), 10);
		
		primaryStage.setTitle("Seven Seas");
		
		// setting fxml file as root
		Parent root = FXMLLoader.load(getClass().getResource("Resources/fxml/startupView.fxml"));
		
		double width = Screen.getPrimary().getBounds().getWidth() / 1.2;
		double height = Screen.getPrimary().getBounds().getHeight() / 1.3;
		
		Scene login = new Scene(root, width, height);
		
		// setting app icon
		primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("Resources/images/icon.png")));
		
		primaryStage.setScene(login);
		primaryStage.show();
	}
}
