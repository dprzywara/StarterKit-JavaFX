package com.capgemini.images;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {

	/*
	 * REV: these fields are not used
	 */
	List<Image> images = new ArrayList<>();
	 ImageView[] imagesView;


	@Override
	public void start(Stage primaryStage) {

		try {
		Parent	root = FXMLLoader.load(
					getClass().getResource("/com/capgemini/images/view/imageBrowser.fxml")); //
					//ResourceBundle.getBundle("bundle"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/com/capgemini/images/css/application.css").toExternalForm());


			primaryStage.setTitle("Przegladarka Obrazkow");

			primaryStage.setScene(scene);

			primaryStage.show();






		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}



