package fr.uge.events;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class EventRepositoryTest {

    @Test
    @Transactional
    public void getAddedEventToRepositoryShouldSucceed(){
        var repository = new EventRepository();
        var event = new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        repository.persist(event);
        assertTrue(repository.isPersistent(event));
        assertEquals(event, repository.getEventById(event.getEventId()));
    }

    @Test
    @Transactional
    public void getEventByIdNotInRepositoryShouldReturnNull(){
        var repository = new EventRepository();
        repository.deleteAll();
        assertNull(repository.getEventById(1000));
    }

    @Test
    @Transactional
    public void getEventsFromUserIdShouldSucceed(){
        var repository = new EventRepository();
        var event1 = new Event(2, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event2 = new Event(2, "Examen modélisation", "Examen fin de semestre de modélisation", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event3 = new Event(1, "Examen Crypto", "Examen fin de semestre de Cryptographie", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event4 = new Event(2, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var listEvents = List.of(event1, event2, event3, event4);
        listEvents.forEach(repository::persist);
        listEvents.forEach(e -> assertTrue(repository.isPersistent(e)));
        assertEquals(listEvents.stream().filter(e -> e.getUserId() == 2).collect(Collectors.toUnmodifiableList()), repository.listAllByUserId(2));
    }
}
