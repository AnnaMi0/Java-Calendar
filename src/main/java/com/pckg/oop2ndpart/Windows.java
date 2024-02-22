package com.pckg.oop2ndpart;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import gr.hua.dit.oop2.calendar.TimeService;


public class Windows extends JFrame {// JFrame is where our program will rely on

    //INTERACTIVE THINGS
    private JList<String> stringList;
    private static String filename;
    private final JLabel currentTimeLabel;
    private final ArrayList<JCheckBoxMenuItem> checkBox = new ArrayList<>();
    private final JPanel checkBoxPanelWithScrollPane = new JPanel(new BorderLayout());

    JMenu filterEmoji = new JMenu(); // in bar, for choosing calendars
    ArrayList<ICSFile> loadedFiles = new ArrayList<>();
    myCalendar calendar;
    Calendars loadedCalendars = new Calendars();
    Calendars chosenCalendars = new Calendars();
    LinkedList<eventsInterface> shownEvents = new LinkedList<>(); //the list of events that is visible to the user at any given moment
    JButton allJButton;
    JButton dayJButton;
    JButton weekJButton;
    JButton monthJButton;
    JButton pastDayJButton;
    JButton pastWeekJButton;
    JButton pastMonthJButton;
    JButton todoJButton;
    JButton dueJButton;

