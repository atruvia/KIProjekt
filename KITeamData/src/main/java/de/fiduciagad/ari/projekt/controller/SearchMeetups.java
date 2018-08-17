package de.fiduciagad.ari.projekt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.fiduciagad.ari.projekt.model.MyEvent;

public class SearchMeetups
{
    private static final Logger log = LogManager.getLogger("Main");

    private MyEvent n_events;
    private Initializer init;

    public SearchMeetups(MyEvent n_events, Initializer init)
    {
        this.n_events = n_events;
        this.init = init;
    }
    public void searchNewMeetups()
    {
        try
        {
            URL url = new URL("http://api.meetup.com/find/upcoming_events?photo-host=public&key="
                + init.getMeetupApiKey() + "&page=1000&order=time");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setConnectTimeout(20000);
            connect.setReadTimeout(20000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));

            JsonArray tagungsorte = new JsonArray();
            JsonArray gruppen = new JsonArray();
            JsonArray events = new JsonArray();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(in, JsonObject.class);
            events = json.getAsJsonArray("events");


            for (int j = 0; j < events.size(); j++)
            {
                JsonObject event = events.get(j).getAsJsonObject();
                tagungsorte.add(event.get("venue"));
                gruppen.add(event.get("group"));
            }
            for (int i = 0; i < events.size(); i++)
            {
                JsonObject event = events.get(i).getAsJsonObject();
                JsonObject tagungsort;
                if (tagungsorte.get(i).isJsonNull())
                {
                    tagungsort = new JsonObject();
                    tagungsort.addProperty("city", "nicht sichtbar");
                    tagungsort.addProperty("address_1", "nicht sichtbar");
                }
                else
                {
                    tagungsort = tagungsorte.get(i).getAsJsonObject();
                }
                JsonObject gruppe = gruppen.get(i).getAsJsonObject();
                String sTime;
                String eTime;
                String day;
                String desc;
                if (event.get("local_time") != null)
                {
                    day = event.get("local_date").getAsString();
                    String[] startTime = event.get("local_time").getAsString().split(":");
                    sTime = berechneStundenStart(startTime) + ":" + startTime[1];

                    if (event.get("duration") != null)
                    {
                        int endTime = event.get("duration").getAsInt();
                        int endTimeMin = (endTime / 1000) / 60;
                        int endTimeH = (int) (endTimeMin / 60);
                        int endTimeM = (int) (endTimeMin % 60);
                        eTime = berechneStunden(startTime, endTimeH) + ":" + berechneMinuten(startTime, endTimeM);
                    }
                    else
                    {
                        eTime = "";
                    }
                }
                else
                {
                    sTime = "";
                    eTime = "";
                    day = "";
                }
                if (event.get("description") == null)
                {
                    desc = "";
                }
                else
                {
                    desc = event.get("description").getAsString();
                }
                n_events.addNewEvent(
                    new MyEvent(gruppe.get("name").getAsString(), event.get("name").getAsString(), sTime, day, eTime,
                        day, tagungsort.get("address_1").getAsString() + ", " + tagungsort.get("city").getAsString(),
                        desc, event.get("link").getAsString()));
            }
            in.close();
        }
        catch (MalformedURLException e1)
        {
            log.error("MalformedURLException in SearchMeetups", e1.getMessage());
        }
        catch (IOException e)
        {
            log.error("IOException in SearchMeetups", e.getMessage());
        }
    }
    public void searchOldMeetups()
    {
        try
        {
            URL url = new URL("http://api.meetup.com/2/open_events?photo-host=public&key=" + init.getMeetupApiKey()
                + "&country=DE&city=Karlsruhe&status=past&page=4000&category=34");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setConnectTimeout(20000);
            connect.setReadTimeout(20000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));

            JsonArray tagungsorte = new JsonArray();
            JsonArray gruppen = new JsonArray();
            JsonArray events = new JsonArray();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(in, JsonObject.class);
            events = json.getAsJsonArray("results");


            for (int j = 0; j < events.size(); j++)
            {
                JsonObject event = events.get(j).getAsJsonObject();
                tagungsorte.add(event.get("venue"));
                gruppen.add(event.get("group"));
            }
            for (int i = 0; i < events.size(); i++)
            {
                JsonObject event = events.get(i).getAsJsonObject();
                JsonObject tagungsort;
                if (tagungsorte.get(i).isJsonNull())
                {
                    tagungsort = new JsonObject();
                    tagungsort.addProperty("city", "nicht sichtbar");
                    tagungsort.addProperty("address_1", "nicht sichtbar");
                }
                else
                {
                    tagungsort = tagungsorte.get(i).getAsJsonObject();
                }
                JsonObject gruppe = gruppen.get(i).getAsJsonObject();
                String sTime;
                String eTime;
                String day;
                String desc;
                if (event.get("local_time") != null)
                {
                    day = event.get("local_date").getAsString();
                    String[] startTime = event.get("local_time").getAsString().split(":");
                    sTime = berechneStundenStart(startTime) + ":" + startTime[1];

                    if (event.get("duration") != null)
                    {
                        int endTime = event.get("duration").getAsInt();
                        int endTimeMin = (endTime / 1000) / 60;
                        int endTimeH = (int) (endTimeMin / 60);
                        int endTimeM = (int) (endTimeMin % 60);
                        eTime = berechneStunden(startTime, endTimeH) + ":" + berechneMinuten(startTime, endTimeM);
                    }
                    else
                    {
                        eTime = "";
                    }
                }
                else
                {
                    sTime = "";
                    eTime = "";
                    day = "";
                }
                if (event.get("description") == null)
                {
                    desc = "";
                }
                else
                {
                    desc = event.get("description").getAsString();
                }
                n_events.addNewEvent(
                    new MyEvent(gruppe.get("name").getAsString(), event.get("name").getAsString(), sTime, day, eTime,
                        day, tagungsort.get("address_1").getAsString() + ", " + tagungsort.get("city").getAsString(),
                        desc, event.get("event_url").getAsString()));
            }
            in.close();
        }
        catch (MalformedURLException e1)
        {
            log.error("MalformedURLException in SearchMeetups", e1.getMessage());
        }
        catch (IOException e)
        {
            log.error("IOException in SearchMeetups", e.getMessage());
        }
    }
    private String berechneStundenStart(String[] startTime)
    {
        if (Integer.parseInt(startTime[0]) < 10)
        {
            return "0" + String.valueOf(Integer.parseInt(startTime[0]));
        }
        return String.valueOf(Integer.parseInt(startTime[0]));
    }

    private String berechneMinuten(String[] startTime, int time)
    {
        if ((Integer.parseInt(startTime[1]) + time) < 10)
        {
            return "0" + String.valueOf(Integer.parseInt(startTime[1]) + time);
        }
        return Integer.toString(Integer.parseInt(startTime[1]) + time);
    }
    private String berechneStunden(String[] startTime, int time)
    {
        if ((Integer.parseInt(startTime[0]) + time) < 10)
        {
            return "0" + String.valueOf(Integer.parseInt(startTime[0]) + time);
        }
        return Integer.toString((Integer.parseInt(startTime[0])) + time);
    }
}
