public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static FSM fsm=new FSM();
    private static States states=new States();
    private static Symbols symbols=new Symbols();
    private static Transitions transitions=new Transitions(states, symbols);
    private static CommandParser c=new CommandParser();
    private static Terminal terminal = new Terminal(fsm, c);


    public static void main(String[] args) {
        String version = "FSM DESIGNER v2.3";

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm", Locale.ENGLISH);

        String formattedDateTime = now.format(formatter);

        String output = version + " " + formattedDateTime;

        System.out.println(output);

        if (args.length == 0) {
            System.out.println("Error: No file name provided.");
            System.out.println("Usage: java Main <filename>");
            terminal.startREPL();
        } else {
            String file = args[0];
            try {
                terminal.processFile(new File(file));
                System.out.println("FSM built from script: " + file);
                terminal.startREPL();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        String file = args[0];
        try{
            fsm.loadFromScript(file);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        try(BufferedReader reader=new BufferedReader(new FileReader(file))) {
            while (true) {
                System.out.print("?"); // başa ? koyar

                String input = scanner.nextLine().trim();

                fsm.writeLog(">> " + input);


                if (input.isBlank()) {
                    continue;
                }

                try {
                    c.parseAndExecute(input,fsm);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    fsm.writeLog("Error: " + e.getMessage());
                }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("Exiting FSM Designer...");
    }

    private static void handleSymbols(String input) {

        try {
            System.out.println("Handling SYMBOLS: " +"\n");
            symbols.handleSymbols(input);
        } catch (ExistingSymbolException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleInitialState(String input) {
        try {
            System.out.println("Handling INITIAL-STATE: " + "\n");
            states.handleInitialState(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleFinalStates(String input) {
        try {
            System.out.println("Handling FINAL-STATES: " + "\n");
            states.handleFinalStates(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleStates(String input) {
        try {
            System.out.println("Handling STATES: " + "\n");
            states.handleStates(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleTransitions(String input) {
        try {
            System.out.println("Handling TRANSITIONS: " + "\n");
            transitions.handleTransitions(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handlePrint() {
        try {
            System.out.println("Printing FSM state...");
            fsm.printFile("output.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