    public Windows(){
        super("MyCalendar"); // name of the window
        setLayout(new FlowLayout());
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/calendar_icon.png")));
        setIconImage(icon.getImage());

        //Actions when "Red X" is pressed (save changes)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!loadedCalendars.getAllCalendars().isEmpty()) {
                    int choice = JOptionPane.showConfirmDialog(Windows.this, "Before exiting, do you want to save your progress?");

                    if (choice == JOptionPane.YES_OPTION) {
                        if (!loadedFiles.isEmpty()) {
                            int j = 0;
                            for (myCalendar i : loadedCalendars.getAllCalendars()) {
                                loadedFiles.get(j).writetoICS(i); //save file
                                j++;
                            }
                        }
                        dispose();
                        System.exit(0);
                    } else if (choice == JOptionPane.NO_OPTION) {
                        dispose();
                        System.exit(0);
                    }
                } else{
                    dispose();
                }
            }
        });


        //Add Events button, located far-right in bar, allowing the user to create a new event
        JButton addEventJButton = new JButton("+");
        addEventJButton.setToolTipText("Add Events");
        addEventJButton.setFont(new Font("Arial", Font.PLAIN, 20));
        DefaultListModel<String> listModel = new DefaultListModel<>();// Create a DefaultListModel to hold the data for the JList

        addEventJButton.addActionListener(
                e -> EventAdder.addEvent(Windows.this, loadedCalendars, chosenCalendars, loadedFiles, allJButton)
        );


        JScrollPane scrollPane = new JScrollPane(); //where events' titles are be shown

        JMenuItem chooseFile = new JMenuItem("Choose a file");
        AtomicBoolean chooseFileUsedOnce = new AtomicBoolean(false);
        chooseFile.setMnemonic('C');
        chooseFile.addActionListener(
                e -> {
                    filename = chooseFile();
                    handleFile(scrollPane, chooseFileUsedOnce);
                    // Once the file is loaded, we have to present its Events

                    // So we collect all of its titles FOR ALL EVENTS
                    listModel.clear();
                    shownEvents.clear();
                    shownEvents.addAll(chosenCalendars.getAllEvents());
                    populateListModel(shownEvents,listModel);

                    //buttons to show events filtered by their time related to our systems' local time
                    allJButton = new CalendarButton("All", "Present all events from all selected calendars", this, mode.all, listModel, shownEvents);
                    dayJButton = new CalendarButton("Day", "Present all today's remaining events", this, mode.day, listModel, shownEvents);
                    weekJButton = new CalendarButton("Week", "Present all week's remaining events", this, mode.week, listModel, shownEvents);
                    monthJButton = new CalendarButton("Month", "Present all month's remaining events", this, mode.month, listModel, shownEvents);
                    pastDayJButton = new CalendarButton("Past Day", "Present all past day's events", this, mode.pastday, listModel, shownEvents);
                    pastWeekJButton = new CalendarButton("Past Week", "Present all past week's events", this, mode.pastweek, listModel, shownEvents);
                    pastMonthJButton = new CalendarButton("Past Month", "Present all past month's events", this, mode.pastmonth, listModel, shownEvents);
                    todoJButton = new CalendarButton("To do", "Present all to-do marked events", this, mode.todo, listModel, shownEvents);
                    dueJButton = new CalendarButton("Due", "Present all events that are due", this, mode.due, listModel, shownEvents);



                    // Create the JList with the populated model
                    stringList = new JList<>(listModel);
                    stringList.setCellRenderer(new ColoredListCellRenderer());

                    // Add a ListSelectionListener to the JList
                    stringList.addListSelectionListener(e12 -> {
                        if (!e12.getValueIsAdjusting()) {
                            int selectedItemIndex = stringList.getSelectedIndex();
                            if (selectedItemIndex == -1) return;

                            // Once we have the index of the item that has been pressed, we take the additional info for it
                            // by searching on our events list
                            String selectedItem = stringList.getSelectedValue();
                            eventsInterface chosenEvent = shownEvents.get(selectedItemIndex);
                            String eventInfo = chosenEvent.printEvent();

                            if (selectedItem != null) {
                                JOptionPane.showMessageDialog(Windows.this, eventInfo, "Info For " + stringList.getSelectedValue(), JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

                    // Add MouseMotionListener to dynamically set the tooltip based on the mouse position
                    stringList.addMouseMotionListener(new MouseMotionAdapter() {
                        private int previousIndex = -1; // Initialize to a value that doesn't conflict with valid indices

                        @Override
                        public void mouseMoved(MouseEvent e) {
                            // Get the index of the item under the mouse pointer
                            int index = stringList.locationToIndex(e.getPoint());
                            // Set tooltip only if the mouse is over an item
                            if (index != -1) {
                                stringList.setToolTipText("Click To View Info, Right Click To Edit");
                            }if (index != previousIndex){
                                // Hide the tooltip when the mouse moves away
                                stringList.setToolTipText(null);
                            }
                            previousIndex = index;
                        }
                    });



                    stringList.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) { //for windows
                            handleUpdate(e, stringList, shownEvents);
                        }
                        public void mousePressed(MouseEvent e) { //for unix
                            handleUpdate(e, stringList, shownEvents);
                        }

                    });
                    stringList.setFont(new Font("Times New Roman", Font.BOLD, 14));
                    scrollPane.setViewportView(stringList); //scroll pane presents what is inside stringLIst

                    Dimension preferredSize = new Dimension(400, 500);
                    scrollPane.setPreferredSize(preferredSize);


                    //Actions to handle multiple calendars
                    for (JCheckBoxMenuItem cbox: checkBox){
                        cbox.repaint();
                        cbox.revalidate();
                        cbox.setVisible(true);
                        cbox.addItemListener(e112 -> {
                            if (e112.getStateChange() == ItemEvent.SELECTED) { //when a calendar is selected
                                //clear lists to fill them with the files are requested when a checkbox is checked
                                listModel.clear();
                                shownEvents.clear();

                                //add selected calendar to chosenCalendars
                                for (myCalendar calendar: loadedCalendars.getAllCalendars()) {
                                    if (myCalendar.getFileNameOfPath(calendar.getFilePath()).equals(cbox.getText()) && !chosenCalendars.getAllCalendars().contains(calendar)){
                                        chosenCalendars.addCalendar(calendar);
                                    }
                                }

                                shownEvents.addAll(chosenCalendars.getAllEvents());
                                populateListModel(shownEvents,listModel);
                            } else if (e112.getStateChange() == ItemEvent.DESELECTED) {//when a calendar is no selected
                                listModel.clear();
                                shownEvents.clear();

                                for (myCalendar calendar: loadedCalendars.getAllCalendars()) {
                                    if (myCalendar.getFileNameOfPath(calendar.getFilePath()).equals(cbox.getText())){
                                        chosenCalendars.removeCalendar(calendar);
                                    }
                                }

                                shownEvents.addAll(chosenCalendars.getAllEvents());
                                populateListModel(shownEvents,listModel);
                            }
                        });
                        cbox.setSelected(true); //we set each cbox as selected because it will be operating as selected when a calendar is loaded
                        filterEmoji.add(cbox);
                    }
                    add(scrollPane);
                    add(checkBoxPanelWithScrollPane);
                    CalendarButton add = new CalendarButton(this);
                    add.addButtons();
                    chooseFileUsedOnce.set(true);
                }
        );


        //About choice (will be inside of File)
        JMenuItem aboutItem = getAbout();


        //Exit choice (will be inside of File)
        JMenuItem exitItem = getMenuItem();

        JPanel panel = new JPanel();
        //FILE MENU
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(chooseFile);
        fileMenu.add(aboutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        //BAR
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(fileMenu);
        bar.add(Box.createHorizontalGlue());

        filterEmoji.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/filter.png"))));
        filterEmoji.setToolTipText("Filter Calendars");

        bar.add(chosenCalendars.getNotificationEmoji());
        bar.add(filterEmoji);
        bar.add(addEventJButton);

        //currentTimeLabel
        currentTimeLabel = new JLabel();
        currentTimeLabel.setToolTipText("Current Time");
        currentTimeLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        add(currentTimeLabel, BorderLayout.NORTH);
        Thread updateTimeThread = new Thread(() -> {
            while (true) {
                currentTimeLabel.setText(TimeService.getTeller().now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy '|' HH:mm:ss")));

                try {
                    if (!chosenCalendars.getAllCalendars().isEmpty()) {
                        chosenCalendars.notifyUser(Windows.this);
                    }
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateTimeThread.start(); // Start the thread
        add(panel, BorderLayout.WEST);
    }

    private static void handleUpdate(MouseEvent e, JList<String> stringList, LinkedList<eventsInterface> shownEvents) {
        if (e.isPopupTrigger()) {
            int selectedItemIndex = stringList.getSelectedIndex();

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem editEventItem = new JMenuItem("Edit Event...");

            editEventItem.addActionListener(
                    e1 ->{
                        if(selectedItemIndex == -1) return;
                        shownEvents.get(selectedItemIndex).update();
                    }
            );

            popupMenu.add(editEventItem);
            popupMenu.show(stringList, e.getX(), e.getY());
        }
    }

    private JMenuItem getMenuItem() {
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        //Same actions with "red X"
        exitItem.addActionListener(
                e -> {
                    if(!loadedCalendars.getAllCalendars().isEmpty()) {
                        int choice = JOptionPane.showConfirmDialog(Windows.this, "Before exiting, do you want to save your progress?");

                        if (choice == JOptionPane.YES_OPTION) {
                            if (!loadedFiles.isEmpty()) {
                                int j = 0;
                                for (myCalendar i : loadedCalendars.getAllCalendars()) {
                                    loadedFiles.get(j).writetoICS(i); //save file
                                    j++;
                                }
                            }
                            dispose();
                            System.exit(0);
                        } else if (choice == JOptionPane.NO_OPTION) {
                            dispose();
                            System.exit(0);
                        }
                    } else{
                        dispose();
                }
                }
        );
        return exitItem;
    }

    private JMenuItem getAbout() {
        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(
                e -> JOptionPane.showMessageDialog(Windows.this, """
                            Version 1.0.0
                            Developed by:
                            Lymperopoulos Ioannis (it2022059)
                            Vasileios Kotoumpas (it2022044)
                            Anna Michailidou (it2022066)""", "About", JOptionPane.INFORMATION_MESSAGE)
                );
        return aboutItem;
    }

    //press allJButton when it is needed
    public void clickallJButton(){
        allJButton.doClick();
    }

    //fill listmodel with corresponding events
    public static void populateListModel(LinkedList<eventsInterface> shownEvents, DefaultListModel<String> listModel) {
        for (eventsInterface event : shownEvents) {
            listModel.addElement(event.getTitle());
        }
    }

    //Actions when user adds a calendar
    public String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("iCal files", "ics");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(Windows.this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        } else {
            JOptionPane.showMessageDialog(Windows.this, "File selection has been canceled.");
            return null; // User canceled the file selection
        }

    }

    // Actions to check if the selected path has already been loaded, if it is not we are loading it
    public void handleFile(JScrollPane scrollPane, AtomicBoolean chooseFileUsedOnce){
        CalendarButton removal = new CalendarButton(this);
        for (ICSFile file : loadedFiles){
            if (file.getFilepath().equals(filename)){
                JOptionPane.showMessageDialog(Windows.this, "This file has already been loaded");
                removal.removeButtons();
                return;
            }
            if(myCalendar.getFileNameOfPath(file.getFilepath()).equals(myCalendar.getFileNameOfPath(filename))){
                JOptionPane.showMessageDialog(Windows.this, "Another calendar by this name has already been loaded");
                removal.removeButtons();
                return;
            }
        }

        ICSFile newFile = new ICSFile(filename);
        calendar = newFile.loadCalendarFromICS();

        if (calendar != null) { // for any problem that occurs we just end the running action, calendar will be null
            //file has been loaded successfully
            JOptionPane.showMessageDialog(Windows.this, "Your file has been loaded.", "File Loaded", JOptionPane.INFORMATION_MESSAGE);
            loadedFiles.add(newFile);
            checkBox.add(new JCheckBoxMenuItem(myCalendar.getFileNameOfPath(filename)));

            // We remove the previous attributes due to the fact that when we choose file to run for a second time, the previous attributes are useless
            // We do this process just after the calendar loading to avoid any trouble such as user loading falsely a file
            remove(scrollPane);
            if (chooseFileUsedOnce.get()) {
                removal.removeButtons();
                repaint();
                revalidate();
            }

            loadedCalendars.addCalendar(calendar);
            chosenCalendars.addCalendar(calendar); //by default, it is checked therefore added
        }
    }
}