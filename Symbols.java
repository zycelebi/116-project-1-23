import java.util.HashSet;
import java.util.Set;

class ExistingSymbolException extends Exception{
  public ExistingSymbolException(String message){
        super(message);
        }
  }

class Symbols extends Elements{
  private Set<String> symbols;

  public Symbols(){
    this.symbols=new Hashset<>();
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
    if (symbols.contains(name) {
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
    public boolean isValid(String symbol) {
        return symbol.matches("[a-zA-Z0-9]+");
    }

}
