package com.capgemini.books.javafx;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Startup extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * Set the default locale based on the '--lang' startup argument.
		 */
		String langCode = getParameters().getNamed().get("lang");
		if (langCode != null && !langCode.isEmpty()) {
			Locale.setDefault(Locale.forLanguageTag(langCode));
		}

		primaryStage.setTitle("Library-JavaFX");

		Parent root = FXMLLoader.load(getClass().getResource("/com/capgemini/books/javafx/view/books-search.fxml"), //
				ResourceBundle.getBundle("com/capgemini/books/javafx/bundle/bundle"));

		Scene scene = new Scene(root);

		scene.getStylesheets()
				.add(getClass().getResource("/com/capgemini/books/javafx/css/standard.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
