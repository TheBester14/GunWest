package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connexion extends JPanel {
    private JTextField ipField;
    private JTextField usernameField;
    private JButton connectButton;
    private GameFrame gameFrame;

    public Connexion(GameFrame gameFrame) {
        this.gameFrame = gameFrame;

        setLayout(new GridLayout(3, 2, 10, 10));

        JLabel ipLabel = new JLabel("IP Address:");
        ipField = new JTextField("127.0.0.1"); // Default IP address
        add(ipLabel);
        add(ipField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField("Player1"); // Default username
        add(usernameLabel);
        add(usernameField);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipField.getText();
                String username = usernameField.getText();

                if (!ip.isEmpty() && !username.isEmpty()) {
                    // Logic pour connecter en utiliser l'ip et le username
                    //gameFrame.switchToGamePanel(ip, username);
                } else {
                    JOptionPane.showMessageDialog(gameFrame, "Please enter both IP address and username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(connectButton);

        // Optional: set the panel size and visibility
        setPreferredSize(new Dimension(400, 200));
        setVisible(true);
    }
}
