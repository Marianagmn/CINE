package GUI;

import DB.DBManager;
import Objects.Cinema;
import Objects.City;
import Objects.Function;
import Objects.Movies;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CinemaGUI {
    // GUI components
    private JFrame frame;
    private JComboBox<City> citySelector; // Dropdown to select city
    private JComboBox<Cinema> theaterSelector;  // Dropdown to select theater
    private JComboBox<Function> functionSelector;  // Dropdown to select movie showtime
    private JComboBox<String> comboSelector;  // Dropdown to select combo
    private JButton[][] seats;  // Matrix of seat buttons
    private JTextArea cartArea;  // Text area showing selected tickets
    private java.util.List<String> cart;   // List to store selected tickets
    private Map<String, Integer> comboPrices; // Map storing combo names and prices

    // Lists to hold available cities, cinemas, movies, and showtimes
    private java.util.List<City> cities = new ArrayList<>();
    private java.util.List<Cinema> cinemas = new ArrayList<>();
    private java.util.List<Movies> movies = new ArrayList<>();
    private java.util.List<Function> functionsList = new ArrayList<>();

    public CinemaGUI() {
        // Ensure database is initialized first
        DBManager.initDatabase();
        initializeData();
        createAndShowGUI();
    }

    private void initializeData() {
        // Add predefined cities
        City medellin = new City("Medellin");
        City bogota = new City("Bogota");
        cities.add(medellin);
        cities.add(bogota);

        // Initialize cinemas with specific names
        // Add predefined cinemas for each city
        // Medellin cinemas
        cinemas.add(new Cinema("Cinema Medellin Downtown"));
        cinemas.add(new Cinema("Cinema Medellin North"));
        cinemas.add(new Cinema("Cinema Medellin Sur"));
        // Bogota cinemas
        cinemas.add(new Cinema("Cinema Bogota Downtown"));
        cinemas.add(new Cinema("Cinema Bogota North"));
        cinemas.add(new Cinema("Cinema Bogota South"));

        // Initialize movies in English and add movies with title, genre, classification, and synopsis
        Movies m1 = new Movies("Avengers", "Action", "PG-13", "Superheroes saving the world.");
        Movies m2 = new Movies("Barbie", "Fantasy", "PG", "A doll's journey.");
        Movies m3 = new Movies("Oppenheimer", "Drama", "R", "The atomic bomb story.");
        Movies m4 = new Movies("The Lion King", "Animation", "PG", "A young lion prince's journey.");
        Movies m5 = new Movies("Inception", "Sci-Fi", "PG-13", "A thief steals secrets through dream-sharing.");
        Movies m6 = new Movies("Frozen II", "Animation", "PG", "Elsa and Anna embark on a new adventure.");

        movies.add(m1);
        movies.add(m2);
        movies.add(m3);
        movies.add(m4);
        movies.add(m5);
        movies.add(m6);

        // Initialize functions (movie showtimes)
        // Original afternoon/evening functions
        functionsList.add(new Function(m1, "3:00 PM"));
        functionsList.add(new Function(m2, "5:00 PM"));
        functionsList.add(new Function(m3, "8:00 PM"));

        // Morning functions (before 12 PM) with special pricing
        functionsList.add(new Function(m4, "9:00 AM"));
        functionsList.add(new Function(m5, "10:30 AM"));
        functionsList.add(new Function(m6, "11:45 AM"));

        // Initialize combo prices
        comboPrices = new HashMap<>();
        comboPrices.put("No Combo", 0);
        comboPrices.put("Personal Combo (Popcorn + Soda)", 8000);
        comboPrices.put("Duo Combo (2 Popcorns + 2 Sodas)", 12000);
        comboPrices.put("Family Combo (4 Popcorns + 4 Sodas + Nachos)", 20000);
    }

    private void createAndShowGUI() {
        // Create and configure main window
        frame = new JFrame("Cinema Ticket System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // Panel with selectors (city, theater, time, combo, etc.)
        JPanel selectionPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel for city selection
        JPanel cityPanel = new JPanel(new BorderLayout());
        cityPanel.add(new JLabel("Select City:"), BorderLayout.NORTH);
        citySelector = new JComboBox<>(cities.toArray(new City[0]));
        cityPanel.add(citySelector, BorderLayout.CENTER);
        selectionPanel.add(cityPanel);

        // Panel for theater selection
        JPanel theaterPanel = new JPanel(new BorderLayout());
        theaterPanel.add(new JLabel("Select Theater:"), BorderLayout.NORTH);
        theaterSelector = new JComboBox<>();
        theaterPanel.add(theaterSelector, BorderLayout.CENTER);
        selectionPanel.add(theaterPanel);

        // Panel for function (showtime) selection
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.add(new JLabel("Select Showtime:"), BorderLayout.NORTH);
        functionSelector = new JComboBox<>();
        functionPanel.add(functionSelector, BorderLayout.CENTER);
        selectionPanel.add(functionPanel);

        // Panel for combo selection
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.add(new JLabel("Select Combo:"), BorderLayout.NORTH);
        comboSelector = new JComboBox<>(comboPrices.keySet().toArray(new String[0]));
        comboPanel.add(comboSelector, BorderLayout.CENTER);
        selectionPanel.add(comboPanel);

        // Panel to show movie details
        JPanel movieDetailsPanel = new JPanel(new BorderLayout());
        JTextArea movieDetails = new JTextArea(3, 20);
        movieDetails.setEditable(false);
        movieDetails.setLineWrap(true);
        movieDetails.setWrapStyleWord(true);
        movieDetailsPanel.add(new JLabel("Movie Details:"), BorderLayout.NORTH);
        movieDetailsPanel.add(new JScrollPane(movieDetails), BorderLayout.CENTER);

        // Panel to show ticket base price
        JPanel ticketPricePanel = new JPanel(new BorderLayout());
        JLabel ticketPriceLabel = new JLabel("Base Ticket Price: $15,000");
        ticketPricePanel.add(ticketPriceLabel, BorderLayout.NORTH);

        // Button to view all tickets
        JPanel viewTicketsPanel = new JPanel(new BorderLayout());
        JButton viewTicketsButton = new JButton("View All Tickets");
        viewTicketsButton.addActionListener(e -> showTicketsWindow());
        viewTicketsPanel.add(viewTicketsButton, BorderLayout.CENTER);

        // Add panels for details, price, view button
        selectionPanel.add(movieDetailsPanel);
        selectionPanel.add(ticketPricePanel);
        selectionPanel.add(viewTicketsPanel);

        // Legend to explain seat colors
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton availableSample = new JButton("Available");
        availableSample.setBackground(Color.GREEN);
        availableSample.setEnabled(false);
        JButton reservedSample = new JButton("Reserved");
        reservedSample.setBackground(Color.RED);
        reservedSample.setEnabled(false);
        JButton selectedSample = new JButton("Selected");
        selectedSample.setBackground(Color.BLUE);
        selectedSample.setEnabled(false);

        legendPanel.add(new JLabel("Legend: "));
        legendPanel.add(availableSample);
        legendPanel.add(reservedSample);
        legendPanel.add(selectedSample);

        selectionPanel.add(legendPanel);

        // Listener for city selector to filter theaters
        citySelector.addActionListener(e -> {
            theaterSelector.removeAllItems();
            City selected = (City) citySelector.getSelectedItem();
            if (selected != null) {
                for (Cinema c : cinemas) {
                    if (c.getNombre().contains(selected.getName())) {
                        theaterSelector.addItem(c);
                    }
                }
                if (theaterSelector.getItemCount() > 0) {
                    theaterSelector.setSelectedIndex(0);
                }
            }
        });

        // Listener for theater selector to load functions
        theaterSelector.addActionListener(e -> {
            functionSelector.removeAllItems();
            for (Function f : functionsList) {
                functionSelector.addItem(f);
            }
            if (functionSelector.getItemCount() > 0) {
                functionSelector.setSelectedIndex(0);
            }
        });

        // Listener for function selector to show movie details and update seats
        functionSelector.addActionListener(e -> {
            Function selectedFunction = (Function) functionSelector.getSelectedItem();
            if (selectedFunction != null) {
                // Update movie details
                Movies movie = selectedFunction.getMovie();
                movieDetails.setText(
                        "Title: " + movie.getTitle() + "\n"
                        + "Genre: " + movie.getGenre() + "\n"
                        + "Rating: " + movie.getClassification() + "\n"
                        + "Synopsis: " + movie.getSinopsis()
                );

                updateSeatsAvailability();
            }
        });

        
        // Listener to update ticket price when combo is changed
        comboSelector.addActionListener(e -> {
            String selectedCombo = (String) comboSelector.getSelectedItem();
            if (selectedCombo != null) {
                int comboPrice = comboPrices.get(selectedCombo);
                if (comboPrice > 0) {
                    ticketPriceLabel.setText("Base Ticket Price: $15,000 + Combo: $" + comboPrice);
                } else {
                    ticketPriceLabel.setText("Base Ticket Price: $15,000");
                }
            }
        });

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Screen panel (to simulate cinema screen)
        JPanel screenPanel = new JPanel();
        screenPanel.setPreferredSize(new Dimension(500, 30));
        screenPanel.setBackground(Color.GRAY);
        screenPanel.setBorder(BorderFactory.createTitledBorder("Screen"));

        // Seats Panel
        JPanel seatsPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        seatsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        seats = new JButton[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btn = new JButton((char) ('A' + i) + String.valueOf(j + 1));
                btn.setPreferredSize(new Dimension(60, 60));
                btn.setBackground(Color.GREEN);
                int row = i, col = j;
                btn.addActionListener(e -> handleSeatSelection(row, col));
                seats[i][j] = btn;
                seatsPanel.add(btn);
            }
        }

        // Combine screen and seats in a cinema panel
        JPanel cinemaPanel = new JPanel(new BorderLayout());
        cinemaPanel.add(screenPanel, BorderLayout.NORTH);
        cinemaPanel.add(seatsPanel, BorderLayout.CENTER);

        // Initialize the cart (selected tickets list)
        cart = new ArrayList<>();
        cartArea = new JTextArea(10, 40);
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder("Your Selected Tickets"));

        // Buttons panel placed at the bottom right of the interface
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        // Button to clear the shopping cart
        JButton clearButton = new JButton("Clear Cart"); // Button labeled "Clear Cart"

        
        // Action performed when the clear cart button is clicked
        clearButton.addActionListener(e -> {
            // Check if the cart is not empty
            if (!cart.isEmpty()) {
                // Show a confirmation dialog to the user
                int response = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to clear all selected tickets?",
                        "Confirm Clear",  // Dialog title
                        JOptionPane.YES_NO_OPTION); // Options: Yes or No

                        // If the user confirms to clear the cart
                if (response == JOptionPane.YES_OPTION) {
                    cart.clear(); // Clear the cart
                    updateSeatsAvailability();  // Update seat availability (make seats available again if reserved)
                    updateCartDisplay();  // Update the cart display area to reflect the empty cart
                }
            }
        });

        // Button to initiate ticket purchase
        JButton purchaseButton = new JButton("Purchase Tickets");
        // Action performed when the purchase button is clicked
        purchaseButton.addActionListener(e -> {
             // Check if the cart is empty (no tickets selected)
            if (cart.isEmpty()) {
                // Show a warning message prompting the user to select at least one seat
                JOptionPane.showMessageDialog(frame,
                        "Please select at least one seat before purchasing.",
                        "No Tickets Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;  // Exit the event handler since no tickets are selected
            }

            // Show confirmation dialog with the total purchase amount
            int total = calculateTotal();
            int response = JOptionPane.showConfirmDialog(frame,
                    "Total purchase amount: $" + total + "\nProceed with purchase?",
                    "Confirm Purchase",
                    JOptionPane.YES_NO_OPTION);

            
                // If user confirms the purchase
            if (response == JOptionPane.YES_OPTION) {
                // Attempt to confirm all tickets in the cart in the database
                boolean allConfirmed = confirmAllTickets();
                if (allConfirmed) {
                    // If successful, show a success message
                    JOptionPane.showMessageDialog(frame,
                            "Purchase successful! Thank you for your purchase.",
                            "Purchase Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                            // Clear the cart and update the UI accordingly
                    cart.clear();
                    updateCartDisplay();
                    updateSeatsAvailability();
                } else {
                    // Show an error message if there was an issue processing tickets
                    JOptionPane.showMessageDialog(frame,
                            "There was an error processing some tickets. Please try again.",
                            "Purchase Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Add the Clear and Purchase buttons to the buttons panel
        buttonsPanel.add(clearButton);
        buttonsPanel.add(purchaseButton);

        // Create a right panel with BorderLayout to hold the cart and buttons
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(cartScrollPane, BorderLayout.CENTER); // Cart display in the center
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);  // Buttons panel at the bottom
/** Add the cinema seating panel to the center of content panel,
 * and the rightPanel (cart + buttons) to the east side
 * */ 
        contentPanel.add(cinemaPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

/** Add the selection panel (with dropdowns) to the top (north)
 * and the content panel to the center of the main frame
 * */ 
        frame.add(selectionPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        // Automatically select the first city in the city selector if available
        if (citySelector.getItemCount() > 0) {
            citySelector.setSelectedIndex(0);
        }

        // Center the frame on screen and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // Get currently selected function (showtime), combo, theater, and city
    private void handleSeatSelection(int row, int col) {
        
        Function selectedFunction = (Function) functionSelector.getSelectedItem();
        String combo = (String) comboSelector.getSelectedItem();
        Cinema theater = (Cinema) theaterSelector.getSelectedItem();
        City city = (City) citySelector.getSelectedItem();

         // Validate that all necessary selections have been made before allowing seat selection
        if (selectedFunction == null || combo == null || theater == null || city == null) {
            JOptionPane.showMessageDialog(frame,
                    "Please select City, Theater, Showtime, and Combo before selecting a seat.",
                    "Selection Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build seat code string (e.g., "A1", "B5") based on row and column indices
        String seatCode = (char) ('A' + row) + String.valueOf(col + 1);
        // Check seat availability in the database for the selected movie, time, and seat
        if (!DBManager.isSeatAvailable(
                selectedFunction.getMovie().getTitle(),
                selectedFunction.getTime(),
                seatCode)) {
            JOptionPane.showMessageDialog(frame,
                    "Sorry, this seat is already taken.",
                    "Seat Unavailable",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate the price of the ticket by adding the base price and combo price
        int comboPrice = comboPrices.getOrDefault(combo, 0);
        int price = 15000 + comboPrice;

        // Create the ticket details string with formatted movie, time, theater, city, seat, and total price
        String item = String.format("%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s $%,d\n%s\n",
                "Movie:", selectedFunction.getMovie().getTitle(),
                "Time:", selectedFunction.getTime(),
                "Theater:", theater.getNombre(),
                "City:", city.getName(),
                "Seat:", seatCode,
                "Total:", price,
                "-------------------------------------------");

        // Attempt to save the ticket information in the database
        try {
            boolean saved = DBManager.saveTicket(
                    selectedFunction.getMovie().getTitle(),
                    selectedFunction.getTime(),
                    theater.getNombre(),
                    city.getName(),
                    seatCode,
                    combo,
                    price
            );

             // Show error message if ticket could not be saved and exit the method
            if (!saved) {
                JOptionPane.showMessageDialog(frame,
                        "There was an error saving this ticket. Please try again.",
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update seat button color to blue indicating selection
            JButton btn = seats[row][col];
            btn.setBackground(Color.BLUE);
            // Add the ticket details to the cart and update the cart display area
            cart.add(item);
            updateCartDisplay();
        } catch (Exception ex) {
             // Print exception stack trace and show error dialog if something unexpected happens

            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error: " + ex.getMessage(),
                    "Ticket Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSeatsAvailability() {
         // Get the currently selected function (showtime)
        Function selectedFunction = (Function) functionSelector.getSelectedItem();
        if (selectedFunction == null) {
            return; // If no function selected, exit early
        }

         // Loop through all seat buttons by row and column
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                String seatCode = (char) ('A' + i) + String.valueOf(j + 1);
                JButton seat = seats[i][j];

                // Check seat availability from database
                boolean isAvailable = DBManager.isSeatAvailable(
                        selectedFunction.getMovie().getTitle(),
                        selectedFunction.getTime(),
                        seatCode
                );

                // Set seat button color and enable status based on availability
                if (isAvailable) {
                    seat.setBackground(Color.GREEN);
                    seat.setEnabled(true);
                } else {
                    seat.setBackground(Color.RED);
                    seat.setEnabled(false);
                }
            }
        }
    }

    private void updateCartDisplay() {
        cartArea.setText(""); // Clear cart display area
        // If cart is empty, show placeholder message
        if (cart.isEmpty()) {
            cartArea.append("No tickets selected yet.\nSelect seats to add tickets to your cart.");
            return;
        }

        // Otherwise, append all ticket details stored in cart to the display
        for (String item : cart) {
            cartArea.append(item);
        }

        // Append the total cost of tickets in the cart at the bottom
        int total = calculateTotal();
        cartArea.append(String.format("\nTOTAL: $%,d", total));
    }

    private int calculateTotal() {
        int total = 0;
        // Query database for all tickets
        try (ResultSet rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                String status = rs.getString("status");
                // Sum prices only for tickets with status "reserved"
                if ("reserved".equals(status)) {
                    total += rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    private boolean confirmAllTickets() {
        boolean allSuccessful = true;

        // Query all tickets to confirm purchase for those reserved
        try (ResultSet rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                String status = rs.getString("status");
                if ("reserved".equals(status)) {
                    String movie = rs.getString("movie");
                    String time = rs.getString("time");
                    String seat = rs.getString("seat");

                     // Attempt to confirm purchase in database
                    boolean confirmed = DBManager.confirmPurchase(movie, time, seat);
                    if (!confirmed) {
                        allSuccessful = false;  // Mark failure if any ticket fails to confirm
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            allSuccessful = false;
        }

        return allSuccessful;
        // Return true if all ticket confirmations were successful; 
        // false if any confirmation failed or an exception occurred
    }

    private void showTicketsWindow() {
        // Create a new window (frame) for displaying tickets
        JFrame ticketFrame = new JFrame("Tickets");
        ticketFrame.setSize(800, 500);
        ticketFrame.setLayout(new BorderLayout());

        // Define column headers for the ticket table
        String[] columns = {"ID", "Movie", "Time", "Theater", "City", "Seat", "Combo", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Create a JTable with the model; make all cells non-editable
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Populate the table with ticket data from the database
        try (ResultSet rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("movie"),
                    rs.getString("time"),
                    rs.getString("theater"),
                    rs.getString("city"),
                    rs.getString("seat"),
                    rs.getString("combo"),
                    String.format("$%,d", rs.getInt("total")),
                    rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ticketFrame,
                    "Error loading tickets: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Customize the rendering of the "Status" column to color code statuses
        if (table.getColumnCount() >= 9) { // Make sure the status column exists
            table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (value != null) {
                        String status = value.toString();
                        if ("purchased".equals(status)) {
                            c.setForeground(Color.GREEN.darker()); // Purchased tickets shown in dark green
                        } else if ("reserved".equals(status)) {
                            c.setForeground(Color.RED);   // Reserved tickets shown in red
                        } else {
                            c.setForeground(table.getForeground()); // Reset to default
                        }
                    }

                    return c; // Return the component used to render the cell, with the appropriate color applied
                }
            });
        }

        // Confirm Purchase Button
        JButton confirmPurchaseButton = new JButton("Confirm Selected Tickets");
        confirmPurchaseButton.addActionListener(e -> {
            // Get selected rows in the table
            int[] selectedRows = table.getSelectedRows();
            // If no rows are selected, show warning and return
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(ticketFrame,
                        "Please select tickets to confirm.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmedCount = 0;
            // Iterate over selected rows
            for (int row : selectedRows) {
                // Retrieve ticket info from the table model
                String movie = (String) model.getValueAt(row, 1);
                String time = (String) model.getValueAt(row, 2);
                String seat = (String) model.getValueAt(row, 5);
                String status = (String) model.getValueAt(row, 8);

                // Only confirm tickets that are currently "reserved"
                if ("reserved".equals(status)) {
                    // Attempt to confirm purchase in database
                    if (DBManager.confirmPurchase(movie, time, seat)) {
                        confirmedCount++;
                        // Update ticket status in table to "purchased"
                        model.setValueAt("purchased", row, 8);
                    }
                }
            }

            // Show confirmation message with number of confirmed tickets
            JOptionPane.showMessageDialog(ticketFrame,
                    "Confirmed " + confirmedCount + " ticket(s).",
                    "Purchase Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Cancel Reservation Button
        JButton cancelButton = new JButton("Cancel Selected Reservations");
        cancelButton.addActionListener(e -> {
            // Get selected rows in the table
            int[] selectedRows = table.getSelectedRows();
            // If no rows selected, show warning and return
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(ticketFrame,
                        "Please select reservations to cancel.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ask user to confirm cancellation
            int response = JOptionPane.showConfirmDialog(ticketFrame,
                    "Are you sure you want to cancel the selected reservations?",
                    "Confirm Cancelation",
                    JOptionPane.YES_NO_OPTION);

                    // If user selects NO, exit the action
            if (response != JOptionPane.YES_OPTION) {
                return;
            }

            // Iterate over selected rows in reverse order to safely remove rows
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                String status = (String) model.getValueAt(row, 8);

                // Only cancel tickets that are currently "reserved"
                if ("reserved".equals(status)) {
                    String movie = (String) model.getValueAt(row, 1);
                    String time = (String) model.getValueAt(row, 2);
                    String seat = (String) model.getValueAt(row, 5);

                    // Delete reservation from the database
                    try {
                        boolean deleted = deleteReservation(movie, time, seat);
                        if (deleted) {
                            // Remove the row from the table model
                            model.removeRow(row);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ticketFrame,
                                "Error canceling reservation: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // Update seat availability in main UI after cancellations
            updateSeatsAvailability();
        });

        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Clear all rows from the table model
            model.setRowCount(0);
            try (ResultSet rs = DBManager.getAllTickets()) {
                // Reload ticket data from the database and populate the table
                while (rs != null && rs.next()) {
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("movie"),
                        rs.getString("time"),
                        rs.getString("theater"),
                        rs.getString("city"),
                        rs.getString("seat"),
                        rs.getString("combo"),
                        String.format("$%,d", rs.getInt("total")),
                        rs.getString("status")
                    };
                    model.addRow(row);
                }
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }

            // Refresh seat availability in main UI
            updateSeatsAvailability();
        });

        // Panel to hold buttons at the bottom of the ticket window
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmPurchaseButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        // Add the table and button panel to the ticket window frame
        ticketFrame.add(new JScrollPane(table), BorderLayout.CENTER);
        ticketFrame.add(buttonPanel, BorderLayout.SOUTH);
        ticketFrame.setLocationRelativeTo(null);
        ticketFrame.setVisible(true);
    }
    
    // Helper method to delete a reservation from the database
    private boolean deleteReservation(String movie, String time, String seat) {
        // Use DBManager to execute a SQL DELETE query for reserved tickets
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "DELETE FROM tickets WHERE movie = ? AND time = ? AND seat = ? AND status = 'reserved'")) {
            
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);
            
            // Execute deletion and return true if at least one row was deleted
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     // MAIN
    public static void main(String[] args) {
        // Ensure Swing components are created on the Event Dispatch Thread
        SwingUtilities.invokeLater(CinemaGUI::new);
    }
}