package com.hahn.client.ui.dialog;

import com.hahn.client.model.TicketRequest;
import com.hahn.client.service.TicketService;
import com.hahn.client.ui.TicketManagementUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CreateTicketDialog extends JDialog {
    private final JTextField titleField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final JComboBox<String> priorityCombo;
    private final JComboBox<String> categoryCombo;
    private final TicketService ticketService;

    public CreateTicketDialog(Frame owner, TicketService ticketService) {
        super(owner, "Create New Ticket", true);
        this.ticketService = ticketService;

        setSize(400, 400);
        setLocationRelativeTo(owner);

        // Initialize ComboBoxes
        priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        categoryCombo = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components
        addLabelAndComponent(mainPanel, gbc, "Title:", titleField, 0);

        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);

        addLabelAndComponent(mainPanel, gbc, "Priority:", priorityCombo, 2);
        addLabelAndComponent(mainPanel, gbc, "Category:", categoryCombo, 3);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = createStyledButton("Submit", new Color(70, 130, 180));
        JButton cancelButton = createStyledButton("Cancel", new Color(220, 53, 69));

        submitButton.addActionListener(e -> createTicket());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void addLabelAndComponent(JPanel panel, GridBagConstraints gbc,
                                      String labelText, JComponent component, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void createTicket() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title and description are required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TicketRequest request = new TicketRequest(title, description, priority, category);

        new Thread(() -> {
            try {
                ticketService.createTicket(request);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Ticket created successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    ((TicketManagementUI) getOwner()).refreshTickets();
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error creating ticket: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}

