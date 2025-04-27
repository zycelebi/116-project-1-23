public interface Methods{
    void exit(){}
    void log(String filename){}
    void addSymbol(Symbol){}//parametre
    void printSymbol(){}
    void addState(String stateName){}
    void setInitialState(String state){}
    void addFinalState(String finalState){}
    //void handleStates(String input){}
    //void handleInitialState(String input){}
    //void handleFinalState(String input){}
    void addTransition(Transition){}
    void print(){}
    void printFile(String filename){}
    void compile(String filename){}
    void clear(){}
    void load(String filename){}
    void execute(String filename){}

}
