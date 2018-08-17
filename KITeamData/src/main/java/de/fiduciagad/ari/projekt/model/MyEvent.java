package de.fiduciagad.ari.projekt.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.fiduciagad.ari.projekt.controller.GoogleAuth;
import de.fiduciagad.ari.projekt.controller.MyEventBuilder;

public class MyEvent
{
    private static final Logger log = LogManager.getLogger("Main");
    private com.google.api.services.calendar.Calendar authService;
    private List<MyEvent> newEvents = new ArrayList<>();
    private List<MyEvent> calEvents = new ArrayList<>();

    private String datumEnde;
    private String uhrzeitEnde;
    private String gruppenName;
    private String name;
    private String uhrzeitBeginn;
    private String datumBeginn;
    private String url;
    private String ort;
    private String desc;
    private int score = 0;
    private boolean speichern = true;

    public MyEvent(String gruppenName, String name, String datumUndBeginn, String datumUndEnde, String ort,
        String details, String url)
    {
        if (datumUndBeginn.contains("T"))
        {
            String[] temp = datumUndBeginn.split("T");
            String[] temp2 = temp[1].split(":00.000");
            this.uhrzeitBeginn = temp2[0];
            this.datumBeginn = temp[0];
            String[] temp3 = datumUndEnde.split("T");
            String[] temp4 = temp3[1].split(":00.000");
            this.uhrzeitEnde = temp4[0];
            this.datumEnde = temp3[0];
        }
        else
        {
            this.datumBeginn = datumUndBeginn;
            this.uhrzeitBeginn = "";
            this.datumEnde = datumUndEnde;
            this.uhrzeitEnde = "";
        }
        this.name = name;
        this.ort = ort;
        this.desc = details;
        this.gruppenName = gruppenName;
        this.url = url;
    }
    public MyEvent(String gruppenName, String name, String uhrzeitBeginn, String datumBeginn, String uhrzeitEnde,
        String datumEnde, String ort, String details, String url)
    {
        this.datumBeginn = datumBeginn;
        this.uhrzeitBeginn = uhrzeitBeginn;
        this.uhrzeitEnde = uhrzeitEnde;
        this.datumEnde = datumEnde;
        this.name = name;
        this.ort = ort;
        this.desc = details;
        this.gruppenName = gruppenName;
        this.url = url;
    }
    public String getDatumEnde()
    {
        return datumEnde;
    }

    public String getUhrzeitEnde()
    {
        return uhrzeitEnde;
    }
    public String getGruppenName()
    {
        return gruppenName;
    }
    public String getName()
    {
        return name;
    }
    public String getUhrzeitBeginn()
    {
        return uhrzeitBeginn;
    }
    public String getDatumBeginn()
    {
        return datumBeginn;
    }
    public String getUrl()
    {
        return url;
    }
    public String getOrt()
    {
        return ort;
    }
    public String getDesc()
    {
        return desc;
    }
    public int getScore()
    {
        return score;
    }
    public void setScore(int score)
    {
        this.score = score;
    }
    public boolean isSpeichern()
    {
        return speichern;
    }
    public void setSpeichern(boolean speichern)
    {
        this.speichern = speichern;
    }
    public static MyEventBuilder builder()
    {
        return new MyEventBuilder();
    }
    public List<MyEvent> events(MyEvent... events)
    {
        return new ArrayList<MyEvent>(Arrays.asList(events));
    }
    public List<MyEvent> getNewEvents()
    {
        return newEvents;
    }

    public void addNewEvent(MyEvent gEvent)
    {
        newEvents.add(gEvent);
    }
    public List<MyEvent> getKiTeamCalendarEvents()
    {
        return calEvents;
    }
    public void addKiTeamCalendarEvent(MyEvent gEvent)
    {
        calEvents.add(gEvent);
    }
    public void setNewEvents(List<MyEvent> newEvents)
    {
        this.newEvents = newEvents;
    }
    public List<MyEvent> removeDuplicates(List<MyEvent> events, List<MyEvent> calEvents)
    {
        List<MyEvent> events2 = new ArrayList<>();
        events2.addAll(events);
        return new ArrayList<MyEvent>(compareAndDelete(events2, calEvents));
    }
    private List<MyEvent> compareAndDelete(List<MyEvent> events, List<MyEvent> calEvents)
    {
        for (int i = 0; i < events.size(); i++)
        {
            MyEvent e1 = events.get(i);
            for (int j = 0; j < events.size(); j++)
            {
                MyEvent e2 = events.get(j);
                if (i != j && sindGleich(e1, e2) && e2.isSpeichern())
                {
                    e1.setSpeichern(false);
                }
            }
        }
        if (calEvents != null)
        {
            for (int i = 0; i < calEvents.size(); i++)
            {
                MyEvent e1 = calEvents.get(i);
                for (int j = 0; j < events.size(); j++)
                {
                    MyEvent e2 = events.get(j);
                    if (sindGleich(e1, e2))
                    {
                        e2.setSpeichern(false);
                    }
                }
            }
        }
        for (int i = 0; i < events.size(); i++)
        {
            if (!events.get(i).isSpeichern())
            {
                events.remove(events.get(i));
            }
        }
        return events;
    }
    private boolean sindGleich(MyEvent e1, MyEvent e2)
    {
        return (e2.getName().equals(e1.getName()) || e2.getName().contains(e1.getName())
            || e1.getName().contains(e2.getName())) && e1.getDatumBeginn().equals(e2.getDatumBeginn())
            && e1.getUhrzeitBeginn().equals(e2.getUhrzeitBeginn());
    }
    public void reviewAndSort(JsonObject bewertungskriterienJson)
    {
        Set<Entry<String, JsonElement>> bewertungskriterien = bewertungskriterienJson.entrySet();
        for (MyEvent event : newEvents)
        {
            int score = 0;
            for (Entry<String, JsonElement> entry : bewertungskriterien)
            {
                if (event.getName().contains(entry.getKey()))
                {
                    score += entry.getValue().getAsInt();
                }
                if (event.getDesc().contains(entry.getKey()))
                {
                    score += entry.getValue().getAsInt();
                }
                if (event.getOrt().contains(entry.getKey()))
                {
                    score += entry.getValue().getAsInt();
                }
            }
            event.setScore(score);
        }
        Collections.sort(newEvents, Comparator.comparing(MyEvent::getScore));
    }
   
    public void writeJson()
    {
        try (Writer writer = new FileWriter("C:\\Users\\xcg2060\\Desktop\\testdatei.json"))
        {
            Gson gson = new GsonBuilder().create();
            gson.toJson(newEvents, writer);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void entscheidenObSpeichern()
    {
        for (MyEvent e : newEvents)
        {
            if (e.getScore() >= 15)
            {
                e.setSpeichern(true);
            }
            else
            {
                e.setSpeichern(false);
            }
        }
    }
}

