package com.capgemini.books.javafx.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.books.javafx.dataprovider.data.AuthorVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;


public class BookAddModel {

	private final StringProperty title = new SimpleStringProperty();
	private final ListProperty<AuthorVO> authorsList = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));



	public final String getTitle() {
		return title.get();
	}
	public final void setTitle(String value) {
		title.set(value);
	}
	public StringProperty TitleProperty() {
		return title;
	}

	public final List<AuthorVO> getAuthorsList() {
		return authorsList.get();
	}

	public final void setAuthorsList(List<AuthorVO> value) {
		authorsList.setAll(value);
	}

	public ListProperty<AuthorVO> authorsListProperty() {
		return authorsList;
	}

}
