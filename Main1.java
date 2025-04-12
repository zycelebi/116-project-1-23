import java.io.File;
import java.util.Scanner;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Formatter;
import java.util.*;


class LoggingException extends Exception {
    public LoggingException(String message) {
        super(message);
    }
}
//...


public class Main{
    private char symbol; //aa 1 sembol mü
    private String state;
    private String transition;

    public void exit(){
        System.out.println("TERMINATED BY USER");
        //return "TERMINETAED BY USER";
    }
    public void log(){}
    public void symbols(){}//parametre
    public void PrintSymbols(){}
    public void states(){}
    public void InitialState(){}
    public void FinalStates(){}
    public void transitions(){}
    public void print(){}
    public void compile(){}
    public void clear(){}
    public void load(){}
    public void execute(){}

}
