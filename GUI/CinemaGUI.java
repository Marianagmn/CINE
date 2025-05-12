import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
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

    private java.util.List<City> cities = new ArrayList<>();
    private java.util.List<Cinema> cinemas = new ArrayList<>();
    private java.util.List<Movies> movies = new ArrayList<>();
    private java.util.List<Function> functionsList = new ArrayList<>();

    public CinemaGUI() {
        DBManager.initDatabase();
        initializeData();
        createAndShowGUI();
    }

    private void initializeData() {
        City medellin = new City("Medellin");
        City bogota = new City("Bogota");
        cities.add(medellin);
        cities.add(bogota);

        for (int i = 1; i <= 3; i++) {
            cinemas.add(new Cinema("Cine Medellin " + i));
            cinemas.add(new Cinema("Cine Bogota " + i));
        }

        Movies m1 = new Movies("Avengers", "Action", "PG-13", "Superheroes saving the world");
        Movies m2 = new Movies("Barbie", "Fantasy", "PG", "A doll's journey");
        Movies m3 = new Movies("Oppenheimer", "Drama", "R", "The atomic bomb story");

        movies.add(m1);
        movies.add(m2);
        movies.add(m3);

        functionsList.add(new Function(m1, "3:00 PM"));
        functionsList.add(new Function(m2, "5:00 PM"));
        functionsList.add(new Function(m3, "8:00 PM"));
    }

    private void createAndShowGUI() {
        frame = new JFrame("Cinema System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new FlowLayout());

        citySelector = new JComboBox<>(cities.toArray(new City[0]));
        theaterSelector = new JComboBox<>();
        functionSelector = new JComboBox<>();

        citySelector.addActionListener(e -> {
            theaterSelector.removeAllItems();
            City selected = (City) citySelector.getSelectedItem();
            if (selected != null) {
                for (Cinema c : cinemas) {
                    if (c.getNombre().contains(selected.getName())) {
                        theaterSelector.addItem(c);
                    }
                }
            }
        });

        theaterSelector.addActionListener(e -> {
            functionSelector.removeAllItems();
            for (Function f : functionsList) {
                functionSelector.addItem(f);
            }
        });

        comboSelector = new JComboBox<>(new String[]{"No Combo", "Personal Combo", "Family Combo", "Duo Combo"});

        JPanel seatsPanel = new JPanel(new GridLayout(5, 5));
        seats = new JButton[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btn = new JButton((char)('A' + i) + String.valueOf(j + 1));
                btn.setBackground(Color.GREEN);
                int row = i, col = j;
                btn.addActionListener(e -> {
                    btn.setBackground(Color.RED);
                    String seatCode = (char)('A' + row) + String.valueOf(col + 1);
                    Cinema theater = (Cinema) theaterSelector.getSelectedItem();
                    City city = (City) citySelector.getSelectedItem();
                    Function selectedFunction = (Function) functionSelector.getSelectedItem();
                    String combo = (String) comboSelector.getSelectedItem();
                    int price = 15000 + switch (combo) {
                        case "Personal Combo" -> 8000;
                        case "Family Combo" -> 12000;
                        case "Duo Combo" -> 10000;
                        default -> 0;
                    };

                    String item = "Ticket for " + selectedFunction.getMovie().getTitle()
                        + " at " + selectedFunction.getTime()
                        + " in " + theater.getNombre() + " (" + city.getName() + ")"
                        + ", Seat: " + seatCode + ", Combo: " + combo + ", Total: $" + price;

                    cart.add(item);
                    DBManager.saveTicket(
                        selectedFunction.getMovie().getTitle(),
                        selectedFunction.getTime(),
                        theater.getNombre(),
                        city.getName(),
                        seatCode,
                        combo,
                        price
                    );
                    updateCartDisplay();
                });
                seats[i][j] = btn;
                seatsPanel.add(btn);
            }
        }

        cartArea = new JTextArea(10, 40);
        cartArea.setEditable(false);
        cart = new ArrayList<>();

        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(e -> {
            cart.clear();
            updateCartDisplay();
        });

        JButton viewTicketsButton = new JButton("View Sold Tickets");
        viewTicketsButton.addActionListener(e -> showTicketsWindow());

        frame.add(new JLabel("Select City:"));
        frame.add(citySelector);
        frame.add(new JLabel("Select Theater:"));
        frame.add(theaterSelector);
        frame.add(new JLabel("Select Showtime:"));
        frame.add(functionSelector);
        frame.add(new JLabel("Select Combo:"));
        frame.add(comboSelector);
        frame.add(seatsPanel);
        frame.add(new JScrollPane(cartArea));
        frame.add(clearButton);
        frame.add(viewTicketsButton);

        frame.setVisible(true);
    }

    private void updateCartDisplay() {
        cartArea.setText("");
        for (String item : cart) {
            cartArea.append(item + "\n");
        }
    }

    private void showTicketsWindow() {
        JFrame ticketFrame = new JFrame("Sold Tickets");
        ticketFrame.setSize(700, 400);
        ticketFrame.setLayout(new BorderLayout());

        String[] columns = {"ID", "Movie", "Time", "Theater", "City", "Seat", "Combo", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try (var rs = DBManager.getAllTickets()) {
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("movie"),
                    rs.getString("time"),
                    rs.getString("theater"),
                    rs.getString("city"),
                    rs.getString("seat"),
                    rs.getString("combo"),
                    rs.getInt("total")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ticketFrame.add(new JScrollPane(table), BorderLayout.CENTER);
        ticketFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CinemaGUI::new);
    }
}

