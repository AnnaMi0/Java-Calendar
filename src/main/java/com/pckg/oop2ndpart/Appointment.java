package com.pckg.oop2ndpart;

import javax.swing.*;
import java.time.LocalDateTime;


public class Appointment extends myEvent {
    private long duration; //in minutes

    /**
     * constructor for appointments (containing time)
     */
    public Appointment(LocalDateTime dateandTime, String title, String description, long duration, String uid) {
        super(dateandTime, title, description,uid);
        this.duration = duration;
        this.hasTime = true;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    // we get duration from the window
    public static void add(myEvent e, myCalendar calendar, int duration){
        String uid = ICSFile.generateUniqueUid();
        Appointment new_Appointment = new Appointment(e.getDateandTime(),e.getTitle(),e.getDescription(),duration,uid);
        calendar.getEventsList().add(new_Appointment);
        JOptionPane.showMessageDialog(null, "Appointment added!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public String getEventClass(){
        return "appointment";
    }


    @Override
    public String printEvent() {
        long hours = duration/60;
        long minutes = duration - hours*60;
        return super.printEvent() + "\nDuration(Hours:Minutes): " + hours + ":" + minutes;
    }
    

    @Override
    public void update() {
        eventsInterface.editEventWindow("Update Appointment",this);
    }
}