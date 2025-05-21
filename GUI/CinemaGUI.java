package GUI;

import DB.DBManager;
import Objects.Cinema;
import Objects.City;
import Objects.Function;
import Objects.Movies;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CinemaGUI {

    // GUI components
    private JFrame frame;
    private JComboBox<City> citySelector; // Dropdown to select city
    private JComboBox<Cinema> theaterSelector; // Dropdown to select theater
    private JComboBox<Function> functionSelector; // Dropdown to select movie showtime
    private JComboBox<String> comboSelector; // Dropdown to select combo
    private JComboBox<String> languageSelector; // Dropdown to select language
    private JButton[][] seats; // Matrix of seat buttons
    private JTextArea cartArea; // Text area showing selected tickets
    private JTextArea movieDetails; // Text area for movie details
    private java.util.List<String> cart; // List to store selected tickets
    private Map<String, Integer> comboPrices; // Map storing combo names and prices
    LocalTime currentTime = LocalTime.now();
    LocalTime timeLimit = LocalTime.of(12, 0);

    // Lists to hold available cities, cinemas, movies, and showtimes
    private java.util.List<City> cities = new ArrayList<>(); // ED
    private java.util.List<Cinema> cinemas = new ArrayList<>();
    private java.util.List<Movies> movies = new ArrayList<>();
    private java.util.List<Function> functionsList = new ArrayList<>();

    // Language related
    private String currentLanguage = "en";

    private final Map<String, String> texts_en = new HashMap<>();
    private final Map<String, String> texts_es = new HashMap<>();

    public CinemaGUI() {
        DBManager.initDatabase();
        initializeTexts();
        initializeData();
        createAndShowGUI();
    }

    private void initializeTexts() {
        // English texts
        texts_en.put("app_title", "Cinema Ticket System");
        texts_en.put("label_select_city", "Select City:");
        texts_en.put("label_select_theater", "Select Theater:");
        texts_en.put("label_select_function", "Select Showtime:");
        texts_en.put("label_select_combo", "Select Combo:");
        texts_en.put("label_movie_details", "Movie Details:");
        texts_en.put("label_ticket_price_am", "Ticket price: $15,000");
        texts_en.put("label_ticket_price_pm", "Base Ticket Price: $25,000");
        texts_en.put("btn_view_tickets", "View All Tickets");
        texts_en.put("legend_title", "Legend: ");
        texts_en.put("legend_available", "Available");
        texts_en.put("legend_reserved", "Reserved");
        texts_en.put("legend_selected", "Selected");
        texts_en.put("legend_vip", "VIP");
        texts_en.put("msg_select_before_seat",
                "Please select City, Theater, Showtime, and Combo before selecting a seat.");
        texts_en.put("title_selection_error", "Selection Error");
        texts_en.put("msg_seat_taken", "Sorry, this seat is already taken.");
        texts_en.put("title_seat_unavailable", "Seat Unavailable");
        texts_en.put("msg_save_ticket_error", "There was an error saving this ticket. Please try again.");
        texts_en.put("title_save_error", "Save Error");
        texts_en.put("cart_empty", "No tickets selected yet.\nSelect seats to add tickets to your cart.");
        texts_en.put("btn_clear_cart", "Clear Cart");
        texts_en.put("dialog_confirm_clear", "Are you sure you want to clear all selected tickets?");
        texts_en.put("confirm_clear_title", "Confirm Clear");
        texts_en.put("btn_purchase_tickets", "Purchase Tickets");
        texts_en.put("msg_no_tickets_selected", "Please select at least one seat before purchasing.");
        texts_en.put("title_no_tickets", "No Tickets Selected");
        texts_en.put("confirm_purchase_title", "Confirm Purchase");
        texts_en.put("msg_confirm_purchase", "Total purchase amount: $%d\nProceed with purchase?");
        texts_en.put("msg_purchase_success", "Purchase successful! Thank you for your purchase.");
        texts_en.put("purchase_complete_title", "Purchase Complete");
        texts_en.put("msg_purchase_error", "There was an error processing some tickets. Please try again.");
        texts_en.put("purchase_error_title", "Purchase Error");
        texts_en.put("tickets_title", "Tickets");
        texts_en.put("confirm_selected_tickets", "Confirm Selected Tickets");
        texts_en.put("btn_cancel_reservations", "Cancel Selected Reservations");
        texts_en.put("msg_select_tickets_confirm", "Please select tickets to confirm.");
        texts_en.put("title_no_selection", "No Selection");
        texts_en.put("msg_confirmed_tickets", "Confirmed %d ticket(s).");
        texts_en.put("cancel_selected_reservations", "Cancel Selected Reservations");
        texts_en.put("msg_select_reservations_cancel", "Please select reservations to cancel.");
        texts_en.put("confirm_cancel_title", "Confirm Cancelation");
        texts_en.put("msg_confirm_cancel_reservations", "Are you sure you want to cancel the selected reservations?");
        texts_en.put("error_cancel_reservation", "Error canceling reservation: %s");
        texts_en.put("btn_refresh", "Refresh");
        texts_en.put("legend", "Legend: ");
        texts_en.put("movie_label_title", "Title:");
        texts_en.put("movie_label_genre", "Genre:");
        texts_en.put("movie_label_rating", "Rating:");
        texts_en.put("movie_label_synopsis", "Synopsis:");
        texts_en.put("ticket_label_movie", "Movie:");
        texts_en.put("ticket_label_time", "Time:");
        texts_en.put("ticket_label_theater", "Theater:");
        texts_en.put("ticket_label_city", "City:");
        texts_en.put("ticket_label_seat", "Seat:");
        texts_en.put("ticket_label_total", "Total:");
        texts_en.put("separator_line", "-------------------------------------------");
        texts_en.put("lang_en", "English");
        texts_en.put("lang_es", "Español");

        // Spanish texts
        texts_es.put("app_title", "Sistema de Boletos de Cine");
        texts_es.put("label_select_city", "Seleccione Ciudad:");
        texts_es.put("label_select_theater", "Seleccione Teatro:");
        texts_es.put("label_select_function", "Seleccione Función:");
        texts_es.put("label_select_combo", "Seleccione Combo:");
        texts_es.put("label_movie_details", "Detalles de la Película:");
        texts_es.put("label_ticket_price_am", "Precio del boleto: $15.000");
        texts_es.put("label_ticket_price_pm", "Precio base del boleto: $25.000");
        texts_es.put("btn_view_tickets", "Ver Todos los Boletos");
        texts_es.put("legend_title", "Leyenda: ");
        texts_es.put("legend_available", "Disponible");
        texts_es.put("legend_reserved", "Reservado");
        texts_es.put("legend_selected", "Seleccionado");
        texts_es.put("legend_vip", "VIP");
        texts_es.put("msg_select_before_seat",
                "Por favor seleccione Ciudad, Teatro, Función y Combo antes de elegir un asiento.");
        texts_es.put("title_selection_error", "Error de Selección");
        texts_es.put("msg_seat_taken", "Lo siento, este asiento ya está ocupado.");
        texts_es.put("title_seat_unavailable", "Asiento No Disponible");
        texts_es.put("msg_save_ticket_error", "Hubo un error al guardar este boleto. Por favor intente de nuevo.");
        texts_es.put("title_save_error", "Error al Guardar");
        texts_es.put("cart_empty",
                "No se han seleccionado boletos aún.\nSeleccione asientos para agregar boletos a su carrito.");
        texts_es.put("btn_clear_cart", "Limpiar Carrito");
        texts_es.put("dialog_confirm_clear", "¿Está seguro que desea borrar todos los boletos seleccionados?");
        texts_es.put("confirm_clear_title", "Confirmar Borrado");
        texts_es.put("btn_purchase_tickets", "Comprar Boletos");
        texts_es.put("msg_no_tickets_selected", "Por favor seleccione al menos un asiento antes de comprar.");
        texts_es.put("title_no_tickets", "No Hay Boletos Seleccionados");
        texts_es.put("confirm_purchase_title", "Confirmar Compra");
        texts_es.put("msg_confirm_purchase", "Monto total de la compra: $%d\n¿Desea continuar con la compra?");
        texts_es.put("msg_purchase_success", "¡Compra exitosa! Gracias por su compra.");
        texts_es.put("purchase_complete_title", "Compra Completa");
        texts_es.put("msg_purchase_error", "Hubo un error procesando algunos boletos. Por favor intente de nuevo.");
        texts_es.put("purchase_error_title", "Error en la Compra");
        texts_es.put("tickets_title", "Boletos");
        texts_es.put("confirm_selected_tickets", "Confirmar Boletos Seleccionados");
        texts_es.put("btn_cancel_reservations", "Cancelar Reservas Seleccionadas");
        texts_es.put("msg_select_tickets_confirm", "Por favor seleccione boletos para confirmar.");
        texts_es.put("title_no_selection", "Sin Selección");
        texts_es.put("msg_confirmed_tickets", "Confirmados %d boleto(s).");
        texts_es.put("cancel_selected_reservations", "Cancelar Reservas Seleccionadas");
        texts_es.put("msg_select_reservations_cancel", "Por favor seleccione reservas para cancelar.");
        texts_es.put("confirm_cancel_title", "Confirmar Cancelación");
        texts_es.put("msg_confirm_cancel_reservations", "¿Está seguro que desea cancelar las reservas seleccionadas?");
        texts_es.put("error_cancel_reservation", "Error cancelando la reserva: %s");
        texts_es.put("btn_refresh", "Actualizar");
        texts_es.put("legend", "Leyenda: ");
        texts_es.put("movie_label_title", "Título:");
        texts_es.put("movie_label_genre", "Género:");
        texts_es.put("movie_label_rating", "Clasificación:");
        texts_es.put("movie_label_synopsis", "Sinopsis:");
        texts_es.put("ticket_label_movie", "Película:");
        texts_es.put("ticket_label_time", "Hora:");
        texts_es.put("ticket_label_theater", "Teatro:");
        texts_es.put("ticket_label_city", "Ciudad:");
        texts_es.put("ticket_label_seat", "Asiento:");
        texts_es.put("ticket_label_total", "Total:");
        texts_es.put("separator_line", "-------------------------------------------");
        texts_es.put("lang_en", "Inglés");
        texts_es.put("lang_es", "Español");
    }

    // Method to get text according to current language
    private String getText(String key) {
        if ("es".equals(currentLanguage)) {
            return texts_es.getOrDefault(key, key);
        } else {
            return texts_en.getOrDefault(key, key);
        }
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

        // Initialize movies in English and add movies with title, genre,
        // classification, and synopsis
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

        // Initialize combo prices with keys that will need localization in display
        comboPrices = new LinkedHashMap<>();
        comboPrices.put(getText("combo_no"), 0);
        comboPrices.put(getText("combo_personal"), 8000);
        comboPrices.put(getText("combo_duo"), 12000);
        comboPrices.put(getText("combo_family"), 20000);

        // Key in English
        texts_en.put("combo_no", "No Combo");
        texts_en.put("combo_personal", "Personal Combo (Popcorn + Soda)");
        texts_en.put("combo_duo", "Duo Combo (2 Popcorns + 2 Sodas)");
        texts_en.put("combo_family", "Family Combo (4 Popcorns + 4 Sodas + Nachos)");
        // Key in Spanish
        texts_es.put("combo_no", "Sin Combo");
        texts_es.put("combo_personal", "Combo Personal (Palomitas + Refresco)");
        texts_es.put("combo_duo", "Combo Dúo (2 Palomitas + 2 Refrescos)");
        texts_es.put("combo_family", "Combo Familiar (4 Palomitas + 4 Refrescos + Nachos)");
    }

    private void createAndShowGUI() {
        // Create and configure main window
        frame = new JFrame(getText("app_title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1700, 1040);
        frame.setLayout(new BorderLayout());

        // Panel with selectors (language, city, theater, function, combo, etc.)
        JPanel selectionPanel = new JPanel(new GridLayout(2, 5, 10, 5)); // Added 5th column for language
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Language selector panel
        JPanel languagePanel = new JPanel(new BorderLayout());
        languagePanel.add(new JLabel("Language:"), BorderLayout.NORTH);
        languageSelector = new JComboBox<>(new String[] { getText("lang_en"), getText("lang_es") });
        languageSelector.setSelectedIndex(currentLanguage.equals("es") ? 1 : 0);
        languagePanel.add(languageSelector, BorderLayout.CENTER);
        selectionPanel.add(languagePanel);

        // Panel for city selection
        JPanel cityPanel = new JPanel(new BorderLayout());
        JLabel cityLabel = new JLabel(getText("label_select_city"));
        cityPanel.add(cityLabel, BorderLayout.NORTH);
        citySelector = new JComboBox<>(cities.toArray(new City[0]));
        cityPanel.add(citySelector, BorderLayout.CENTER);
        selectionPanel.add(cityPanel);

        // Panel for theater selection
        JPanel theaterPanel = new JPanel(new BorderLayout());
        JLabel theaterLabel = new JLabel(getText("label_select_theater"));
        theaterPanel.add(theaterLabel, BorderLayout.NORTH);
        theaterSelector = new JComboBox<>();
        theaterPanel.add(theaterSelector, BorderLayout.CENTER);
        selectionPanel.add(theaterPanel);

        // Panel for function (showtime) selection
        JPanel functionPanel = new JPanel(new BorderLayout());
        JLabel functionLabel = new JLabel(getText("label_select_function"));
        functionPanel.add(functionLabel, BorderLayout.NORTH);
        functionSelector = new JComboBox<>();
        functionPanel.add(functionSelector, BorderLayout.CENTER);
        selectionPanel.add(functionPanel);

        // Panel for combo selector panel
        JPanel comboPanel = new JPanel(new BorderLayout());
        JLabel comboLabel = new JLabel(getText("label_select_combo"));
        comboPanel.add(comboLabel, BorderLayout.NORTH);
        comboSelector = new JComboBox<>(comboPrices.keySet().toArray(new String[0]));
        comboPanel.add(comboSelector, BorderLayout.CENTER);
        selectionPanel.add(comboPanel);

        // Panel to show movie details
        JPanel movieDetailsPanel = new JPanel(new BorderLayout());
        movieDetails = new JTextArea(3, 20);
        movieDetails.setEditable(false);
        movieDetails.setLineWrap(true);
        movieDetails.setWrapStyleWord(true);
        movieDetailsPanel.add(new JLabel(getText("label_movie_details")), BorderLayout.NORTH);
        movieDetailsPanel.add(new JScrollPane(movieDetails), BorderLayout.CENTER);

        // Panel to show ticket base price
        JPanel ticketPricePanel = new JPanel(new BorderLayout());
        JLabel ticketPriceLabel;
        if (currentTime.isBefore(timeLimit)) {
            ticketPriceLabel = new JLabel(getText("label_ticket_price_am"));
        } else {
            ticketPriceLabel = new JLabel(getText("label_ticket_price_pm"));
        }
        ticketPricePanel.add(ticketPriceLabel, BorderLayout.NORTH);

        // Button to view all tickets
        JPanel viewTicketsPanel = new JPanel(new BorderLayout());
        JButton viewTicketsButton = new JButton(getText("btn_view_tickets"));
        viewTicketsButton.addActionListener(e -> showTicketsWindow());
        viewTicketsPanel.add(viewTicketsButton, BorderLayout.CENTER);

        // Add panels for details, price, view button.
        selectionPanel.add(movieDetailsPanel);
        selectionPanel.add(ticketPricePanel);
        selectionPanel.add(viewTicketsPanel);

        // Legend to explain seat colors
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton availableSample = new JButton(getText("legend_available"));
        availableSample.setBackground(Color.GREEN);
        availableSample.setEnabled(false);
        JButton reservedSample = new JButton(getText("legend_reserved"));
        reservedSample.setBackground(Color.RED);
        reservedSample.setEnabled(false);
        JButton selectedSample = new JButton(getText("legend_selected"));
        selectedSample.setBackground(Color.BLUE);
        selectedSample.setEnabled(false);
        JButton vipSample = new JButton(getText("legend_vip"));
        vipSample.setBackground(Color.MAGENTA);
        vipSample.setEnabled(false);
        legendPanel.add(new JLabel(getText("legend_title")));
        legendPanel.add(availableSample);
        legendPanel.add(vipSample);
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
                Movies movie = selectedFunction.getMovie();
                movieDetails.setText(
                        getText("movie_label_title") + " " + movie.getTitle() + "\n"
                                + getText("movie_label_genre") + " " + movie.getGenre() + "\n"
                                + getText("movie_label_rating") + " " + movie.getClassification() + "\n"
                                + getText("movie_label_synopsis") + " " + movie.getSinopsis());
                updateSeatsAvailability();
            }
        });
        // Listener to update ticket price when combo is changed
        comboSelector.addActionListener(e -> {
            String selectedCombo = (String) comboSelector.getSelectedItem();
            int comboPrice = comboPrices.get(selectedCombo);

            if (currentTime.isBefore(timeLimit)) {
                ticketPriceLabel.setText(getText("label_ticket_price_am") + " + Combo: $" + comboPrice);
            } else {
                ticketPriceLabel.setText(getText("label_ticket_price_pm") + " + Combo: $" + comboPrice);

            }

        });

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Screen panel (to simulate cinema screen)
        JPanel screenPanel = new JPanel();
        screenPanel.setPreferredSize(new Dimension(500, 30));
        screenPanel.setBackground(Color.GRAY);
        screenPanel.setBorder(BorderFactory.createTitledBorder("Screen"));

        JPanel seatsPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        seatsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        seats = new JButton[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btn = new JButton((char) ('A' + i) + String.valueOf(j + 1));
                btn.setPreferredSize(new Dimension(60, 60));
                if (i >= 3) { // Rows D and E (index 3 and 4)
                    btn.setBackground(Color.MAGENTA);
                } else {
                    btn.setBackground(Color.GREEN);
                }
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
        cartScrollPane.setBorder(BorderFactory.createTitledBorder(getText("btn_view_tickets")));

        // Buttons panel placed at the bottom right of the interface
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        // Button to clear the shopping cart
        JButton clearButton = new JButton(getText("btn_clear_cart"));

        // Action performed when the clear cart button is clicked
        clearButton.addActionListener(e -> {
            // Check if the cart is not empty
            if (!cart.isEmpty()) {
                // Show a confirmation dialog to the user
                int response = JOptionPane.showConfirmDialog(frame,
                        getText("dialog_confirm_clear"),
                        getText("confirm_clear_title"),
                        JOptionPane.YES_NO_OPTION); // Options: Yes or No

                // If the user confirms to clear the cart
                if (response == JOptionPane.YES_OPTION) {
                    cart.clear(); // Clear the cart
                    updateSeatsAvailability(); // Update seat availability (make seats available again if reserved)
                    updateCartDisplay(); // Update the cart display area to reflect the empty cart
                }
            }
        });
        // Button to initiate ticket purchase
        JButton purchaseButton = new JButton(getText("btn_purchase_tickets"));
        // Action performed when the purchase button is clicked
        purchaseButton.addActionListener(e -> {
            // Check if the cart is empty (no tickets selected)
            if (cart.isEmpty()) {
                // Show a warning message prompting the user to select at least one seat
                JOptionPane.showMessageDialog(frame,
                        getText("msg_no_tickets_selected"),
                        getText("title_no_tickets"),
                        JOptionPane.WARNING_MESSAGE);
                return; // Exit the event handler since no tickets are selected
            }

            // Show confirmation dialog with the total purchase amount
            int total = calculateTotal();
            int response = JOptionPane.showConfirmDialog(frame,
                    String.format(getText("msg_confirm_purchase"), total),
                    getText("confirm_purchase_title"),
                    JOptionPane.YES_NO_OPTION);

            // If user confirms the purchase
            if (response == JOptionPane.YES_OPTION) {
                // Attempt to confirm all tickets in the cart in the database
                boolean allConfirmed = confirmAllTickets();
                if (allConfirmed) {
                    // If successful, show a success message
                    JOptionPane.showMessageDialog(frame,
                            getText("msg_purchase_success"),
                            getText("purchase_complete_title"),
                            JOptionPane.INFORMATION_MESSAGE);
                    // Clear the cart and update the UI accordingly
                    cart.clear();
                    updateCartDisplay();
                    updateSeatsAvailability();
                } else {
                    // Show an error message if there was an issue processing tickets
                    JOptionPane.showMessageDialog(frame,
                            getText("msg_purchase_error"),
                            getText("purchase_error_title"),
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
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH); // Buttons panel at the bottom
        /**
         * Add the cinema seating panel to the center of content panel, and the
         * rightPanel (cart + buttons) to the east side
         *
         */
        contentPanel.add(cinemaPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        /**
         * Add the selection panel (with dropdowns) to the top (north) and the
         * content panel to the center of the main frame
         *
         */
        frame.add(selectionPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        // Automatically select the first city in the city selector if available
        if (citySelector.getItemCount() > 0) {
            citySelector.setSelectedIndex(0);
        }

        // Language change listener - update all labels and texts accordingly
        languageSelector.addActionListener(e -> {
            String selectedLang = (String) languageSelector.getSelectedItem();
            if (selectedLang != null) {
                if (selectedLang.equals(getText("lang_es"))) {
                    currentLanguage = "es";
                } else {
                    currentLanguage = "en";
                }
                // Rebuild combo prices keys according to language
                updateComboPricesKeys();

                // Update combo box items
                comboSelector.setModel(new DefaultComboBoxModel<>(comboPrices.keySet().toArray(new String[0])));
                comboSelector.setSelectedIndex(0);

                // Update all visible texts:
                updateLabelsAndTexts();

                // Force update seats availability and cart display to refresh prices and text
                updateSeatsAvailability();
                updateCartDisplay();

                // Update frame title
                frame.setTitle(getText("app_title"));
            }
        });

        // Center the frame on screen and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateComboPricesKeys() {
        // Reconstruct the comboPrices map with keys in selected language
        Map<String, Integer> newComboPrices = new LinkedHashMap<>();
        newComboPrices.put(getText("combo_no"), 0);
        newComboPrices.put(getText("combo_personal"), 8000);
        newComboPrices.put(getText("combo_duo"), 12000);
        newComboPrices.put(getText("combo_family"), 20000);
        comboPrices = newComboPrices;
    }

    private void updateLabelsAndTexts() {
        // Update labels in selectionPanel (language, city, theater, function, combo)
        // Language label
        ((JLabel) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(0)).getComponent(0))
                .setText("Language:");
        // City label
        ((JLabel) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(1)).getComponent(0))
                .setText(getText("label_select_city"));
        // Theater label
        ((JLabel) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(2)).getComponent(0))
                .setText(getText("label_select_theater"));
        // Function label
        ((JLabel) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(3)).getComponent(0))
                .setText(getText("label_select_function"));
        // Combo label
        ((JLabel) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(4)).getComponent(0))
                .setText(getText("label_select_combo"));

        // Movie details label
        JPanel movieDetailsPanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(5);
        ((JLabel) movieDetailsPanel.getComponent(0)).setText(getText("label_movie_details"));

        // Ticket price label
        JPanel ticketPricePanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(6);
        JLabel ticketPriceLabel = (JLabel) ticketPricePanel.getComponent(0);
        if (currentTime.isBefore(timeLimit)) {
            ticketPriceLabel.setText(getText("label_ticket_price_am"));
        } else {
            ticketPriceLabel.setText(getText("label_ticket_price_pm"));
        }

        // View tickets button
        JPanel viewTicketsPanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(7);
        JButton viewTicketsButton = (JButton) viewTicketsPanel.getComponent(0);
        viewTicketsButton.setText(getText("btn_view_tickets"));

        // Legend panel labels
        JPanel legendPanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(8);
        ((JLabel) legendPanel.getComponent(0)).setText(getText("legend_title"));
        ((JButton) legendPanel.getComponent(1)).setText(getText("legend_available"));
        ((JButton) legendPanel.getComponent(2)).setText(getText("legend_vip"));
        ((JButton) legendPanel.getComponent(3)).setText(getText("legend_reserved"));
        ((JButton) legendPanel.getComponent(4)).setText(getText("legend_selected"));

        // Cart border title
        JPanel rightPanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(1)).getComponent(1);
        JScrollPane cartScrollPane = (JScrollPane) rightPanel.getComponent(0);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder(getText("btn_view_tickets")));

        // Buttons panel buttons text
        JPanel buttonsPanel = (JPanel) rightPanel.getComponent(1);
        ((JButton) buttonsPanel.getComponent(0)).setText(getText("btn_clear_cart"));
        ((JButton) buttonsPanel.getComponent(1)).setText(getText("btn_purchase_tickets"));
    }

    // Get currently selected function (showtime), combo, theater, and city
    private void handleSeatSelection(int row, int col) {
        Function selectedFunction = (Function) functionSelector.getSelectedItem();
        String combo = (String) comboSelector.getSelectedItem();
        Cinema theater = (Cinema) theaterSelector.getSelectedItem();
        City city = (City) citySelector.getSelectedItem();

        // Validate that all necessary selections have been made before allowing seat
        // selection
        if (selectedFunction == null || combo == null || theater == null || city == null) {
            JOptionPane.showMessageDialog(frame,
                    getText("msg_select_before_seat"),
                    getText("title_selection_error"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build seat code string (e.g., "A1", "B5") based on row and column indices
        String seatCode = (char) ('A' + row) + String.valueOf(col + 1);
        // Check seat availability in the database for the selected movie, time, and
        // seat
        if (!DBManager.isSeatAvailable(selectedFunction.getMovie().getTitle(), selectedFunction.getTime(), seatCode)) {
            JOptionPane.showMessageDialog(frame,
                    getText("msg_seat_taken"),
                    getText("title_seat_unavailable"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Calculate the price of the ticket by adding the base price and combo price
        int comboPrice = comboPrices.getOrDefault(combo, 0);
        int price;
        if (currentTime.isBefore(timeLimit)) {
            price = 15000 + comboPrice;
        } else {
            price = 25000 + comboPrice;
        }
        // Create the ticket details string with formatted movie, time, theater, city,
        // seat, and total price
        String item = String.format("%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s %s\n%-15s $%,d\n%s\n",
                getText("ticket_label_movie"), selectedFunction.getMovie().getTitle(),
                getText("ticket_label_time"), selectedFunction.getTime(),
                getText("ticket_label_theater"), theater.getNombre(),
                getText("ticket_label_city"), city.getName(),
                getText("ticket_label_seat"), seatCode,
                getText("ticket_label_total"), price,
                getText("separator_line"));

        // Attempt to save the ticket information in the database
        try {
            boolean saved = DBManager.saveTicket(
                    selectedFunction.getMovie().getTitle(),
                    selectedFunction.getTime(),
                    theater.getNombre(),
                    city.getName(),
                    seatCode,
                    combo,
                    price);

            // Show error message if ticket could not be saved and exit the method
            if (!saved) {
                JOptionPane.showMessageDialog(frame,
                        getText("msg_save_ticket_error"),
                        getText("title_save_error"),
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
            // Print exception stack trace and show error dialog if something unexpected
            // happens

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
                        seatCode);

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
                String seat = rs.getString("seat");
                // Sum prices only for tickets with status "reserved"
                if ("reserved".equals(status)) {
                    if ("VIP".equals(seat)) {
                        total += (rs.getInt("total") + 10.000);
                    }
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
        // Create a new window (frame) for displaying tickets
        JFrame ticketFrame = new JFrame("Tickets");
        ticketFrame.setSize(1900, 1000);
        ticketFrame.setLayout(new BorderLayout());

        // Define column headers for the ticket table
        String[] columns = { "ID", "Movie", "Time", "Theater", "City", "Seat", "Combo", "Total", "Status" };
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

        if (table.getColumnCount() >= 9) {
            table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                        boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                    if (value != null) {
                        String status = value.toString();
                        if ("purchased".equals(status)) {
                            c.setForeground(Color.GREEN.darker()); // Purchased tickets shown in dark green
                        } else if ("reserved".equals(status)) {
                            c.setForeground(Color.RED); // Reserved tickets shown in red
                        } else {
                            c.setForeground(table.getForeground()); // Reset to default
                        }
                    }
                    return c;// Return the component used to render the cell, with the appropriate color
                             // applied
                }
            });
        }

        // Confirm Purchase Button
        JButton confirmPurchaseButton = new JButton(getText("confirm_selected_tickets"));
        confirmPurchaseButton.addActionListener(e -> {
            // Get selected rows in the table
            int[] selectedRows = table.getSelectedRows();
            // If no rows are selected, show warning and return
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(ticketFrame,
                        getText("msg_select_tickets_confirm"),
                        getText("title_no_selection"),
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
                    String.format(getText("msg_confirmed_tickets"), confirmedCount),
                    getText("confirm_purchase_title"),
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
