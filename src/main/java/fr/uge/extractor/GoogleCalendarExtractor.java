package fr.uge.extractor;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import fr.uge.events.EventRepository;

import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleCalendarExtractor {
    private static final String APPLICATION_NAME = "Friday";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarExtractor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Get a calendar instance of Google Calendar by connection.
     *
     * @return Events object of Google Calendar API
     * @throws IOException If getCredentials throw an IOException.
     */
    private static Events getCalendarFromConnection() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service.events().list("primary")
                .setTimeMin(new DateTime(System.currentTimeMillis())) //min time = now to exclude past events
                .execute();
    }

    /**
     * Create a Friday event from a Google Calendar Event.
     *
     * @param userId The user id which asked to extract all events.
     * @param calendarEvent A Google Calendar Event.
     * @return Event A Friday Event wich contains Google Calendar Event informations.
     */
    private static fr.uge.events.Event calendarEventToFridayEvent(long userId, Event calendarEvent){
        var startDateTime = calendarEvent.getStart().getDateTime();
        var endDateTime = calendarEvent.getEnd().getDateTime();
        var calendarRecurrence = calendarEvent.getRecurrence();
        var recurrenceRule = "";

        if(startDateTime == null) {
            startDateTime = calendarEvent.getStart().getDate();
        }
        if(endDateTime == null) {
            endDateTime = calendarEvent.getEnd().getDate();
        }

        if(calendarRecurrence != null && !calendarRecurrence.isEmpty()){
             recurrenceRule = calendarRecurrence.get(0);
        }
        return new fr.uge.events.Event(userId, calendarEvent.getSummary(), calendarEvent.getDescription(),
                calendarEvent.getLocation(), new Date(startDateTime.getValue()), new Date(endDateTime.getValue()),
                recurrenceRule);
    }

    /**
     * Persist all Friday events created from Google Calendar Events.
     *
     * @param userId The user id which asked to extract all events.
     * @param evRepos The event repository where we persist the events.
     * @return Response A response to send to the user.
     */
    public static Response getAllEvent(long userId, EventRepository evRepos) throws IOException, GeneralSecurityException {
        // List the next events from the primary calendar
        Events events = getCalendarFromConnection();
        List<Event> items = events.getItems();
        items.stream()
                .map(calendarEvent -> calendarEventToFridayEvent(userId, calendarEvent))
                .forEach(evRepos::persist);
        return Response.ok("All google events added.").build();
    }
}
