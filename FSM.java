java.io.FileWriter;

public class FSM implements Methods{
  private Symbols symbols;//or protected?
  private States states;
  private Transitions transitions;
  private boolean logged;
  

  public FSM(){
    this.symbols=new Symbols();
    this.states=new States();
    this.transitions=new Transitions();
  }

  @Override
  public void exit(){
    System.out.println("TERMINATED BY USER");
    logged=false;
    break;
  }

  @Override
  public void log(String filename){
    FileWriter writer=new FileWriter();
    //writer.write();
    try{
      if(filename==null){//logged or filename first?
        if(logged=true){
          System.out.println("STOPPED LOGGING");
          logged=false;
          exit();
        }else{
          System.out.pritnln("LOGGING was not enabled");
        }
      }else{
        if(logged=true){
          exit();
        }

      try{
        writer = new FileWriter(filename);
        logged = true;
      } catch (Exception e){
        logged=false;
        System.out.println("Error: File cannot be created or written.");
      }
      }
    }catch (Exception e){
      System.out.println("Error: File cannot be close.");
    }
      




}
