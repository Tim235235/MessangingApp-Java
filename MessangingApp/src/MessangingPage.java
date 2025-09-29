import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class MessangingPage extends JFrame implements ActionListener {

    // --- Core fields ---
    Client client;
    String username;
    String activeUser;

    // --- Panels ---
    JPanel textSwitchPanel;
    JPanel usersListPanel;
    JPanel sideBar;
    JPanel userSearchPanel;
    JPanel connectionInfoPanel;
    JPanel messagePanel;

    // --- Components ---
    JLabel receiver;
    JTextField message;
    JTextField userSelect;
    JButton sendButton;
    JButton searchUserButton;
    JButton expandButton;

    // --- Helpers ---
    Map<String, JScrollPane> conversationPanels;
    CardLayout cardLayout;
    Integer openClose;

    //--------------------------------------------------------------------------------
    public MessangingPage(Client client) throws IOException {
        this.client = client;
        this.username = client.username;
        this.activeUser = "";

        // Start client reader thread
        new Thread(client).start();

        // Window setup
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new MigLayout("fill"));
        setBackground(new Color(52, 53, 54));
        ((JPanel) getContentPane()).setOpaque(false);

        // Layout setup
        openClose = 0;
        cardLayout = new CardLayout();
        textSwitchPanel = new JPanel(cardLayout);
        conversationPanels = new HashMap<>();

        // GUI building
        sideBar();
        add(textSwitchPanel, "dock center, push, grow");
        connectionInfo();
        messageBar();

        setVisible(true);

        // Start input listener
        new Thread(this::inputListener).start();
    }

    //--------------------------------------------------------------------------------
    // --- Sidebar setup ---
    public void sideBar() {
        searchUserButton = new JButton();
        searchUserButton.addActionListener(this);

        userSelect = new JTextField();

        sideBar = new JPanel(new MigLayout("fill"));
        sideBar.setBackground(new Color(45, 45, 51));

        expandButton = new JButton();
        expandButton.setBorder(null);
        expandButton.setBackground(null);
        expandButton.setFocusPainted(false);
        expandButton.setIcon(new ImageIcon(getScaledImage(
                new ImageIcon("icons/expandArrow.png").getImage(), 20, 20)));
        expandButton.addActionListener(this);

        usersListPanel = new JPanel(new MigLayout());
        usersListPanel.setBackground(Color.lightGray);
        usersListPanel.setVisible(false);

        userSearchPanel = new JPanel(new MigLayout("fill"));
        userSearchPanel.setBackground(new Color(88, 101, 242));
        userSearchPanel.setVisible(false);

        userSearchPanel.add(new JLabel(username) {{
            setForeground(Color.white);
        }});
        userSearchPanel.add(searchUserButton, "alignx left, aligny center, height 20!");
        userSearchPanel.add(userSelect, "aligny center, pushx, growx");

        sideBar.add(userSearchPanel, "dock north, height 50!");
        sideBar.add(usersListPanel, "alignx left, pushy, growy");
        sideBar.add(expandButton, "alignx right, aligny center, gap 10 10");

        this.add(sideBar, "dock west");
    }

    //--------------------------------------------------------------------------------
    // --- Message bar setup ---
    public void messageBar() {
        messagePanel = new GradientPanel(
                new Color(110, 116, 124, 120),
                new Color(110, 116, 124, 120),
                20, 20
        );
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

        messagePanel.add(sendButton);
        messagePanel.add(message, "push, grow, wrap");

        add(messagePanel, "dock south, pushx, growx, gap 10 10 10 10");
    }

    //--------------------------------------------------------------------------------
    // --- Connection info bar ---
    public void connectionInfo() {
        connectionInfoPanel = new JPanel(new MigLayout("fill"));
        connectionInfoPanel.setBackground(new Color(88, 101, 242));

        receiver = new JLabel();
        receiver.setFont(new Font("ArialBlack", Font.BOLD, 25));
        receiver.setForeground(Color.white);

        connectionInfoPanel.add(receiver, "alignx left, aligny center");
        this.add(connectionInfoPanel, "dock north, height 50!");
    }

    //--------------------------------------------------------------------------------
    // --- Input listener thread ---
    public void inputListener() {
        try {
            String msg;
            while ((msg = client.in.readLine()) != null) {
                String[] parts = msg.split(">");
                String text = parts[0];
                String senderPort = parts[2];
                String senderName = UserInfoData.find_name(senderPort);

                SwingUtilities.invokeLater(() -> {
                    GradientPanel msgPanel = new GradientPanel(Color.white, Color.white, 20, 20);
                    msgPanel.add(new JLabel(senderName + ": " + text));

                    JScrollPane convo = conversationPanels.get(senderName);
                    if (convo == null) {
                        try {
                            addConversation(senderName);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        convo = conversationPanels.get(senderName);
                    }

                    activeUser = senderName;
                    client.recieverPort = senderPort;
                    receiver.setText(senderName + ": " + client.recieverPort);

                    JPanel convoPanel = (JPanel) convo.getViewport().getView();
                    convoPanel.add(msgPanel, "align left, wrap");
                    convoPanel.revalidate();
                    convoPanel.repaint();
                });
            }
            try {
                client.in.close();
                client.out.close();
                client.client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //--------------------------------------------------------------------------------
    // --- Add a new conversation tab ---
    public void addConversation(String username) throws SQLException {
        if (conversationPanels.get(username) == null) {
            JPanel convoPanel = new JPanel(new MigLayout("fillx"));
            convoPanel.setBackground(Color.DARK_GRAY);

            JScrollPane scrollConvo = new JScrollPane(convoPanel);
            conversationPanels.put(username, scrollConvo);
            textSwitchPanel.add(scrollConvo, username);

            JButton goToConvo = new JButton(username);
            usersListPanel.add(goToConvo, "alignx left, aligny top, wrap");

            goToConvo.addActionListener((e) -> {
                JButton pressedButton = (JButton) e.getSource();
                activeUser = pressedButton.getText();

                try {
                    client.recieverPort = UserInfoData.find_port(activeUser);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                receiver.setText(username + ": " + client.recieverPort);
                cardLayout.show(textSwitchPanel, activeUser);
            });
        }
    }

    //--------------------------------------------------------------------------------
    // --- Action handling ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            client.out.println(message.getText() + ">" + client.recieverPort + ">" + client.port.toString());

            GradientPanel myMsgPanel = new GradientPanel(Color.white, Color.white, 20, 20);
            myMsgPanel.add(new JLabel(message.getText()));

            JScrollPane convo = conversationPanels.get(activeUser);
            JPanel convoPanel = (JPanel) convo.getViewport().getView();
            convoPanel.add(myMsgPanel, "align right, wrap");
            convoPanel.revalidate();
            convoPanel.repaint();
        }

        if (e.getSource() == searchUserButton) {
            try {
                if (UserInfoData.find_port(userSelect.getText()) != null) {
                    addConversation(userSelect.getText());
                    receiver.setText(userSelect.getText() + ": " + client.recieverPort);
                    receiver.revalidate();
                    receiver.repaint();
                    setVisible(true);
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

                usersListPanel.setPreferredSize(new Dimension(200, 0));
                usersListPanel.setVisible(true);

                searchUserButton.setText("Enter username");
                userSearchPanel.setVisible(true);
            } else {
                sideBar.setPreferredSize(new Dimension(0, 0));
                openClose = 0;

                usersListPanel.setPreferredSize(new Dimension(0, 0));
                usersListPanel.setVisible(false);

                searchUserButton.setText("");
                userSearchPanel.setVisible(false);
            }

            sideBar.revalidate();
            sideBar.repaint();
        }
    }

    //--------------------------------------------------------------------------------
    // --- Utility ---
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
