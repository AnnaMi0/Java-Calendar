package com.pckg.oop2ndpart;

import javax.swing.*;
import java.time.LocalDateTime;

public class Project extends myEvent implements eventsInterface{
    private boolean status; //true if finished, false if not

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    /**
     * basic constructor using all parameters
     */
    public Project(LocalDateTime deadline, String title, String description, boolean status, String uid) {
        super(deadline, title, description,uid);
        this.status=status;
    }

    public static void add(myEvent e, myCalendar calendar){
        String uid = ICSFile.generateUniqueUid();
        //by default a new project's status is unfinished
        Project new_Project = new Project(e.getDateandTime(),e.getTitle(),e.getDescription(),false,uid);
        calendar.getEventsList().add(new_Project);
        JOptionPane.showMessageDialog(null, "Project added!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public String getEventClass(){
        return "project";
    }


    @Override
    public String printEvent() {
        String status = this.status ? "finished" : "unfinished";
        return super.printEvent() + "\nStatus:" + status;
    }


    @Override
    public void update(){
        eventsInterface.editEventWindow("Update Project", this);
    }

}
