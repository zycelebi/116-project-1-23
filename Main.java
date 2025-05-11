import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.time.LocalDateTime;
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
            fsm.writeLog(e.getMessage());

        }

        System.out.println("Exiting FSM Designer...");
        fsm.writeLog("Exiting FSM Designer...");

    }
}
interface Clear{
    void clear();
}
class CommandParser {
    private static boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        return filename.matches("[a-zA-Z0-9_\\-\\.]+") && (filename.endsWith(".txt") || filename.endsWith(".fs"));
    }

    public static void parseAndExecute(String command, FSM fsm) {
        if (command == null || command.trim().isEmpty()) return;

        command = command.trim();
        if (!command.contains(";")) {
            String warningMsg = "Warning: command must end with ';'";
            System.out.println(warningMsg);
            fsm.writeLog(warningMsg + "\n");
            return;
        }

        int semicolonIndex = command.indexOf(';');
        String comment = command.substring(semicolonIndex + 1).trim();
        command = command.substring(0, semicolonIndex).trim();
        try {
            fsm.writeLog("? " + command + ";" + comment);

            // StringBuilder to collect system responses
            StringBuilder responseLog = new StringBuilder();

            // Redirect System.out to capture output while printing to terminal
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream tempOut = new TerminalPrintStream(bytes, originalOut);
            System.setOut(tempOut);

            try {
                if (command.toUpperCase().startsWith("SYMBOLS")) {
                    String symbols = command.substring(7).trim();
                    fsm.getSymbols().handleSymbols(symbols, fsm.getStates());
                } else if (command.toUpperCase().startsWith("STATES")) {
                    String states = command.substring(6).trim();
                    fsm.getStates().handleStates(states, fsm.getSymbols());
                } else if (command.toUpperCase().startsWith("INITIAL-STATE")) {
                    String initial = command.substring(14).trim();
                    fsm.getStates().handleInitialState(initial, fsm.getSymbols());
                } else if (command.toUpperCase().startsWith("FINAL-STATES")) {
                    String finals = command.substring(12).trim();
                    fsm.getStates().handleFinalStates(finals, fsm.getSymbols());
                } else if (command.toUpperCase().startsWith("TRANSITIONS")) {
                    String transitions = command.substring(11).trim();
                    fsm.getTransitions().handleTransitions(transitions);
                } else if (command.toUpperCase().startsWith("PRINT")) {
                    String[] parts = command.split("\\s+");
                    if (parts.length == 1) {
                        fsm.printToConsole();
                    } else {
                        String filename = parts[1].trim();
                        if (!isValidFilename(filename)) {
                            String errorMsg = "Error: Invalid filename '" + filename +
                                    "'. Use alphanumeric characters and valid extensions (e.g., .txt, .fs).";
                            System.out.println(errorMsg);
                            responseLog.append(errorMsg).append("\n");
                            return;
                        }
                        fsm.printFile(filename);
                    }
                } else if (command.equalsIgnoreCase("CLEAR")) {
                    fsm.clear();
                } else if (command.toUpperCase().startsWith("LOG")) {
                    String filename = command.substring(3).trim();
                    fsm.log(filename);
                } else if (command.equalsIgnoreCase("EXIT")) {
                    fsm.exit();
                } else if (command.toUpperCase().startsWith("EXECUTE")) {
                    String input = command.substring(7).trim();
                    fsm.execute(input);
                } else if (command.toUpperCase().startsWith("LOAD")) {
                    String[] parts = command.split("\\s+", 2);
                    if (parts.length < 2) {
                        String errorMsg = "Error: LOAD requires a filename.";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                        return;
                    }
                    String filename = parts[1].trim();
                    File file = new File(filename);
                    if (!file.exists()) {
                        String errorMsg = "LOAD error: File '" + filename + "' not found.";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                        return;
                    }
                    if (!file.canRead()) {
                        String errorMsg = "LOAD error: File '" + filename + "' is not readable.";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                        return;
                    }
                    try {
                        FSM loadedFSM;
                        if (filename.endsWith(".fs")) {
                            loadedFSM = FSM.loadFromBinary(filename);
                        } else {
                            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                                String line;
                                StringBuilder commandBuffer = new StringBuilder();
                                while ((line = reader.readLine()) != null) {
                                    line = line.trim();
                                    if (line.startsWith(";") || line.isEmpty()) continue;
                                    commandBuffer.append(" ").append(line);
                                    if (line.contains(";")) {
                                        String fullCommand = commandBuffer.toString().trim();
                                        parseAndExecute(fullCommand, fsm);
                                        commandBuffer.setLength(0);
                                    }
                                }
                            } catch (IOException e) {
                                fsm.writeLog("Error reading file: " + e.getMessage());
                                System.err.println("Error reading file: " + e.getMessage());
                            }
                            loadedFSM = fsm;
                        }
                        fsm.copyFrom(loadedFSM);
                        String successMsg = "Load successful: " + filename;
                        System.out.println(successMsg);
                    } catch (FileNotFoundException e) {
                        String errorMsg = "LOAD error: File '" + filename + "' not found.";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (NotSerializableException e) {
                        String errorMsg = "LOAD error: FSM or its components are not serializable (" + e.getMessage() + ")";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (IOException e) {
                        String errorMsg = "LOAD error: " + e.getMessage() + " (IOException)";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (ClassNotFoundException e) {
                        String errorMsg = "LOAD error: Invalid binary file format (" + e.getMessage() + ")";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (Exception e) {
                        String errorMsg = "LOAD error: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    }
                } else if (command.toUpperCase().startsWith("COMPILE")) {
                    String filename = command.substring(7).trim();
                    if (filename.isEmpty()) {
                        String errorMsg = "Error: COMPILE requires a filename.";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                        return;
                    }
                    try {
                        fsm.saveToBinary(filename);
                        String successMsg = "Compile successful: " + filename;
                        System.out.println(successMsg);
                    } catch (NotSerializableException e) {
                        String errorMsg = "COMPILE error: FSM or its components are not serializable (" + e.getMessage() + ")";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (IOException e) {
                        String errorMsg = "COMPILE error: " + e.getMessage() + " (IOException)";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    } catch (Exception e) {
                        String errorMsg = "COMPILE error: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")";
                        System.out.println(errorMsg);
                        responseLog.append(errorMsg).append("\n");
                    }
                } else {
                    String warningMsg = "Warning: invalid command " + command;
                    System.out.println(warningMsg);
                    responseLog.append(warningMsg).append("\n");
                }

                // Capture and filter console output
                tempOut.flush();
                String consoleOutput = bytes.toString();
                if (!consoleOutput.isEmpty()) {
                    String[] lines = consoleOutput.split("\n");
                    Set<String> uniqueLines = new LinkedHashSet<>();
                    for (String line : lines) {
                        if (!line.startsWith("Warning:") &&
                                !line.startsWith("Transition added:") &&
                                !line.startsWith("FSM loaded from binary:") &&
                                !line.startsWith("FSM compiled and saved to binary:") &&
                                !line.startsWith("FSM built from script:")) {
                            uniqueLines.add(line);
                        }
                    }
                    for (String line : uniqueLines) {
                        if (!line.trim().isEmpty()) {
                            responseLog.append(line).append("\n");
                        }
                    }
                }

            } finally {
                // Restore original System.out
                System.setOut(originalOut);
            }

            // Log all collected responses
            if (responseLog.length() > 0) {
                fsm.writeLog(responseLog.toString());
            }

        } catch (Exception e) {
            String errorMsg = "Error while parsing command: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")";
            System.out.println(errorMsg);
            fsm.writeLog(errorMsg + "\n");
        }
    }
}
class TerminalPrintStream extends PrintStream {
    private final PrintStream print;

    public TerminalPrintStream(OutputStream out, PrintStream print) {
        super(out);
        this.print =print;
    }

    @Override
    public void write(byte[] bytes, int start, int length) {
        super.write(bytes, start, length);
        print.write(bytes, start, length);
    }

    @Override
    public void write(int b) {
        super.write(b);
        print.write(b);
    }

    @Override
    public void flush() {
        super.flush();
        print.flush();
    }

    @Override
    public void close() {
        super.close();
        print.close();
    }
}
abstract class Elements{
    protected String name;

    public Elements() {
    }
    @Override
    public String toString() {
        return name;
    }
    public abstract boolean isValid(String symbol);
}

class FSM implements Methods,Serializable {
    private static final long serialVersionUID = 1L;
    private Symbols symbols;
    private States states;
    private Transitions transitions;
    private States initialState;
    private States finalStates;
    private boolean logged;
    private transient FileWriter logWriter;
    public void copyFrom(FSM other) {
        this.states = other.states;
        this.symbols = other.symbols;
        this.transitions = other.transitions;
        this.initialState = other.initialState;
        this.finalStates = other.finalStates;

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
    public FSM() {
        this.symbols = new Symbols();
        this.states = new States();
        this.transitions = new Transitions(states, symbols);
        this.finalStates=new States();
        this.initialState=new States();
    }
    public static FSM loadFromScript(String filePath) throws IOException {
        List<String> commands = Files.readAllLines(Paths.get(filePath));
        FSM fsm = new FSM();
        for (String command : commands) {
            CommandParser.parseAndExecute(command, fsm);
        }
        System.out.println("FSM built from script: " + filePath);
        fsm.writeLog("FSM built from script: " + filePath);
        return fsm;
    }

    @Override
    public void exit() {
        System.out.println("TERMINATED BY USER");
        logged = false;
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
            // LOG; → stop logging
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

            // Eğer daha önce log vardıysa kapat
            if (logged) {
                try {
                    logWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                logWriter = new FileWriter(filename, false); // overwrite
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
        // Validate filename
        if (filename == null || filename.trim().isEmpty()) {
            System.out.println("Error: Filename is null or empty.");
            return;
        }


        // Check if the file can be created or overwritten
        File file = new File(filename);
        try {
            // Ensure parent directory exists
            if (file.getParent() != null && !file.getParentFile().exists()) {
                System.out.println("Error: Directory '" + file.getParent() + "' does not exist.");
                return;
            }

            // Check if file exists and is writable
            if (file.exists() && !file.canWrite()) {
                System.out.println("Error: Cannot write to file '" + filename + "'. File is read-only or access is denied.");
                return;
            }

            try (FileWriter writer = new FileWriter(file)) {
                // Write SYMBOLS
                if (!symbols.getSymbols().isEmpty()) {
                    writer.write("SYMBOLS " + String.join(" ", symbols.getSymbols()) + ";\n");
                }

                // Write STATES
                if (!states.getStates().isEmpty()) {
                    writer.write("STATES " + String.join(" ", states.getStates()) + ";\n");
                }

                // Write INITIAL-STATE
                if (states.getInitialState() != null) {
                    writer.write("INITIAL-STATE " + states.getInitialState() + ";\n");
                }

                // Write FINAL-STATES
                if (!states.getFinalStates().isEmpty()) {
                    writer.write("FINAL-STATES " + String.join(" ", states.getFinalStates()) + ";\n");
                }

                // Write TRANSITIONS
                if (!transitions.getTransitions().isEmpty()) {
                    writer.write("TRANSITIONS ");
                    List<String> transitionList = new ArrayList<>();
                    for (Map.Entry<String, Map<String, String>> fromState : transitions.getTransitions().entrySet()) {
                        String state = fromState.getKey();
                        for (Map.Entry<String, String> symbolToState : fromState.getValue().entrySet()) {
                            transitionList.add(symbolToState.getKey() + " " + state + " " + symbolToState.getValue());
                        }
                    }
                    writer.write(String.join(", ", transitionList) + ";\n");
                }

                System.out.println("FSM commands written to file: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error: Cannot create or write to file '" + filename + "': " + e.getMessage());
        }
    }

    public void printToConsole() {
        System.out.print("SYMBOLS {" + (symbols.getSymbols().isEmpty() ? "" : String.join(" ", symbols.getSymbols())));
        System.out.println("}");
        System.out.print("STATES {" + (states.getStates().isEmpty() ? "" : String.join(" ", states.getStates())));
        System.out.println("}");
        System.out.println("INITIAL STATE {" + (states.getInitialState()==null ? "" : states.getInitialState())+"}");
        System.out.print("FINAL STATES {" + (states.getFinalStates().isEmpty() ? "" : String.join(" ", states.getFinalStates()))+"}");
        System.out.println();
        System.out.print("TRANSITIONS {");
        if (transitions.getTransitions().isEmpty()) {
            System.out.println("}");
        } else {
            for (Map.Entry<String, Map<String, String>> fromState : transitions.getTransitions().entrySet()) {
                String state = fromState.getKey();
                for (Map.Entry<String, String> symbolTo : fromState.getValue().entrySet()) {
                    System.out.print(" "+symbolTo.getKey()+" "+ state+ " "+symbolTo.getValue()+", ");
                }
                System.out.println();
            }
            System.out.println("}");
        }
    }


    @Override
    public void clear() {
        symbols.clear();
        states.clear();
        transitions.clear();
        System.out.println("FSM cleared.");
    }


    public void execute(String input) {

        if (states == null || states.getInitialState() == null || states.getFinalStates() == null || transitions == null || symbols == null) {
            System.out.println("Error: FSM is not fully defined.");
            return;
        }
        String[] inputSymbols;
        if (input.contains(" ")) {
            inputSymbols = input.trim().split("\\s+");
        } else {
            inputSymbols = input.trim().split("");
        }

        String currentState = states.getInitialState().toUpperCase();
        List<String> stateSequence = new ArrayList<>();
        stateSequence.add(currentState);



        for (String symbol : inputSymbols) {



            if (!symbols.getSymbols().contains(symbol)) {
                System.out.println("Error: Symbol '" + symbol + "' is not declared.");
                writeLog("Error: Symbol '" + symbol + "' is not declared.");
                return;
            }


            Map<String, String> symbolMap = transitions.getTransitions().get(currentState);
            if (symbolMap == null || !symbolMap.containsKey(symbol)) {
                System.out.println("Error: No transition from state '" + currentState + "' with symbol '" + symbol + "'.");
                writeLog("Error: No transition from state '" + currentState + "' with symbol '" + symbol + "'.");
                return;
            }


            currentState = symbolMap.get(symbol).toUpperCase();
            stateSequence.add(currentState);

        }


        String output = String.join(" ", stateSequence);

        boolean isAccepting = states.getFinalStates() != null && states.getFinalStates().contains(currentState);
        output += isAccepting ? " YES" : " NO";


        System.out.println(output);
    }
    public void saveToBinary(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);

        }
    }
    public static FSM loadFromBinary(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            FSM fsm = (FSM) in.readObject();
            fsm.writeLog("FSM loaded from binary: " + filePath);
            return fsm;
        }
    }
}
interface Methods{
    void exit();
    void log(String filename);
    void printFile(String filename);

    void clear();


}
interface Print {
    void printToFile(String filename);
    //all print funcs too?
    //void print();

}

class States extends Elements implements Clear, Print,Serializable {
    private static final long serialVersionUID = 1L;
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
    public String toString() {
        return getClass().getSimpleName();
    }
    public boolean contains(String state) {
        return this.states.contains(state);
    }


    @Override
    public void clear() {
        states.clear();
        finalStates.clear();
        initialState = null;
    }

    @Override
    public void printToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("STATES: ");

            for (String state : states) {
                String label = "";
                if (state.equalsIgnoreCase(initialState)) {
                    label += " (initial)";
                }
                if (finalStates.contains(state)) {
                    label += " (final)";
                }
                writer.write(state + " " + label);
            }
            writer.write("\n");

        } catch (IOException e) {
            System.out.println("Error: Cannot write states to file " + filename);
        }
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

    private void printStates() {
        if (states.isEmpty()) {
            System.out.println("No states declared!");
            return;
        }
        for (String state : states) {
            String label = " ";
            if (state.equalsIgnoreCase(initialState)) {
                label += "(initial)";
            }
            if (finalStates.contains(state)) {
                if (!label.isEmpty()) {
                    label += " ";
                    label += "(final)";
                }
            }
            System.out.println(state + " " + label);
        }
    }

    public void handleStates(String input, Symbols symbols) {
        if (input.isBlank()) {
            printStates();
            return;
        }

        String[] tokens = input.trim().split("\\s+");
        Set<String> duplicates = new TreeSet<>();  // Alfabetik sıralı ve tekrarsız
        StringBuilder conflictMessage = new StringBuilder();
        List<String> reservedWords = Arrays.asList("TRANSITIONS", "SYMBOLS", "STATES", "INITIAL-STATE", "FINAL-STATES", "PRINT", "CLEAR", "EXECUTE", "LOG", "LOAD", "COMPILE", "EXIT");


        for (String state : tokens) {
            state = state.trim().replace(",", ""); // Virgül gibi karakterleri temizle
            if (reservedWords.contains(state.toUpperCase())) {
                continue; // Bu bir komut, state değil
            }

            if (!isValid(state)) {
                System.out.println("Warning: '" + state + "' is not a valid state name.");
                continue;
            }
            String upperState = state.toUpperCase();
            if (symbols.getSymbols().contains(upperState)) {
                if (conflictMessage.length() > 0) {
                    conflictMessage.append(", ");
                }
                conflictMessage.append(upperState);
                continue;
            }
            if (states.contains(upperState)) {
                duplicates.add(upperState); // tekrar edenleri toplar
                continue;
            }
            states.add(upperState);
        }

        if (conflictMessage.length() > 0) {
            System.out.println("Warning: '" + conflictMessage + "' is both a symbol and a state.");
        }
        if (!duplicates.isEmpty()) {
            System.out.println("Warning: States " + String.join(", ", duplicates) + " were already declared.");
        }
    }

    public void handleInitialState(String input, Symbols symbols) {
        String symbol = input.trim();

        if (symbol.isEmpty() || !isValid(symbol)) {
            System.out.println("Warning: No valid initial state specified.");
            return;
        }
        String upperSymbol = symbol.toUpperCase(); // Büyük harfe çevir
        if (symbols.getSymbols().contains(upperSymbol)) {
            System.out.println("Warning: '" + upperSymbol + "' is already declared as a symbol and cannot be used as a state.");
            return;
        }

        if (!states.contains(upperSymbol)) {
            states.add(upperSymbol);
            System.out.println("Warning: Initial state '" + upperSymbol + "' was not declared before.");
        }

        initialState = upperSymbol;
    }

    public void handleFinalStates(String input, Symbols symbols) {
        if (input.isBlank()) {
            System.out.println("Warning: No final states specified.");
            return;
        }
        String[] tokens = input.trim().split("\\s+");
        for (String state : tokens) {
            if (!isValid(state)) {
                System.out.println("Warning: '" + state + "' is not a valid state name.");
                continue;
            }
            String upperState = state.toUpperCase(); // Büyük harfe çevir
            if (symbols.getSymbols().contains(upperState)) {
                System.out.println("Warning: '" + upperState + "' is already declared as a symbol and cannot be a final state.");
                continue;
            }

            if (!states.contains(upperState)) {
                System.out.println("Warning: State '" + upperState + "' was not declared before. Added automatically.");
                states.add(upperState);
            }
            if (!finalStates.contains(upperState)) {
                finalStates.add(upperState);
            } else {
                System.out.println("Warning: State '" + upperState + "' is already a final state.");
            }
        }
    }
}

class Symbols extends Elements implements Clear, Print,Serializable{
    private static final long serialVersionUID = 1L;
    private Set<String> symbols=new HashSet<>();
    private static FSM fsm=new FSM();


    public Symbols(){
        this.symbols=symbols;
    }

    public Set<String> getSymbols(){
        return this.symbols;
    }
    public void setSymbols(Set<String> symbols){
        this.symbols=symbols;
    }

    private void addSymbol(String name){
        if(isValid(name)==false){
            System.out.println("Warning: Symbol is not alphanumeric, it will be ignored!");
            fsm.writeLog("Warning: Symbol is not alphanumeric, it will be ignored!");
            return;
        }
        if (symbols.contains(name)){
            fsm.writeLog("Warning: " + name + " was already declared as a symbol");
            System.out.println("Warning: " + name + " was already declared as a symbol");
        } else {
            symbols.add(name);
        }
    }

    private void printSymbols() {
        if (symbols.isEmpty()) {
            System.out.println("No symbols declared!");
            fsm.writeLog("No symbols declared!");

            return;
        }


        for (String s: symbols) {
            System.out.print(s +" ");
            fsm.writeLog(s);

        }
        System.out.println();
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void clear(){
        symbols.clear();
        fsm.writeLog("FSM cleared.");

    }

    @Override
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

    @Override
    public void printToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("SYMBOLS: ");
            fsm.writeLog("SYMBOLS ");

            for (String symbol : symbols) {
                writer.write(symbol+" ");
                fsm.writeLog(symbol+" ");
            }
            writer.write("\n");
            fsm.writeLog("\n");
        } catch (IOException e) {
            fsm.writeLog("Error: Cannot write symbols to file " + filename);
            System.out.println("Error: Cannot write symbols to file " + filename);
        }
    }
    public void handleSymbols(String input, States states){

        if (input.isBlank()) {
            printSymbols();
            return;
        }

        String[] array = input.trim().split("\\s+");
        for (String s : array) {
            if (s.length() != 1) {
                System.out.println("Warning: '" + s + "' is not allowed as a symbol, length must be 1.");
                continue;
            }

            if(states.getStates().contains(s)){
                System.out.println("Warning: '" + s + "' is already declared as a state and cannot be a symbol.");
                continue;
            }
            addSymbol(s);
        }
    }
}
class Transitions extends Elements implements Clear, Print,Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Map<String, String>> transitions = new HashMap<>();
    private States states;
    private Symbols symbols;

    public Map<String, Map<String, String>> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<String, Map<String, String>> transitions) {
        this.transitions = transitions;
    }

    public States getStates() {
        return states;
    }

    public void setStates(States states) {
        this.states = states;
    }

    public Symbols getSymbols() {
        return symbols;
    }

    public void setSymbols(Symbols symbols) {
        this.symbols = symbols;
    }

    public Transitions(States states, Symbols symbols) {
        this.transitions = new HashMap<>();
        this.states = states;
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

    @Override
    public void clear() {
        transitions.clear();
    }

    @Override
    public void printToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            for (String fromState : transitions.keySet()) {
                Map<String, String> symbolMap = transitions.get(fromState);
                for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
                    writer.write("TRANSITION " + fromState + " -" + entry.getKey() + "-> " + entry.getValue() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Cannot write transitions to file " + filename);
        }
    }

    private void addTransition(String fromState, String symbol, String toState) {
        if (!isValid(fromState) || !isValid(symbol) || !isValid(toState)) {
            System.out.println("Warning: Invalid transition input (" + fromState + ", " + symbol + ", " + toState + "), ignored.");
            return;
        }

        String upperFromState = fromState.toUpperCase(); // Büyük harfe çevir
        String upperToState = toState.toUpperCase(); // Büyük harfe çevir

        if (!states.getStates().contains(upperFromState)) {
            System.out.println("Error: fromState '" + upperFromState + "' is not a state.");
            return;
        }
        if (!states.getStates().contains(upperToState)) {
            System.out.println("Error: toState '" + upperToState + "' is not a state.");
            return;
        }
        if (!symbols.getSymbols().contains(symbol)) {
            System.out.println("Warning: symbol '" + symbol + "' not declared. Adding automatically.");
            symbols.getSymbols().add(symbol);
        }

        transitions.putIfAbsent(upperFromState, new HashMap<>());
        Map<String, String> symbolToState = transitions.get(upperFromState);

        if (symbolToState.containsKey(symbol)) {
            System.out.println("Warning: multiple transitions for symbol '" + symbol + "' and state '" + upperFromState + "'.");
            return;
        }

        symbolToState.put(symbol, upperToState);
    }

    public void handleTransitions(String input) {
        if (input.isBlank()) {
            printTransitions();
            return;
        }

        input = input.trim();
        if (input.toUpperCase().startsWith("TRANSITIONS")) {
            input = input.substring("TRANSITIONS".length()).trim();
        }

        String[] transitionArray = input.split("\\s*,\\s*");
        for (String transition : transitionArray) {
            String[] parts = transition.trim().split("\\s+");
            if (parts.length != 3) {
                System.out.println("Warning: Invalid transition format, should be 'symbol fromState toState'. Ignored: " + transition);
                continue;
            }

            String symbol = parts[0];
            String fromState = parts[1];
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
class Terminal {
    private final Scanner scanner = new Scanner(System.in);
    private final FSM fsm;
    private final CommandParser c;

    public Terminal(FSM fsm, CommandParser c) {
        this.fsm = fsm;
        this.c = c;
    }

    public void startREPL() {
        StringBuilder commandBuffer = new StringBuilder();
        System.out.print("? ");
        fsm.writeLog("? ");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.startsWith(";")) {
                System.out.print("? ");
                fsm.writeLog("? ");
                continue;
            }

            commandBuffer.append(" ").append(line);

            if (line.contains(";")) {
                String fullCommand = commandBuffer.toString().trim();
                c.parseAndExecute(fullCommand, fsm);
                commandBuffer.setLength(0);
                System.out.print("? ");
                fsm.writeLog("? ");
            }
        }
    }

    public void processFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder commandBuffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(";") || line.isEmpty()) continue;
                commandBuffer.append(" ").append(line);
                if (line.contains(";")) {
                    String fullCommand = commandBuffer.toString().trim();
                    c.parseAndExecute(fullCommand, fsm);
                    commandBuffer.setLength(0);
                }
            }
        } catch (IOException e) {
            fsm.writeLog("Error reading file: " + e.getMessage());
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
