package com.capgemini.books.javafx.dataprovider;

import java.util.Collection;

import com.capgemini.books.javafx.dataprovider.data.BookVO;
import com.capgemini.books.javafx.dataprovider.impl.DataProviderImpl;


public interface DataProvider {

	DataProvider INSTANCE = new DataProviderImpl();

	Collection<BookVO> findBooks(String prefix);
	BookVO saveBook(BookVO book);
	void deleteBook(long id);
}
