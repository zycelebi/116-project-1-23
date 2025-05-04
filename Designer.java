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



        

