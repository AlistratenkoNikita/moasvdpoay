package nc.nut.dao.product;

/**
 * Created by Rysakova Anna on 24.04.2017.
 */
public class ProductCategories {

    private Integer id;
    private String name;
    private String description;

    public ProductCategories() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}