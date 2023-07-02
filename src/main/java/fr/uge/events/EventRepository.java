package fr.uge.events;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {

    public Event getEventById(long eventId){
        return findById(eventId);
    }

    public Optional<Event> getNextEvent(long userId){
        var now = new Date();
        return listAllByUserId(userId).stream().filter(e -> 0 <= e.getStart().compareTo(now)).findFirst();
    }

    public List<Event> getEventsBetweenDates(long userId, Date firstDate, Date secondDate){
        var events = list("userId", Sort.by("start"), userId);
        return events.stream()
                .filter(e -> firstDate.compareTo(e.getStart()) <= 0 && 0 <= secondDate.compareTo(e.getEnd()))
                .collect(Collectors.toList());
    }

    public List<Event> listAllByUserId(long userId){
        return list("userId", Sort.by("start"), userId);
    }
}
