class CommandParser {
    private static boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        return filename.matches("[a-zA-Z0-9_\\-\\.]+") && filename.endsWith(".txt");
    }

    public static void parseAndExecute(String command, FSM fsm) {
        if (command == null || command.trim().isEmpty()) return;

        command = command.trim();
        if (!command.endsWith(";")) {
            System.out.println("Warning: command must end with ';'");
            return;
        }

        command = command.substring(0, command.length() - 1).trim();

        try {
            if (command.toUpperCase().startsWith("SYMBOLS")) {
                String symbols = command.substring(7).trim();
                fsm.getSymbols().handleSymbols(symbols);
            } else if (command.toUpperCase().startsWith("STATES")) {
                String states = command.substring(6).trim();
                fsm.getStates().handleStates(states);
            } else if (command.toUpperCase().startsWith("INITIAL-STATE")) {
                String initial = command.substring(14).trim();
                fsm.getStates().handleInitialState(initial);
            } else if (command.toUpperCase().startsWith("FINAL-STATES")) {
                String finals = command.substring(12).trim();
                fsm.getStates().handleFinalStates(finals);
            } else if (command.toUpperCase().startsWith("TRANSITIONS")) {
                String transitions = command.substring(11).trim();
                fsm.getTransitions().handleTransitions(transitions);
            } else if (command.equalsIgnoreCase("PRINT")) {
                String[] parts = command.split("\\s+", 2);
                if (parts.length == 1) {
                    // PRINT without filename: print to console
                    fsm.printToConsole();
                } else {
                    // PRINT with filename: write to file
                    String filename = parts[1].trim();
                    if (!isValidFilename(filename)) {
                        System.out.println("Error: Invalid filename '" + filename + "'. Use alphanumeric characters and valid extensions (e.g., .txt).");
                        return;
                    }
                    fsm.printFile(filename);
                }
            } else if (command.equalsIgnoreCase("CLEAR")) {
                fsm.clear();
            }else if (command.toUpperCase().startsWith("LOG")) {
                fsm.log(command.substring(3).trim());
            }
            else if(command.equalsIgnoreCase("EXIT")) {
                fsm.exit();
            }
            else if(command.startsWith("EXECUTE")){
                fsm.execute(command);
            }else if(command.startsWith("LOAD")){

                try{
                    fsm.loadFromScript(command.substring(4).trim());
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }else if(command.equalsIgnoreCase("COMPILE")){
                fsm.saveToBinary(fsm,command.substring(6).trim());
            }
            else {
                System.out.println("Warning: unknown command: " + command);
            }
        } catch (Exception e) {
            System.out.println("Error while parsing command: " + e.getMessage());
        }
    }
}
