package com.hahn.client.ui;

import com.hahn.client.model.AuthenticatedUserResponse;
import com.hahn.client.model.EmployeeRequest;
import com.hahn.client.model.TicketResponse;
import com.hahn.client.service.AuthService;
import com.hahn.client.service.TicketService;
import com.hahn.client.ui.dialog.AddCommentDialog;
import com.hahn.client.ui.dialog.CreateTicketDialog;
import com.hahn.client.ui.dialog.UpdateStatusDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import java.util.stream.Collectors;

public class TicketManagementUI extends JFrame {
    private JTable table;
    private JLabel userInfoLabel;
    private final TicketService ticketService = new TicketService();
    private final AuthService authService = new AuthService();
    private boolean isITSupport;

    public TicketManagementUI() {
        super("Ticket Management System");

        // Vérifier le rôle de l'utilisateur
        AuthenticatedUserResponse user = null;
        try {
            user = authService.getUserInfo();
            isITSupport = user.getAuthorities().stream().anyMatch(authority -> authority.endsWith("IT_SUPPORT"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 250));
        userInfoLabel = new JLabel("Welcome Back!");
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        topPanel.add(userInfoLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.setBackground(new Color(230, 230, 250));

        JTextField searchField = new JTextField(15);
        String[] statusOptions = {"All", "NEW", "IN_PROGRESS", "RESOLVED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);

        JButton filterButton = new JButton("Apply Filters");
        styleButton(filterButton);

        filterPanel.add(new JLabel("Search by ticket ID :"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusComboBox);
        filterPanel.add(filterButton);

        // Table
        String[] columns = {"ID", "Title", "Description", "Priority", "Category", "Status", "Created By"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Ticket");
        JButton addCommentButton = new JButton("Add Comment");
        JButton updateStatusButton = new JButton("Update Status");

        styleButton(createButton);
        styleButton(addCommentButton);
        styleButton(updateStatusButton);

        buttonPanel.add(createButton);
        if (isITSupport) {
            buttonPanel.add(addCommentButton);
            buttonPanel.add(updateStatusButton);
        }

        // Action Listeners
        createButton.addActionListener(e -> {
            CreateTicketDialog dialog = new CreateTicketDialog(this, ticketService);
            dialog.setVisible(true);
            refreshTickets();
        });

        addCommentButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Long ticketId = (Long) table.getValueAt(selectedRow, 0);
                AddCommentDialog dialog = new AddCommentDialog(this, ticketService, ticketId);
                dialog.setVisible(true);
                refreshTickets();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a ticket first");
            }
        });

        updateStatusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Long ticketId = (Long) table.getValueAt(selectedRow, 0);
                UpdateStatusDialog dialog = new UpdateStatusDialog(this, ticketService, ticketId);
                dialog.setVisible(true);
                refreshTickets();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a ticket first");
            }
        });

        filterButton.addActionListener(e -> {
            String search = searchField.getText();
            String status = (String) statusComboBox.getSelectedItem();
            refreshTickets(search, status);
        });

        // Layout Assembly
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.CENTER);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        fetchUserInfo();
        refreshTickets();
    }

    public void refreshTickets() {
        refreshTickets(null, null);
    }

    /*private void refreshTickets(String search, String status) {
        new Thread(() -> {
            try {
                // Récupérer les informations de l'utilisateur connecté
                AuthenticatedUserResponse user = authService.getUserInfo();
                List<TicketResponse> tickets;

                // Vérifier le rôle de l'utilisateur
                boolean isITSupport = user.getAuthorities().stream()
                        .anyMatch(authority -> authority.equals("ROLE_IT_SUPPORT"));

                if (isITSupport) {
                    // Pour IT_SUPPORT : récupérer tous les tickets
                    tickets = ticketService.getAllTickets();
                } else {
                    // Pour EMPLOYEE : récupérer uniquement ses tickets
                    EmployeeRequest employeeRequest = new EmployeeRequest(user.getUsername());
                    tickets = ticketService.getTicketsByEmployee(employeeRequest);
                }

                // Appliquer les filtres si nécessaire
                if (search != null && !search.isEmpty() || status != null && !status.equals("All")) {
                    final List<TicketResponse> filteredTickets = tickets.stream()
                            .filter(ticket -> (search == null || search.isEmpty() ||
                                    ticket.getTitle().toLowerCase().contains(search.toLowerCase()))
                                    && (status == null || status.equals("All") ||
                                    ticket.getStatus().equals(status)))
                            .collect(Collectors.toList());

                    SwingUtilities.invokeLater(() -> updateTicketTable(filteredTickets));
                } else {
                    SwingUtilities.invokeLater(() -> updateTicketTable(tickets));
                }

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "Error loading tickets: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }*/
    private void refreshTickets(String search, String status) {
        new Thread(() -> {
            try {
                // Récupérer les informations de l'utilisateur connecté
                AuthenticatedUserResponse user = authService.getUserInfo();
                List<TicketResponse> tickets;

                // Vérifier le rôle de l'utilisateur
                boolean isITSupport = user.getAuthorities().stream()
                        .anyMatch(authority -> authority.equals("ROLE_IT_SUPPORT"));

                if (isITSupport) {
                    // Pour IT_SUPPORT : récupérer tous les tickets
                    tickets = ticketService.getAllTickets();
                } else {
                    // Pour EMPLOYEE : récupérer uniquement ses tickets
                    EmployeeRequest employeeRequest = new EmployeeRequest(user.getUsername());
                    tickets = ticketService.getTicketsByEmployee(employeeRequest);
                }

                // Appliquer les filtres si nécessaire
                if (search != null && !search.isEmpty() || status != null && !status.equals("All")) {
                    final List<TicketResponse> filteredTickets = tickets.stream()
                            .filter(ticket -> {
                                // Vérifier si la recherche correspond à l'ID
                                boolean matchesId = search == null || search.isEmpty() ||
                                        String.valueOf(ticket.getId()).equals(search);

                                // Vérifier si le status correspond
                                boolean matchesStatus = status == null || status.equals("All") ||
                                        ticket.getStatus().equals(status);

                                return matchesId && matchesStatus;
                            })
                            .collect(Collectors.toList());

                    SwingUtilities.invokeLater(() -> updateTicketTable(filteredTickets));
                } else {
                    SwingUtilities.invokeLater(() -> updateTicketTable(tickets));
                }

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "Error loading tickets: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    private void updateTicketTable(List<TicketResponse> tickets) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (TicketResponse ticket : tickets) {
                model.addRow(new Object[]{
                        ticket.getId(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getPriority(),
                        ticket.getCategory(),
                        ticket.getStatus(),
                        ticket.getCreatedBy()
                });
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private void fetchUserInfo() {
        new Thread(() -> {
            try {
                AuthenticatedUserResponse user = authService.getUserInfo();
                SwingUtilities.invokeLater(() -> userInfoLabel.setText(
                        "Welcome Back, " + user.getFullName() +
                                " (" + String.join(", ", user.getAuthorities()) + ")"
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void logout() {
        new Thread(() -> {
            try {
                if (authService.logout()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginUI().setVisible(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Logout failed.", "Error", JOptionPane.ERROR_MESSAGE));
                }
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "An error occurred during logout.", "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicketManagementUI().setVisible(true));
    }
}
