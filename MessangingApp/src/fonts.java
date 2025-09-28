import javax.swing.*;
import java.awt.*;

public class fonts extends JFrame {
    private JComboBox<String> fontBox;
    private JLabel previewLabel;

    public fonts() {
        setTitle("Java Font Previewer");
        setSize(600, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Get all available fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        // Dropdown with all font names
        fontBox = new JComboBox<>(fonts);
        add(fontBox, BorderLayout.NORTH);

        // Preview label
        previewLabel = new JLabel("The quick brown fox jumps over the lazy dog", SwingConstants.CENTER);
        previewLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        add(previewLabel, BorderLayout.CENTER);

        // Change font when user selects from dropdown
        fontBox.addActionListener(e -> {
            String selectedFont = (String) fontBox.getSelectedItem();
            previewLabel.setFont(new Font(selectedFont, Font.PLAIN, 24));
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new fonts().setVisible(true));
    }
}