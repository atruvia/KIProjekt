package de.fiduciagad.ari.projekt.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Initializer
{
    private static final Logger log = LogManager.getLogger("Main");

    private String proxyHost;
    private String proxyPort;
    private String google_App_Name;
    private String credentialsParentPathName;
    private String credentialsChildPathName;
    private String kiTeamGoogleCalendar;
    private String itTermineKaGoogleCalendar;
    private String meetupApiKey;
    private String client_secret;
    private JsonObject bewertungskriterien;

    @Option(name = "-i", aliases = "--initialize", usage = "Initialize Application", required = true)
    private File init;

    @Argument
    public List<String> arguments = new ArrayList<String>();

    public void initialize()
    {
        Gson gson = new Gson();
        try
        {
            JsonObject ini = gson.fromJson(new FileReader(init), JsonObject.class);
            proxyPort = ini.get("proxyPort").getAsString();
            proxyHost = ini.get("proxyHost").getAsString();
            google_App_Name = ini.get("Google_App_Name").getAsString();
            credentialsParentPathName = ini.get("credentialsParentPathName").getAsString();
            credentialsChildPathName = ini.get("credentialsChildPathName").getAsString();
            kiTeamGoogleCalendar = ini.get("KiTeamGoogleCalendar").getAsString();
            itTermineKaGoogleCalendar = ini.get("ItTermineKaGoogleCalendar").getAsString();
            meetupApiKey = ini.get("MeetupApiKey").getAsString();
            bewertungskriterien = ini.get("bewertungskriterien").getAsJsonObject();
            client_secret =  ini.get("client_secret").toString();
        }
        catch (JsonSyntaxException e)
        {
            log.error("JsonSyntaxException beim initialisieren", e.getMessage());
        }
        catch (JsonIOException e)
        {
            log.error("JsonIOException beim initialisieren", e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            log.error("ini.json nicht gefunden", e.getMessage());
        }
    }
    public String getProxyHost()
    {
        return proxyHost;
    }
    public String getProxyPort()
    {
        return proxyPort;
    }
    public String getGoogle_App_Name()
    {
        return google_App_Name;
    }
    public String getCredentialsParentPathName()
    {
        return credentialsParentPathName;
    }
    public String getCredentialsChildPathName()
    {
        return credentialsChildPathName;
    }
    public String getKiTeamGoogleCalendar()
    {
        return kiTeamGoogleCalendar;
    }
    public String getItTermineKaGoogleCalendar()
    {
        return itTermineKaGoogleCalendar;
    }
    public JsonObject getBewertungskriterien()
    {
        return bewertungskriterien;
    }
    public String getClient_secret()
    {
        return client_secret;
    }
    public String getMeetupApiKey()
    {
        return meetupApiKey;
    }
}
