import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FSMSerializer {
    public static void saveToBinary(FSM fsm, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(fsm);
            System.out.println("FSM compiled and saved to binary: " + filePath);
        }
    }
    public static FSM loadFromBinary(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            FSM fsm = (FSM) in.readObject();
            System.out.println("FSM loaded from binary: " + filePath);
            return fsm;
        }
    }
}
