import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

class MessangingPage extends JFrame implements ActionListener {
    JButton sendButton;
    Client client;
    JTextField message;
    String username;
    JPanel textingPanel;
    JPanel messagePanel;
    JPanel connectionInfoPanel;
    JTextField userSelect;
    JButton searchUserButton;
    JButton expandButton;
    JPanel sideBar;
    Integer openClose;
    JPanel userSearchPanel;
    JLabel reciever;

    public MessangingPage(Client client) throws IOException {
        this.client = client;
        this.username = client.username;

        // Start client reader thread
        new Thread(client).start();
        new Thread(() -> {
            try {
                String msg;
                while ((msg = client.in.readLine()) != null) {
                    String finalMsg = msg.split(">")[0];
                    SwingUtilities.invokeLater(() -> {
                        GradientPanel msgPanel = new GradientPanel(Color.white, Color.white, 20, 20);
                        msgPanel.add(new JLabel(finalMsg));
                        textingPanel.add(msgPanel, "align left, wrap");
                        textingPanel.revalidate();
                        textingPanel.repaint();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // GUI ---------------------------------------------------------------
        this.setSize(600, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new MigLayout("fill"));
        this.setBackground(new Color(52, 53, 54));
        ((JPanel) this.getContentPane()).setOpaque(false);

        openClose = 0;

        textingPanel = new JPanel();
        textingPanel.setBackground(new Color(52, 53, 54));
        textingPanel.setLayout(new MigLayout("fillx"));

        messagePanel = new GradientPanel(new Color(110, 116, 124, 120),
                new Color(110, 116, 124, 120), 20, 20);
        messagePanel.setLayout(new MigLayout());

        message = new JTextField();
        message.setPreferredSize(new Dimension(100, 20));

        sendButton = new JButton();
        sendButton.addActionListener(this);
        sendButton.setPreferredSize(new Dimension(50, 20));
        sendButton.setBorder(null);
        sendButton.setBackground(null);
        sendButton.setFocusPainted(false);
        sendButton.setIcon(new ImageIcon(getScaledImage(
                new ImageIcon("icons/arrow-up.png").getImage(), 20, 20)));

        connectionInfoPanel = new JPanel();
        connectionInfoPanel.setBackground(new Color(88, 101, 242));
        connectionInfoPanel.setLayout(new MigLayout("fill"));
        reciever = new JLabel();
        reciever.setFont(new Font("ArialBlack", Font.BOLD, 25));
        reciever.setForeground(Color.white);
        connectionInfoPanel.add(reciever, "alignx left, aligny center");

        searchUserButton = new JButton();
        searchUserButton.addActionListener(this);

        userSelect = new JTextField();

        messagePanel.add(sendButton);
        messagePanel.add(message, "push, grow, wrap");

        sideBar = new JPanel();
        sideBar.setLayout(new MigLayout("fill"));
        sideBar.setBackground(new Color(45, 45, 51));

        userSearchPanel = new JPanel(new MigLayout("fill"));
        userSearchPanel.setBackground(new Color(88, 101, 242));
        userSearchPanel.add(searchUserButton, "alignx left, aligny center, height 20!");
        userSearchPanel.add(userSelect, "aligny center, pushx, growx");
        sideBar.add(userSearchPanel, "dock north, height 50!");
        userSearchPanel.setVisible(false);

        expandButton = new JButton();
        expandButton.setBorder(null);
        expandButton.setBackground(null);
        expandButton.setFocusPainted(false);

        expandButton.setIcon(new ImageIcon(getScaledImage(
                new ImageIcon("icons/expandArrow.png").getImage(), 20, 20)));
        expandButton.addActionListener(this);
        sideBar.add(expandButton, "alignx right, aligny center, gap 10 10");

        this.add(sideBar, "dock west");
        this.add(connectionInfoPanel, "dock north, height 50!");
        this.add(textingPanel, "dock center, push, grow");
        this.add(messagePanel, "dock south, pushx, growx, gap 10 10 10 10");
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            client.out.println(message.getText() + ">" + client.recieverPort);
            GradientPanel myMsgPanel = new GradientPanel(Color.white, Color.white, 20, 20);
            myMsgPanel.add(new JLabel(message.getText()));
            textingPanel.add(myMsgPanel, "align right, wrap");
            textingPanel.revalidate();
            textingPanel.repaint();
        }

        if (e.getSource() == searchUserButton) {
            try {
                if (UserInfoData.find_port(userSelect.getText()) != null) {
                    client.recieverPort = UserInfoData.find_port(userSelect.getText());
                    reciever.setText(userSelect.getText() + ": " + client.recieverPort);
                    reciever.revalidate();
                    reciever.repaint();
                    this.setVisible(true);
                } else {
                    System.out.println("No user found");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (e.getSource() == expandButton) {
            if (openClose == 0) {
                sideBar.setPreferredSize(new Dimension(400, 0));
                openClose = 1;
                searchUserButton.setText("Enter username");
                userSearchPanel.setVisible(true);
            } else {
                sideBar.setPreferredSize(new Dimension(0, 0));
                openClose = 0;
                searchUserButton.setText("");
                userSearchPanel.setVisible(false);
            }
            sideBar.revalidate();
            sideBar.repaint();
        }
    }

    public static BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}
