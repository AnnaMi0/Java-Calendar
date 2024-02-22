package com.pckg.oop2ndpart;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.NoSuchElementException;
import java.util.UUID;
import static java.lang.System.exit;


public class ICSFile {
    private final String filepath;

    public String getFilepath() {
        return filepath;
    }

    public ICSFile(String filepath) {
        this.filepath = filepath;
    }

    public Boolean fileExists() {// Check if the file exists, and create it if it doesn't
        Path file = Paths.get(this.filepath);
        try {
            if (Files.notExists(file)) {
                Files.createFile(file);
                JOptionPane.showMessageDialog(null, "New file:" + filepath, "New file has been created", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isValidICSFile() {
        /*
         * A file's contents may be valid, but the file may not have the .ics extension therefore
         * this method checks if a file is valid regardless of its extension
         */

        File file = new File(this.filepath);
        try (FileInputStream fin = new FileInputStream(file)) {
            new CalendarBuilder().build(fin);// Attempt to parse the content using iCal4j to see if it's valid
            return true; // If parsing succeeds, consider it a valid ICS file
        } catch (IOException | ParserException | ValidationException e) {
            return false; // If parsing or validation fails, consider it not an ICS file
        }
    }

    public myCalendar loadCalendarFromICS() {
        if (!this.fileExists()) {//check if file exists, if it doesn't, create a file and return to main
            myCalendar newCal = new myCalendar();
            newCal.setFilePath(this.filepath);
            return newCal;
        }
        if(!isValidICSFile()){ //check if a file's *contents* are valid
            JOptionPane.showMessageDialog(null, "This file is corrupt or not valid ICS file!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try (FileInputStream fin = new FileInputStream(this.filepath)) { //create new FileInputStream object using the filepath
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin); //create calendar using the file to store its information
            myCalendar resCalendar = new myCalendar(calendar.getProductId(), calendar.getCalendarScale()); //resulting calendar
            int count = 0; // event counter in the calendar

            for (Component c : calendar.getComponents()) {//parse through the file's VEVENT components (VEVENT,VTODO)
                count++;

                try { //used to catch any exceptions while reading the events, so we can skip a corrupt event
                    if (c instanceof VEvent vevent) { //appointment, no-time event or simple event
                        processVEVENT(vevent, resCalendar);
                    } else if (c instanceof VToDo todo) { //project
                        processVTODO(todo, resCalendar);
                    }
                } catch (
                        NoSuchElementException e) {//exception for checkfortime and getLocalDateTime methods, and in case SUMMARY or DTSTART is missing
                    System.out.println("Event " + count + " is missing the required information moving on...");
                }
            }
            resCalendar.setFilePath(this.filepath);
            return resCalendar;

        } catch (IOException | ParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processVEVENT(VEvent vevent, myCalendar resCalendar){
        String title, description, uid;
        Temporal endDateTime;
        Temporal startDateTime;
        long duration;
        if (vevent.getSummary() == null || vevent.getProperty(Property.DTSTART) == null) {//prerequisites
            throw new NoSuchElementException();
        }

        title = vevent.getSummary().getValue();//event's name
        if (vevent.getProperty(Property.DESCRIPTION) == null) {
            description = "";
        } else {
            description = vevent.getDescription().getValue();
        }
        startDateTime = getLocalDateTime(vevent, 1);
        uid = vevent.getProperty("UID").getValue();

        if (vevent.getProperty(Property.DURATION) != null) {
            String durationValue = vevent.getProperty(Property.DURATION).getValue();
            duration = Duration.parse(durationValue).toMinutes();// Get the duration in minutes as a long
            Appointment appt = new Appointment((LocalDateTime) startDateTime, title, description, duration,uid);
            resCalendar.getEventsList().add(appt);
        }

        if (vevent.getProperty(Property.DTEND) != null && vevent.getProperty(Property.DURATION) == null) { //DTEND IS PRESENT, find duration
            endDateTime = getLocalDateTime(vevent, 2); //get DTEND and turn to Temporal object

            //calculate duration in minutes between 2 temporal objects
            duration = getDurationTemporal(startDateTime, endDateTime);
            Appointment appt;
            if (checkforTime(vevent, 1)) { //time in DTSTART -> cast to LocalDateTime
                appt = new Appointment((LocalDateTime) startDateTime, title, description, duration,uid);
            } else { //no date in DTSTART
                LocalDateTime temp = LocalDateTime.of((LocalDate) startDateTime, LocalTime.MIDNIGHT);
                appt = new Appointment(temp, title, description, duration,uid);
            }

            resCalendar.getEventsList().add(appt);
        } else { //DTEND is not present save as simple event or no-time event
            if (checkforTime(vevent, 1)) { //event with time
                myEvent simpleEvent = new myEvent((LocalDateTime) startDateTime, title, description,uid); //constructor for event w time
                resCalendar.getEventsList().add(simpleEvent);
            } else {  //event with no time (DTSTART doesn't have time)
                myEvent notime = new myEvent(title, description, (LocalDate) startDateTime,uid); //cast temporal to LocalDate
                resCalendar.getEventsList().add(notime);
            }
        }
    }

    private void processVTODO(VToDo todo, myCalendar resCalendar){
        String title, description, uid;
        Temporal endDateTime;
        if (todo.getProperty(Property.SUMMARY) == null || todo.getProperty(Property.STATUS) == null) {
            throw new NoSuchElementException();
        }
        title = todo.getSummary().getValue();
        uid = todo.getProperty("UID").getValue();

        if (todo.getProperty(Property.DESCRIPTION) == null) {
            description = "";
        } else {
            description = todo.getDescription().getValue();
        }

        // Extract STATUS property value
        Property statusProperty = todo.getProperty(Property.STATUS);
        String statusValue = statusProperty.getValue();
        boolean isFinished = "COMPLETED".equals(statusValue);//if status is finished then true, else false

        if (todo.getProperty(Property.DUE) != null) { //DUE exists
            endDateTime = getLocalDateTime(todo, 3);
        } else {// DTEND could be present instead of DUE
            if (todo.getProperty(Property.DTEND) == null) {//if DUE and DTEND are not present, vtodo is corrupt
                throw new NoSuchElementException();
            }
            endDateTime = getLocalDateTime(todo, 2);
        }
        if (endDateTime instanceof LocalDate) { //if it doesn't have time set it to 00:00:00
            endDateTime = LocalDateTime.of((LocalDate) endDateTime, LocalTime.MIDNIGHT);
        }
        Project temp = new Project((LocalDateTime) endDateTime, title, description, isFinished,uid);
        resCalendar.getEventsList().add(temp);//add to the linked list
    }

    private static Temporal getLocalDateTime(Component event, int mode){ //mode=1 -> DTSTART, mode=2 -> DTEND, mode=3 -> DUE
        //Temporal is a common interface of LocalDate and LocalDateTime
        DtEnd dtend = event.getProperty("DTEND");
        DtStart dtstart = event.getProperty("DTSTART");
        LocalDateTime localDateTime;
        LocalDate date;
        Instant instant;
        String str;

        try {
            boolean hasTime = checkforTime(event, mode);
            Class<? extends Temporal> type = hasTime ? LocalDateTime.class : LocalDate.class;

            switch (mode) {
                case 1 -> {
                    instant = dtstart.getDate().toInstant();
                    str = dtstart.getValue();
                    if (str == null) throw new NoSuchElementException();
                }

                case 2 -> {
                    instant = dtend.getDate().toInstant();
                    str = dtend.getValue();
                    if (str == null) throw new NoSuchElementException();
                }
                case 3 -> {
                    VToDo c = (VToDo) event;
                    Due dueDate = c.getDue();
                    str = c.getProperty("DUE").getValue();
                    instant = dueDate.getDate().toInstant();
                    if (str == null) throw new NoSuchElementException();//due is not present
                }
                default -> throw new NoSuchElementException();
            }

            if (type.equals(LocalDate.class)) { //event doesn't have time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");//date format used in the iCal files
                date = LocalDate.parse(str, formatter);
                return type.cast(date);
            } else { //event has time
                localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                return type.cast(localDateTime);
            }
        } catch (NoSuchElementException e) {
            exit(1);
        }

        throw new NoSuchElementException();
    }

    public static boolean checkforTime(Component event, int mode) throws NoSuchElementException { //check if an event has time
        //mode determines whether we check DTSTART (mode = 1) or DTEND (mode = 2) or DUE (mode = 3)
        String str;

        switch (mode) {
            case 1 -> str = event.getProperty("DTSTART").getValue(); //getValue ignores VALUE=DATE: in case there's no time in DTSTART
            case 2 -> str = event.getProperty("DTEND").getValue();
            case 3 -> str = event.getProperty("DUE").getValue();
            default -> throw new NoSuchElementException(); //invalid mode
        }

        if (str.length() == 8) { //YYYYmmDD length is 8
            //DTSTART doesn't have time
            return false;
        } else if (str.length() == 16 || str.length() == 15) {//YYYYmmDDTHHmmSSZ length is 16 or 15 (doesn't always include Z)
            //DTSTART has time
            return true;
        }
        throw new NoSuchElementException("DateTime format in file is invalid.");
    }

    public void writetoICS(myCalendar mycal) {
        try (OutputStream outputStream = new FileOutputStream(this.filepath)) {
            CalendarOutputter outputter = new CalendarOutputter();
            Calendar calendar = new Calendar();

            calendar.getProperties().add(mycal.getCalTitle());
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(mycal.getCalscale());

            for (myEvent i : mycal.getEventsList()) {
                CalendarComponent eventComponent;

                switch (myEvent.getEventTypeINT(i)) {

                    case 0, 1, 2 -> {
                        // Common code for VEvent
                        VEvent vevent = new VEvent();
                        vevent.getProperties().add(new Uid(i.getUid()));
                        vevent.getProperties().add(new Summary(i.getTitle()));
                        vevent.getProperties().add(new Description(i.getDescription()));

                        // Common code for setting DTSTART
                        DateTime dtStart = new DateTime(i.getDateandTime()
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                        vevent.getProperties().add(new DtStart(dtStart));

                        // Additional handling for VEvent with duration
                        if (myEvent.getEventTypeINT(i) == 2) {
                            long milisecs = dtStart.getTime() +((Appointment) i).getDuration() * 60 * 1000;
                            DateTime end = new DateTime(milisecs);
                            vevent.getProperties().add(new DtEnd(end));
                        }
                        eventComponent = vevent;
                    }

                    case 3 -> {
                        VToDo vtodo = new VToDo();
                        vtodo.getProperties().add(new Uid(i.getUid()));
                        vtodo.getProperties().add(new Summary(i.getTitle()));
                        vtodo.getProperties().add(new Description(i.getDescription()));

                        //setting DUE date
                        DateTime dueDate = new DateTime(i.getDateandTime()
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                        vtodo.getProperties().add(new Due(dueDate));

                        //setting status
                        String status = ((Project) i).isStatus() ? "COMPLETED" : "IN-PROCESS";
                        vtodo.getProperties().add(new Status(status));
                        eventComponent = vtodo;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + myEvent.getEventTypeINT(i));
                }
                calendar.getComponents().add(eventComponent);
            }

            outputter.output(calendar, outputStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long getDurationTemporal(Temporal start, Temporal end){
        //check if either is LocalDate, and set time to 00:00
        if(start instanceof LocalDate){
            start = LocalDateTime.of((LocalDate) start,LocalTime.MIDNIGHT);
        }
        if(end instanceof LocalDate){
            end = LocalDateTime.of((LocalDate) end,LocalTime.MIDNIGHT);
        }
        return ChronoUnit.MINUTES.between(start,end);
    }


    public static String generateUniqueUid() {// Method to generate unique UID
        UidGenerator uidGenerator = () -> {
            // Create unique UID by generating a random UUID and converting it to a string
            return new Uid(UUID.randomUUID() + "@OOP2ProjectCalendar");
        };

        String uidString = uidGenerator.generateUid().toString(); //this returns UID:....\n
        uidString = uidString.replace("UID:", "");
        uidString = uidString.trim();

        return uidString;
    }
}