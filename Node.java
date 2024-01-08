import java.util.*;
public class Node {

    private final int [][] state;
    private ArrayList<Move> free = new ArrayList<Move>();
    private final Move move;
    private final int toMove;
    int player;
    private float wins = 0;
    private int n = 0;
    private boolean isLeaf = true;
    private Node parent;
    private ArrayList<Node> childs = new ArrayList<Node>();
    private int wValue;
    private double constant;


    public Node(int[][] newState, ArrayList<Move> free, Move move, int toMove, Node parent, int wValue, double constant){
        this.wValue = wValue;
        this.constant = constant;
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
            if(child.wins > bestChance.wins){//child.getn() > bestChance.getn()
                bestChance = child;
            }
        }
        System.out.println(bestChance.n + " " + bestChance.wins);
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
            int[][] newState = new int[HexBoard.boardSize][HexBoard.boardSize];
            newState = this.deepStateCopy(this.state);
            Move nextMove = this.free.get(i);
            newState[nextMove.x][nextMove.y] = this.toMove;
            ArrayList<Move> newFree = new ArrayList<>(this.free);
            newFree.remove(i);
            this.childs.add(new Node(newState, newFree, nextMove, 3 - this.toMove, this,  wValue, constant));
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
            if (this.n != 0 && this.free.size() !=0){
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

    public int [] neighborInBridge(Move move, int [][] state){
        int[] cell = {move.x, move.y};
        if (cell[0] == HexBoard.boardSize | cell[0] == 0 | cell[1] == HexBoard.boardSize | cell[1] == 0){
            return borderCompanion(cell[0], cell[1], state);
        }
        int[][] neighboursCoord = {{cell[0] - 1, cell[1]}, {cell[0], cell[1] - 1}, {cell[0] + 1, cell[1] - 1}, {cell[0] + 1, cell[1]}, {cell[0], cell[1] + 1}, {cell[0] - 1, cell[1] + 1}};
        for(int i = 0; i < 6; i++){
            int [] neighbour = neighboursCoord[i];
            int [] bridge = neighboursCoord[(i+2)%6];
            int [] otherNeighbor = neighboursCoord[(i+1)%6];
            int x = neighbour[0];
            int y = neighbour[1];
            int x2 = bridge[0];
            int y2 = bridge[1];
            if (!this.validCell(neighbour) | !this.validCell(bridge)){
                break;
            }
            if (state[otherNeighbor[0]][otherNeighbor[1]] == 0 && state[x][y] == state [x2][y2]){
                return otherNeighbor;
            }
        }
        return null;
    }

    private int[] borderCompanion(int x, int y, int [][] state) {
        int[][] neighboursCoord = {{x - 1, y}, {x, y - 1}, {x + 1, y - 1}, {x + 1, y}, {x, y + 1}, {x - 1, y + 1}};
        if (x == 0 && y == 0) {
            if(state[1][0] == 1 && state[0][1] == 0){
                return new int[]{0,1};
            } else if (state[0][1] == 2 && state[1][0] == 0) {
                return new int[]{1,0};
            }
            return null;
        }

        // Coin en bas à droite (boardSize-1,boardSize-1)
        if (x == HexBoard.boardSize-1 && y == HexBoard.boardSize-1) {
            if(state[HexBoard.boardSize-2][HexBoard.boardSize-1] == 1 && state[HexBoard.boardSize-1][HexBoard.boardSize-2] == 0){
                return new int[]{HexBoard.boardSize-1,HexBoard.boardSize-2};
            } else if (state[HexBoard.boardSize-1][HexBoard.boardSize-2] == 2 && state[HexBoard.boardSize-2][HexBoard.boardSize-1] == 0){
                return new int[]{HexBoard.boardSize-2,HexBoard.boardSize-1};
            }
            return null;
        }

        // Coin en haut à droite (boardSize-1,0)
        if (x == HexBoard.boardSize-1 && y == 0) {
            if(state[HexBoard.boardSize-2][1] == 1 && state[HexBoard.boardSize-1][1] == 0){
                return new int[]{HexBoard.boardSize-1,1};
            } else if (state[HexBoard.boardSize-2][1] == 2 && state[HexBoard.boardSize-2][0] == 0){
                return new int[]{HexBoard.boardSize-2,0};
            }
            return null;
        }
        // Coin en bas à gauche (0,boardSize-1)
        if (x == 0 && y == HexBoard.boardSize-1) {
            if(state[1][HexBoard.boardSize-2] == 1 && state[0][HexBoard.boardSize-2] == 0){
                return new int[]{0,HexBoard.boardSize-2};
            } else if (state[1][HexBoard.boardSize-2] == 2 && state[1][HexBoard.boardSize-1] == 0){
                return new int[]{1,HexBoard.boardSize-1};
            }
            return null;
        }
        //Bord de gauche
        if(x == 0){
            int [] neighbour1 = neighboursCoord[1];
            int [] neighbour2 = neighboursCoord[2];
            if(state[neighbour1[0]][neighbour1[1]] == 0 && state[neighbour2[0]][neighbour2[1]] == 1){
                return neighbour1;
            }
            int [] neighbour3 = neighboursCoord[3];
            int [] neighbour4 = neighboursCoord[4];
            if(state[neighbour4[0]][neighbour4[1]] == 0 && state[neighbour3[0]][neighbour3[1]] == 1){
                return neighbour4;
            }
            return null;
        }
        //Bord de droite
        if(x == HexBoard.boardSize-1){
            int [] neighbour4 = neighboursCoord[4];
            int [] neighbour5 = neighboursCoord[5];
            if(state[neighbour4[0]][neighbour4[1]] == 0 && state[neighbour5[0]][neighbour5[1]] == 1){
                return neighbour4;
            }
            int [] neighbour0 = neighboursCoord[0];
            int [] neighbour1 = neighboursCoord[1];
            if(state[neighbour1[0]][neighbour1[1]] == 0 && state[neighbour0[0]][neighbour0[1]] == 1){
                return neighbour1;
            }
            return null;
        }
        //Bord du haut
        if(y == 0){
            int [] neighbour3 = neighboursCoord[3];
            int [] neighbour4 = neighboursCoord[4];
            if(state[neighbour3[0]][neighbour3[1]] == 0 && state[neighbour4[0]][neighbour4[1]] == 2){
                return neighbour3;
            }
            int [] neighbour5 = neighboursCoord[5];
            int [] neighbour0 = neighboursCoord[0];
            if(state[neighbour0[0]][neighbour0[1]] == 0 && state[neighbour5[0]][neighbour5[1]] == 2){
                return neighbour0;
            }
            return null;
        }
        //Bord du bas
        if(y == HexBoard.boardSize-1){
            int [] neighbour0 = neighboursCoord[0];
            int [] neighbour1 = neighboursCoord[1];
            if(state[neighbour0[0]][neighbour0[1]] == 0 && state[neighbour1[0]][neighbour1[1]] == 2){
                return neighbour0;
            }
            int [] neighbour2 = neighboursCoord[2];
            int [] neighbour3 = neighboursCoord[3];
            if(state[neighbour3[0]][neighbour3[1]] == 0 && state[neighbour2[0]][neighbour2[1]] == 2){
                return neighbour3;
            }
        }
        return null;
    }

    public void rollout(){
        int fakePlayer = this.toMove;
        int [][] fakeState = this.deepStateCopy(this.state);
        List<Move>fakeFree = new ArrayList<Move>(this.free);
        Collections.shuffle(fakeFree, new Random());
        for (Move move : fakeFree){
            int [] neighborBridge = this.neighborInBridge(move, fakeState);
            if (neighborBridge == null){
                if(fakeState[move.x][move.y] == 0){
                    fakeState[move.x][move.y] = fakePlayer;
                    fakePlayer = 3 - fakePlayer;
                }
            }else {
                fakeState[move.x][move.y] = fakePlayer;
                fakeState[neighborBridge[0]][neighborBridge[1]] = 3 - fakePlayer;
            }
        }
        if(this.testVictory(fakeState)){
            this.backpropagation(wValue);
        }else {
            this.backpropagation(-1);
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
        boolean [][] tested = new boolean[HexBoard.boardSize][HexBoard.boardSize];
        if(this.player == 1){
            for(int i = 0; i < HexBoard.boardSize; i++){
                if (state[0][i] == this.player && !tested[0][i]){
                    int[] cellTest = {0,i};
                    if (testPath(cellTest, tested, state)){
                        return true;
                    }
                }
            }return false;
        }
        for(int i = 0; i < HexBoard.boardSize; i++){
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
                    if(y == HexBoard.boardSize - 1){
                        return true;
                    }else if(this.testPath(neighbour, tested, state)){
                        return true;
                    }
                } else if (state[x][y] == 1 && this.player == 1 && !tested[x][y]) {
                    if(x == HexBoard.boardSize -1){
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
        return cell[1] >= 0 && cell[1] < HexBoard.boardSize && cell[0] >= 0 && cell[0] < HexBoard.boardSize;
    }

    public int getn(){
        return this.n;
    }

    public int[][] getState() {
        return this.state;
    }

    public double UCB1(int N){
        return this.wins / this.n + constant*Math.sqrt(Math.log(N)/ this.n);
    }
}