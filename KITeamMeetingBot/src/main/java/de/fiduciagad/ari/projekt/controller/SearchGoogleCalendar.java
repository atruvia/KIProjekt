package de.fiduciagad.ari.projekt.controller;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import de.fiduciagad.ari.projekt.model.MyEvent;

public class SearchGoogleCalendar
{
    private static final Logger log = LogManager.getLogger("Main");
    private com.google.api.services.calendar.Calendar authService;
    private MyEvent allEvents;
    private Initializer init;

    public SearchGoogleCalendar(MyEvent allEvents, Initializer init, GoogleAuth auth)
    {
        this.allEvents = allEvents;
        this.init = init;
        try
        {
            authService = auth.getCalendarService();
        }
        catch (IOException e)
        {
            log.error("AuthService Fehler: ", e.getMessage());
        }
    }

    public void search()
    {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events;
        try
        {
            events = authService.events().list(init.getKiTeamGoogleCalendar()).setTimeMin(now).setOrderBy(
                "startTime").setSingleEvents(true).execute();
            List<Event> items = events.getItems();
            if (items.size() != 0)
            {
                hinzufuegenKalendareintraege(items, "KI-Team");
            }
            events = authService.events().list(init.getItTermineKaGoogleCalendar()).setTimeMin(now).setOrderBy(
                "startTime").setSingleEvents(true).execute();
            List<Event> items2 = events.getItems();
            if (items.size() != 0)
            {
                hinzufuegenKalendareintraege(items2, "IT-Termine");
            }
        }
        catch (IOException e)
        {
            log.error("Google Kalendar konnten nicht geladen werden", e.getMessage());
        }
    }

    private void hinzufuegenKalendareintraege(List<Event> items, String name)
    {
        for (Event event : items)
        {
            String desc = "";
            String start;
            String end;
            String ort = "";
            if (event.containsKey("description"))
            {
                desc = event.getDescription();
            }
            if (event.containsKey("location"))
            {
                ort = event.getLocation();
            }
            if (event.getStart().containsKey("dateTime"))
            {
                start = event.getStart().getDateTime().toString();
                end = event.getEnd().getDateTime().toString();
            }
            else
            {
                start = event.getStart().getDate().toString();
                end = event.getEnd().getDate().toString();
            }
            if (name == "KI-Team")
            {
                allEvents.addKiTeamCalendarEvent(
                    new MyEvent(name, event.getSummary(), start, end, ort, desc, event.getHtmlLink()));
            }
            else
            {
                allEvents.addNewEvent(
                    new MyEvent(name, event.getSummary(), start, end, ort, desc, event.getHtmlLink()));
            }
        }
    }
}
