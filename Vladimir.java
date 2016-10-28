import java.util.*;

/**
 * A Java class for an agent to play in Resistance.
 * Each agent is given a single capital letter, which will be their name for the game.
 * The game actions will be encoded using strings.
 * The agent will be created entirely in a single game, and the agent must maintain its own state.
 * Methods will be used for informing agents of game events (get_ methods, must return in 100ms) or requiring actions (do_ methods, must return in 1000ms).
 * If actions do not meet the required specification, a nominated default action will be recorded.
 * @author Tim French
 * **/


public class Vladimir implements Agent{

  /*
    Class to describe other Players
  */
  private class Player implements Comparable<Player>{
    private Double suspicion;
    private int fails;
    private char name;
    @Override
      public int compareTo(Player otherPlayer) {
        return otherPlayer.getSuspicion().compareTo(this.suspicion);
      }

    public Double getSuspicion(){
      return this.suspicion;
    }
    public char getName(){
      return this.name;
    }
    public void setSuspicion(Double suspicion){
      this.suspicion = suspicion;
    }

    public Player(char name){
      this.name = name;
      suspicion = 0.0;
      fails = 0;
    }
  }

  /*
    Variables common to all from Tim's implementation
  */
  private String name;
  private String players;
  private boolean spy;
  private Random random;

  /*
    Global variables for Vladimir
  */
  private int size;
  private int mission;
  private int failures;

  // If a spy then these are used, as know the team makeup
  private String spies;
  private String resistance;

  // If resistance then used to store 100% confirmed spies
  private String known = "";

  private ArrayList<Player> suspects = new ArrayList<Player>();

  private String proposed, leader, latest, yays;
  private int traitors;
  private boolean present;

  public Vladimir(){
    random = new Random();
  }

  /**
   * Reports the current status, inlcuding players name, the name of all players, the names of the spies (if known), the mission number and the number of failed missions
   * @param name a string consisting of a single letter, the agent's names.
   * @param players a string consisting of one letter for everyone in the game.
   * @param spies a String consisting of the latter name of each spy, if the agent is a spy, or n questions marks where n is the number of spies allocated; this should be sufficient for the agent to determine if they are a spy or not.
   * @param mission the next mission to be launched
   * @param failures the number of failed missions
   * */
  public void get_status(String name, String players, String spies, int mission, int failures){

    this.mission = mission;
    this.failures = failures;

    if (mission == 1){
      // Tim's code
      this.name = name;
      this.players = players;
      spy = spies.indexOf(name)!=-1;

      this.size = players.length();
      this.spies = spies;

      // Add Players representations
      for (int i = 0; i < size; i++){
        Player p = new Player(players.charAt(i));
        // p.setSuspicion(random.nextInt(100));
        suspects.add(p);
      }

      Collections.sort(suspects, Collections.reverseOrder());

      for (Player s : suspects){
        // System.out.println("PLAYER : " + s.getName() + " SUSPICION IS : " + s.getSuspicion());
      }
    }

    if (spy && mission == 1){
        set_Resistance(players, spies);
        // System.out.println("I'M A SPY.");
    }
  }

  /*
    If Vladimir is a spy then he wants to know who resistance are
  */
  public void set_Resistance(String players, String spies){
    String temp = "";
    for (Character c : players.toCharArray()){
      if (spies.indexOf(c) == -1){
        temp += c;
      }
    }
    this.resistance = temp;
  }

  /**
   * Nominates a group of agents to go on a mission.
   * If the String does not correspond to a legitimate mission (<i>number</i> of distinct agents, in a String),
   * a default nomination of the first <i>number</i> agents (in alphabetical order) will be used, as if this was what the agent nominated.
   * @param number the number of agents to be sent on the mission
   * @return a String containing the names of all the agents in a mission
   * */
  public String do_Nominate(int number){
    // If the first mission then pick at random
    if (mission == 1){
      HashSet<Character> team = new HashSet<Character>();
      for(int i = 0; i<number-1; i++){
        char c = players.charAt(random.nextInt(players.length()));
        while(team.contains(c)) c = players.charAt(random.nextInt(players.length()));
        team.add(c);
      }
    }
    // Don't pick the same from the first team
    HashSet<Character> team = new HashSet<Character>();
    for(int i = 0; i<number-1; i++){
      char c = players.charAt(random.nextInt(players.length()));
      while(team.contains(c)) c = players.charAt(random.nextInt(players.length()));
      team.add(c);
    }
    team.add(name.charAt(0));
    String tm = "";
    for(Character c: team)tm+=c;
    // System.out.println("\n*****\nI " + name + " nominate : " + tm + "\n*****\n");
    return tm;
  }

