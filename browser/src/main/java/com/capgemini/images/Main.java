package com.capgemini.images;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class Main extends Application {

	List<Image> images = new ArrayList<>();
	 ImageView[] imagesView;


	@Override
	public void start(Stage primaryStage) throws IOException {


		Parent	root = FXMLLoader.load(
					getClass().getResource("/com/capgemini/images/view/imageViewer.fxml"), //
					ResourceBundle.getBundle("com/capgemini/images/bundle/bundle"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/com/capgemini/images/css/application.css").toExternalForm());
			primaryStage.setTitle("Images Viewer");

			primaryStage.setScene(scene);

			primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}



