package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Host extends JFrame {
    private JTextField ipField;
    private JTextField portField;
    private JButton startButton;
    private GameFrame gameFrame;

    public Host(GameFrame gameFrame) {
        this.gameFrame = gameFrame;

        setTitle("Host Game");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridLayout(3, 2, 10, 10));

        JLabel ipLabel = new JLabel("IP Address:");
        ipField = new JTextField("127.0.0.1");
        add(ipLabel);
        add(ipField);

        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("5000");
        add(portLabel);
        add(portField);

        startButton = new JButton("Start Hosting");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipField.getText();
                String port = portField.getText();
                
                if (!ip.isEmpty() && !port.isEmpty()) {
                   // gameFrame.startHosting(ip, port); // Call startHosting in GameFrame
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(gameFrame, "Please enter both IP address and port.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(startButton);

        setVisible(true);
    }
}