  /**
   * Provides information of a given mission.
   * @param leader the leader who proposed the mission
   * @param mission a String containing the names of all the agents in the mission
   **/
  public void get_ProposedMission(String leader, String mission){
    // Re-evaluate suspects based on new information
    this.leader = leader;
    this.proposed = mission;
  }

  /**
   * Gets an agents vote on the last reported mission
   * @return true, if the agent votes for the mission, false, if they vote against it.
   * */
  public boolean do_Vote(){

    // Else if first mission then there is no knowledge so vote to confirm
    if (mission == 1 || failures == 4){
      return true;
    }
    // If spy then vote for if a spy is in the nomination
    else if (spy){
      for (int i = 0; i < proposed.length(); i++){
        if (spies.indexOf(proposed.charAt(i)) != -1){
          return true;
        }
      }
      // TODO: Add stealthy voting to not always reject the mission if spy not in it
      return false;
    }
    else {
      for (int i = 0; i < spies.length(); i++){
          Player p = suspects.get(i);
          // // System.out.println(p.getName() + " : " + p.getSuspicion());
          if (proposed.indexOf(p.getName()) != -1){
            return false;
          }
      }
      // Check if our highest suspects are in the team and vote if better than half? Needs refining of premise.
      return true;
    }

    // return (random.nextInt(2)!=0);
  }

  private void reevaluate(){

    /*
      THIS IS WHERE WE GONNA GET OUR DETECTIVE HAT ON
      OH SH*T! THE GAME IS AFOOT.

      First spread blame for votes among number on mission evenly,
      accounting for not blaming self if were present on mission
    */
    if (present){
      double increase = (double)traitors / (latest.length() - 1);
      // System.out.println("*****   " + name + "     *****");
      // System.out.println("TRAITORS: "+traitors+ " LENGTH: "+latest.length()+" INCREASE: "+increase);
      for (Player p : suspects){
        if (latest.indexOf(p.getName()) != -1){
          p.setSuspicion(p.getSuspicion() + increase);
        }
      }
    }
    else {
      double increase = (double)traitors / (latest.length());
      // System.out.println("*****   " + name + "     *****");
      // System.out.println("TRAITORS: "+traitors+ " LENGTH: "+latest.length()+" INCREASE: "+increase);
      for (Player p : suspects){
        if (latest.indexOf(p.getName()) != -1){
          p.setSuspicion(p.getSuspicion() + increase);
        }
      }
    }

    Collections.sort(suspects);
    for (int i = 0; i < 2; i++){
        Player p = suspects.get(i);
        // System.out.println(p.getName() + " : " + p.getSuspicion());
    }

  }

  /**
   * Reports the votes for the previous mission
   * @param yays the names of the agents who voted for the mission
   **/
  public void get_Votes(String yays){
    this.yays = yays;
  }

  /**
   * Reports the agents being sent on a mission.
   * Should be able to be infered from tell_ProposedMission and tell_Votes, but incldued for completeness.
   * @param mission the Agents being sent on a mission
   **/
  public void get_Mission(String mission){
    this.latest = mission;
    if (mission.indexOf(name) != -1){
      this.present = true;
    }
    else this.present = false;
  }

  /**
   * Agent chooses to betray or not.
   * @return true if agent betrays, false otherwise
   **/
  public boolean do_Betray(){
    if (mission == 1) return false;
    else return (spy ? true : false);
  }

  /**
   * Reports the number of people who betrayed the mission
   * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
   **/
  public void get_Traitors(int traitors){
    if (traitors != 0){
      this.traitors = traitors;
      if (!spy){
        reevaluate();
      }
    }
  }


  /**
   * Optional method to accuse other Agents of being spies.
   * Default action should return the empty String.
   * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
   * Of course convention can be ignored.
   * @return a string containing the name of each accused agent.
   * */
  public String do_Accuse(){
    /*
      If a spy, then Vladimir will nominate a random number of resistance
    */
    if (spy){
      int number = random.nextInt(resistance.length());
      HashSet<Character> team = new HashSet<Character>();
      for(int i = 0; i<number; i++){
        char c = resistance.charAt(random.nextInt(resistance.length()));
        while(team.contains(c)) c = resistance.charAt(random.nextInt(resistance.length()));
        team.add(c);
      }
      String tm = "";
      for(Character c: team)tm+=c;
      return tm;
    }
    // If resistance then Vladimir will only notify of confirmed spies
    else {
      return "Z";
      // return known;
    }
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

}
