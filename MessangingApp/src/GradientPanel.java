import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    Color startColor;
    Color endColor;
    Integer arcWidth;
    Integer arcHeight;

    public GradientPanel(Color startColor, Color endColor, Integer arcWidth, Integer arcHeight){
        this.startColor = startColor;
        this.endColor = endColor;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setOpaque(false);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, startColor,
                getWidth(), getHeight(), endColor);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
    }
}
