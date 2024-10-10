import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExpenseAndIncome {
    private JFrame frame;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JLabel closeLabel;
    private JLabel minimizeLabel;
    private JPanel dashBoardPanel;
    private JPanel buttonsPanel;
    private JButton addTransactionButton;
    private JButton removeTransactionButton;
    

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private double totalAmount = 0.0;
    private ArrayList<String> dataPanelValues = new ArrayList<>();
    private boolean isDragging = false;
    private Point mouseOffset;

    public ExpenseAndIncome() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(52, 73, 94)));

        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(52, 73, 94));
        titleBar.setPreferredSize(new Dimension(frame.getWidth(), 60));
        frame.add(titleBar, BorderLayout.NORTH);

        titleLabel = new JLabel("Expense And Income Tracker");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(10, 15, 300, 30);
        titleBar.add(titleLabel);

        closeLabel = new JLabel("X");
        closeLabel.setForeground(Color.white);
        closeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setBounds(frame.getWidth() - 50, 15, 30, 30);
        closeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        titleBar.add(closeLabel);

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(Color.red);
                closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(Color.white);
                closeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        minimizeLabel = new JLabel("-");
        minimizeLabel.setForeground(Color.white);
        minimizeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        minimizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minimizeLabel.setBounds(frame.getWidth() - 90, 15, 30, 30);
        minimizeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        titleBar.add(minimizeLabel);

        minimizeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setState(JFrame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeLabel.setForeground(Color.darkGray);
                minimizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeLabel.setForeground(Color.white);
                minimizeLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDragging = true;
                mouseOffset = e.getPoint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    Point newLocation = e.getLocationOnScreen();
                    newLocation.translate(-mouseOffset.x, -mouseOffset.y);
                    frame.setLocation(newLocation);
                }
            }
        });

        dashBoardPanel = new JPanel();
        dashBoardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        dashBoardPanel.setBackground(new Color(236, 240, 241));
        frame.add(dashBoardPanel, BorderLayout.CENTER);

        totalAmount = TransactionValuesCalculation.getTotalExpenses(TransactionDao.getAllTransaction());
        dataPanelValues.add(String.format("TD %,.2f",TransactionValuesCalculation.getTotalExpenses(TransactionDao.getAllTransaction())));
        dataPanelValues.add(String.format("TD %,.2f",TransactionValuesCalculation.getTotalIncomes(TransactionDao.getAllTransaction())));
        dataPanelValues.add("TD" + totalAmount);


        addDataPanel("Expense", 0);
        addDataPanel("Income", 1);
        addDataPanel("Total", 2);

        addTransactionButton = createButton("Add Transaction", new Color(41, 128, 185));
        addTransactionButton.addActionListener((e -> {
            showAddTransactionDialog();
        }));
        removeTransactionButton = createButton("Remove Transaction", new Color(231, 76, 60));
        removeTransactionButton.addActionListener((e -> {
            removeSelectedTransaction();
        }));

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 0, 10));
        buttonsPanel.setPreferredSize(new Dimension(200, 100));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(addTransactionButton);
        buttonsPanel.add(removeTransactionButton);

        dashBoardPanel.add(buttonsPanel);

        String[] columns = {"ID", "Type", "Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        configureTransactionTable();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        dashBoardPanel.add(scrollPane);

        frame.setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private String fixNegativeValueDisplay(double value) {
        return String.format("TD %.2f", value);
    }


    private void removeSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow != -1) {
            int transactionId = (int) transactionTable.getValueAt(selectedRow, 0);
            String type = transactionTable.getValueAt(selectedRow, 1).toString();
            String amountStr = transactionTable.getValueAt(selectedRow, 3).toString();
            double amount = Double.parseDouble(amountStr.replace("TD", "").replace(" ", "").replace(",", ""));

            totalAmount -= amount; // Simply subtract the amount, as expenses are already negative

            JPanel totalPanel = (JPanel) dashBoardPanel.getComponent(2);
            totalPanel.repaint();

            int indexToUpdate = type.equals("Income") ? 1 : 0;
            String currentValue = dataPanelValues.get(indexToUpdate);
            double currentAmount = Double.parseDouble(currentValue.replace("TD", "").replace(" ", "").replace(",", ""));
            double updatedAmount = currentAmount - Math.abs(amount);

            dataPanelValues.set(indexToUpdate, String.format("TD %.2f", Math.abs(updatedAmount)));

            JPanel dataPanel = (JPanel) dashBoardPanel.getComponent(indexToUpdate);
            dataPanel.repaint();

            tableModel.removeRow(selectedRow);
            removeTransactionFromDatabase(transactionId);
        }
    }

    private void removeTransactionFromDatabase (int transactionID){
        try {
            Connection connection = DbConnect.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `transaction_table`WHERE `id` = ?");
            ps.setInt(1,transactionID);
            ps.executeUpdate();
            System.out.println("Transaction Removed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Display the dialog for adding a new transaction
    private void showAddTransactionDialog(){
        JDialog dialog = new JDialog(frame , "Add Transaction",true);
        dialog.setSize(500 ,300);
        dialog.setLocationRelativeTo(frame);

        JPanel dialogPanel = new JPanel(new GridLayout(4,0,10,10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        dialogPanel.setBackground(Color.lightGray);

        JLabel typeLabel = new JLabel("Type");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{
                "Expense","Income"
        });

        typeComboBox.setBackground(Color.white);
        JLabel descriptionLabel = new JLabel("Description");
        JTextField descriptionField = new JTextField();


        JLabel amountLabel = new JLabel("Amount");
        JTextField amountField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(41,128,185));
        addButton.setForeground(Color.white);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener((e) ->{
            addTransaction(typeComboBox,descriptionField,amountField);
        } );

        dialogPanel.add(typeLabel);
        dialogPanel.add(typeComboBox);
        dialogPanel.add(descriptionLabel);
        dialogPanel.add(descriptionField);
        dialogPanel.add(amountLabel);
        dialogPanel.add(amountField);
        dialogPanel.add(new JLabel());
        dialogPanel.add(addButton);

        DbConnect.getConnection();


        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void addTransaction(JComboBox<String> typeComboBox, JTextField descriptionField, JTextField amountField) {
        String type = (String) typeComboBox.getSelectedItem();
        String description = descriptionField.getText();
        String amount = amountField.getText();

        double newAmount = Math.abs(Double.parseDouble(amount.replace("TD", "").replace(",", "").replace(" ", "")));
        if (type.equals("Expense")) {
            newAmount = -newAmount; // Make expenses negative
        }

        totalAmount += newAmount;

        JPanel totalPanel = (JPanel) dashBoardPanel.getComponent(2);
        totalPanel.repaint();

        int indexToUpdate = type.equals("Income") ? 1 : 0;
        String currentValue = dataPanelValues.get(indexToUpdate);
        double currentAmount = Double.parseDouble(currentValue.replace("TD", "").replace(",", "").replace(" ", ""));
        double updatedAmount = currentAmount + newAmount;

        dataPanelValues.set(indexToUpdate, String.format("TD %.2f", Math.abs(updatedAmount)));

        JPanel dataPanel = (JPanel) dashBoardPanel.getComponent(indexToUpdate);
        dataPanel.repaint();

        try {
            Connection connection = DbConnect.getConnection();
            String insertQuery = "INSERT INTO `transaction_table`(`transaction_type`, `description`, `amount`) VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setString(1, type);
            ps.setString(2, description);
            ps.setDouble(3, newAmount);
            ps.executeUpdate();
            tableModel.setRowCount(0);
            System.out.println("Data inserted successfully");
            populateTableTransactions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateTableTransactions() {
        for (Transaction transaction : TransactionDao.getAllTransaction()) {
            String formattedAmount = String.format("TD %.2f", transaction.getAmount());
            Object[] rowData = {transaction.getId(), transaction.getType(), transaction.getDescription(), formattedAmount};
            tableModel.addRow(rowData);
        }
    }


    private void configureTransactionTable() {
        transactionTable.setBackground(Color.WHITE);
        transactionTable.setRowHeight(30);
        transactionTable.setShowGrid(true);
        transactionTable.setGridColor(new Color(236, 240, 241));
        transactionTable.setBorder(new LineBorder(new Color(189, 195, 199)));
        transactionTable.setDefaultRenderer(Object.class ,new TransactionTableCellRender());
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        populateTableTransactions();
        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setBackground(new Color(52, 73, 94));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void addDataPanel(String title, int index) {
        JPanel dataPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING ,RenderingHints.VALUE_ANTIALIAS_ON);
                if (title.equals("Total")) {
                    drawDataPanel(g2d, title, String.format("TD %.2f", totalAmount), getWidth(), getHeight());
                } else {
                    drawDataPanel(g2d, title, dataPanelValues.get(index), getWidth(), getHeight());
                }
            }
        };
        dataPanel.setPreferredSize(new Dimension(220, 120));
        dataPanel.setBackground(Color.WHITE);
        dataPanel.setBorder(new LineBorder(new Color(189, 195, 199), 1));
        dashBoardPanel.add(dataPanel);
    }


    private void drawDataPanel(Graphics g, String title, String value, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(52, 73, 94));
        g2d.fillRect(0, 0, width, 40);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, 10, 25);

        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        if (title.equals("Expense")) {
            value = "-" + value; // Add minus sign for expenses
        }
        g2d.drawString(value, 10, 80);
    }
}


class TransactionTableCellRender extends DefaultTableCellRenderer{
    @Override
    public  Component getTableCellRendererComponent (JTable table ,Object value , boolean isSelected , boolean hasFocus , int row,int column){
        Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        String type = (String) table.getValueAt(row, 1);
        if (isSelected){
            c.setForeground(Color.white);

        }
        else {
            if ("Income".equals(type)){
                c.setBackground(new Color(144,238,144));
            }else {
                c.setBackground(new Color(255,99,71));
            }
        }
        return c;
    }
}