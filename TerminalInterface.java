public class FSMDesigner {
    private static final String VERSION = "2.3";

    public static void main(String[] args) {
        if (args.length == 0) {
            printHeader();
            TerminalInterface terminal = new TerminalInterface();
            terminal.startREPL();
        } else {
         
            File commandFile = new File(args[0]);
            if (!commandFile.exists()) {
                System.err.println("Error: Cannot open file " + args[0]);
                return;
            }
            TerminalInterface terminal = new TerminalInterface();
            terminal.processFile(commandFile);
        }
    }

    private static void printHeader() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm");
        System.out.println("FSM DESIGNER " + VERSION + " " + now.format(formatter));
    }
}

public class TerminalInterface {
    private final Scanner scanner = new Scanner(System.in);

    public void startREPL() {
        StringBuilder commandBuffer = new StringBuilder();
        System.out.print("? ");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.startsWith(";")) {
                System.out.print("? ");
                continue;
            }

            commandBuffer.append(" ").append(line);

            if (line.contains(";")) {
                String fullCommand = commandBuffer.toString();
                processCommand(fullCommand);
                commandBuffer.setLength(0); // Clear buffer
                System.out.print("? ");
            }
        }
    }

    private void processCommand(String input) {
        String command = input.split(";", 2)[0].trim(); 
        if (command.isEmpty()) return;

        String[] tokens = command.split("\\s+");
        String keyword = tokens[0].toUpperCase();

        switch (keyword) {
            case "EXIT":
                System.out.println("TERMINATED BY USER");
                System.exit(0);
                break;
            case "SYMBOLS":
              
                break;
            default:
                System.out.println("Warning: invalid command;");
        }
    }

    public void processFile(File file) {
        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder commandBuffer = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.startsWith(";")) continue;
                commandBuffer.append(" ").append(line);
                if (line.contains(";")) {
                    String fullCommand = commandBuffer.toString();
                    processCommand(fullCommand);
                    commandBuffer.setLength(0);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
