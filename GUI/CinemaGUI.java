package GUI;

import Objects.Movies;
import Objects.Seat;
import Objects.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class CinemaGUI {
    // Lists to store objects
    private ArrayList<Movies> moviesList = new ArrayList<>();
    private ArrayList<Seat> seatsList = new ArrayList<>();
    private ArrayList<Function> functionsList = new ArrayList<>();
    private HashMap<String, ArrayList<Seat>> reservedSeatsMap = new HashMap<>();

    private JComboBox<String> movieSelector;
    private JComboBox<String> functionSelector;
    private JPanel seatsGrid;

    public static void main(String[] args) {
        new CinemaGUI().createAndShowGUI();
    }

    public CinemaGUI() {
        // Predefined movies
        moviesList.add(new Movies("English", "Actor1, Actor2", "Director1", "Movie1", "Action", 120, "+16", "Synopsis1"));
        moviesList.add(new Movies("Spanish", "Actor3, Actor4", "Director2", "Movie2", "Comedy", 90, "+13", "Synopsis2"));
        moviesList.add(new Movies("English", "Actor5, Actor6", "Director3", "Movie3", "Drama", 150, "+18", "Synopsis3"));

        // Predefined functions
        functionsList.add(new Function("10:00", "Monday", "Cinema1", "Room1", "2D", LocalDate.now()));
        functionsList.add(new Function("12:00", "Monday", "Cinema1", "Room2", "3D", LocalDate.now()));

        // Predefined seats
        for (int i = 1; i <= 25; i++) {
            seatsList.add(new Seat(i, "A", true, i % 5 == 0, 10.0));
        }
    }

    public void createAndShowGUI() {
        // Create the main window
        JFrame frame = new JFrame("Cinema Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create the tabbed panel
        JTabbedPane tabbedPane = new JTabbedPane();

        // Movies tab
        tabbedPane.addTab("Movies", createMoviesPanel());

        // Functions tab
        tabbedPane.addTab("Functions", createFunctionsPanel());

        // Seats tab
        tabbedPane.addTab("Seats", createSeatsPanel());

        // Add the tabbed panel to the window
        frame.add(tabbedPane);

        // Show the window
        frame.setVisible(true);
    }

    private JPanel createMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display movies
        String[] columns = {"Title", "Director", "Genre", "Duration", "Classification"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load predefined movies into the table
        for (Movies movie : moviesList) {
            model.addRow(new Object[]{movie.getTitle(), movie.getDirector(), movie.getGenre(), movie.getMinutes(), movie.getClassification()});
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFunctionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display functions
        String[] columns = {"Hour", "Day", "Cinema", "Room", "Format", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load predefined functions into the table
        for (Function function : functionsList) {
            model.addRow(new Object[]{function.getHour(), function.getDay(), function.getCinema(), function.getRoom(), function.getFormat(), function.getDate()});
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Movie selector
        movieSelector = new JComboBox<>();
        for (Movies movie : moviesList) {
            movieSelector.addItem(movie.getTitle());
        }

        // Function selector
        functionSelector = new JComboBox<>();
        for (Function function : functionsList) {
            functionSelector.addItem(function.getCinema() + " - " + function.getRoom() + " (" + function.getHour() + ")");
        }

        // Seats grid
        seatsGrid = new JPanel(new GridLayout(5, 5, 5, 5));
        updateSeatsGrid();

        // Action listeners for selectors
        movieSelector.addActionListener(e -> updateSeatsGrid());
        functionSelector.addActionListener(e -> updateSeatsGrid());

        // Add components to panel
        JPanel selectorsPanel = new JPanel(new GridLayout(1, 2));
        selectorsPanel.add(movieSelector);
        selectorsPanel.add(functionSelector);

        panel.add(selectorsPanel, BorderLayout.NORTH);
        panel.add(seatsGrid, BorderLayout.CENTER);
        return panel;
    }

    private void updateSeatsGrid() {
        seatsGrid.removeAll();

        // Get selected movie and function
        String selectedMovie = (String) movieSelector.getSelectedItem();
        String selectedFunction = (String) functionSelector.getSelectedItem();
        String key = selectedMovie + " - " + selectedFunction;

        // Get reserved seats for the selected combination
        ArrayList<Seat> reservedSeats = reservedSeatsMap.getOrDefault(key, new ArrayList<>());

        for (Seat seat : seatsList) {
            JButton seatButton = new JButton(seat.getSeat());
            seatButton.setBackground(reservedSeats.contains(seat) ? Color.RED : Color.GREEN);
            seatButton.addActionListener(e -> {
                if (reservedSeats.contains(seat)) {
                    reservedSeats.remove(seat);
                    seatButton.setBackground(Color.GREEN);
                } else {
                    reservedSeats.add(seat);
                    seatButton.setBackground(Color.RED);
                }
                reservedSeatsMap.put(key, reservedSeats);
            });
            seatsGrid.add(seatButton);
        }

        seatsGrid.revalidate();
        seatsGrid.repaint();
    }
}