import java.util.*;
import java.io.*;
/**
* A Class to test the Game state and AI's
* @author Jake Nelson
* */

public class Solo{

  private static int n = 1;

  public static class RunGame{
    public boolean government;

    public RunGame(int players, String bot){
      Logger g = new Logger();
      for (int i = 0; i < players; i++){
        try {
          Object agent = Class.forName(bot).newInstance();
          g.addPlayer((Agent) agent);
        }
        catch (Exception e){
          System.out.println(e.getMessage());
        }
      }
      g.setup();
      g.play();
      government = g.government;
    }
  }

  public static void main(String[] args){
    if (args.length > 0){
      n = Integer.parseInt(args[0]);
    }
    int count = 0;
    for (int i = 0; i < n; i++){
      RunGame rg = new RunGame(Integer.parseInt(args[1]), args[2]);
      if (rg.government){
        count++;
      }
    }
    System.out.format("Government: %6.2f %%\nResistance: %6.2f %%\n", (double)count/n * 100, (double)(n-count)/n * 100 );
  }

}
