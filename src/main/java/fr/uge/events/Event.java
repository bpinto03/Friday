package fr.uge.events;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.ws.rs.ClientErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private long eventId;
    @Column(nullable = false)
    private long userId;
    private String title;
    private String description;
    private String location;
    @Column(nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm")
    private Date start;
    @Column(nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm")
    private Date end;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private String recurrency;

    public Event(long userId, String title, String description, String location, Date start, Date end, String recurrenceRules){
        this.setUserId(userId);
        this.setTitle(title);
        this.setDescription(description);
        this.setLocation(location);
        this.modifyBegin(start);
        this.modifyEnd(end);
        this.setRecurrency(recurrenceRules);
    }

    public Event() {} //Used by hibernate

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNullElse(title, "No title");
    }

    public Date getStart() {
        return start;
    }

    public void setStart(String begin) throws ParseException {
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC")); //set UTC timezone to avoid time difference
        var beginDate = df.parse(begin);
        if(end != null && beginDate.compareTo(end) > 0){
            throw new ClientErrorException(406);
        }
        this.start = beginDate;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(String end) throws ParseException {
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));    //set UTC timezone to avoid time difference
        var endDate = df.parse(end);
        if(start != null && endDate.compareTo(start) < 0){
            throw new ClientErrorException(406);
        }
        this.end = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = Objects.requireNonNullElse(location, "");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNullElse(description, "");
    }

    public String getRecurrency() {
        return recurrency;
    }

    public void setRecurrency(String stringRecurrency) {
        this.recurrency = Objects.requireNonNullElse(stringRecurrency, "");
    }

    public void modifyBegin(Date begin) {
        this.start = Objects.requireNonNull(begin);
    }

    public void modifyEnd(Date end){
        this.end = Objects.requireNonNull(end);
    }
}
