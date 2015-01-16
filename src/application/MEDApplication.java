package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class MEDApplication extends Application {
	
	private static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		MEDApplication.primaryStage = primaryStage;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/Main.fxml"));
		Scene scene = new Scene(loader.load());
		primaryStage.setTitle("MED Application");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
}
