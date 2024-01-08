import java.util.ArrayList;
import java.lang.Math;
public class Mcts_IA {
    private String name;
    private int player;
    private Node root;
    private int wValue;
    private double constant;

    public Mcts_IA(String name, int player, int wValue, double constant){
        this.name = name;
        this.player = player;
        this.wValue = wValue;
        this.constant = constant;
        int[][] arr = new int[HexBoard.boardSize][HexBoard.boardSize];
        ArrayList<Move> free = new ArrayList<>();
        for (int i = 0; i < HexBoard.boardSize; i++){
            for (int j = 0; j < HexBoard.boardSize; j++){
                free.add(new Move(i,j));
            }
        }
        this.root = new Node(arr, free, null, 1, null, wValue, constant);
        this.root.player = this.player;
    }
    public Move searchMove(Move move){
        if (move != null){
            this.root = this.root.selectNewRoot(move);
        }else {
            this.root.expand();
        }
        long start = System.nanoTime();
        long timer = System.nanoTime() - start;
        int counter = 0;
        while (timer < 2*Math.pow(10,9)){ //Temps de reflexion en seconde (ici 4)
            timer = System.nanoTime() - start;
            this.root.traversialR();
            counter ++;
        }
        Move bestMove = this.root.bestMove();
        this.root = this.root.selectNewRoot(bestMove);
        System.out.println(counter);
        return bestMove;
    }
    public Boolean hasWon(){
        return this.root.testVictory(root.getState()) && this.root.player == player;
    }
    public String getName(){return this.name;}
}