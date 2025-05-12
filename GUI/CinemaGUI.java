package GUI;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Objects.City;
import Objects.Cinema;
import Objects.Function;
import Objects.Movies;
import DB.DBManager; 

public class CinemaGUI {
    private JFrame frame;
    private JComboBox<City> citySelector;
    private JComboBox<Cinema> theaterSelector;
    private JComboBox<Function> functionSelector;
    private JComboBox<String> comboSelector;
    private JButton[][] seats;
    private JTextArea cartArea;
    private java.util.List<String> cart;
    private Map<String, Integer> comboPrices;

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
        // Initialize cities
        City medellin = new City("Medellin");
        City bogota = new City("Bogota");
        cities.add(medellin);
        cities.add(bogota);

        // Initialize cinemas
        for (int i = 1; i <= 3; i++) {
            cinemas.add(new Cinema("Cine Medellin " + i));
            cinemas.add(new Cinema("Cine Bogota " + i));
        }

        // Initialize movies
        Movies m1 = new Movies("Avengers", "Action", "PG-13", "Superheroes saving the world");
        Movies m2 = new Movies("Barbie", "Fantasy", "PG", "A doll's journey");
        Movies m3 = new Movies("Oppenheimer", "Drama", "R", "The atomic bomb story");

        movies.add(m1);
        movies.add(m2);
        movies.add(m3);

        // Initialize functions
        functionsList.add(new Function(m1, "3:00 PM"));
        functionsList.add(new Function(m2, "5:00 PM"));
        functionsList.add(new Function(m3, "8:00 PM"));
        
