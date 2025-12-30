import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r, c;
        boolean opened = false;
        boolean isFlagged = false;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    JFrame frame = new JFrame("Minesweeper");
    int tileSize = 70;
    int numRows = 9;
    int numCols = 9;
    int widthBoard = tileSize * numCols;
    int heightBoard = tileSize * numRows;

    JPanel textPanel = new JPanel();
    JLabel textLabel = new JLabel();
    JButton resetButton = new JButton("Yeniden Başlat");
    JPanel boardPanel = new JPanel();

    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList = new ArrayList<>();

    int mineCount = 10;
    int flagsUsed = 0;
    int tilesClicked = 0;
    boolean gameOver = false;
    Random random = new Random();

    Minesweeper() {
        frame.setSize(widthBoard, heightBoard + 80);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        updateLabel();
        textLabel.setForeground(Color.decode("#F7F6D3"));
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);

        resetButton.setFocusable(false);
        resetButton.addActionListener(e -> resetGame());

        textPanel.setBackground(Color.decode("#B8DB80"));
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.add(resetButton, BorderLayout.EAST);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.decode("#EAE0CF"));
        frame.add(boardPanel);

        initializeBoard();
        setMines();
        frame.setVisible(true);
    }


    void updateLabel() {
        textLabel.setText("Minesweeper: " + (mineCount - flagsUsed));
    }

    void initializeBoard() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
                tile.setBackground(Color.WHITE);

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (!tile.opened && !tile.isFlagged) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }

                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (!tile.opened) {
                                if (!tile.isFlagged) {
                                    tile.isFlagged = true;
                                    tile.setText("🚩");
                                    tile.setForeground(Color.RED);
                                    flagsUsed++;
                                } else {
                                    tile.isFlagged = false;
                                    tile.setText("");
                                    flagsUsed--;
                                }
                                updateLabel();
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
    }

    void resetGame() {
        gameOver = false;
        tilesClicked = 0;
        flagsUsed = 0;
        mineList.clear();
        updateLabel();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c].opened = false;
                board[r][c].isFlagged = false;
                board[r][c].setText("");
                board[r][c].setBackground(Color.WHITE);
                board[r][c].setForeground(Color.BLACK);
            }
        }
        setMines();
    }



    void setMines() {
        int minesLeft = mineCount;
        while (minesLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                minesLeft--;
            }
        }
    }

    void revealMines() {
        gameOver = true;
        for (MineTile tile : mineList) {
            tile.setText("💣");
            tile.setBackground(Color.RED);
        }
        textLabel.setText("KAYBETTİNİZ!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;
        MineTile tile = board[r][c];
        if (tile.opened || mineList.contains(tile) || tile.isFlagged) return;

        tile.opened = true;
        tilesClicked++;
        tile.setBackground(Color.decode("#EAE0CF"));

        int minesFound = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                minesFound += countMine(r + i, c + j);
            }
        }

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
            setNumberColor(tile, minesFound);
        } else {
            tile.setText("");
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    checkMine(r + i, c + j);
                }
            }
        }

        if (tilesClicked == (numRows * numCols) - mineList.size()) {
            gameOver = true;
            textLabel.setText("KAZANDINIZ!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;
        if (mineList.contains(board[r][c])) return 1;
        return 0;
    }

    void setNumberColor(MineTile tile, int count) {
        if (count == 1) tile.setForeground(Color.BLUE);
        else if (count == 2) tile.setForeground(new Color(0, 128, 0));
        else if (count == 3) tile.setForeground(Color.RED);
    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}