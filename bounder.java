import java.util.*;

public class bounder{
  //Internal Variables
  boolean init = true;
  String name;
  ArrayList<String> players;//list of all players not including self
  String spies;
  // The teams we don't suspect to be spies without guarantees
  ArrayList<Boolean[]> optimistic = new ArrayList<Boolean[]>();
  //All teams except those 100% proven to be spies
  ArrayList<Boolean[]> pessimistic = new ArrayList<Boolean[]>();
  //Intialise
  ArrayList<String> members;//members on the current mission
  int traitors; //people that betrayed
  //map people to index;
  String curLeader;
  String curMission;
  Random randomGenerator = new Random();
  public Boolean[] selectBase(int size){
    switch(size){
      case 9:return new Boolean[] {true,true,true,true,false,false,false,false,false};
      case 8: return new Boolean[] {true,true,true,false,false,false,false,false};
      case 7: return new Boolean[] {true,true,true,false,false,false,false};
      case 6: return new Boolean[] {true,true,true,false,false,false};
      case 5: return new Boolean[] {true,true,false,false,false};
      case 4: return new Boolean[] {true,true,false,false};
      default: return null;
    }
  }

  public void init(){
    Permutations<Boolean> perm = new Permutations<Boolean>( selectBase(players.size()));
    int j =0;
    while(perm.hasNext()){
      Boolean[] bool = new Boolean[players.size()];
      Boolean[] temp = perm.next();
      for (int i = 0; i < players.size(); i++){
        bool[i] = temp[i];
      }
      optimistic.add(bool);
      System.out.println(Arrays.toString(optimistic.get(j)));
      j++;
    }
    pessimistic.addAll(optimistic);
    System.out.println("------------------------");
  }

  public ArrayList<Integer> onMission(){
    int size = players.size();
    ArrayList<Integer> onMission = new ArrayList<Integer>();
    for(int i =0; i < size;i++){

      int membersSize = members.size();
      for(int j = 0;j< membersSize;j++){
        if(players.get(i).equals(members.get(j))){
          onMission.add(i);
        }
      }
    }
    return onMission;
  }

  public void removeOptimistic(){
    System.out.println("Removing from Optimistic");
    System.out.println("------------------------");
    ArrayList<Integer> onMission = onMission();
    for(int i =0; i<optimistic.size();i++){
      int noSpies =0;
      for(int j =0; j < onMission.size();j++){
        if(optimistic.get(i)[onMission.get(j)]){
          noSpies++;
        }
      }
      if(noSpies != traitors){
        System.out.println("Number of Spies " + noSpies);
        System.out.println("Number of Sabotages " + traitors);
        System.out.println("Configuration remove: "+Arrays.toString(optimistic.get(i)));
        optimistic.remove(i);
        i--;
        System.out.println("------------------------");
      }

    }
  }

  public void removePessimistic(){
    System.out.println("Removing from Pessimistic");
    System.out.println("------------------------");
    ArrayList<Integer> onMission = onMission();
    for(int i =0; i < pessimistic.size();i++){
      int noSpies =0;
      for(int j =0; j < onMission.size();j++){
        if(pessimistic.get(i)[onMission.get(j)]){
          noSpies++;
        }
      }
      if(noSpies < traitors){
        System.out.println("Number of Spies " + noSpies);
        System.out.println("Number of Sabotages " + traitors);
        System.out.println("Configuration remove: "+Arrays.toString(pessimistic.get(i)));
        pessimistic.remove(i);
        System.out.println("------------------------");
        i--;

      }

    }
  }
  public void get_status(String name, String players, String spies, int mission, int failures){
    if(init){
      this.name = name;
      this.players = new ArrayList<String>(Arrays.asList(players.replaceAll(name,"").split("")));
      System.out.println(this.players.toString());
      this.spies = spies;
      init();
    }
  }

  public String do_Nominate(int number){
    int randomInt =0;
    String players = name;
    if(optimistic.size()!=0){
      System.out.println("Nominating from Optimistic");
      randomInt = randomGenerator.nextInt(optimistic.size());
      Boolean[] bool = optimistic.get(randomInt);
      int count = 1;
      for(int i =0; i < bool.length;i++){
        if(!bool[i] && count < number){
          players = players + this.players.get(i);
          count++;
        }
      }
    }
    else{
      System.out.println("Nominating from Pessimistic");
      randomInt = randomGenerator.nextInt(pessimistic.size());
      Boolean[] bool = pessimistic.get(randomInt);
      int count = 1;
      for(int i =0; i < bool.length;i++){
        if(!bool[i] && count < number){
          players = players + this.players.get(i);
          count++;
        }
      }
    }
    System.out.println(players);
    return "";

  }
  public void get_Mission(String mission){
    members = new ArrayList<String>(Arrays.asList(mission.split("")));
  }
  public void get_Traitors(int traitors){
    this.traitors = traitors;
  }
  public boolean do_Betray(){
    return true;
  }
  public void get_ProposedMission(String leader, String mission){
    curLeader = leader;
    curMission = mission;
  }
  public boolean do_Vote(){
    ArrayList<String> team = new ArrayList<String>();
    for(int i =0 ;i < optimistic.size();i++){
      Boolean[] bool = optimistic.get(i);

    }
  }
}
