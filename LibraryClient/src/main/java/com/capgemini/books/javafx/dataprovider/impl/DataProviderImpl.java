package com.capgemini.books.javafx.dataprovider.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;

import com.capgemini.books.javafx.dataprovider.DataProvider;
import com.capgemini.books.javafx.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	public DataProviderImpl() {

	}

	@Override
	public Collection<BookVO> findBooks(String prefix) {

		LOG.debug("Entering findBooks()");
		List<BookVO> result = new ArrayList<BookVO>();
		ObjectMapper mapper = new ObjectMapper();

		if (prefix == null) {
			prefix = "";

		}

		try {

			URL url = new URL("http://localhost:9721/workshop/rest/books/books-by-title?titlePrefix=" + prefix);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output = br.readLine();
			output = new String(output.getBytes(), "UTF-8");
			result = mapper.readValue(output, new TypeReference<List<BookVO>>() {
			});
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("Brak serwera");
			e.printStackTrace();

		}

		LOG.debug("Leaving findBooks()");
		return result;
	}

	@Override
	public BookVO saveBook(BookVO book) {

		ObjectMapper objectMapper = new ObjectMapper();
		String Bookjson = null;
		BookVO result = new BookVO();

		try {

			ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
			Bookjson = ow.writeValueAsString(book);

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		try {

			URL url = new URL("http://localhost:9721/workshop/rest/books/book");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(Bookjson.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output = br.readLine();
			result = objectMapper.readValue(output, BookVO.class);
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return result;
	}

	@Override
	public void deleteBook(long id) {

		LOG.debug("Entering deleteBook()");

		try {

			URL url = new URL("http://localhost:9721/workshop/rest/books/book/" + id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("blad z serwera");
			e.printStackTrace();
		}
	}
}
