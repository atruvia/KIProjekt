package de.fiduciagad.ari.projekt.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import de.fiduciagad.ari.projekt.model.MyEvent;

public class NewEventTest
{

    @Test
    public void einEventInListeBleibtInListe()
    {
        MyEvent event = MyEvent.builder().name("Event1").gruppe("Gruppe1").build();
        List<MyEvent> events = event.removeDuplicates(event.events(event), null);
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event));
    }
    @Test
    public void beiZweiIdentischenEventsInNameUndEventZeitWirdDasErsteAusListeEntfernt()
    {
        MyEvent event = MyEvent.builder().name("Event1").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
            "11.06.2018").startDatum("11.06.2018").build();
        MyEvent event2 = MyEvent.builder().name("Event1").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
            "11.06.2018").startDatum("11.06.2018").build();
        List<MyEvent> events = event.removeDuplicates(event.events(event, event2), null);
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event2));
    }
    @Test
    public void beiZweiSichWiederholendenEventsInNameUndEventZeitWirdKeinEventAusListeEntfernt()
    {
        MyEvent event = MyEvent.builder().name("Event1").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
            "11.06.2018").startDatum("11.06.2018").build();
        List<MyEvent> events = event.removeDuplicates(
            event.events(event, MyEvent.builder().name("Event1").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
                "13.06.2018").startDatum("13.06.2018").build()),
            null);
        assertThat(events.size(), is(2));
    }
    @Test
    public void beiZweiIdentischenEventsBeiDemEinsDenNamenDesAnderenBeinhaltettWirdDasErsteAusListeEntfernt()
    {
        MyEvent event = MyEvent.builder().name("Eventgruppe: Event1").startUhrzeit("11:00").endUhrzeit(
            "12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event2 = MyEvent.builder().name("Event1").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
            "11.06.2018").startDatum("11.06.2018").build();
        List<MyEvent> events = event.removeDuplicates(event.events(event, event2), null);
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event2));
    }
    @Test
    public void beiSchonEingetragenenEventWirdEventAusListeEntferntWobeiEventNameEvent3NameBeinhaltet()
    {
        MyEvent event = MyEvent.builder().gruppe("Meetup").name("GDG Karlsruhe: Google I/O Extended 2018").startUhrzeit(
            "11:00").endUhrzeit("12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event2 = MyEvent.builder().gruppe("Meetup").name("Event2").startUhrzeit("11:00").endUhrzeit(
            "12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event3 = MyEvent.builder().gruppe("KI-Team").name("Google I/O Extended 2018").startUhrzeit(
            "11:00").endUhrzeit("12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        List<MyEvent> events = event.removeDuplicates(event.events(event, event2), event3.events(event3));
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event2));
    }
    @Test
    public void beiSchonEingetragenenEventWirdEventAusListeEntferntWobeiEvent3NameEventNameBeinhaltet()
    {
        MyEvent event = MyEvent.builder().gruppe("Meetup").name("Google I/O Extended 2018").startUhrzeit(
            "11:00").endUhrzeit("12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event2 = MyEvent.builder().gruppe("Meetup").name("Event2").startUhrzeit("11:00").endUhrzeit(
            "12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event3 = MyEvent.builder().gruppe("KI-Team").name(
            "GDG Karlsruhe: Google I/O Extended 2018").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
                "11.06.2018").startDatum("11.06.2018").build();
        List<MyEvent> events = event.removeDuplicates(event.events(event, event2), event3.events(event3));
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event2));
    }
    @Test
    public void beiSchonEingetragenenEventWirdNichtsNeuesEingetragen()
    {
        MyEvent event = MyEvent.builder().gruppe("Meetup").name("Cloud & Big Data im Entwicklungsalltag").startUhrzeit(
            "11:00").endUhrzeit("12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event2 = MyEvent.builder().gruppe("Meetup").name("Cloud & Big Data im Entwicklungsalltag").startUhrzeit(
            "11:00").endUhrzeit("12:00").endDatum("11.06.2018").startDatum("11.06.2018").build();
        MyEvent event3 = MyEvent.builder().gruppe("KI-Team").name(
            "Cloud & Big Data im Entwicklungsalltag").startUhrzeit("11:00").endUhrzeit("12:00").endDatum(
                "11.06.2018").startDatum("11.06.2018").build();
        event.removeDuplicates(event.events(event, event2), event3.events(event3));
        assertThat(event.getNewEvents().size(), is(0));
    }
    @Test
    public void bewerteEvent() throws FileNotFoundException, JsonSyntaxException, JsonIOException, UnsupportedEncodingException
    {
        Gson gson = new Gson();
        JsonObject json = null;
        InputStream in = new FileInputStream(new File("src/test/resources/bewertungskriterienTest.json"));
        json = gson.fromJson(new BufferedReader(new InputStreamReader(in, "UTF-8")), JsonObject.class);
        MyEvent event = MyEvent.builder().gruppe("Meetup").name("Event1").startUhrzeit("11:00").endUhrzeit(
            "12:00").endDatum("11.06.2018").startDatum("11.06.2018").desc("Software").anschrift("2").build();
        event.addNewEvent(event);
        event.reviewAndSort(json);
        assertThat(event.getScore(), is(1));
    }
}
