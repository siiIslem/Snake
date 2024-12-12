import java.awt.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 720;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use CardLayout for switching screens
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Create snake game panel
        snakeSource snakeGame = new snakeSource(boardWidth, boardHeight);
        mainPanel.add(snakeGame, "Game");

        // Create game-over panel
        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gameOverPanel.add(gameOverLabel, BorderLayout.CENTER);

        JLabel restartLabel = new JLabel("Press 'Any Arrow' to Restart", SwingConstants.CENTER);
        restartLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gameOverPanel.add(restartLabel, BorderLayout.SOUTH);

        mainPanel.add(gameOverPanel, "GameOver");

        // Add mainPanel to frame
        frame.add(mainPanel);
        frame.setVisible(true);

        // Set GameOverListener to snakeSource
        snakeGame.setGameOverListener(() -> {
            cardLayout.show(mainPanel, "GameOver");
            gameOverPanel.requestFocusInWindow();
        });

        // Add key listener for the restart action (to the gameOverPanel)
        gameOverPanel.setFocusable(true);
        gameOverPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP || 
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT || 
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    snakeGame.resetGame();
                    cardLayout.show(mainPanel, "Game");
                    snakeGame.requestFocusInWindow();
                }
            }
        });

        // Start the game in the "Game" panel initially
        frame.pack();
        snakeGame.requestFocusInWindow(); // Give focus to snakeGame to start
    }
}
