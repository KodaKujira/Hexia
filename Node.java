import java.util.*;
public class Node {

    private int [][] state;
    private ArrayList<Move> free = new ArrayList<Move>();
    private Move move;
    private int toMove;
    int player;
    private float wins = 0;
    private int n = 0;
    private boolean isLeaf = true;
    private Node parent;
    private ArrayList<Node> childs = new ArrayList<Node>();


    public Node(int[][] newState, ArrayList<Move> free, Move move, int toMove, Node parent){
        this.state = newState;
        this.free = free;
        this.move = move;
        this.toMove = toMove;
        this.parent = parent;
        if(parent != null){
            this.player = parent.player;
        }
    }

    public Move bestMove(){
        Node bestChance = this.childs.get(0);
        for(Node child : this.childs){
            if(child.wins/child.getn() > bestChance.wins/bestChance.getn() ){//child.getn() > bestChance.getn()
                bestChance = child;
            }
        }
        return bestChance.move;
    }

    public Node selectNewRoot(Move move){
        if (this.childs.size() == 0){
            this.expand();
        }
        for(Node child : this.childs){
            if (child.move.x == move.x && child.move.y == move.y){
                child.parent = null;
                return child;
            }
        }
        return null;
    }

    public void expand(){
        for(int i = 0; i < this.free.size(); i++){
            int[][] newState = new int[11][11];
            newState = this.deepStateCopy(this.state);
            Move nextMove = this.free.get(i);
            newState[nextMove.x][nextMove.y] = this.toMove;
            ArrayList<Move> newFree = new ArrayList<>(this.free);
            newFree.remove(i);
            this.childs.add(new Node(newState, newFree, nextMove, 3 - this.toMove, this));
        }
        this.isLeaf = false;
    }

    public int[][] deepStateCopy(int[][] state){
        int[][] deepCopy = new int [state.length][];
        for(int i = 0; i < state.length; i++){
            deepCopy[i] = Arrays.copyOf(state[i], state[i].length);
        }
        return deepCopy;
    }

    public void traversal(int N){
        if (this.isLeaf){
            if (this.n != 0){
                this.expand();
            }else {
                this.rollout();
            }
        }else{
            Node current = this.childs.get(0);
            for (Node child : this.childs) {
                if (child.n == 0) {
                    current = child;
                    break;
                } else if (this.toMove == this.player && child.UCB1(N) > current.UCB1(N)) {
                    current = child;
                } else if (this.toMove != this.player && child.UCB1(N) < current.UCB1(N)) {
                    current = child;
                }
            }
            current.traversal(N);
        }
    }

    public void traversialR(){
        traversal(this.n);
    }

    public void rollout(){
        int fakePlayer = this.toMove;
        int [][] fakeState = this.deepStateCopy(this.state);
        List<Move>fakeFree = new ArrayList<Move>(this.free);
        Collections.shuffle(fakeFree);
        for (Move move : fakeFree){
            fakeState[move.x][move.y] = fakePlayer;
            fakePlayer = 3 - fakePlayer;
        }
        if(this.testVictory(fakeState)){
            this.backpropagation(10);
        }else {
            this.backpropagation(0);
        }
    }

    public void backpropagation(int w){
        Node current = this;
        while(current != null){
            current.n ++;
            current.wins += w;
            current = current.parent;
        }
    }

    public boolean testVictory(int[][] state){
        boolean [][] tested = new boolean[11][11];
        if(this.player == 1){
            for(int i = 0; i < 11; i++){
                if (state[0][i] == this.player && !tested[0][i]){
                    int[] cellTest = {0,i};
                    if (testPath(cellTest, tested, state)){
                        return true;
                    }
                }
            }return false;
        }
        for(int i = 0; i < 11; i++){
            if (state[i][0] == this.player && !tested[i][0]){
                int[] cellTest = {i,0};
                if (testPath(cellTest, tested, state)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean testPath(int[] cell, boolean[][] tested, int[][] state){
        tested[cell[0]][cell[1]] = true;
        int[][] neighboursCoord = {{cell[0] - 1, cell[1]}, {cell[0], cell[1] - 1}, {cell[0] + 1, cell[1] - 1}, {cell[0] + 1, cell[1]}, {cell[0], cell[1] + 1}, {cell[0] - 1, cell[1] + 1}};
        for(int [] neighbour : neighboursCoord){
            if (this.validCell(neighbour)){
                int x = neighbour[0];
                int y = neighbour[1];
                if (state[x][y] == 2 && this.player == 2 && !tested[x][y]){
                    if(y == 11 - 1){
                        return true;
                    }else if(this.testPath(neighbour, tested, state)){
                        return true;
                    }
                } else if (state[x][y] == 1 && this.player == 1 && !tested[x][y]) {
                    if(x == 11 -1){
                        return true;
                    } else if (this.testPath(neighbour,tested,state)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public boolean validCell(int [] cell){
        return cell[1] >= 0 && cell[1] < 11 && cell[0] >= 0 && cell[0] < 11;
    }

    public int getn(){
        return this.n;
    }

    public int[][] getState() {
        return this.state;
    }

    public double UCB1(int N){
        return this.wins / this.n + 2*Math.sqrt(Math.log(N)/ this.n);
    }
}