public class FSMExecutor {
    public static void execute(FSM fsm, List<String> inputs) {
        String currentState = fsm.getInitialState();
        System.out.println("Initial State: " + currentState);
        for (String input : inputs) {
            String nextState = fsm.getNextState(currentState, input);
            if (nextState == null) {
                System.out.println("No transition from " + currentState + " with input " + input);
                return;
            }
            System.out.println("Input: " + input + " → State: " + nextState);
            currentState = nextState;
        }
        System.out.println("Final State: " + currentState);
    }
}
