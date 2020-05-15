package Foods;

import java.util.List;

public class Food {
    private String name;
    private String animal;
    private String category;
    private List<Component> componentList;

    public Food(String name, String animal, String category, List<Component> componentList) {
        this.name = name;
        this.animal = animal;
        this.category = category;
        this.componentList = componentList;
    }

}
