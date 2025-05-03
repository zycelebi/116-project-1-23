import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FSMSerializer {
    public static void saveToBinary(FSM fsm, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(fsm);
            System.out.println("FSM compiled and saved to binary: " + filePath);
        }
    }
}
