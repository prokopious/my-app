package app;

public abstract class AbstractAnimal implements Animal {
    protected String type;  // Common property for all animals

    public AbstractAnimal(String type) {
        this.type = type;
    }

    public void describe() {
        System.out.println("I am a " + type);
    }

    // Note: We haven't implemented makeSound, so any concrete class inheriting from this must provide an implementation.
}
