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
        Events events;
        try
        {
            events = authService.events().list("v1g4a4eq9en3gh5vn47tmth5cc@group.calendar.google.com").setSingleEvents(true).execute();
            List<Event> items1 = events.getItems();
            if (items1.size() != 0)
            {
                hinzufuegenKalendareintraege(items1, "Stuttgart-Termine");
            }
            events = authService.events().list(init.getItTermineKaGoogleCalendar()).setSingleEvents(true).execute();
            List<Event> items2 = events.getItems();
            if (items2.size() != 0)
            {
                hinzufuegenKalendareintraege(items2, "IT-Termine");
            }
            events = authService.events().list("niele@jugs.org").setSingleEvents(true).execute();
            List<Event> items3 = events.getItems();
            if (items3.size() != 0)
            {
                hinzufuegenKalendareintraege(items3, "Stuttgart-Termine");
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
            if (event.containsKey("description"))
            {
                desc = event.getDescription();
            }
            
            if (name.isEmpty())
            {
                name = "";
            }
            allEvents.addNewEvent(new MyEventBuilder().desc(desc).name("").anschrift("").build());
        }
    }
}
