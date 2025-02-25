package com.hahn.client.ui.dialog;

import com.hahn.client.service.TicketService;
import com.hahn.client.ui.TicketManagementUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class UpdateStatusDialog extends JDialog {
    private final TicketService ticketService;
    private final Long ticketId;
    private final JComboBox<String> statusCombo;

    public UpdateStatusDialog(Frame owner, TicketService ticketService, Long ticketId) {
        super(owner, "Update Ticket Status", true);
        this.ticketService = ticketService;
        this.ticketId = ticketId;

        setSize(300, 150);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Status ComboBox
        statusCombo = new JComboBox<>(new String[]{"NEW", "IN_PROGRESS", "RESOLVED"});

        mainPanel.add(new JLabel("New Status:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton updateButton = createStyledButton("Update", new Color(70, 130, 180));
        JButton cancelButton = createStyledButton("Cancel", new Color(220, 53, 69));

        updateButton.addActionListener(e -> updateStatus());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void updateStatus() {
        String newStatus = (String) statusCombo.getSelectedItem();

        new Thread(() -> {
            try {
                ticketService.updateTicketStatus(ticketId, newStatus);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Status updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    ((TicketManagementUI) getOwner()).refreshTickets();
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error updating status: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}

