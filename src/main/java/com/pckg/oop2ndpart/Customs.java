package com.pckg.oop2ndpart;

import javax.swing.*;
import java.awt.*;

/**
 * Used to customise (add colors)
 */

public class Customs {
    // icon choices depending on the size of the list which contains the notifications
    public static String[] bellIcons = {"/bell_icon.png", "/bell_icon1.png", "/bell_icon2.png", "/bell_icon3.png", "/bell_icon4.png", "/bell_icon5.png", "/bell_icon6.png" ,"/bell_icon7.png",
        "/bell_icon8.png", "/bell_icon9.png", "/bell_icon9plus.png"};


    // customize
    public static void customizeButton(JButton button) {
        button.setBackground(new Color(87, 176, 187)); // Custom background color
        button.setForeground(Color.white); // Text color
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Custom font
    }
}


class ColoredListCellRenderer extends DefaultListCellRenderer { //customize main JList
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Custom background and foreground colors
        if (index % 2 == 0) {
            component.setBackground(new Color(200, 220, 255));  // Custom background color for even-indexed items
        } else {
            component.setBackground(new Color(230, 240, 255));  // Custom background color for odd-indexed items
        }

        return component;
    }
}