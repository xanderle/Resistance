import java.util.*;
import java.lang.reflect.Array;
import java.util.*;
@SuppressWarnings({"unchecked", "deprecation"})
class Permutations<E> implements  Iterator<E[]>{

    private E[] arr;
    private int[] ind;
    private boolean has_next;
    public E[] output;//next() returns this array, make it public

    Permutations(E[] arr){
        this.arr = arr.clone();
        ind = new int[arr.length];
        //convert an array of any elements into array of integers - first occurrence is used to enumerate
        Map<E, Integer> hm = new HashMap<E, Integer>();
        for(int i = 0; i < arr.length; i++){
            Integer n = hm.get(arr[i]);
            if (n == null){
                hm.put(arr[i], i);
                n = i;
            }
            ind[i] = n.intValue();
        }
        Arrays.sort(ind);//start with ascending sequence of integers


        //output = new E[arr.length]; <-- cannot do in Java with generics, so use reflection
        output = (E[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
        has_next = true;
    }

    public boolean hasNext() {
        return has_next;
    }

    /**
     * Computes next permutations. Same array instance is returned every time!
     * @return
     */
    public E[] next() {
        if (!has_next)
            throw new NoSuchElementException();

        for(int i = 0; i < ind.length; i++){
            output[i] = arr[ind[i]];
        }


        //get next permutation
        has_next = false;
        for(int tail = ind.length - 1;tail > 0;tail--){
            if (ind[tail - 1] < ind[tail]){//still increasing

                //find last element which does not exceed ind[tail-1]
                int s = ind.length - 1;
                while(ind[tail-1] >= ind[s])
                    s--;

                swap(ind, tail-1, s);

                //reverse order of elements in the tail
                for(int i = tail, j = ind.length - 1; i < j; i++, j--){
                    swap(ind, i, j);
                }
                has_next = true;
                break;
            }

        }
        return output;
    }

    private void swap(int[] arr, int i, int j){
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public void remove() {

    }
}

public class Bounder implements Agent{
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
  ArrayList<Integer> propMembers;
  String yays;
  Boolean spy;
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

  public void get_status(String name, String players, String spies, int mission, int failures){
    if(init){
      // System.out.println("Spies are " + spies);
      this.name = name;
      this.players = new ArrayList<String>(Arrays.asList(players.replaceAll(name,"").split("")));
      // System.out.println(name);
      // System.out.println(this.players.toString());
      this.spies = spies;
      if(spies.indexOf(name)!=-1){spy = true;}
      else{spy = false;};
      // System.out.println(spy);
      init();
      init = false;
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
      // System.out.println(Arrays.toString(optimistic.get(j)));
      j++;
    }
    pessimistic.addAll(optimistic);
    // System.out.println("------------------------");
  }

  public ArrayList<Integer> onMission(){
    int size = players.size();
    ArrayList<Integer> onMission = new ArrayList<Integer>();
    for(int i =0; i < size;i++){
      if(members == null){
        break;
      }
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
    // System.out.println("Removing from Optimistic");
    // System.out.println("------------------------");
    ArrayList<Integer> onMission = onMission();
    for(int i =0; i<optimistic.size();i++){
      int noSpies =0;
      for(int j =0; j < onMission.size();j++){
        if(optimistic.get(i)[onMission.get(j)]){
          noSpies++;
        }
      }
      if(noSpies != traitors){
        // System.out.println("Number of Spies " + noSpies);
        // System.out.println("Number of Sabotages " + traitors);
        // System.out.println("Configuration remove: "+Arrays.toString(optimistic.get(i)));
        optimistic.remove(i);
        i--;
        // System.out.println("------------------------");
      }

    }
  }
  //TODO: Check Pessimistic Removal
  public void removePessimistic(){
    // System.out.println("Removing from Pessimistic");
    // System.out.println("------------------------");
    ArrayList<Integer> onMission = onMission();
    for(int i =0; i < pessimistic.size();i++){
      int noSpies =0;
      for(int j =0; j < onMission.size();j++){
        if(pessimistic.get(i)[onMission.get(j)]){
          noSpies++;
        }
      }
      if(noSpies < traitors){
        // System.out.println("Number of Spies " + noSpies);
        // System.out.println("Number of Sabotages " + traitors);
        // System.out.println("Configuration remove: "+Arrays.toString(pessimistic.get(i)));
        pessimistic.remove(i);
        // System.out.println("------------------------");
        i--;

      }

    }
  }


  public String do_Nominate(int number){
    removeOptimistic();
    removePessimistic();
    int randomInt =0;
    String players = name;
    if(!optimistic.isEmpty()){
      // System.out.println("Nominating from Optimistic");
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

    else if (!pessimistic.isEmpty() && optimistic.isEmpty()){
      // System.out.println("Nominating from Pessimistic");
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
    else{
    //TODO: Case if pessimistic runs out
    for(int i =0; i < number-1;i++){
      players = players + this.players.get(i);

    }
  }
    // System.out.println(players);
    return players;

  }
  public void get_Mission(String mission){
    // System.out.println("Mission is " + mission);
    members = new ArrayList<String>(Arrays.asList(mission.split("")));
  }
  public void get_Traitors(int traitors){
    // System.out.println("Traitors "+traitors);
    this.traitors = traitors;
  }
  public boolean do_Betray(){
    if(spy){
      // System.out.println("Betraying!");
      return true;
    }
    else{
      // System.out.println("Not Betraying");
      return false;
    }
  }
  public void get_ProposedMission(String leader, String mission){
    // System.out.println("Proposed Mission " +mission);
    ArrayList<String> temp = new ArrayList<String>(Arrays.asList(mission.split("")));
    propMembers = new ArrayList<Integer>();
    for(int i =0; i < players.size();i++){
      for(int j = 0; j< temp.size();j++){
        if(players.get(i).equals(temp.get(j))){
          propMembers.add(i);
        }
      }
    }
  }

  public boolean do_Vote(){
    // System.out.println("Voting");
    removeOptimistic();
    removePessimistic();
    for(int i = 0; i < optimistic.size();i++){
      int count = 0;
      Boolean[] bool = optimistic.get(i);
      for(int j =0; j < propMembers.size();j++){
        if(bool[propMembers.get(j)]){//is a spy
          break;
        }
        count++;
      }

      if(count == propMembers.size()){
        // System.out.println("Voting Pass");
        return true;
      }
    }
    // System.out.println("Voting Fail");
    return false;
  }
  public void get_Accusation(String accuser, String accused){

  }
  public void get_Votes(String yays){
    this.yays = yays;
  }
  public String do_Accuse(){
    return "";
  }
}
