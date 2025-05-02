import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Transitions extends Elements implements CLEAR {
    private Map<String, Map<String, String>> transitions;
    private States states;
    private Symbols symbols;

    public Transitions(States states, Symbols symbols) {
        this.transitions = new HashMap<>();
        this.states = states;
        this.symbols = symbols;
    }

    @Override
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

    @Override
    public void clear(){
        transitions.clear();
    }

    private void addTransition(String fromState, String symbol, String toState) {
     
        if (!states.isValid(fromState) || !symbols.isValid(symbol) || !states.isValid(toState)) {
            System.out.println("Warning: Invalid transition input, ignored.");
            return;
        }

   
        if (!states.getStates().contains(fromState)) {
            System.out.println("Warning: fromState '" + fromState + "' is not declared as a state.");
            return;
        }
        if (!symbols.getSymbols().contains(symbol)) {
            System.out.println("Warning: symbol '" + symbol + "' is not declared.");
            return;
        }
        if (!states.getStates().contains(toState)) {
            System.out.println("Warning: toState '" + toState + "' is not declared as a state.");
            return;
        }


        transitions.putIfAbsent(fromState, new HashMap<>());
        Map<String, String> symbolToState = transitions.get(fromState);

        if (symbolToState.containsKey(symbol)) {
            System.out.println("Warning: Transition already exists from '" + fromState + "' with symbol '" + symbol + "'.");
            return;
        }

        symbolToState.put(symbol, toState);
    }

    public void handleTransitions(String input) {
        if (input.isBlank()) {
            printTransitions();
            return;
        }

        String[] lines = input.trim().split("\\s*;\\s*"); 

        for (String line : lines) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 3) {
                System.out.println("Warning: Invalid transition format, should be 'fromState symbol toState'. Ignored: " + line);
                continue;
            }
            String fromState = parts[0];
            String symbol = parts[1];
            String toState = parts[2];
            addTransition(fromState, symbol, toState);
        }
    }

    private void printTransitions() {
        if (transitions.isEmpty()) {
            System.out.println("No transitions declared!");
            return;
        }
        System.out.println("Declared transitions:");
        for (String fromState : transitions.keySet()) {
            Map<String, String> symbolMap = transitions.get(fromState);
            for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
                System.out.println(fromState + " -" + entry.getKey() + "-> " + entry.getValue());
            }
        }
    }
}
