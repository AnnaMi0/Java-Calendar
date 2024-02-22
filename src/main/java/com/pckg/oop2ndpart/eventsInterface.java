package com.pckg.oop2ndpart;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;


public interface eventsInterface {
    void update();
    String printEvent();
    String getEventClass();
    LocalDateTime getDateandTime();
    void setDateandTime(LocalDateTime dateandTime);
    String getTitle();
    void setTitle(String title);
    String getDescription();
    void setDescription(String description);
    LocalDate getDate();
    String getUid();


    /**
     * handles updating of project/appointment objects and GUI
     * @param windowTitle the title of the window
     * @param event the event to be updated
     */

    //for editing events
    static void editEventWindow(String windowTitle, eventsInterface event) {
        JFrame updateWindow = getWindow(windowTitle);

        JLabel eventName = new JLabel("Event name");
        updateWindow.add(eventName);
        JTextField eventNameTextField = new JTextField();
        eventNameTextField.setText(event.getTitle()); //set with the previous info
        updateWindow.add(eventNameTextField);

        JLabel eventDescription = new JLabel("Event Description");
        updateWindow.add(eventDescription);

        JTextField eventDescriptionTextField = new JTextField();
        eventDescriptionTextField.setText(event.getDescription()); //set with the previous info
        updateWindow.add(eventDescriptionTextField);

        JCheckBox finishedJCheckBox = null;
        TimeChooser durationChooser = null;

        if (event instanceof Project p) { //add finished object in case where a project is edited
            JPanel finishedPanel = new JPanel();
            finishedPanel.setLayout(new FlowLayout());

            JLabel finished = new JLabel("Finished");
            finishedJCheckBox = new JCheckBox();

            if (p.isStatus()) {
                finishedJCheckBox.setSelected(true); //set with the previous info
            }

            finishedPanel.add(finished);
            finishedPanel.add(finishedJCheckBox);
            updateWindow.add(finishedPanel);

        }else if (event instanceof Appointment appt) { //add duration object in case where a project is edited
            JPanel durationPanel = new JPanel();
            durationChooser = new TimeChooser((int) appt.getDuration() / 60, (int) appt.getDuration() % 60);
            JLabel durationLabel = new JLabel("Duration");
            durationPanel.add(durationLabel);
            durationPanel.add(durationChooser);

            updateWindow.add(durationPanel);
        }


        //CALENDAR AND TIME
        //A panel to add textField and calendar png it the same grid cell
        JPanel mainPanel = new JPanel(new BorderLayout());
        JTextField dateTextField = new JTextField(event.getDateandTime().getDayOfMonth() + "/" + event.getDateandTime().getMonthValue() +  "/" + event.getDateandTime().getYear() + "  "
                + event.getDateandTime().getHour() + ":" + event.getDateandTime().getMinute()); //set with the previous info

        dateTextField.setEditable(false);
        mainPanel.add(dateTextField);

        JLabel imageLabel = new JLabel(new ImageIcon(Objects.requireNonNull(eventsInterface.class.getResource("/calendar_emoji.png"))));
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UtilDateModel model = new UtilDateModel();

        //set model with what we have now
        model.setDate(event.getDateandTime().getYear(), event.getDateandTime().getMonthValue()-1, event.getDateandTime().getDayOfMonth());
        Properties p = new Properties(); //properties for time
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        TimeChooser timeChooser = new TimeChooser(event.getDateandTime().getHour(), event.getDateandTime().getMinute());

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame chooseDate = new JFrame("Choose Deadline");
                eventsInterface.updateWindowTime(chooseDate,updateWindow,datePanel,timeChooser,model,dateTextField);
            }
        });

        mainPanel.add(imageLabel, BorderLayout.EAST);
        updateWindow.add(mainPanel);
        eventsInterface.updateSaveCancel(updateWindow,eventNameTextField,eventDescriptionTextField,timeChooser,model,event,finishedJCheckBox,durationChooser);
    }

    private static JFrame getWindow(String windowTitle) {
        JFrame updateWindow = new JFrame(windowTitle);
        updateWindow.setLayout(new GridLayout(4, 2));
        updateWindow.setSize(520, 300);
        updateWindow.setVisible(true);

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - updateWindow.getWidth()) / 2;
        int centerY = (screenSize.height - updateWindow.getHeight()) / 2;

        // Set the location of the frame to be in the center
        updateWindow.setLocation(centerX, centerY);
        return updateWindow;
    }


    //actions to change time in the Edit Window
    private static void updateWindowTime(JFrame chooseDate, JFrame updateWindow, JDatePanelImpl datePanel, TimeChooser timeChooser, UtilDateModel model, JTextField dateTextField) {
        chooseDate.setLocation(updateWindow.getLocation().x, updateWindow.getLocation().y); //the same location as addEvent
        chooseDate.setLayout(new FlowLayout());
        chooseDate.add(datePanel);
        chooseDate.add(timeChooser);

        JButton okButton = new JButton("Ok");
        chooseDate.add(okButton);
        chooseDate.setVisible(true);
        chooseDate.pack();

        okButton.addActionListener(
                e1 -> {
                    String message;

                    message = model.getDay() + "/" + (model.getMonth()+1) + "/" + model.getYear() + " " + timeChooser.getSelectedTime().getHour() + ":" + timeChooser.getSelectedTime().getMinute();

                    dateTextField.setText(message);
                    chooseDate.dispose();
                });
    }


    // in case where user presses cancel button
    private static void cancelUpdating(JFrame updateWindow) {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(
                e -> {
                    int result = JOptionPane.showConfirmDialog(updateWindow, "Are you sure to cancel? All changes will be lost.", "Cancel", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION){
                        updateWindow.dispose();
                    }
                }
        );
        updateWindow.add(cancelButton);
    }


    //save edited events
    private static void updateSaveCancel(JFrame updateWindow, JTextField eventNameTextField, JTextField eventDescriptionTextField,
                                         TimeChooser timeChooser, UtilDateModel model, eventsInterface event, JCheckBox finishedJcbox, TimeChooser durationChooser){
        JButton save = new JButton("Save");
        updateWindow.add(save);
        save.addActionListener(
                e -> {
                    String eventName1 = EventAdder.titleAndDescriptionBox("title", eventNameTextField);
                    String eventDescription1 = EventAdder.titleAndDescriptionBox("description", eventDescriptionTextField);
                    if(eventName1 != null && eventDescription1 != null) {
                        //get month + 1 cause if january is pressed it returns 0
                        LocalDateTime deadLine = LocalDateTime.of(model.getYear(), model.getMonth() + 1, model.getDay(), timeChooser.getSelectedTime().getHour(), timeChooser.getSelectedTime().getMinute());

                        event.setDateandTime(deadLine);
                        event.setTitle(eventName1);
                        event.setDescription(eventDescription1);

                        if(event instanceof Project p){
                            p.setStatus(finishedJcbox.isSelected());
                        } else if (event instanceof  Appointment appt) {
                            if (durationChooser.getDuration() == 0){
                                JOptionPane.showMessageDialog(null, "An appointment cannot have a duration of 0.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            appt.setDuration(durationChooser.getDuration());
                        }

                        //to refresh our list
                        Main.windows.clickallJButton();
                        updateWindow.dispose();
                    }
                }
        );
        cancelUpdating(updateWindow);
    }
}
