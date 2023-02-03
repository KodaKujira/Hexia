import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int [][] state = new int[11][11];
        Mcts_IA playerBlue = new Mcts_IA("matthieu", 1);
        Mcts_IA playerRed = new Mcts_IA("JMM", 2);
        Move move = null;
        int count = 0;
        while(!testVictory(state, 1) && !testVictory(state, 2)){
            Scanner clavier = new Scanner(System.in);
            System.out.println("Entrez x (entre 1 et 11)");
            int x = clavier.nextInt() - 1;
            System.out.println("Entrez y (entre 1 et 11)");
            int y = clavier.nextInt() - 1;
            state[x][y] = 1;
            move = new Move(x,y);
            state[move.x][move.y] = 1;
            /*move = playerBlue.searchMove(move);
            state[move.x][move.y] = 1;
            System.out.println(move);*/
            move = playerRed.searchMove(move);
            state[move.x][move.y] = 2;
            System.out.println(move);
            count ++;
            System.out.println(count);
        }
        if(testVictory(state,1)){
            System.out.println(playerBlue.getName());
        }else{
            System.out.println(playerRed.getName());
        }
    }
    public static boolean testVictory(int[][] state, int player){
        boolean [][] tested = new boolean[11][11];
        for(int i = 0; i < 11; i++){
            if (state[0][i] == 1 && !tested[0][i]){
                int[] cellTest = {0,i};
                if (testPath(cellTest, tested, state, player)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean testPath(int[] cell, boolean[][] tested, int[][] state, int player){
        tested[cell[0]][cell[1]] = true;
        int[][] neighboursCoord = {{cell[0] - 1, cell[1]}, {cell[0], cell[1] - 1}, {cell[0] + 1, cell[1] - 1}, {cell[0] + 1, cell[1]}, {cell[0], cell[1] + 1}, {cell[0] - 1, cell[1] + 1}};
        for(int [] neighbour : neighboursCoord){
            if (validCell(neighbour)){
                int x = neighbour[0];
                int y = neighbour[1];
                if (state[x][y] == 2 && player == 2 && !tested[x][y]){
                    if(y == 11 - 1){
                        return true;
                    }else if(testPath(neighbour, tested, state, player)){
                        return true;
                    }
                } else if (state[x][y] == 1 && player == 1 && !tested[x][y]) {
                    if(x == 11 -1){
                        return true;
                    } else if (testPath(neighbour,tested,state,player)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public static boolean validCell(int [] cell){
        return cell[1] >= 0 && cell[1] < 11 && cell[0] >= 0 && cell[0] < 11;
    }
}
