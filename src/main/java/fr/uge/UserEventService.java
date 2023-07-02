package fr.uge;

import fr.uge.crypt.Encrypt;
import fr.uge.events.Event;
import fr.uge.events.EventRepository;
import fr.uge.extractor.GoogleCalendarExtractor;
import fr.uge.extractor.ICalExtractor;
import fr.uge.users.User;
import fr.uge.users.UserRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Path("/friday")
@ApplicationScoped
public class UserEventService {
    private final Encrypt crypt = new Encrypt("secret");
    @Inject
    EventRepository eventRepository;
    @Inject
    UserRepository userRepository;

    //NoSuchAlgorithmException is for the encryption type
    public UserEventService() throws NoSuchAlgorithmException {
    }

    @Path("/getNextEvent/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getNextEvent(@PathParam("userId") long userId){
        return Uni.createFrom().item(() -> {
            var nextEvent = eventRepository.getNextEvent(userId);
            if(nextEvent.isEmpty()){
                return Response.ok("{}").build();
            }
            return Response.ok(nextEvent.get()).build();
        });
    }

    @Path("/modifyEvent/{eventId}")
    @PUT
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> modifyEvent(@PathParam("eventId") long eventId, Event modifiedEvent){
        return Uni.createFrom().item(() -> {
            var eventToModify = eventRepository.getEventById(eventId);
            if(eventToModify == null || eventToModify.getUserId() != modifiedEvent.getUserId()){
                return Response.status(404, "Event with eventId "+ eventId +" not found.").build();
            }
            eventToModify.setTitle(modifiedEvent.getTitle());
            eventToModify.setDescription(modifiedEvent.getDescription());
            eventToModify.setLocation(modifiedEvent.getLocation());
            eventToModify.setRecurrency(modifiedEvent.getRecurrency());
            eventToModify.modifyBegin(modifiedEvent.getStart()); //Doesn't need to check begin < end
            eventToModify.modifyEnd(modifiedEvent.getEnd());    //Because it already does at the creation
            return Response.ok("Event modified successfully.").build();
        });
    }

    @Path("/getEventsBetween/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> getEventBetween(@PathParam("userId") long userId, @QueryParam("start") String firstDate, @QueryParam("end") String secondDate){
        return Uni.createFrom().item(() -> {
            try {
                var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                df.setTimeZone(TimeZone.getTimeZone("UTC")); //set UTC timezone to avoid time difference
                var start = df.parse(firstDate);
                var end = df.parse(secondDate);
                return Response.ok(eventRepository.getEventsBetweenDates(userId, start, end)).build();
            }
            catch(ParseException e){
                return Response.status(400, "Wrond date format.").build();
            }
        });
    }

    @Path("/getEventsFromUserId/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllEventsFromUserId(@PathParam("userId") long userId){
        return Uni.createFrom().item(Response.ok(eventRepository.listAllByUserId(userId)).build());
    }

    @Path("/delete/{eventId}")
    @DELETE
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> deleteEventFromEventId(@PathParam("eventId") long eventId){
        return Uni.createFrom().item(() -> {
            var eventToDelete = eventRepository.getEventById(eventId);
            if(eventToDelete == null){
                return Response.status(404, "Event with eventId "+ eventId +" not found.").build();
            }
            eventRepository.delete(eventToDelete);
            return Response.ok("Event deleted.").build();
        });
    }

    @Path("/createEvent")
    @POST
    @Transactional
    @Produces
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createEvent(Event event){
        return Uni.createFrom().item(() -> {
            eventRepository.persist(event);
            if(!eventRepository.isPersistent(event)) {
                eventRepository.delete(event);
                return Response.status(304, "Event not added to database.").build();
            }
            return Response.status(201, "Event created.").build();
        });
    }

    @Path("/newUser")
    @POST
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createUser(User user){
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            try {
                user.setPassword(crypt.encrypt(user.getPassword()));
            } catch (NoSuchAlgorithmException e){
                return Response.status(500, "Password encryption error.").build();
            }
            userRepository.persist(user);
            if(!userRepository.isPersistent(user)){
                return Response.status(304, "User not added to database.").build();
            }
            return Response.status(201, "User created successfully").build();
        }));
    }

    //THIS METHOD IS FOR TESTS ONLY
    @Path("/newUsers")
    @POST
    @Transactional
    @Produces
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<List<User>> createUsers(List<User> users){
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            for(var user : users) {
                try {
                    user.setPassword(crypt.encrypt(user.getPassword()));
                } catch (NoSuchAlgorithmException e) {
                    throw new BadRequestException("Password encryption error");
                }
                userRepository.persist(user);
            }
            return users;
        }));
    }

    @Path("/login")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> checkLogin(User user){
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            var cryptedPassword = "";
            try {
                cryptedPassword = crypt.encrypt(user.getPassword());

            } catch (NoSuchAlgorithmException e){
                return Response.status(500, "Password encryption error.").build();
            }
            var userToLog = userRepository.checkUsernameAndPassword(user.getUsername(), cryptedPassword);
            if(userToLog != null){
                return Response.ok(userToLog.getUserId()).build();
            }
            return Response.status(406, "Wrong username or password").build();
        }));
    }

    @Path("/getUser/{username}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getUserFromUsername(@PathParam("username") String username){
        return Uni.
                createFrom().item(() -> {
                    var user = userRepository.getUserByUsername(username);
                    return Response.ok(Objects.requireNonNullElse(user, "{}")).build();
                });
    }

    @Path("/GoogleCalendarCallback/{userId}")
    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> getEventFromGoogleCalendarAccount(@PathParam("userId") long userId) throws GeneralSecurityException, IOException {
        return Uni.
                createFrom().
                item(GoogleCalendarExtractor.getAllEvent(userId, eventRepository));
    }

    @Path("/iCalendarFile/{userId}")
    @POST
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes
    public Uni<Response> getEventFromIcsFile(File file, @PathParam("userId") long userId) throws FileNotFoundException {
        return Uni.
                createFrom().
                item(ICalExtractor.extractEventsFromIcs(new FileInputStream("src/main/resources/test.ics"), userId, eventRepository));
    }

    @Path("/iCalendarUrlFile/{userId}")
    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> getEventFromUrlIcs(@QueryParam("url") String url, @PathParam("userId") long userId) {
        return Uni.
                createFrom().
                item(() -> {
                    try {
                        return ICalExtractor.extractEventsFromIcs(new URL(url).openStream(), userId, eventRepository);
                    }
                    catch(IOException e){
                        return Response.status(404, "ics file not found in url").build();
                    }
                });
    }
}