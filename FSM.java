import java.util.HashSet;
import java.util.Set;

public class FSM {
}
abstract class Elements{
    public abstract boolean isValid(String symbol);
}
class States extends Elements {
    private Set<String> states = new HashSet<>();
    private Set<String> finalStates = new HashSet<>();
    private String initialState;

    public Set<String> getStates() {
        return states;
    }

    public void setStates(Set<String> states) {
        this.states = states;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(Set<String> finalStates) {
        this.finalStates = finalStates;
    }

    public String getInitialState() {
        return initialState;
    }
    public States() {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.initialState = null;
    }

    @Override
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

    private boolean addState(String stateName) {
        if (!isValid(stateName)) {
            System.out.println("Warning: invalid state name: " + stateName + ", state couldn't add!");
            return false;
        }
        if (states.contains(stateName)) {
            System.out.println("Warning: " + stateName + " was already declared as a state.");
            return false;
        } else {
            states.add(stateName);
            return true;
        }
    }

    private void addFinalState(String finalState) {
        if (!isValid(finalState)) {
            System.out.println("Warning: invalid state name: " + finalState + ", state couldn't add!");
            return;
        }
        if (finalStates.contains(finalState)) {
            System.out.println("Warning: " + finalState + " was already declared as a state.");
        } else {
            finalStates.add(finalState);
        }
    }

    private void setInitialState(String state) {
        if (!isValid(state)) {
            System.out.println("Warning: invalid state name '" + state + "', ignored.");
            return;
        }
        if (!states.contains(state)) {
            System.out.println("Warning: " + state + " was not previously declared as a state.");
            states.add(state);
        }
        initialState = state;
    }
    private void printStates(){
        if(states.isEmpty()){
            System.out.println("No states declared!");
            return;
        }
        for(String state:states){
            String label=" ";
            if(state.equalsIgnoreCase(initialState)){
                label+="(initial)";
            }
            if(finalStates.contains(state)){
                if(!label.isEmpty()){
                    label+=" ";
                    label+="(final)";
                }
            }
            System.out.println(state+" "+ label);
        }
    }
    public void handleStates(String input) {
        if (input.isBlank()) {
            printStates();
            return;
        }

        String[] symbols = input.trim().split("\\s+");
        for (String symbol : symbols) {
            if (addState(symbol) && initialState == null) {
                initialState = symbol.toUpperCase();
            }
        }
    }
    public void handleInitialState(String input) {
        String symbol = input.trim();
        if (symbol.isEmpty()) {
            System.out.println("Warning: no initial state specified.");
            return;
        }

        setInitialState(symbol);
    }
    public void handleFinalStates(String input) {
        if (input.isBlank()) {
            System.out.println("Warning: no final states specified.");
            return;
        }

        String[] symbols = input.trim().split("\\s+");
        for (String symbol : symbols) {
            addFinalState(symbol);
        }
    }
}
