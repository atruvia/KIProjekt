package de.fiduciagad.ari.projekt.controller;

import de.fiduciagad.ari.projekt.model.MyEvent;

public class MyEventBuilder
{
    private String name;
    private String gruppe;
    private String endDatum;
    private String endUhrzeit;
    private String startUhrzeit;
    private String startDatum;
    private String url;
    private String anschrift;
    private String desc;

    public MyEventBuilder name(String name)
    {
        this.name = name;
        return this;
    }
    public MyEventBuilder gruppe(String gruppe)
    {
        this.gruppe = gruppe;
        return this;
    }
    public MyEventBuilder endDatum(String endDatum)
    {
        this.endDatum = endDatum;
        return this;
    }
    public MyEventBuilder startDatum(String startDatum)
    {
        this.startDatum = startDatum;
        return this;
    }
    public MyEventBuilder startUhrzeit(String startUhrzeit)
    {
        this.startUhrzeit = startUhrzeit;
        return this;
    }
    public MyEventBuilder endUhrzeit(String endUhrzeit)
    {
        this.endUhrzeit = endUhrzeit;
        return this;
    }
    public MyEventBuilder url(String url)
    {
        this.url = url;
        return this;
    }
    public MyEventBuilder anschrift(String anschrift)
    {
        this.anschrift = anschrift;
        return this;
    }
    public MyEventBuilder desc(String desc)
    {
        this.desc = desc;
        return this;
    }
    public MyEvent build()
    {
        return new MyEvent(gruppe, name, startUhrzeit, startDatum, endUhrzeit, endDatum, anschrift, desc, url);
    }

}
