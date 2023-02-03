import java.util.ArrayList;
import java.lang.Math;
public class Mcts_IA {
    private String name;
    private int player;
    private Node root;

    public Mcts_IA(String name, int player){
        this.name = name;
        this.player = player;
        int[][] arr = new int[11][11];
        ArrayList<Move> free = new ArrayList<>();
        for (int i = 0; i < 11; i++){
            for (int j = 0; j < 11; j++){
                free.add(new Move(i,j));
            }
        }
        this.root = new Node(arr, free, null, 1, null);
        this.root.player = this.player;
    }

    public Move searchMove(Move move){
        if (move != null){
            this.root = this.root.selectNewRoot(move);
        }
        long start = System.nanoTime();
        long timer = System.nanoTime() - start;
        while (timer < 5*Math.pow(10,9)){ //Temps de reflexion en seconde (ici 5)
            timer = System.nanoTime() - start;
            this.root.traversialR();
        }
        Move bestMove = this.root.bestMove();
        this.root = this.root.selectNewRoot(bestMove);
        return bestMove;
    }

    public String getName(){return this.name;}
}
