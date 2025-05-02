import java.util.HashSet;
import java.util.Set;

class Symbols extends Elements implements clear{
    private Set<String> symbols=new HashSet<>();

    public Symbols(){
        this.symbols=symbols;
    }

    public Set<String> getSymbols(){
        return this.symbols;
    }
    public void setSymbols(Set<String> symbols){
        this.symbols=symbols;
    }

    private void addSymbol(String name) throws ExistingSymbolException{
        if(isValid(name)==false){
            System.out.println("Warning: Symbol is not alphanumeric, it will be ignored!");
            return;
        }
        if (symbols.contains(name)){
            throw new ExistingSymbolException("Warning: Symbol '" + name + "' exists!");
        } else {
            symbols.add(name);
        }
    }

    private void printSymbols() {
        if (symbols.isEmpty()) {
            System.out.println("No symbols declared!");
            return;
        }
        System.out.println("Declared symbols:");
        for (String s: symbols) {
            System.out.println(s);
        }
    }
    @Override
    public void clear(){
        symbols.clear();
        System.out.println("FSM cleared.");
    }

    @Override
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

}
