package fr.uge.extractor;

import fr.uge.events.Event;
import fr.uge.events.EventRepository;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ICalExtractor {
    private static Event iCalVEventToFridayEvent(long userId, VEvent component){
        var rule = component.getProperty("RRULE");
        var rrule = "";
        if (rule != null){
            rrule = rule.getValue();
        }
        return new Event(userId, component.getSummary().getValue(), component.getDescription().getValue(),
                component.getLocation().getValue(), new Date(component.getStartDate().getDate().getTime()),
                new Date(component.getEndDate().getDate().getTime()), rrule);
    }

    private static List<Event> extractEventsToCalendar(Calendar calendar, long userId, EventRepository evRepos){
        var components = calendar.getComponents(Component.VEVENT);
        var events = components
                .stream()
                .map(e -> iCalVEventToFridayEvent(userId, new VEvent(e.getProperties())))
                .collect(Collectors.toList());
        events.forEach(evRepos::persist);
        return events;
    }

    public static Response extractEventsFromIcs (InputStream icsFile, long userId, EventRepository evRepos) {
        try{
            if(extractEventsToCalendar(new CalendarBuilder().build(icsFile), userId, evRepos).isEmpty()){
                return Response.status(404, "No upcoming events found").build();
            }
            return Response.ok("All icalendar events added.").build();
        }
        catch(ParserException e){
            return Response.status(400, "Could not parse correctly ics file.").build();
        }
        catch(IOException e){
            return Response.status(404, "Could not open ics file.").build();
        }
    }
}
