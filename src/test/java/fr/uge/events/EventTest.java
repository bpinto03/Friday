package fr.uge.events;

import org.junit.jupiter.api.Test;

import javax.ws.rs.ClientErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventTest {

    @Test
    public void eventWithNullStringFieldsShouldBeADefaultValue(){
        var event = new Event(1, null, null, null, new Date(1L), new Date(1L), null);
        assertEquals("No title", event.getTitle());
        assertEquals("", event.getDescription());
        assertEquals("", event.getLocation());
        assertEquals("", event.getRecurrency());
    }

    @Test
    public void createEventWithDateAsNullShouldFail(){
        assertThrows(NullPointerException.class, () -> new Event(1, null, null, null, null, null, null));
    }

    @Test
    public void createEventWithEndDateBeforeBeginDateShouldFail() throws ParseException {
        var event = new Event();
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        event.modifyBegin(df.parse("2020-05-13T03:50"));
        assertThrows(ClientErrorException.class, () -> event.setEnd("2020-05-12T03:50"));
    }

    @Test
    public void createEventWithBeginDateAfterEndDateShouldFail() throws ParseException {
        var event = new Event();
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        event.modifyEnd(df.parse("2020-05-12T03:50"));
        assertThrows(ClientErrorException.class, () -> event.setStart("2020-05-13T03:50"));
    }

}
