import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class ElectricityBillingSystem extends JFrame {
    private JTextField customerIDField, customerNameField, unitsConsumedField, billAmountField, billingDateField, totalAmountField;
    private JComboBox<String> connectionTypeDropdown;
    private final double TAX_RATE = 0.08;
    private final double SERVICE_CHARGE = 50;
    private boolean isDiscountApplied = false;

    public ElectricityBillingSystem() {
        setTitle("Electricity Billing System 🎇");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("images/background.jpg"); // <- Change path here if needed
        Image backgroundImage = backgroundIcon.getImage();

        // Create background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);  // Set the content pane to background panel

        // Main Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);  // So background shows through
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        Font labelFont = new Font("Arial", Font.BOLD | Font.ITALIC, 24);
        Font fieldFont = new Font("Arial", Font.BOLD | Font.ITALIC, 22);
        Font buttonFont = new Font("Arial", Font.BOLD, 22);

        JLabel[] labels = {
            new JLabel("Customer ID:"), new JLabel("Customer Name:"),
            new JLabel("Connection Type:"), new JLabel("Billing Date:"),
            new JLabel("Units Consumed:"), new JLabel("Bill Amount:"),
            new JLabel("Total Payable Amount:")
        };

        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(Color.WHITE);
        }

        customerIDField = new JTextField(generateCustomerID(), 15);
        customerNameField = new JTextField(15);
        unitsConsumedField = new JTextField(15);
        billAmountField = new JTextField(15);
        totalAmountField = new JTextField(15);
        billingDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 15);
        billingDateField.setEditable(false);
        billAmountField.setEditable(false);
        totalAmountField.setEditable(false);

        String[] connectionTypes = {"Residential", "Commercial", "Industrial"};
        connectionTypeDropdown = new JComboBox<>(connectionTypes);

        JComponent[] fields = {customerIDField, customerNameField, connectionTypeDropdown, billingDateField, unitsConsumedField, billAmountField, totalAmountField};
        for (JComponent field : fields) {
            field.setFont(fieldFont);
            if (field instanceof JTextField)
                ((JTextField) field).setHorizontalAlignment(JTextField.CENTER);
        }

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(labels[i], gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton calculateButton = new JButton("💡 Calculate Bill");
        JButton discountButton = new JButton("🎯 Apply EWS Discount");
        JButton exportButton = new JButton("📁 Export CSV");
        JButton printButton = new JButton("🖨️ Print Bill");
        JButton resetButton = new JButton("🔄 Reset");
        JButton exitButton = new JButton("❌ Exit");
        JButton bplButton = new JButton("🏡 BPL Exemption");
        JButton searchButton = new JButton("🔍 Search");

        JButton[] buttons = {
            calculateButton, discountButton, exportButton, printButton,
            resetButton, exitButton, bplButton, searchButton
        };

        Color[] colors = {
            Color.ORANGE, Color.GREEN, Color.YELLOW, Color.CYAN,
            Color.PINK, Color.RED, Color.MAGENTA, Color.YELLOW
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(buttonFont);
            buttons[i].setBackground(colors[i]);
            buttonPanel.add(buttons[i]);
            buttons[i].addActionListener(e -> playClickSound());
        }

        // Button Actions
        calculateButton.addActionListener(e -> calculateBill());
        discountButton.addActionListener(e -> applyDiscount());
        exportButton.addActionListener(e -> exportToCSV());
        printButton.addActionListener(e -> printBill());
        resetButton.addActionListener(e -> resetFields());
        exitButton.addActionListener(e -> System.exit(0));
        bplButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "You don't have to pay the bill.", "BPL Exemption", JOptionPane.INFORMATION_MESSAGE));
        searchButton.addActionListener(e -> searchCustomerName());

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void calculateBill() {
        try {
            int unitsConsumed = Integer.parseInt(unitsConsumedField.getText());
            String connectionType = (String) connectionTypeDropdown.getSelectedItem();
            double unitRate = switch (connectionType) {
                case "Residential" -> 5.0;
                case "Commercial" -> 7.5;
                default -> 10.0;
            };
            double billAmount = unitsConsumed * unitRate;
            double tax = billAmount * TAX_RATE;
            double totalAmount = billAmount + tax + SERVICE_CHARGE;
            if (isDiscountApplied) totalAmount *= 0.9;

            billAmountField.setText(String.format("₹%.2f", billAmount));
            totalAmountField.setText(String.format("₹%.2f", totalAmount));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input. Please enter valid numbers.");
        }
    }

    private void applyDiscount() {
        isDiscountApplied = true;
        calculateBill();
        JOptionPane.showMessageDialog(this, "✅ 10% Discount Applied!");
    }

    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("ElectricityBills.csv", true)) {
            writer.write(customerIDField.getText() + "," +
                    customerNameField.getText() + "," +
                    connectionTypeDropdown.getSelectedItem() + "," +
                    billingDateField.getText() + "," +
                    unitsConsumedField.getText() + "," +
                    billAmountField.getText().replace("₹", "") + "," +
                    totalAmountField.getText().replace("₹", "") + "\n");
            JOptionPane.showMessageDialog(this, "📂 Data exported successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + e.getMessage());
        }
    }

    private void printBill() {
        String billDetails = String.format("""
                📜 Electricity Bill 🏠
                ──────────────────────────────
                🆔 Customer ID: %s
                👤 Name: %s
                🔌 Connection: %s
                📅 Date: %s
                ⚡ Units: %s
                💰 Bill: %s
                💳 Total: %s
                ──────────────────────────────
                """,
                customerIDField.getText(), customerNameField.getText(),
                connectionTypeDropdown.getSelectedItem(), billingDateField.getText(),
                unitsConsumedField.getText(), billAmountField.getText(), totalAmountField.getText());

        JOptionPane.showMessageDialog(this, new JTextArea(billDetails), "🖨️ Printed Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetFields() {
        customerIDField.setText(generateCustomerID());
        customerNameField.setText("");
        unitsConsumedField.setText("");
        billAmountField.setText("");
        totalAmountField.setText("");
        connectionTypeDropdown.setSelectedIndex(0);
        isDiscountApplied = false;
    }

    private void searchCustomerName() {
        String nameToSearch = JOptionPane.showInputDialog(this, "🔍 Enter name to search:");
        if (nameToSearch == null || nameToSearch.isEmpty()) return;

        File csvFile = new File("ElectricityBills.csv");
        boolean found = false;

        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                if (data.length >= 2 && data[1].equalsIgnoreCase(nameToSearch)) {
                    found = true;

                    // Load data into the fields
                    customerIDField.setText(data[0]);
                    customerNameField.setText(data[1]);
                    connectionTypeDropdown.setSelectedItem(data[2]);
                    billingDateField.setText(data[3]);
                    unitsConsumedField.setText(data[4]);
                    billAmountField.setText(data[5]);
                    totalAmountField.setText(data[6]);

                    JOptionPane.showMessageDialog(this, "✅ Found and loaded data for: " + nameToSearch);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "⚠️ CSV file not found.");
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "❌ Name not found.");
        }
    }

    private String generateCustomerID() {
        return "CUST" + new Random().nextInt(10000);
    }

    private void playClickSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ElectricityBillingSystem::new);
    }
}
