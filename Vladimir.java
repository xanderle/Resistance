import java.util.*;

/**
 * A Java class for an agent to play in Resistance.
 * @author Jake Nelson
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
    public int getFails(){
      return fails;
    }
    public void setFails(int fails){
      this.fails = fails;
    }

    public Player(char name){
      this.name = name;
      suspicion = 0.0;
      fails = 0;
    }
  }

  /*
    GAME VARIABLES
  */
  private int[] spyNum = {2,2,3,3,3,4};
  private int[][] missionNum = {{2,3,2,3,3},{2,3,4,3,4},{2,3,3,4,4},{3,4,4,5,5},{3,4,4,5,5},{3,4,4,5,5}};
  // private int[] threshold = {3, 4, 4, 5, 10 , 10};

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
  private boolean certain;

  // Suspicion measures
  double FAILED = 1.0;
  double VOTED_FAILED = 0.5;
  double SUSPECT_VOTE = 0.5;

  public Vladimir(){
    random = new Random();
    this.certain = false;
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
        if (players.charAt(i) != name.charAt(0)){
            Player p = new Player(players.charAt(i));
            suspects.add(p);
        }
      }
      Collections.sort(suspects, Collections.reverseOrder());
      for (Player s : suspects){
      }
    }

    if (spy && mission == 1){
        set_Resistance(players, spies);
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
    // NOTE: Is this the best way of nominating first round?
    if (mission == 1){
      HashSet<Character> team = new HashSet<Character>();
      for(int i = 0; i<number-1; i++){
        char c = players.charAt(random.nextInt(players.length()));
        while(team.contains(c)) c = players.charAt(random.nextInt(players.length()));
        team.add(c);
      }
    }
    /*
      If spy or resistance then benefit from appearing to nominate spyless teams
    */
    Collections.sort(suspects, Collections.reverseOrder());
    String team = "";
    for(int i = 0; i<number-1; i++){
      Player p = suspects.get(i);
      team += p.getName();
    }
    // Always add self to team nominated
    team += name.charAt(0);
    return team;
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
    if (mission == 1){
      return true;
    }
    // If spy then vote for if a spy is in the nomination
    else if (spy){
      int count = 0;
      for (int i = 0; i < proposed.length(); i++){
        if (spies.indexOf(proposed.charAt(i)) != -1){
          count++;
        }
      }
      if (count == 1 || (mission == 7 && count == 2) ){
        return true;
      }
      // TODO: Add stealthy voting to not always reject the mission if spy not in it
      return false;
    }
    else {
      if (proposed.length() > spies.length()){
        if (proposed.indexOf(name) == -1){
          return false;
        }
      }
      Collections.sort(suspects);
      for (int i = 0; i < spies.length(); i++){
          Player p = suspects.get(i);
          // // System.out.println(p.getName() + " : " + p.getSuspicion());
          if (proposed.indexOf(p.getName()) != -1){
            return false;
          }
      }
      return true;
    }
  }

  private void reevaluate(){

    /*
      If there are 0 traitors, then look at those who voted against the mission
    */

    if (traitors == 0){
      if (players.length() != yays.length()){
        double increase = 1 / (players.length() - yays.length());
        for (Player p : suspects){
          if (yays.indexOf(p.getName()) == -1){
            p.setSuspicion(p.getSuspicion() + SUSPECT_VOTE * increase);
          }
        }
      }

    }

    /*
      THIS IS WHERE WE GONNA GET OUR DETECTIVE HAT ON
      OH SH*T! THE GAME IS AFOOT.

      First spread blame for votes among number on mission evenly,
      accounting for not blaming self if were present on mission
    */

    //  Look at traitors and add a percentage
    if (present){
      double increase = (double)traitors / (latest.length() - 1);
      /*
      If the number of traitors is equal to the number of people on a mission
      then the spies must be known
      */
      if (traitors == (latest.length() - 1)){
        for (Player p : suspects){
          if (latest.indexOf(p.getName()) != -1){
            p.setSuspicion(1000.0);
            certain = true;
          }
          else {
            p.setSuspicion(0.0);
          }
        }
      }
      else {

        for (Player p : suspects){
          if (latest.indexOf(p.getName()) != -1){
            p.setFails(p.getFails() + 1);
            p.setSuspicion(p.getSuspicion() + FAILED * p.getFails() * increase);
          }
          if (yays.indexOf(p.getName()) != -1){
            p.setSuspicion(p.getSuspicion() + VOTED_FAILED * 1/yays.length());
          }
        }
      }
    }
    else {
      double increase = (double)traitors / (latest.length());
      for (Player p : suspects){
        if (latest.indexOf(p.getName()) != -1){
          p.setSuspicion(p.getSuspicion() + FAILED * increase);
        }
        if (yays.indexOf(p.getName()) != -1){
          p.setSuspicion(p.getSuspicion() + VOTED_FAILED * 1/yays.length());
        }
      }
    }

    /*
      Look at votes for the failed mission and assign suspicion
    */

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
    this.traitors = traitors;
    if (!spy && !certain){
      reevaluate();
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
      return "";
    }
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

}
