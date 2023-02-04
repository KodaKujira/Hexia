import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class HexBoard extends JPanel {

    private static final int diameter = 40;
    private static final int boardSize = 11;
    private static final int radius = diameter / 2;
    private static Mcts_IA playerRed;

    private final Color[][] colors = new Color[boardSize][boardSize];

    public HexBoard() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int x = (int) point.getX();
                int y = (int) point.getY();
                int row = y / (diameter);
                int col = (x - row * radius) / (diameter);
                System.out.println("Row: " + row + ", Col: " + col);
                colors[col][row] = Color.BLUE;
                Move move = playerRed.searchMove(new Move(col, row));
                col = move.x;
                row = move.y;
                colors[col][row] = Color.RED;
                repaint();
            }
        });
    }

    public void iaMove(int col, int row){

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int row = 0; row < boardSize; ++row){
            for (int col = 0; col < boardSize; ++col){
                int x = diameter * (col + 1) + radius * row - radius;
                int y = diameter * (row + 1) - radius;
                Color color = colors[col][row];
                if (color == null) {
                    color = Color.WHITE;
                }
                g.setColor(color);
                int[] xPoints = new int[6];
                int[] yPoints = new int[6];
                double angle = Math.toRadians(30);
                for (int i = 0; i < 6; i++) {
                    xPoints[i] = x + (int) (radius * Math.cos(angle + i * Math.toRadians(60)));
                    yPoints[i] = y + (int) (radius * Math.sin(angle + i * Math.toRadians(60)));
                }
                g.fillPolygon(xPoints, yPoints, 6);
                g.setColor(Color.BLACK);
                g.drawPolygon(xPoints, yPoints, 6);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hex Game");
        frame.add(new HexBoard());
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        playerRed = new Mcts_IA("JMM", 2);
    }
}
