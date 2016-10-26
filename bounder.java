import java.util.*;

public class bounder{
  //Internal Variables
  boolean init = true;
  String name;
  String players;
  String spies;
  ArrayList<Boolean[]> configurations = new ArrayList<Boolean[]>();


  // The teams we don't suspect to be spies without guarantees
  ArrayList<Boolean[]> optimistic;
  //All teams except those 100% proven to be spies
  ArrayList<Boolean[]> pessimistic;
  //Intialise

  public Boolean[] selectBase(int size){
    switch(size){
      case 10:return new Boolean[] {true,true,true,true,false,false,false,false,false};
      case 9: return new Boolean[] {true,true,true,false,false,false,false,false};
      case 8: return new Boolean[] {true,true,true,false,false,false,false};
      case 7: return new Boolean[] {true,true,true,false,false,false};
      case 6: return new Boolean[] {true,true,false,false,false};
      case 5: return new Boolean[] {true,true,false,false};
      default: return null;
    }
  }
  public void init(){
    Permutations<Boolean> perm = new Permutations<Boolean>( selectBase(players.length()));
    while(perm.hasNext()){
      Boolean[] bool = new Boolean[players.length()-1];
      Boolean[] temp = perm.next();
      /*
      Bool was getting set to the iterable value so a memory address,
      which was having it's value updated each call. By instead copying each
      value to a new array we can avoid this. Appears to work.
      Can we do better than the below code for copying though?
      */
      for (int i = 0; i < players.length()-1; i++){
        bool[i] = temp[i];
      }
      System.out.println(Arrays.toString(bool));
      configurations.add(bool);
    }
  }

  public void get_status(String name, String players, String spies, int mission, int failures){
    long startTime = System.nanoTime();
    this.name = name;
    this.players = players;
    this.spies = spies;
    if(init){
      init();
    }
    for(int i =0;i<configurations.size();i++){
      System.out.println(Arrays.toString(configurations.get(i)));
    }
    long endTime = System.nanoTime();
    System.out.println(endTime-startTime);
  }
  public String do_Nominate(int number){
    return "";

  }
  public void get_Mission(String mission){

  }
}
