

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

    /* class to demonstarte use of Drive files list API */
    public class DriveQuickstart {
        /** Application name. */
        private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
        /** Global instance of the JSON factory. */
        private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        /** Directory to store authorization tokens for this application. */
        private static final String TOKENS_DIRECTORY_PATH = "tokens";

        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
        private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

        /**
         * Creates an authorized Credential object.
         * @param HTTP_TRANSPORT The network HTTP Transport.
         * @return An authorized Credential object.
         * @throws IOException If the credentials.json file cannot be found.
         */
        private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
            // Load client secrets.
            InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("692192151192-ideb8ntavv1fj4o9k51pdu6031lt5fmg.apps.googleusercontent.com");
            //returns an authorized Credential object.
            return credential;
        }

        public static void main(String... args) throws IOException, GeneralSecurityException {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Print the names and IDs for up to 10 files.
            FileList result = service.files().list()
                    .setPageSize(10)
                    .setQ("mineType='image/jpeg' or mineType='application/pdf'")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                System.out.println("Files:");
                for (File file : files) {
                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
                }
                // busco la imagen en el directorio
                FileList resultImagenes = service.files().list()
                        .setQ("name contains 'angelysaras' and parents in '"+BotDiscord+"'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .execute();
                List<File> filesImagenes = resultImagenes.getFiles();
                for (File file : filesImagenes) {
                    System.out.printf("Imagen: %s\n", file.getName());
                    // guardamos el 'stream' en el fichero aux.jpeg qieune qe existir
                    OutputStream outputStream = new FileOutputStream("/Users/damiannogueiras/aux.jpeg");
                    service.files().get(file.getId())
                            .executeMediaAndDownloadTo(outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        }
    }

