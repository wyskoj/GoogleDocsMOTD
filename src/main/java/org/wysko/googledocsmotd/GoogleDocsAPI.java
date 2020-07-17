/*
 *
 * MIT License
 *
 * Copyright (c) 2020 Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.wysko.googledocsmotd;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.Document;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDocsAPI {
	private static final String APPLICATION_NAME = "Minecraft Google Docs MOTD";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "plugins/GoogleDocsMOTD/tokens";
	private static final List<String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS_READONLY);
	private static final String CREDENTIALS_FILE_PATH = "plugins/GoogleDocsMOTD/credentials.json";
	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8000).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	public static Document getDocument(String documentID) throws GeneralSecurityException, IOException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Docs service = new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME)
				.build();
		return service.documents().get(documentID).execute();
	}
}
