package com.capgemini.books.javafx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.capgemini.books.javafx.dataprovider.DataProvider;
import com.capgemini.books.javafx.dataprovider.data.AuthorVO;
import com.capgemini.books.javafx.dataprovider.data.BookVO;
import com.capgemini.books.javafx.model.BookSearchModel;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BookSearchController {

	private static final Logger LOG = Logger.getLogger(BookSearchController.class);

	@FXML
	ResourceBundle resources;

	@FXML
	URL location;

	@FXML
	TextField prefixField;

	@FXML
	Button searchButton;

	@FXML
	TableView<BookVO> resultTable;

	@FXML
	TableColumn<BookVO, String> idColumn;

	@FXML
	TableColumn<BookVO, String> titleColumn;

	@FXML
	TableColumn<BookVO, String> authorsColumn;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final BookSearchModel model = new BookSearchModel();

	@FXML
	Button saveButton;

	@FXML
	TextField title;

	@FXML
	MenuButton menu;

	@FXML
	MenuItem AddBook;

	@FXML
	MenuItem deleteBook;

	public BookSearchController() {
		LOG.debug("Constructor: prefixField = " + prefixField);
	}

	@FXML
	private void initialize() {
		LOG.debug("initialize(): prefixField = " + prefixField);

		initializeResultTable();

		prefixField.textProperty().bindBidirectional(model.prefixProperty());
		resultTable.itemsProperty().bind(model.resultProperty());

	}

	private void initializeResultTable() {
		idColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId().toString()));

		titleColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTitle()));

		authorsColumn.setCellValueFactory(cellData -> {
			StringProperty string = new SimpleStringProperty();
			string.set("");
			for (AuthorVO author : cellData.getValue().getAuthors()) {
				string.set(string.get() + author.getFirstName() + " " + author.getLastName() + ", ");
			}
			return string;
		});

		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));

	}

	@FXML
	public void searchButtonAction(ActionEvent event) {
		LOG.debug("'Search' button clicked");
		searchBooks();

	}

	private void searchBooks() {

		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			@Override
			protected Collection<BookVO> call() throws Exception {
				LOG.debug("call() called");

				return dataProvider.findBooks(model.getPrefix()); //
			}
		};

		backgroundTask.stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED) {
					LOG.debug("changed() called");

					model.setResult(new ArrayList<BookVO>(backgroundTask.getValue()));

					resultTable.getSortOrder().clear();
				}

			}
		});
		new Thread(backgroundTask).start();
	}


	/*
	 * REV: nazwa metody z malej litery
	 */
	@FXML
	public void OpenBookView(ActionEvent event) throws IOException {

		Stage stage = new Stage();

		stage.setTitle("Add Book");

		Parent root = FXMLLoader.load(getClass().getResource("/com/capgemini/books/javafx/view/AddBook.fxml"), //
				ResourceBundle.getBundle("com/capgemini/books/javafx/bundle/bundle"));

		Scene scene = new Scene(root);

		scene.getStylesheets()
				.add(getClass().getResource("/com/capgemini/books/javafx/css/standard.css").toExternalForm());

		stage.setScene(scene);
		stage.show();

	}

	@FXML
	public void deleteBook(ActionEvent event) {
		if (resultTable.getSelectionModel().getSelectedItem() == null) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Alert");
			alert.setHeaderText("Error");
			alert.setContentText("No selected book");
			alert.showAndWait();

		} else {
			Task<Void> backgroundTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					LOG.debug("call() called");
					dataProvider.deleteBook(resultTable.getSelectionModel().getSelectedItem().getId());
					return null;
				}
			};

			backgroundTask.stateProperty().addListener(new ChangeListener<Worker.State>() {

				@Override
				public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
					if (newValue == State.SUCCEEDED) {
						LOG.debug("changed() called");
						model.getResult().remove(resultTable.getSelectionModel().getSelectedIndex());
						resultTable.getSortOrder().clear();
					}
				}
			});
			new Thread(backgroundTask).start();

		}
	}

}
