package com.capgemini.books.javafx.controller;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.capgemini.books.javafx.dataprovider.DataProvider;
import com.capgemini.books.javafx.dataprovider.data.AuthorVO;
import com.capgemini.books.javafx.dataprovider.data.BookVO;
import com.capgemini.books.javafx.model.BookAddModel;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class BookAddController {

	private static final Logger LOG = Logger.getLogger(BookSearchController.class);

	@FXML
	ResourceBundle resources;

	@FXML
	URL location;

	@FXML
	Button AddAuthor;

	@FXML
	Button removeAuthor;

	@FXML
	TextField title;

	@FXML
	TableView<AuthorVO> authorsTable;

	@FXML
	TableColumn<AuthorVO, String> firstNameColumn;

	@FXML
	TableColumn<AuthorVO, String> lastNameColumn;

	@FXML
	Button saveButton;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final BookAddModel model = new BookAddModel();

	public BookAddController() {

	}

	@FXML
	private void initialize() {
		title.textProperty().bindBidirectional(model.TitleProperty());
		authorsTable.itemsProperty().bind(model.authorsListProperty());

		firstNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFirstName()));

		lastNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLastName()));

		authorsTable.setPlaceholder(new Label("Brak autorow"));

		saveButton.disableProperty()
				.bind(title.textProperty().isEmpty().or(model.authorsListProperty().emptyProperty()));
		authorsTable.setPlaceholder(new Label(resources.getString("table.emptyText")));

	}

	@FXML
	public void saveBook(ActionEvent event) {

		BookVO book = new BookVO(model.getTitle(), new HashSet<AuthorVO>(model.getAuthorsList()));

		Task<BookVO> backgroundTask = new Task<BookVO>() {
			@Override
			protected BookVO call() throws Exception {
				LOG.debug("call() called");
				return dataProvider.saveBook(book);
			}
		};

		new Thread(backgroundTask).start();

		Stage stage = (Stage) saveButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void AddAuthor(ActionEvent event) {

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Add Author");
		dialog.setHeaderText("Adding new Author");

		// set buttons
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField firstNameField = new TextField();
		firstNameField.setPromptText("First name");
		TextField lastNameField = new TextField();
		lastNameField.setPromptText("Last name");

		grid.add(new Label("First Name:"), 0, 0);
		grid.add(firstNameField, 1, 0);
		grid.add(new Label("Last Name:"), 0, 1);
		grid.add(lastNameField, 1, 1);
		Node addButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
		addButton.setDisable(true);

		firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			addButton.setDisable(newValue.trim().isEmpty());
		});
		lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			addButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> firstNameField.requestFocus());

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return new Pair<>(firstNameField.getText(), lastNameField.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(author -> {
			model.getAuthorsList().add(new AuthorVO(author.getKey(), author.getValue()));
		});

	}

	@FXML
	public void removeAuthor(ActionEvent event) {
		if (authorsTable.getSelectionModel().getSelectedItem() == null) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Alert");
			alert.setHeaderText("Error");
			alert.setContentText("No selected Author");
			alert.showAndWait();

		} else {

			model.getAuthorsList().remove(authorsTable.getSelectionModel().getSelectedIndex());
		}
	}

}
