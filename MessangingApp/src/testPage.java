import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

class testPage extends JFrame implements ActionListener{
    JButton sendButton;
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

    public testPage() throws IOException {

        //GUI-----------------------------------------------------------------
        this.setSize(600, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new MigLayout("fill"));
        this.setBackground(new Color(52, 53, 54));
        ((JPanel)this.getContentPane()).setOpaque(false);

        openClose = 0;

        textingPanel = new JPanel();
        textingPanel.setBackground(new Color(52, 53, 54));
        textingPanel.setLayout(new MigLayout("fill"));

        messagePanel = new GradientPanel(new Color(110, 116, 124, 120),
                new Color(110, 116, 124, 120) , 20, 20);
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
        connectionInfoPanel.add(new JLabel("Username"){{setFont(new Font("ArialBlack", Font.BOLD, 25));
            setForeground(Color.white);}});
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

        //--------------------------------------------------------------------

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == expandButton){
            if (openClose == 0){
                sideBar.setPreferredSize(new Dimension(400, 0));
                openClose = 1;
                searchUserButton.setText("Enter username");
                userSearchPanel.setVisible(true);
            }
            else if (openClose == 1){
                sideBar.setPreferredSize(new Dimension(0, 0));
                openClose = 0;
                searchUserButton.setText("");
                userSearchPanel.setVisible(false);
            }
            sideBar.revalidate();
            sideBar.repaint();
            this.setVisible(true);
        }
    }
    public static BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        // Enable high-quality scaling
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    public static void main(String[] args) throws IOException {
        testPage page = new testPage();
    }
}
