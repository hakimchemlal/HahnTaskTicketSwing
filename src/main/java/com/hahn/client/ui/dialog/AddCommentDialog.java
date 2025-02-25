package com.hahn.client.ui.dialog;

import com.hahn.client.service.TicketService;
import com.hahn.client.ui.TicketManagementUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class AddCommentDialog extends JDialog {
        private final TicketService ticketService;
        private final Long ticketId;
        private final JTextArea commentArea;

        public AddCommentDialog(Frame owner, TicketService ticketService, Long ticketId) {
            super(owner, "Add Comment", true);
            this.ticketService = ticketService;
            this.ticketId = ticketId;

            setSize(400, 300);
            setLocationRelativeTo(owner);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Comment Area
            commentArea = new JTextArea(8, 30);
            commentArea.setLineWrap(true);
            commentArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(commentArea);

            // Buttons
            JPanel buttonPanel = new JPanel();
            JButton addButton = createStyledButton("Add Comment", new Color(70, 130, 180));
            JButton cancelButton = createStyledButton("Cancel", new Color(220, 53, 69));

            addButton.addActionListener(e -> addComment());
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);

            mainPanel.add(new JLabel("Comment:"), BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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

        private void addComment() {
            String comment = commentArea.getText().trim();

            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a comment",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Thread(() -> {
                try {
                    ticketService.addCommentX(ticketId, comment);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Comment added successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        ((TicketManagementUI) getOwner()).refreshTickets();
                    });
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Error adding comment: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }

