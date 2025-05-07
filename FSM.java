import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

java.io.FileWriter;

public class FSM implements Methods{
  private Symbols symbols;
  private States states;
  private Transitions transitions;
  private boolean logged;
  private FileWriter logWriter;
  private String initialState; 
  private Set<String> finalStates; 

  public FSM(){
    this.symbols=new Symbols();
    this.states=new States();
    this.transitions=new Transitions(states, symbols);
    this.finalStates=new HashSet<>();
    this.initialState=null;
  }

  public Symbols getSymbols() {
        return symbols;
    }

    public void setSymbols(Symbols symbols) {
        this.symbols = symbols;
    }

    public States getStates() {
        return states;
    }

    public void setStates(States states) {
        this.states = states;
    }

    public Transitions getTransitions() {
        return transitions;
    }

    public void setTransitions(Transitions transitions) {
        this.transitions = transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(Set<String> finalStates) {
        this.finalStates = finalStates;
    }

  
  @Override
  public void exit(){
    System.out.println("TERMINATED BY USER");
    logged=false;
    System.exit(0);
    //for errors System.exit(1); maybe
  }

 public void writeLog(String text) {
        if (logged && logWriter != null) {
            try {
                logWriter.write(text + System.lineSeparator());
                logWriter.flush();
            } catch (IOException e) {
                System.out.println("Error: Cannot write log: " + e.getMessage());
            }
        }
    }


    @Override
    public void log(String input) {
        if (input.isBlank()) {
            if (logged) {
                try {
                    logWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                logWriter = null;
                logged = false;
                System.out.println("STOPPED LOGGING");
            } else {
                System.out.println("LOGGING was not enabled");
            }
        } else {
            String filename = input.trim();

            if (logged) {
                try {
                    logWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                logWriter = new FileWriter(filename, false);
                logged = true;
                logWriter.flush();
            } catch (IOException e) {
                logged = false;
                logWriter = null;
                System.out.println("Error: Cannot write to file '" + filename + "'");
            }
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
