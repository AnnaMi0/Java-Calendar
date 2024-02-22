package com.pckg.oop2ndpart;

import javax.swing.*;
import java.util.LinkedList;

/**
 * corresponds to the buttons below (All,Day,Week etc)
 * handles which events are shown when each button is pressed
 */

public class CalendarButton extends JButton {
    private final Windows windows;
    LinkedList<eventsInterface> shownEvents; //the list of events that is visible to the user at any given moment
    DefaultListModel<String> listModel ;// Create a DefaultListModel to hold the data for the JList

  public CalendarButton (Windows windows){  //Used for add and remove functions
      this.windows = windows;
  }

  //Used for the actual buttons
    public CalendarButton(String buttonText, String tooltip, Windows windows, mode calendarMode,DefaultListModel<String> listModel, LinkedList<eventsInterface> shownEvents) {
        super(buttonText);
        this.setToolTipText(tooltip);
        this.windows = windows;
        this.listModel = listModel;
        this.shownEvents = shownEvents;
        Customs.customizeButton(this);
        this.addActionListener(e -> showEventsForMode(calendarMode, mode.modeString(calendarMode)));
    }

    private void showEventsForMode(mode usermode, String noEventsMessage) {
        this.listModel.clear();
        this.shownEvents.clear();

        LinkedList<eventsInterface> result = windows.chosenCalendars.presentEvents(usermode);

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this.windows, noEventsMessage);
            return;
        }

        shownEvents.addAll(result);
        Windows.populateListModel(shownEvents,listModel);
    }

    void removeButtons() {
        windows.remove(windows.allJButton);
        windows.remove(windows.dayJButton);
        windows.remove(windows.weekJButton);
        windows.remove(windows.monthJButton);
        windows.remove(windows.pastDayJButton);
        windows.remove(windows.pastWeekJButton);
        windows.remove(windows.pastMonthJButton);
        windows.remove(windows.todoJButton);
        windows.remove(windows.dueJButton);
}

    void addButtons() {
        windows.add(windows.allJButton);
        windows.add(windows.dayJButton);
        windows.add(windows.weekJButton);
        windows.add(windows.monthJButton);
        windows.add(windows.pastDayJButton);
        windows.add(windows.pastWeekJButton);
        windows.add(windows.pastMonthJButton);
        windows.add(windows.todoJButton);
        windows.add(windows.dueJButton);
    }

    }