        // Initialize combo prices
        comboPrices = new HashMap<>();
        comboPrices.put("No Combo", 0);
        comboPrices.put("Personal Combo (Popcorn + Soda)", 8000);
        comboPrices.put("Duo Combo (2 Popcorns + 2 Sodas)", 12000);
        comboPrices.put("Family Combo (4 Popcorns + 4 Sodas + Nachos)", 20000);
    }

    private void createAndShowGUI() {
        // Create main frame
        frame = new JFrame("Cinema Ticket System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // Create top panel for selections
        JPanel selectionPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // City Selector
        JPanel cityPanel = new JPanel(new BorderLayout());
        cityPanel.add(new JLabel("Select City:"), BorderLayout.NORTH);
        citySelector = new JComboBox<>(cities.toArray(new City[0]));
        cityPanel.add(citySelector, BorderLayout.CENTER);
        selectionPanel.add(cityPanel);

        // Theater Selector
        JPanel theaterPanel = new JPanel(new BorderLayout());
        theaterPanel.add(new JLabel("Select Theater:"), BorderLayout.NORTH);
        theaterSelector = new JComboBox<>();
        theaterPanel.add(theaterSelector, BorderLayout.CENTER);
        selectionPanel.add(theaterPanel);

        // Function (Movie) Selector
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.add(new JLabel("Select Showtime:"), BorderLayout.NORTH);
        functionSelector = new JComboBox<>();
        functionPanel.add(functionSelector, BorderLayout.CENTER);
        selectionPanel.add(functionPanel);

        // Combo Selector
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.add(new JLabel("Select Combo:"), BorderLayout.NORTH);
        comboSelector = new JComboBox<>(comboPrices.keySet().toArray(new String[0]));
        comboPanel.add(comboSelector, BorderLayout.CENTER);
        selectionPanel.add(comboPanel);

        // Movie details panel
        JPanel movieDetailsPanel = new JPanel(new BorderLayout());
        JTextArea movieDetails = new JTextArea(3, 20);
        movieDetails.setEditable(false);
        movieDetails.setLineWrap(true);
        movieDetails.setWrapStyleWord(true);
        movieDetailsPanel.add(new JLabel("Movie Details:"), BorderLayout.NORTH);
        movieDetailsPanel.add(new JScrollPane(movieDetails), BorderLayout.CENTER);
        
        // Ticket Price Panel
        JPanel ticketPricePanel = new JPanel(new BorderLayout());
        JLabel ticketPriceLabel = new JLabel("Base Ticket Price: $15,000");
        ticketPricePanel.add(ticketPriceLabel, BorderLayout.NORTH);
        
        // View Tickets Button Panel
        JPanel viewTicketsPanel = new JPanel(new BorderLayout());
        JButton viewTicketsButton = new JButton("View All Tickets");
        viewTicketsButton.addActionListener(e -> showTicketsWindow());
        viewTicketsPanel.add(viewTicketsButton, BorderLayout.CENTER);
        
        // Add additional panels to the second row
        selectionPanel.add(movieDetailsPanel);
        selectionPanel.add(ticketPricePanel);
        selectionPanel.add(viewTicketsPanel);
        
        // Legend Panel
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

        // City selector listener
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

        // Theater selector listener
        theaterSelector.addActionListener(e -> {
            functionSelector.removeAllItems();
            for (Function f : functionsList) {
                functionSelector.addItem(f);
            }
            if (functionSelector.getItemCount() > 0) {
                functionSelector.setSelectedIndex(0);
            }
        });

        // Function selector listener
        functionSelector.addActionListener(e -> {
            Function selectedFunction = (Function) functionSelector.getSelectedItem();
            if (selectedFunction != null) {
                // Update movie details
                Movies movie = selectedFunction.getMovie();
                movieDetails.setText(
                    "Title: " + movie.getTitle() + "\n" +
                    "Genre: " + movie.getGenre() + "\n" +
                    "Rating: " + movie.getClassification() + "\n" +
                    "Synopsis: " + movie.getSinopsis()
                );
                
                updateSeatsAvailability();
            }
        });
        
        // Combo selector listener to show price
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
                JButton btn = new JButton((char)('A' + i) + String.valueOf(j + 1));
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

        // Cart Area
        cart = new ArrayList<>();
        cartArea = new JTextArea(10, 40);
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder("Your Selected Tickets"));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(e -> {
            if (!cart.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(frame, 
                    "Are you sure you want to clear all selected tickets?", 
                    "Confirm Clear", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (response == JOptionPane.YES_OPTION) {
                    cart.clear();
                    updateSeatsAvailability();
                    updateCartDisplay();
                }
            }
        });

        JButton purchaseButton = new JButton("Purchase Tickets");
        purchaseButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Please select at least one seat before purchasing.", 
                    "No Tickets Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Show confirmation dialog with total
            int total = calculateTotal();
            int response = JOptionPane.showConfirmDialog(frame, 
                "Total purchase amount: $" + total + "\nProceed with purchase?", 
                "Confirm Purchase", 
                JOptionPane.YES_NO_OPTION);
                
            if (response == JOptionPane.YES_OPTION) {
                // Confirm all tickets in the cart
                boolean allConfirmed = confirmAllTickets();
                if (allConfirmed) {
                    JOptionPane.showMessageDialog(frame, 
                        "Purchase successful! Thank you for your purchase.", 
                        "Purchase Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cart.clear();
                    updateCartDisplay();
                    updateSeatsAvailability();
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "There was an error processing some tickets. Please try again.", 
                        "Purchase Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonsPanel.add(clearButton);
        buttonsPanel.add(purchaseButton);

        // Assemble content panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(cartScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(cinemaPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        // Assemble main frame
        frame.add(selectionPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        // Trigger initial selection
        if (citySelector.getItemCount() > 0) {
            citySelector.setSelectedIndex(0);
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleSeatSelection(int row, int col) {
        // Validate function and combo selection
        Function selectedFunction = (Function) functionSelector.getSelectedItem();
        String combo = (String) comboSelector.getSelectedItem();
        Cinema theater = (Cinema) theaterSelector.getSelectedItem();
        City city = (City) citySelector.getSelectedItem();

        if (selectedFunction == null || combo == null || theater == null || city == null) {
            JOptionPane.showMessageDialog(frame, 
                "Please select City, Theater, Showtime, and Combo before selecting a seat.", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check seat availability
        String seatCode = (char)('A' + row) + String.valueOf(col + 1);
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

        // Calculate ticket price
        int comboPrice = comboPrices.getOrDefault(combo, 0);
        int price = 15000 + comboPrice;

        // Create ticket details
        String item = String.format("%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s $%,d\n%s\n",
            "Movie:", selectedFunction.getMovie().getTitle(),
            "Time:", selectedFunction.getTime(),
            "Theater:", theater.getNombre(),
            "City:", city.getName(),
            "Seat:", seatCode,
            "Total:", price,
            "-------------------------------------------");

        // Save ticket to database - FIX FOR LINE 194
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
            
            if (!saved) {
                JOptionPane.showMessageDialog(frame, 
                    "There was an error saving this ticket. Please try again.", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update UI
            JButton btn = seats[row][col];
            btn.setBackground(Color.BLUE);
            cart.add(item);
            updateCartDisplay();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error: " + ex.getMessage(), 
                "Ticket Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSeatsAvailability() {
        Function selectedFunction = (Function) functionSelector.getSelectedItem();
        if (selectedFunction == null) return;

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                String seatCode = (char)('A' + i) + String.valueOf(j + 1);
                JButton seat = seats[i][j];
                
                // Check seat availability
                boolean isAvailable = DBManager.isSeatAvailable(
                    selectedFunction.getMovie().getTitle(), 
                    selectedFunction.getTime(), 
                    seatCode
                );
                
                // Update seat color and availability
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
        cartArea.setText("");
        if (cart.isEmpty()) {
            cartArea.append("No tickets selected yet.\nSelect seats to add tickets to your cart.");
            return;
        }
        
        for (String item : cart) {
            cartArea.append(item);
        }
        
        // Add total at the bottom
        int total = calculateTotal();
        cartArea.append(String.format("\nTOTAL: $%,d", total));
    }
    
    private int calculateTotal() {
        int total = 0;
        try (ResultSet rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                String status = rs.getString("status");
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
        
        try (ResultSet rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                String status = rs.getString("status");
                if ("reserved".equals(status)) {
                    String movie = rs.getString("movie");
                    String time = rs.getString("time");
                    String seat = rs.getString("seat");
                    
                    boolean confirmed = DBManager.confirmPurchase(movie, time, seat);
                    if (!confirmed) {
                        allSuccessful = false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            allSuccessful = false;
        }
        
        return allSuccessful;
    }

   private void showTicketsWindow() {
    JFrame ticketFrame = new JFrame("Tickets");
    ticketFrame.setSize(800, 500);
    ticketFrame.setLayout(new BorderLayout());

    String[] columns = {"ID", "Movie", "Time", "Theater", "City", "Seat", "Combo", "Total", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    JTable table = new JTable(model) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make all cells non-editable
        }
    };
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.getTableHeader().setReorderingAllowed(false);

    // Populate the table with data
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

    // AFTER populating data, now set the renderer for the status column
    if (table.getColumnCount() >= 9) { // Ensure status column exists
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    if ("purchased".equals(status)) {
                        c.setForeground(Color.GREEN.darker());
                    } else if ("reserved".equals(status)) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(table.getForeground()); // Reset to default
                    }
                }
                
                return c;
            }
        });
    }

    // Confirm Purchase Button
    JButton confirmPurchaseButton = new JButton("Confirm Selected Tickets");
    confirmPurchaseButton.addActionListener(e -> {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(ticketFrame, 
                "Please select tickets to confirm.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmedCount = 0;
        for (int row : selectedRows) {
            String movie = (String) model.getValueAt(row, 1);
            String time = (String) model.getValueAt(row, 2);
            String seat = (String) model.getValueAt(row, 5);
            String status = (String) model.getValueAt(row, 8);

            // Only confirm reserved tickets
            if ("reserved".equals(status)) {
                if (DBManager.confirmPurchase(movie, time, seat)) {
                    confirmedCount++;
                    // Update status in the table
                    model.setValueAt("purchased", row, 8);
                }
            }
        }

        JOptionPane.showMessageDialog(ticketFrame, 
            "Confirmed " + confirmedCount + " ticket(s).", 
            "Purchase Confirmation", 
            JOptionPane.INFORMATION_MESSAGE);
    });
    
    // Cancel Reservation Button
    JButton cancelButton = new JButton("Cancel Selected Reservations");
    cancelButton.addActionListener(e -> {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(ticketFrame, 
                "Please select reservations to cancel.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm cancelation
        int response = JOptionPane.showConfirmDialog(ticketFrame, 
            "Are you sure you want to cancel the selected reservations?", 
            "Confirm Cancelation", 
            JOptionPane.YES_NO_OPTION);
            
        if (response != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Process selected rows in reverse order to avoid index problems
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int row = selectedRows[i];
            String status = (String) model.getValueAt(row, 8);
            
            // Only cancel reserved tickets
            if ("reserved".equals(status)) {
                String movie = (String) model.getValueAt(row, 1);
                String time = (String) model.getValueAt(row, 2);
                String seat = (String) model.getValueAt(row, 5);
                
                // Delete from database
                try {
                    boolean deleted = deleteReservation(movie, time, seat);
                    if (deleted) {
                        // Remove from model
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
        
        // Update main view
        updateSeatsAvailability();
    });

    // Refresh Button
    JButton refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(e -> {
        // Refresh the table
        model.setRowCount(0);
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
        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
        }
        
        // Update seat availability
        updateSeatsAvailability();
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(confirmPurchaseButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(refreshButton);

    ticketFrame.add(new JScrollPane(table), BorderLayout.CENTER);
    ticketFrame.add(buttonPanel, BorderLayout.SOUTH);
    ticketFrame.setLocationRelativeTo(null);
    ticketFrame.setVisible(true);
   }
  // Helper method to delete a reservation
    private boolean deleteReservation(String movie, String time, String seat) {
        // Create SQL to delete the reservation
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:cine.db");
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM tickets WHERE movie = ? AND time = ? AND seat = ? AND status = 'reserved'")) {
            
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // Ensure Swing components are created on the Event Dispatch Thread
        SwingUtilities.invokeLater(CinemaGUI::new);
    }
}