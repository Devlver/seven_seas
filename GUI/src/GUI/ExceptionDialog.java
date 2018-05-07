package GUI;

import com.jfoenix.controls.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialog extends JFXDialog {
	public ExceptionDialog(StackPane parent, Exception ex) {
		setDialogContainer(parent);
		setContent(getLayout(ex));
	}
	
	private JFXDialogLayout getLayout(Exception ex) {
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setHeading(new Text("An exception has occurred"));
		
		JFXScrollPane scrollPane = new JFXScrollPane();
		JFXListView<String> list = new JFXListView<>();
		
		StringWriter writer = new StringWriter();
		PrintWriter print = new PrintWriter(writer);
		ex.printStackTrace(print);
		
		list.getItems().add(writer.toString());
		scrollPane.getChildren().add(list);
		layout.setBody(scrollPane);
		
		JFXButton button = new JFXButton("Close");
		button.setOnAction(event -> close());
		
		layout.setActions(button);
		
		return layout;
	}
}
