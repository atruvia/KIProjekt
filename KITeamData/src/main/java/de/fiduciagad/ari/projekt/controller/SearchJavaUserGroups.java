package de.fiduciagad.ari.projekt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.fiduciagad.ari.projekt.model.MyEvent;

public class SearchJavaUserGroups
{
    public void searchKa(MyEvent event)
    {
        try
        {
            Document doc = Jsoup.connect("http://jug-karlsruhe.de/events/").get();
            String hrefs = doc.select("a").eachAttr("href").toString();
            String[] hreflist = hrefs.split(", ");
            List<String> eventlist = new ArrayList<>();
            for (String e : hreflist)
            {
                if (e.contains("content"))
                {
                    eventlist.add(e);
                }
            }
            for (String e : eventlist)
            {
                Document doc2 = Jsoup.connect("http://jug-karlsruhe.de" + e).get();
                event.addNewEvent(
                    new MyEventBuilder().desc(doc2.select("div div div p").eachText().toString()).name("").anschrift(
                        "").build());
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void searchKL(MyEvent event)
    {
        try
        {
            Document doc = Jsoup.connect("http://www.jug-kl.de/events/").get();
            String hrefs = doc.select("ul li p a").eachAttr("href").toString();
            String[] hreflist = hrefs.split(", ");
            for (String e : hreflist)
            {
                Document doc2 = Jsoup.connect("http://www.jug-kl.de" + e.replace("[", "").replace("]", "")).get();
                event.addNewEvent(
                    new MyEventBuilder().desc(doc2.select("#center-panel").eachText().toString()).name("").anschrift(
                        "").build());
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
