package com.pckg.oop2ndpart;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class EventAdder {

    public static void addEvent(JFrame parentFrame, Calendars loadedCalendars, Calendars chosenCalendars, ArrayList<ICSFile> loadedFiles, JButton allJButton) {
        if (loadedCalendars.getAllCalendars().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Can't add events when no files are loaded", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new window for adding events
        JFrame addEventWindow = new JFrame("Add Event");
        addEventWindow.setLayout(new GridLayout(4, 2));
        addEventWindow.setSize(580, 300);
        addEventWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addEventWindow.setLocationRelativeTo(parentFrame);

        // Event Name
        JLabel eventName = new JLabel("Event Name");
        addEventWindow.add(eventName);
        JTextField eventNameTextField = new JTextField();
        addEventWindow.add(eventNameTextField);

        // Event Description
        JLabel eventDescription = new JLabel("Event Description");
        addEventWindow.add(eventDescription);
        JTextField eventDescriptionTextField = new JTextField();
        addEventWindow.add(eventDescriptionTextField);

        // Project or Appointment
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 2));
        JRadioButton projectJRadioButton = new JRadioButton("Project");
        projectJRadioButton.setLayout(new BorderLayout());
        JRadioButton appointmentJRadioButton = new JRadioButton("Appointment");
        buttonsPanel.add(projectJRadioButton);
        buttonsPanel.add(appointmentJRadioButton);
        addEventWindow.add(buttonsPanel);

        // Group for mutually exclusive selection
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(projectJRadioButton);
        buttonGroup.add(appointmentJRadioButton);

        // Calendar and Time
        JPanel mainPanel = new JPanel(new BorderLayout());
        JTextField dateTextField = new JTextField("Choose a date.");
        dateTextField.setEditable(false);
        mainPanel.add(dateTextField);

        JLabel imageLabel = new JLabel(new ImageIcon(Objects.requireNonNull(EventAdder.class.getResource("/calendar_emoji.png"))));
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        TimeChooser timeChooser = new TimeChooser();
        TimeChooser durationChooser = new TimeChooser(); // exclusively for appointments

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Handle the image click event here
                if (!projectJRadioButton.isSelected() && !appointmentJRadioButton.isSelected()) {
                    JOptionPane.showMessageDialog(addEventWindow, "Choose Appointment or Project first!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String windowTitle = projectJRadioButton.isSelected() ? "Choose Deadline" : "Choose Starting Time";
                JFrame chooseDate = new JFrame(windowTitle);
                chooseDate.setLocation(addEventWindow.getLocation().x, addEventWindow.getLocation().y);

                chooseDate.setLayout(new FlowLayout());
                chooseDate.add(datePanel);
                chooseDate.add(timeChooser);

                // APPOINTMENT DURATION
                JPanel durationPanel = new JPanel();
                durationPanel.setLayout(new BoxLayout(durationPanel, BoxLayout.Y_AXIS));

                JLabel durationLabel = new JLabel("Duration");
                durationPanel.add(durationLabel);
                durationPanel.add(durationChooser);
                chooseDate.add(durationPanel);

                if (appointmentJRadioButton.isSelected()) {
                    chooseDate.add(durationPanel);
                } else {
                    chooseDate.remove(durationPanel);
                }

                JButton okButton = new JButton("Ok");
                chooseDate.add(okButton);
                chooseDate.setVisible(true);
                chooseDate.pack();

                okButton.addActionListener(e114 -> {
                    String message = model.getDay() + "/" + (model.getMonth() + 1) + "/" + model.getYear() + " " +
                            timeChooser.getSelectedTime().getHour() + ":" + timeChooser.getSelectedTime().getMinute();

                    if (appointmentJRadioButton.isSelected()) {
                        message += " Duration: " + durationChooser.getDuration() + " minutes";
                    }

                    dateTextField.setText(message);
                    chooseDate.dispose();
                });
            }
        });

        mainPanel.add(imageLabel, BorderLayout.EAST);
        addEventWindow.add(mainPanel);

        // Choose Calendar Panel
        JPanel chooseCalendarPanel = new JPanel(new BorderLayout());
        JTextField calendarTextField = new JTextField("Choose a Calendar.");
        calendarTextField.setEditable(false);
        chooseCalendarPanel.add(calendarTextField);

        JLabel chooseCalendarEmoji = new JLabel(new ImageIcon(Objects.requireNonNull(EventAdder.class.getResource("/change_calendar_emoji.png"))));
        final int[] changeFileIndex = {-1}; // an array to pass through to the mouse listener

        chooseCalendarEmoji.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeFileIndex[0]++;
                if (loadedFiles.size() > changeFileIndex[0]) {
                    calendarTextField.setText("Chosen File: " + myCalendar.getFileNameOfPath(loadedFiles.get(changeFileIndex[0]).getFilepath()));
                } else {
                    changeFileIndex[0] = 0;
                    calendarTextField.setText("Chosen File: " + myCalendar.getFileNameOfPath(loadedFiles.get(changeFileIndex[0]).getFilepath()));
                }
            }
        });

        chooseCalendarEmoji.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chooseCalendarPanel.add(chooseCalendarEmoji, BorderLayout.EAST);
        JButton save = new JButton("Save");
        addEventWindow.add(save);

        save.addActionListener(e113 -> {

            String eventNameToSave = EventAdder.titleAndDescriptionBox("title", eventNameTextField);
            String eventDescriptionToSave = EventAdder.titleAndDescriptionBox("description", eventDescriptionTextField);
            if(eventNameToSave != null && eventDescriptionToSave != null){


                if (!projectJRadioButton.isSelected() && !appointmentJRadioButton.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Choose Appointment or Project first!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (appointmentJRadioButton.isSelected() && durationChooser.getDuration() == 0) {
                    JOptionPane.showMessageDialog(null, "Select duration first!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int chosenFileIndex = changeFileIndex[0];
                if (chosenFileIndex == -1) {
                    JOptionPane.showMessageDialog(null, "Choose a calendar first!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDateTime dateTime = LocalDateTime.of(
                        model.getYear(),
                        model.getMonth() + 1, // No need to add 1 if JDatePanelImpl is consistent with LocalDate
                        model.getDay(),
                        timeChooser.getSelectedTime().getHour(),
                        timeChooser.getSelectedTime().getMinute()
                );
                myEvent temp = new myEvent(dateTime, eventNameToSave, eventDescriptionToSave, ICSFile.generateUniqueUid());

                if (projectJRadioButton.isSelected()) {
                    Project.add(temp, loadedCalendars.getAllCalendars().get(chosenFileIndex));
                } else {
                    Appointment.add(temp, loadedCalendars.getAllCalendars().get(chosenFileIndex), durationChooser.getDuration());
                }

                loadedCalendars.mergeAndSort();
                chosenCalendars.mergeAndSort();

                // To refresh the list
                allJButton.doClick();
                addEventWindow.dispose();
            }
        });

        // Add chooseCalendarPanel to the window
        addEventWindow.add(chooseCalendarPanel);
        addEventWindow.setVisible(true);
    }


    public static String titleAndDescriptionBox(String titleOrDescription,JTextField eventTextField){
        String str = "Input event " + titleOrDescription + " first!";
        String eventField = eventTextField.getText();
        if (eventField.isEmpty()) {
            JOptionPane.showMessageDialog(null, str , "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return eventField;
    }

}