package client.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
* MessageBubble
* <p>
* Each individual text message is stored as a bubble
* to help with formatting and visibility.
*
* @author Ian Ogden
* @version 4/26/25
*/
public class MessageBubble extends JPanel implements IMessageBubble {
  
    private boolean alignRight;
    private JTextArea textArea;
    private String text;

    public MessageBubble(String text, boolean alignRight) {
        this.text = text;
        this.alignRight = alignRight;
      
        setLayout(new BorderLayout());
        setOpaque(false);

        textArea = new JTextArea(this.text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setBorder(null);
        textArea.setFocusable(false);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        add(textArea, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (alignRight) {
            g2.setColor(new Color(0, 120, 250));
        } else {
            g2.setColor(new Color(220, 220, 220));
        }

        g2.fillRoundRect(10, 0, getWidth() - 20, getHeight(), 20, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(textArea.getFont());
        int textWidth = fm.stringWidth(text);
        int width = Math.min(textWidth + 41, 400);
        return new Dimension(width, textArea.getPreferredSize().height+1);
    }

}
