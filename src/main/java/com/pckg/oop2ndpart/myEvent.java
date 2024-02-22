package com.pckg.oop2ndpart;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Event class: used as a superclass by Project and Appointment
 * groups together date&time, title and description of event
 */

public class myEvent implements eventsInterface{
    private LocalDateTime DateandTime;//date and time object, when inherited by Project it is used as deadline
    private String title;
    private String description;
    private final LocalDate Date; //used for events that don't have time
    boolean hasTime;
    private final String uid;

    /**
     * constructor used for events with time
     */
    public myEvent(LocalDateTime dateandTime, String title, String description, String uid) {
        DateandTime = dateandTime;
        this.title = title;
        this.description = description;
        this.hasTime = true;
        this.uid = uid;
        Date = null;
    }
    /**
     * constructor used for events without time
     */
    public myEvent(String title, String description, LocalDate date, String uid) {
        this.title = title;
        this.description = description;
        Date = date;
        this.hasTime = false;
        DateandTime = date.atStartOfDay();
        this.uid = uid;
    }

    public LocalDateTime getDateandTime() {
        return DateandTime;
    }

    public void setDateandTime(LocalDateTime dateandTime) {
        DateandTime = dateandTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return Date;
    }


    public String getUid() {
        return uid;
    }


    public String getEventClass(){
        if(this.hasTime){
            return "event";
        }else {
            return "no-time event";
        }
    }

    public static int getEventTypeINT(myEvent e){ //return INT according to type of Event (useful for switch statements)
        if(e.getEventClass().equals("event")){
            return 0;
        } else if (e.getEventClass().equals("no-time event")) {
            return 1;
        } else if (e.getEventClass().equals("appointment")) {
            return 2;
        } else if (e.getEventClass().equals("project")) {
            return 3;
        }
        return -1;
    }


    public String printEvent(){
        String datetime;
        if(this.hasTime){
            datetime = "Date:" + this.getDateandTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'Time:' HH:mm:ss"));
        } else {
            datetime = "Date:" + this.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        return "Summary: " + this.title+"\nDescription: " + this.description + "\nType: " + this.getEventClass() + "\n" + datetime;
    }

    public void update() {
        JOptionPane.showMessageDialog(null, "Can't edit these type of events", "Error", JOptionPane.ERROR_MESSAGE);
    }

}