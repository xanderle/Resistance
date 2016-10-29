import java.util.*;
import java.io.*;
/**
* A Class to test the Game state and AI's
* @author Jake Nelson
* */

public class Solo{

  private static int n = 1;

  public static boolean RunGame(){
    Logger g = new Logger();
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.addPlayer(new Vladimir());
    g.setup();
    g.play();
    return g.government;
  }

  public static void main(String[] args){
    if (args.length > 0){
      n = Integer.parseInt(args[0]);
    }
    int count = 0;
    for (int i = 0; i < n; i++){
      if (RunGame()){
        count++;
      }
    }
    System.out.println("Government: " + (double)count/n * 100 + "%\nResistance: " + (double)(n-count)/n * 100 + "%");
  }

}
