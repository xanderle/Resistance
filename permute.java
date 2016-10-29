public class permute{
  public static void main(String[] args){
    bounder b = new bounder();
    b.get_status("E","ABCDE","????",1,0);
    b.get_Mission("BC");
    b.get_Traitors(1);
    b.get_ProposedMission("A","BC");
    System.out.println(b.do_Vote());
  }
}
