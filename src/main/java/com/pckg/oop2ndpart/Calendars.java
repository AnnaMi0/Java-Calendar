package com.pckg.oop2ndpart;

import gr.hua.dit.oop2.calendar.TimeService;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;

public class Calendars {
    private final LinkedList<myCalendar> allCalendars = new LinkedList<>();
    private LinkedList<eventsInterface> allEvents = new LinkedList<>();

    private final LinkedList<eventsInterface> notifiedEvents30 = new LinkedList<>();  //used as collector of events that have been notified (in the next 30 minutes)

    private final LinkedList<eventsInterface> notifiedEvents15 = new LinkedList<>();   //used as collector of events that have been notified (in the next 15 minutes)

    private final LinkedList<eventsInterface> notifiedAll = new LinkedList<>();
    JMenu notificationEmoji = new JMenu();

    public LinkedList<myCalendar> getAllCalendars() {
        return allCalendars;
    }

    public LinkedList<eventsInterface> getAllEvents() {
        return allEvents;
    }

    public void addCalendar(myCalendar calendar) {
        this.allCalendars.add(calendar);
        mergeAndSort();
    }

    // remove calendar when it is needed
    public void removeCalendar(myCalendar calendar){
        this.allCalendars.remove(calendar);
        mergeAndSort(); //refresh allEventslist
        //refresh  notified list
        for(myEvent event : calendar.getEventsList()){
            if(notifiedAll.contains(event)){
                notifiedAll.remove(event);
                notifiedEvents15.remove(event);
                notifiedEvents30.remove(event);
                updateNotificationEmoji();
            }
        }
    }

