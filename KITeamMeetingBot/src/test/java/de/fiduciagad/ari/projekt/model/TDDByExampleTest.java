package de.fiduciagad.ari.projekt.model;

import static de.fiduciagad.ari.projekt.model.TDDByExampleTest.Event.builder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

public class TDDByExampleTest
{

    public static class Event
    {

        public static class Builder
        {

            private String bezeichnung;

            public Builder bezeichnung(String bezeichnung)
            {
                this.bezeichnung = bezeichnung;
                return this;
            }

            public Event build()
            {
                return new Event(bezeichnung);
            }

        }

        private String bezeichnung;
        private String foo;

        public Event(String bezeichnung)
        {
            this.bezeichnung = bezeichnung;
        }

        public String getFoo()
        {
            return foo;
        }

        public String getBezeichnung()
        {
            return bezeichnung;
        }

        @Override
        public String toString()
        {
            return "Event [bezeichnung=" + bezeichnung + "]";
        }

        public static Comparator<Event> compareUsingFooBar()
        {
            Comparator<String> nullSafe = nullsFirst(naturalOrder());
            return comparing(Event::getBezeichnung, nullSafe).thenComparing(Event::getFoo, nullSafe);
        }

        public static Builder builder()
        {
            return new Builder();
        }
    }

    @Test
    public void einEventInListeBleibtInListe()
    {
        Event event = builder().bezeichnung("Event1").build();
        List<Event> events = removeDuplicates(events(event));
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event));

    }

    @Test
    public void beiZweiIdentischenEventsWirdDasZweiteAusListeEntfernt()
    {
        Event event = builder().bezeichnung("Event1").build();;
        List<Event> events = removeDuplicates(events(event, builder().bezeichnung("Event1").build()));
        assertThat(events.size(), is(1));
        assertThat(events.get(0), sameInstance(event));
    }


    private List<Event> removeDuplicates(List<Event> events)
    {
        TreeSet<Event> treeSet = new TreeSet<Event>(Event.compareUsingFooBar());
        treeSet.addAll(events);
        return new ArrayList<Event>(treeSet);
    }

    private List<Event> events(Event... events)
    {
        return new ArrayList<Event>(Arrays.asList(events));
    }

}
