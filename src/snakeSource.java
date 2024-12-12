import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class snakeSource extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Game variables
    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    ArrayList<Tile> foodItems;  // List to store food items
    Random random;
    Timer gameLoop;
    double velocityX;
    double velocityY;
    boolean gameOver = false;
    boolean canChangeDirection = true;
    private boolean gameStarted = false;

    // Timer configuration
    private final int initialDelay = 20;  // Start delay in ms
    private final int maxDelay = 70;    // Maximum delay in ms
    private final int delayIncrement = 0;  // Amount to increase the delay per food eaten

    // GameOverListener to notify when the game is over
    private GameOverListener gameOverListener;

    // Constructor
    public snakeSource(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.darkGray);

        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        foodItems = new ArrayList<>();
        random = new Random();
        placeFood();  // Place initial food

        velocityX = 0;
        velocityY = 0;

        // Timer set to start at initial delay
        gameLoop = new Timer(initialDelay, this);  
        gameLoop.start();
    }

    // Set the GameOverListener
    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    // Increase game delay (making it easier)
    private void increaseGameSpeed() {
        int currentDelay = gameLoop.getDelay();
        int newDelay = Math.min(maxDelay, currentDelay + delayIncrement); // Increase delay up to maxDelay
        gameLoop.setDelay(newDelay); // Update the game loop delay
    }

    // Place food at a random position
    private void placeFood() {
        boolean validPosition = false;
        Tile newFood = null;

        // Keep trying to generate a new food position until it's valid
        while (!validPosition) {
            int foodX = random.nextInt(boardWidth / tileSize);
            int foodY = random.nextInt(boardHeight / tileSize);
            newFood = new Tile(foodX, foodY);

            // Check if the new food position collides with the snake's body
            validPosition = true;  // Assume valid until proven otherwise
            for (Tile snakePart : snakeBody) {
                if (collision(newFood, snakePart)) {
                    validPosition = false;  // Found a collision, so food position is invalid
                    break;  // Exit the loop and try again
                }
            }

            // Check if the new food position collides with the snake head
            if (collision(newFood, snakeHead)) {
                validPosition = false;  // Food can't spawn on the snake head
            }
        }
        // Once a valid position is found, place the food in the list
        foodItems.add(newFood);
        // Add food item at a random position
        while (foodItems.size() < (snakeBody.size() / 10) + 1) {
            foodItems.add(new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize)));
            increaseGameSpeed();  // Increase the delay to make it easier

        }
    }

    // Reset the game state
    public void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        foodItems.clear();  // Clear food items
        placeFood();  // Place initial food
        velocityX = 0;
        velocityY = 0;
        gameOver = false;
        gameStarted = false;  // Reset gameStarted
        gameLoop.setDelay(initialDelay);  // Reset the game loop delay to initial fast delay
        gameLoop.start();  // Restart the game loop
    }

    // Move the snake and check for collisions
    private void move() {
        // Move the body of the snake (from back to front)
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            if (i == 0) {
                snakeBody.get(i).x = snakeHead.x;
                snakeBody.get(i).y = snakeHead.y;
            } else {
                snakeBody.get(i).x = snakeBody.get(i - 1).x;
                snakeBody.get(i).y = snakeBody.get(i - 1).y;
            }
        }
        // Move the head by one tile per frame
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Check for collisions with walls
        if (snakeHead.x < 0 || snakeHead.x > boardWidth / tileSize ||
            snakeHead.y < 0 || snakeHead.y > boardHeight / tileSize) {
            gameOver = true;
        }
        // Check for collisions with the body (after the head has moved)
        for (Tile bodyPart : snakeBody) {
            if (collision(snakeHead, bodyPart)) {
                gameOver = true;
            }
        }
        // Check if the snake eats food
        for (Tile food : foodItems) {
            if (collision(snakeHead, food)) {
                snakeBody.add(new Tile(food.x, food.y));  // Add new body part
                foodItems.remove(food);
                placeFood();  // Place new food
                break;  // Exit the loop as the snake eats only one food item per move
            }
        }
    }

    // Collision detection
    private boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            if (gameOverListener != null) {
                gameOverListener.onGameOver(); // Notify the listener
            }
        }
        canChangeDirection = true;  // Allow direction change in the next frame
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Draw the game state
    private void draw(Graphics g) {
        // Draw food items
        g.setColor(new Color(255, 105, 180));  // Color for food
        for (Tile food : foodItems) {
            g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
        }

        // Draw snake head
        g.setColor(Color.white);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Draw snake body
        for (Tile snakePart : snakeBody) {
            g.setColor(Color.lightGray);
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        // Draw score or "Game Over" message
        String score = "Score: ";
        int scoreWidth = g.getFontMetrics().stringWidth(score);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(gameOver ? "Game Over!" : "Score: " + snakeBody.size(), (boardWidth - scoreWidth) / 2, 20);

        // Display message to start the game
        if (!gameStarted) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(new Color(255, 182, 193));
            String message = "Press any arrow key to move!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (boardWidth - messageWidth) / 2, boardHeight / 2);
        }
    }

    // Key press handler
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted && 
            (e.getKeyCode() == KeyEvent.VK_UP || 
            e.getKeyCode() == KeyEvent.VK_DOWN || 
            e.getKeyCode() == KeyEvent.VK_LEFT || 
            e.getKeyCode() == KeyEvent.VK_RIGHT)) {
            gameStarted = true;
        }

        if (canChangeDirection) {
            // Prevent snake from reversing direction
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                velocityX = 0;
                velocityY = -1;  // Move one
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                velocityX = 0;
                velocityY = 1;  // Move one
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                velocityX = -1;
                velocityY = 0;  // Move one
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                velocityX = 1;
                velocityY = 0;  // Move one
            }
            canChangeDirection = false;  // Prevent rapid direction changes
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
