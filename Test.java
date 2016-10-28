import java.util.*;
import java.io.*;
/**
 * A Class to test the Game state and AI's
 * @author Jake Nelson
 * */

public class Test{

  public static void RandomGame(){
    Game g = new Game();
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.setup();
    g.play();
  }

  public static void main(String[] args){
    if (args.length > 0){
      System.out.println("Has args");
    }
    else {
        RandomGame();
    }
  }

}
