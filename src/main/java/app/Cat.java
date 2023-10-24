package app;

public class Cat extends AbstractAnimal {
    public Cat() {
        super("Cat"); 
    }

    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}
