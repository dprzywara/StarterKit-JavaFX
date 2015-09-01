package com.capgemini.books.javafx.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.books.javafx.dataprovider.data.BookVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;


public class BookSearchModel {

	private final StringProperty prefix = new SimpleStringProperty();
	private final ListProperty<BookVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));

	public final String getPrefix() {
		return prefix.get();
	}

	public final void setPrefix(String value) {
		prefix.set(value);
	}

	public StringProperty prefixProperty() {
		return prefix;
	}

	public final List<BookVO> getResult() {
		return result.get();
	}

	public final void setResult(List<BookVO> value) {
		result.setAll(value);
	}

	public ListProperty<BookVO> resultProperty() {
		return result;
	}

}
