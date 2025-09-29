import net.miginfocom.swing.MigLayout;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


class LoginPage extends JFrame implements ActionListener {

    // --- Core fields ---
    UserInfoData databank;

    // --- Panels ---
    JPanel loginPanel;
    JPanel userInfoPanel;

    // --- Input fields ---
    JTextField usernameIn;
    JTextField passwordIn;

    // --- Buttons ---
    gradientButton signupButton;
    gradientButton loginButton;

    //--------------------------------------------------------------------------------
    public LoginPage() {
        setupWindow();
        initDatabase();
        buildLoginPanel();
        this.setVisible(true);
    }

    //--------------------------------------------------------------------------------
    // --- Window setup ---
    private void setupWindow() {
        this.setSize(600, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel bg = new GradientPanel(
                new Color(255, 102, 255),
                new Color(51, 153, 255),
                0, 0
        );
        bg.setLayout(new MigLayout("", "[grow][grow][grow]", "[grow][grow][grow]"));
        this.setContentPane(bg);
    }

    //--------------------------------------------------------------------------------
    // --- Database initialization ---
    private void initDatabase() {
        try {
            databank = new UserInfoData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //--------------------------------------------------------------------------------
    // --- Build login panel ---
    private void buildLoginPanel() {
        // Labels
        JLabel loginLabel = new JLabel("Log in");
        loginLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        loginLabel.setForeground(Color.BLACK);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("ArialBlack", Font.PLAIN, 13));
        usernameLabel.setForeground(Color.BLACK);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("ArialBlack", Font.PLAIN, 13));
        passwordLabel.setForeground(Color.BLACK);

        // User info fields
        userInfoPanel = new JPanel(new MigLayout());
        userInfoPanel.setBackground(Color.white);

        usernameIn = new JTextField();
        usernameIn.setPreferredSize(new Dimension(350, 20));
        usernameIn.setBackground(new Color(250, 250, 250));
        usernameIn.setFont(new Font("ArialBlack", Font.BOLD, 15));
        usernameIn.setForeground(Color.black);

        passwordIn = new JTextField();
        passwordIn.setPreferredSize(new Dimension(350, 20));
        passwordIn.setBackground(Color.white);
        passwordIn.setFont(new Font("ArialBlack", Font.BOLD, 15));
        passwordIn.setForeground(Color.black);

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(usernameIn, "wrap");
        userInfoPanel.add(passwordLabel);
        userInfoPanel.add(passwordIn);

        // Login panel
        loginPanel = new JPanel(new MigLayout("", "[grow][grow][grow]", "[grow][grow][grow][grow][grow]"));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(300, 400));

        // Buttons
        GradientPaint pressedPaint = new GradientPaint(0, 0,
                new Color(51, 153, 255),
                100, 150,
                new Color(255, 102, 255)
        );
        GradientPaint regularPaint = new GradientPaint(0, 0,
                new Color(255, 102, 255),
                100, 150,
                new Color(51, 153, 255)
        );

        signupButton = createGradientButton("Sign up", regularPaint, pressedPaint);
        loginButton = createGradientButton("Log in", regularPaint, pressedPaint);

        // Assemble layout
        loginPanel.add(loginLabel, "cell 1 0, alignx center, aligny top, push");
        loginPanel.add(userInfoPanel, "cell 1 1, alignx center, aligny top");
        loginPanel.add(signupButton, "cell 1 2, span, aligny bottom");
        loginPanel.add(loginButton, "cell 1 3, span, aligny top");

        this.add(loginPanel, "cell 1 1, align center");
    }

    //--------------------------------------------------------------------------------
    // --- Helper for button creation ---
    private gradientButton createGradientButton(String text, GradientPaint regular, GradientPaint pressed) {
        gradientButton button = new gradientButton(regular, pressed);
        button.setPreferredSize(new Dimension(300, 40));
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.white);
        button.setText(text);
        button.addActionListener(this);
        return button;
    }

    //--------------------------------------------------------------------------------
    // --- Button actions ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            handleSignup();
        }
        if (e.getSource() == loginButton) {
            handleLogin();
        }
    }

    // --- Signup handling ---
    private void handleSignup() {
        signupButton.pressed = true;
        flashButton(signupButton);

        if (!usernameIn.getText().isEmpty()) {
            try {
                if (UserInfoData.find_port(usernameIn.getText()) != null) {
                    System.out.println("Name available");
                    UserInfoData.add_credentials(usernameIn.getText(), passwordIn.getText(), "");
                    System.out.println("Sign up successful");
                } else {
                    System.out.println("Username already exists");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            System.out.println("Enter a valid username");
        }
    }

    // --- Login handling ---
    private void handleLogin() {
        loginButton.pressed = true;
        flashButton(loginButton);

        try {
            if (!usernameIn.getText().isEmpty() && UserInfoData.find_port(usernameIn.getText()) != null) {
                if (UserInfoData.check_credentials(usernameIn.getText(), passwordIn.getText())) {
                    try {
                        Client client = new Client(usernameIn.getText());
                        UserInfoData.add_user_port(client.port.toString(), usernameIn.getText());
                        new MessangingPage(client);
                        System.out.println("Log in successful");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    this.setVisible(false);
                    this.dispose();
                } else {
                    System.out.println("Wrong password");
                }
            } else {
                System.out.println("Enter a valid username");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // --- Button press flash animation ---
    private void flashButton(gradientButton button) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            button.pressed = false;
            button.repaint();
        }, 200, TimeUnit.MILLISECONDS);
    }

    //--------------------------------------------------------------------------------
    // --- Custom gradient button ---
    public class gradientButton extends JButton {
        private GradientPaint gp;
        private GradientPaint pressedGradient;
        public boolean pressed;

        public gradientButton(GradientPaint gp, GradientPaint gp2) {
            this.pressed = false;
            this.gp = gp;
            this.pressedGradient = gp2;

            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw gradient background
            if (!pressed) {
                g2d.setPaint(gp);
            } else {
                g2d.setPaint(pressedGradient);
            }
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2d.dispose();
            super.paintComponent(g); // Draw text
        }
    }
}
