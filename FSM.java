public class FSM implements Methods{
  private Symbols symbols;//or protected?
  private States states;
  private Transitions transitions;
  

  public FSM(){
    this.symbols=new Symbols();
    this.states=new States();
    this.transitions=new Transitions();
  }

  @Override
  public void exit(){
    System.out.println("TERMINATED BY USER");
    break;
  }

}
