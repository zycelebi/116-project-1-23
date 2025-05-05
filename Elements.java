
abstract class Elements{
    protected String name;

    public Elements() {
    }


    @Override
    public String toString() {
        return name;
    }

    public abstract boolean isValid();
}
