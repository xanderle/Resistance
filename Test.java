import java.util.*;
import java.io.*;
/**
* A Class to test the Game state and AI's
* @author Jake Nelson
* */

public class Test{

  private static int n = 1;
  private static int bots = 1;
  private static String[] vars;
  private static ArrayList<Player> players = new ArrayList<Player>();

  private static class Player{
    public String bot;
    public String name;
    public int gwins;
    public int government;
    public int rwins;
    public int resistance;

    public Player(String bot, char name){
      this.gwins = 0;
      this.government = 0;
      this.rwins = 0;
      this.resistance = 0;
      this.name = String.valueOf(name);
      this.bot = bot;
    }
  }

  private static class RunGame{
    public boolean government;

    public RunGame(){
      Logger g = new Logger();
      for (int i = 0; i < bots; i++){
        try {
          Object agent = Class.forName(vars[i + 1]).newInstance();
          g.addPlayer((Agent) agent);
        }
        catch (Exception e){
          System.out.println(e.getMessage());
        }
      }
      g.setup();
      g.play();
      government = g.government;
      String spies = g.spyString;
      for (Player p : players){
          if (spies.indexOf(p.name) != -1){
            p.government++;
            if (government){
              p.gwins++;
            }
          }
          else {
            p.resistance++;
            if (!government){
              p.rwins++;
            }
          }
      }

    }
  }

  public static void main(String[] args){
    vars = args;
    if (args.length > 0){
      n = Integer.parseInt(args[0]);
      bots = args.length - 1;
    }
    int count = 0;
    for (int i = 0; i < bots; i++){
      Player p = new Player(vars[i + 1], (char)(65+i));
      players.add(p);
    }
    for (int i = 0; i < n; i++){
      RunGame rg = new RunGame();
      if (rg.government){
        count++;
      }
    }
    System.out.println("RESULTS:\nGovernment: " + (double)count/n * 100 + "%\nResistance: " + (double)(n-count)/n * 100 + "%\n\nPLAYERS:\n");
    for (Player p : players){
      System.out.println(p.bot + " was " + p.name + "\nResistance: " + p.resistance + " games with " + (double)p.rwins/p.resistance*100 + "% win rate\nGovernment: "+ p.government + " games with " + (double)p.gwins/p.government*100 + "%win rate\n");
    }
  }

}