    // fill a list with the events that user asked for via buttons
    public LinkedList<eventsInterface> presentEvents(mode choice) {
        LinkedList<eventsInterface> eventsList = new LinkedList<>();

        if (allEvents.isEmpty()) {
            return eventsList;
        }

        LocalDateTime now = TimeService.getTeller().now();

        eventsList =
        switch (choice) {
            case all -> this.allEvents;
            case day -> findEventList("future", now, now.with(LocalDateTime.MAX.toLocalTime()));
            case week -> findEventList("future", now, now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalDateTime.MAX.toLocalTime()));
            case month -> findEventList("future", now, now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalDateTime.MAX.toLocalTime()));
            case pastday -> findEventList("past",now, now.with(LocalDateTime.MIN.toLocalTime()));
            case pastweek -> findEventList("past",now, now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalDateTime.MIN.toLocalTime()));
            case pastmonth -> findEventList("past", now, now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalDateTime.MIN.toLocalTime()));
            case todo -> getTodoOrDueEvents("todo",now);
            case due -> getTodoOrDueEvents("due",now);
        };
        return eventsList;
    }

    // find the range of an events list, from which event starts until which
    private LinkedList<eventsInterface> findEventList(String futureOrPast, LocalDateTime now, LocalDateTime time) {
        LinkedList<eventsInterface> result = new LinkedList<>();
        boolean condition;
        for(eventsInterface event : allEvents){

            if (futureOrPast.equals("future")){
                condition = event.getDateandTime().isBefore(time) && event.getDateandTime().isAfter(now);
            } else {
                condition = event.getDateandTime().isAfter(time) && event.getDateandTime().isBefore(now);
            }

            if (condition){
                result.add(event);
            }
        }
        result.sort(new EventComparator()); //sort list
        return result;
    }


    // same as findEventsLIst but for todo or due events
    private LinkedList<eventsInterface> getTodoOrDueEvents(String todoOrDue, LocalDateTime now) {
        LinkedList<eventsInterface> eventsList = new LinkedList<>();
        boolean condition;

        for(eventsInterface event : allEvents){
            if(todoOrDue.equals("todo")){
                //if due datetime is after now, and event is a project and status is unfinished
                condition = event.getDateandTime().isAfter(now) && event instanceof Project p && !p.isStatus();
            }else {
                //if due datetime is before now (overdue), and event is a project and status is unfinished
                condition = event.getDateandTime().isBefore(now) && event instanceof Project p && !p.isStatus();
            }

            if(condition){
                eventsList.add(event);
            }
        }
        eventsList.sort(new EventComparator()); //sort list
        return eventsList;
    }


    /**
     * user receives 2 notifications:
     * first is 30 minutes before the start/due date
     * and the second one 15 minutes before the start/due date
     * @param windows where the events will show up (parent component)
     */
    public void notifyUser(Windows windows) {
        LocalDateTime now = TimeService.getTeller().now();
        LocalDateTime notificationTime30 = now.plusMinutes(30);
        LocalDateTime notificationTime15 = now.plusMinutes(15);

        for (eventsInterface event : allEvents) {
            if( notifiedAll.contains(event) && TimeService.getTeller().now().isAfter(event.getDateandTime())) {
                this.notifiedAll.remove(event);
                updateNotificationEmoji();
            }
            //if event hasn't already notified the user for the time between 30 and 20 minutes before happening
            if (!notifiedEvents30.contains(event) && event.getDateandTime().isAfter(notificationTime15) && event.getDateandTime().isBefore(notificationTime30)) {
                notifyWindow(windows, event, notifiedEvents30);
            }
            else{
                //if event hasn't already notified the user for the time between 15 and 0 minutes before happening
                if(!notifiedEvents15.contains(event) && event.getDateandTime().isAfter(now) && event.getDateandTime().isBefore(notificationTime15)){
                    notifyWindow(windows, event, notifiedEvents15);
                }
            }

        }
    }

    private void notifyWindow(Windows windows, eventsInterface event, LinkedList<eventsInterface> notifiedEvents) {
        String notificationMessage;
        if (event instanceof Project) {
            notificationMessage = "Project notification: " + event.getTitle() + " is due to end at " + event.getDateandTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (event instanceof Appointment) {
            notificationMessage = "Appointment notification: " + event.getTitle() + " is coming up at " + event.getDateandTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            notificationMessage = "Event notification: " + event.getTitle() + " is coming up at " + event.getDateandTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        playNotificationSound();
        JOptionPane.showMessageDialog(windows, notificationMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
        notifiedEvents.add(event);  //in order to not show notification for this mark again
        if(!notifiedAll.contains(event)){
            this.notifiedAll.add(event);
            updateNotificationEmoji();
        }
    }

    private void playNotificationSound() {
        try {
            // Load the audio file (adjust the path accordingly)
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResource("/archivo.wav")));

            // Get a Clip instance
            Clip clip = AudioSystem.getClip();

            // Open the audioInputStream to the clip
            clip.open(audioInputStream);

            // Play the sound
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            //ignore exceptions
        }
    }


    //receiving all existing calendars and sorting them
    public void mergeAndSort() {
        LinkedList<eventsInterface> list = new LinkedList<>();

        // Add events from each calendar to the list
        for (myCalendar calendar : this.allCalendars) {
            if (calendar != null) {
                list.addAll(calendar.getEventsList());
            }
        }
        list.sort(new EventComparator()); //sort using the custom comparator
        this.allEvents = list;
    }


    public JMenu getNotificationEmoji(){
        return notificationEmoji;
    }


    // changes the notification emoji depending on the size of the notifiedall list which contains the events that user has been reminded of
    public void updateNotificationEmoji() {
        notificationEmoji.removeAll();
        for (eventsInterface event : notifiedAll) {
            notificationEmoji.add(event.getTitle());
        }
        //set the right icon, depending on the number of the notifications
        if (notifiedAll.size() > 10) {
            notificationEmoji.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(Customs.bellIcons[10])))); //9 plus
        } else {
            notificationEmoji.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(Customs.bellIcons[notifiedAll.size()]))));
        }
    }

    // Custom comparator for myEvent objects
    static class EventComparator implements Comparator<eventsInterface> {
        @Override
        public int compare(eventsInterface event1, eventsInterface event2) {
            LocalDateTime dateTime1 = event1.getDateandTime();
            LocalDateTime dateTime2 = event2.getDateandTime();

            if (dateTime1 != null && dateTime2 != null) {
                return dateTime1.compareTo(dateTime2);
            } else if (dateTime1 != null) {
                return 1; // event2 is considered smaller as it doesn't have time
            } else if (dateTime2 != null) {
                return -1; // event1 is considered smaller as it doesn't have time
            }

            LocalDate date1 = event1.getDate();
            LocalDate date2 = event2.getDate();
            return date1.compareTo(date2);
        }
    }

}
