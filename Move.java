public class Move {
    public int x;
    public int y;

    public Move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "("+(x+1)+","+(y+1)+")";
    }
}
