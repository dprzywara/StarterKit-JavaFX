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
		/*
		 * REV: utworzenie ObjectMappera jest kosztowne, powinien byc utworzony w konstruktorze i przypisany do pola w klasie.
		 */
		ObjectMapper mapper = new ObjectMapper();

		if (prefix == null) {
			prefix = "";

		}

		try {

			/*
			 * REV: adres serwera poiwinien byc pobierany z pliku konfiguracyjnego
			 */
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
			/*
			 * REV: rozlaczenie poiwnno byc w bloku finally, tak zeby rozlaczal tez przy wyjatku
			 */
			conn.disconnect();

		} catch (MalformedURLException e) {
			/*
			 * REV: fajniej byloby rzucic jakis wyjatek i pokazac blad w GUI.
			 */
			e.printStackTrace();

		} catch (IOException e) {
			/*
			 * REV: zawsze uzywaj loggera !
			 */
			System.out.println("Brak serwera");
			e.printStackTrace();

		}

		LOG.debug("Leaving findBooks()");
		return result;
	}

	@Override
	public BookVO saveBook(BookVO book) {
		/*
		 * REV: j.w.
		 */
		ObjectMapper objectMapper = new ObjectMapper();
		String Bookjson = null;
		BookVO result = new BookVO();

		try {

			ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
			Bookjson = ow.writeValueAsString(book);

		} catch (JsonGenerationException e) {
			/*
			 * REV: j.w.
			 */
			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		try {
			/*
			 * REV: j.w.
			 */
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
			/*
			 * REV: j.w.
			 */
			conn.disconnect();

		} catch (MalformedURLException e) {
			/*
			 * REV: j.w.
			 */

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
			/*
			 * REV: j.w.
			 */
			URL url = new URL("http://localhost:9721/workshop/rest/books/book/" + id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

			}
			/*
			 * REV: j.w.
			 */
			conn.disconnect();

		} catch (MalformedURLException e) {
			/*
			 * REV: j.w.
			 */
			e.printStackTrace();

		} catch (IOException e) {
			/*
			 * REV: j.w.
			 */
			System.out.println("blad z serwera");
			e.printStackTrace();
		}
	}
}
