package com.pckg.oop2ndpart;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

class TimeChooser extends JPanel {
    private final JSpinner hourSpinner;
    private final JSpinner minuteSpinner;

    public TimeChooser() {
        setLayout(new FlowLayout());

        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        hourSpinner = new JSpinner(hourModel);

        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        minuteSpinner = new JSpinner(minuteModel);

        add(new JLabel("Hours:"));
        add(hourSpinner);
        add(new JLabel("Minutes:"));
        add(minuteSpinner);
    }

    public TimeChooser(int hours, int minutes){
        setLayout(new FlowLayout());

        SpinnerModel hourModel = new SpinnerNumberModel(hours, 0, 24, 1);
        hourSpinner = new JSpinner(hourModel);

        SpinnerModel minuteModel = new SpinnerNumberModel(minutes, 0, 59, 1);
        minuteSpinner = new JSpinner(minuteModel);

        add(new JLabel("Hours:"));
        add(hourSpinner);
        add(new JLabel("Minutes:"));
        add(minuteSpinner);
    }

    public LocalDateTime getSelectedTime() {
        int hours = (int) hourSpinner.getValue();
        int minutes = (int) minuteSpinner.getValue();

        // Create a LocalDateTime object with the selected time
        return LocalDateTime.of(1970, 2, 1, hours, minutes, 0);
    }

    // for appointment
    public int getDuration() {
        int hours = (int) hourSpinner.getValue();
        int minutes = (int) minuteSpinner.getValue();

        return hours * 60 + minutes;
    }
}