import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CommandExecutor {
    public static FSM loadFromScript(String filePath) throws IOException {
        List<String> commands = Files.readAllLines(Paths.get(filePath));
        FSM fsm = new FSM();
        for (String command : commands) {
            CommandParser.parseAndExecute(command, fsm);
        }
        System.out.println("FSM built from script: " + filePath);
        return fsm;
    }
}
