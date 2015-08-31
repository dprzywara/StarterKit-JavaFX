package com.capgemini.images.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.capgemini.images.dataProvider.data.ImageVO;
import com.capgemini.images.model.ImagesModel;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class BrowserController {

	@FXML
	Button selectDirectory;
	@FXML
	ScrollPane bookList;
	@FXML
	ImageView imageView;
	@FXML
	Button example;
	@FXML
	TableColumn<ImageVO, Integer> imageId;
	@FXML
	TableColumn<ImageVO, String> imageName;
	@FXML
	TableView<ImageVO> imagesTable;

	@FXML
	ScrollPane scrollImage;
	@FXML
	Button nextImage;
	@FXML
	Button previousImage;
	@FXML
	Button play;
	@FXML
	Button stop;

	@FXML
	ScrollPane thumbnailScroll;
	@FXML
	ScrollPane scrollThumbnails;
	@FXML
	Slider zoomSlider;
	@FXML
	HBox HboxThumbnails;
	@FXML
	Button rotateLeft;
	@FXML
	Button rotateRight;

	final private ImagesModel model = new ImagesModel();
	private boolean STOP = false;
	private static List<ImageVO> listOfImages;

	private static final double DEFAULT_THUMBNAIL_WIDTH = 150;
	private static final double MAX_WIDTH = 1024;

	public BrowserController() {
	}

	@FXML
	private void initialize() {

		scrollImage.setPannable(true);
		String url = "https://www.colourbox.com/preview/8649111-no-photo-camera-sign-icon-photo-flash-symbol.jpg";
		Image image = new Image(url, 497, 377, false, true);
		imageView.fitHeightProperty().set(scrollImage.getPrefHeight());
		imageView.fitWidthProperty().set(scrollImage.getPrefWidth());
		imageView.setImage(image);
		scrollImage.setContent(imageView);

		initializeResultTable();

		imagesTable.itemsProperty().bind(model.resultProperty());
		HboxThumbnails.setPadding(new Insets(15, 12, 15, 12));
		HboxThumbnails.setSpacing(10);
		HboxThumbnails.getStyleClass().add("hbox");

		zoomSlider.setMax(2048);
		zoomSlider.setMin(200);

	}

	private void initializeResultTable() {

		imageId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));

		imageName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

		imagesTable.setPlaceholder(new Label("brak obrazkow"));

		imagesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ImageVO>() {

			@Override
			public void changed(ObservableValue<? extends ImageVO> observable, ImageVO oldValue, ImageVO newValue) {
				if (imagesTable.getItems().size() != 0) {
					File file = new File(imagesTable.getSelectionModel().getSelectedItem().getPath());
					Image image = new Image(file.toURI().toString());

					imageView.fitHeightProperty().set(image.getHeight());
					imageView.fitWidthProperty().set(image.getWidth());
					imageView.setImage(image);
					imageView.setRotate(0);
					scrollImage.setContent(imageView);

					zoomSlider.setValue(imageView.getFitWidth());

				}

			}
		});

		imageView.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0 && imageView.getFitWidth() < 2048) {
					imageView.setFitWidth(imageView.getFitWidth() + 30);
				} else if (event.getDeltaY() < 0 && imageView.getFitWidth() > 200) {
					imageView.setFitWidth(imageView.getFitWidth() - 30);
				}
				zoomSlider.setValue(imageView.getFitWidth());
			}

		});

		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.getClickCount() == 2) {

					StackPane sp = new StackPane();
					Image image = imageView.getImage();
					ImageView imgView = new ImageView(image);
					sp.getChildren().add(imgView);

					Scene secondScene = new Scene(sp);

					Stage secondStage = new Stage();
					secondStage.setTitle("Full image");
					secondStage.setScene(secondScene);

					secondStage.show();
				}

				event.consume();
			}
		});

	}

	private ImageView createImageView(final ImageVO imageFile) {
		final File file = new File(imageFile.getPath());
		final Image image = new Image(file.toURI().toString(), DEFAULT_THUMBNAIL_WIDTH, 0, true, true);
		final ImageView imgView = new ImageView(image);
		imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				Image image = new Image(file.toURI().toString());
				imageView.fitHeightProperty().set(image.getHeight());
				imageView.fitWidthProperty().set(image.getWidth());
				imageView.setImage(image);
				scrollImage.setContent(imageView);

				if (event.getClickCount() == 2) {

					StackPane sp = new StackPane();
					ImageView imgView = new ImageView(image);
					sp.getChildren().add(imgView);

					Scene secondScene = new Scene(sp);

					Stage secondStage = new Stage();
					secondStage.setTitle("Full image");
					secondStage.setScene(secondScene);

					secondStage.show();
				}

				event.consume();
			}
		});
		return imgView;
	}

	private void initializeThumbnailsList() {

		for (ImageVO imageVO : listOfImages) {
			ImageView imgView = createImageView(imageVO);
			HboxThumbnails.getChildren().add(imgView);
		}

		scrollThumbnails.setContent(HboxThumbnails);

	}

	public String chooseDirectory() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open File");
		String currentDir = System.getProperty("user.dir") + File.separator;
		directoryChooser.setInitialDirectory(new File(currentDir));

		File selectedDirectory = directoryChooser.showDialog(new Stage());

		if (selectedDirectory == null) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Alert");
			alert.setHeaderText("Error");
			alert.setContentText("Nie wybrales katalogu");
			alert.showAndWait();
			return directoryChooser.getInitialDirectory().getPath();

		} else {
			return selectedDirectory.getAbsolutePath();
		}
	}

	public List<ImageVO> createImageList(String path) {

		List<ImageVO> listOfImages = new ArrayList<ImageVO>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].getName().endsWith("jpg") || listOfFiles[i].getName().endsWith("JPG")
					|| listOfFiles[i].getName().endsWith("png") || listOfFiles[i].getName().endsWith("PNG")) {
				listOfImages.add(new ImageVO(i + 1, listOfFiles[i].getName(), listOfFiles[i].getAbsolutePath()));
			}
		}

		return listOfImages;

	}

	@FXML
	public void selectButtonAction(ActionEvent event) {
		listOfImages = createImageList(chooseDirectory());
		imagesTable.getItems().clear();
		HboxThumbnails.getChildren().clear();

		if (listOfImages.size() == 0) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Empty list information");
			alert.setHeaderText("Information Alert");
			String s = "No images in choosen directory, try again";
			alert.setContentText(s);
			alert.show();

		} else {
			model.setResult(listOfImages);
			imagesTable.getSelectionModel().select(listOfImages.get(0));
			initializeThumbnailsList();

		}

	}

	@FXML
	public void next(ActionEvent event) {

		int currentId = imagesTable.getSelectionModel().getSelectedIndex();
		if (currentId == imagesTable.getItems().size() - 1) {
			imagesTable.getSelectionModel().select(imagesTable.getItems().get(0));
		} else {
			imagesTable.getSelectionModel().select(imagesTable.getItems().get(currentId + 1));

		}

	}

	@FXML
	public void previous(ActionEvent event) {

		int currentId = imagesTable.getSelectionModel().getSelectedIndex();
		if (currentId == 0) {
			imagesTable.getSelectionModel().select(imagesTable.getItems().get(imagesTable.getItems().size() - 1));
		} else {
			imagesTable.getSelectionModel().select(imagesTable.getItems().get(currentId - 1));

		}
	}

	@FXML
	public void playButton(ActionEvent event) {
		STOP = false;

		Task<Collection<ImageVO>> backgroundTask = new Task<Collection<ImageVO>>() {

			@Override
			protected Collection<ImageVO> call() throws Exception {
				for (int i = 0; i < imagesTable.getItems().size(); i++) {
					imagesTable.getSelectionModel().select(imagesTable.getItems().get(i));
					if (STOP) {
						break;
					}
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (STOP) {
						break;
					}
				}

				return imagesTable.getItems();
			}
		};
		new Thread(backgroundTask).start();

	}

	@FXML
	public void stopButton(ActionEvent event) {
		STOP = true;
	}

	@FXML
	public void mouseClicked(MouseEvent event) {

		if (event.getClickCount() == 2) {
			StackPane sp = new StackPane();
			File file = new File(imagesTable.getSelectionModel().getSelectedItem().getPath());
			Image image = new Image(file.toURI().toString(), MAX_WIDTH, 0, true, true);

			ImageView imgView = new ImageView(image);
			sp.getChildren().add(imgView);

			Scene secondScene = new Scene(sp);

			Stage secondStage = new Stage();
			secondStage.setTitle("Full image");
			secondStage.setScene(secondScene);

			secondStage.show();
		}

	}

	@FXML
	public void zoomMove(MouseEvent event) {
		imageView.setFitWidth(zoomSlider.getValue() * 1.5);
		imageView.setFitHeight(zoomSlider.getValue());

	}

	@FXML
	public void rotateLeft(ActionEvent event) {
		double oldRotation = imageView.getRotate();
		imageView.setRotate(oldRotation - 90);
	}

	@FXML
	public void rotateRight(ActionEvent event) {

		double oldRotation = imageView.getRotate();
		imageView.setRotate(oldRotation + 90);
	}



}
