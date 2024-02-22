package com.pckg.oop2ndpart;

import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import java.util.LinkedList;
import java.nio.file.Path;
import java.nio.file.Paths;

public class myCalendar {
    private final LinkedList<myEvent> eventsList; //all events of a calendar
    private final ProdId calTitle;
    private final CalScale calscale;

    private String filePath;

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    public String getFilePath(){
        return filePath;
    }

    public static String getFileNameOfPath(String filePath){
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }

    public LinkedList<myEvent> getEventsList() {
        return eventsList;
    }

    public ProdId getCalTitle() {
        return calTitle;
    }

    public CalScale getCalscale() {
        return calscale;
    }

    /**
     * Constructor using ProdID and CalScale. initializes the eventslist too.
     */
    public myCalendar(ProdId calTitle, CalScale calscale) {
        this.calTitle = calTitle;
        this.calscale = calscale;
        this.eventsList = new LinkedList<>();
    }

    /**
     * Default constructor, for when creating a new calendar
     */
    public myCalendar() {
        this.eventsList = new LinkedList<>();
        this.calscale = new CalScale(CalScale.VALUE_GREGORIAN); //gregorian calendar by default
        this.calTitle = new ProdId("-//My new calendar - Java OOP2//EN");
    }

}


