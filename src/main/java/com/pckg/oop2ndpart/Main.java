package com.pckg.oop2ndpart;

import gr.hua.dit.oop2.calendar.TimeService;

import javax.swing.*;
import java.awt.*;

public class Main {
    static Windows windows;
    public static void main(String[] args) {
        windows = new Windows(); // new instance for our class to open the Main window
        windows.setSize(450, 800);
        windows.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        windows.setVisible(true);
        windows.setResizable(false);

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - windows.getWidth()) / 2;
        int centerY = (screenSize.height - windows.getHeight()) / 2;

        // Set the location of the frame to be in the center
        windows.setLocation(centerX, centerY);

        TimeService.stop();
    }
}