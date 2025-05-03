import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

java.io.FileWriter;

public class FSM implements Methods{
  private Symbols symbols;
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
    System.exit(0);
    //for errors System.exit(1); maybe
  }

  @Override
  public void log(String filename){
    FileWriter writer=new FileWriter();
  
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
  @Override
  public void printFile(String filename) {
      try (FileWriter writer = new FileWriter(filename)) {
          writer.write("SYMBOLS: " + symbols + "\n");
          writer.write("STATES: " + states + "\n");
          writer.write("INITIAL STATE: " + initialState + "\n");
          writer.write("FINAL STATES: " + finalStates + "\n");
          writer.write("TRANSITIONS:\n");

          for (Map.Entry<String, Map<String, String>> fromState : transitions.entrySet()) {
              for (Map.Entry<String, String> symbolTo : fromState.getValue().entrySet()) {
                  String line = "(" + fromState.getKey() + ", " + symbolTo.getKey() + ") -> " + symbolTo.getValue();
                  writer.write(line + "\n");
              }
          }

          System.out.println("FSM printed to file: " + filename);
      } catch (IOException e) {
          System.out.println("Error writing to file: " + e.getMessage());
      }
  }
  @Override
  public void clear() {
      symbols.clear();
      states.clear();
      finalStates.clear();
      transitions.clear();
      initialState = null;
      System.out.println("FSM cleared.");
  }

}
