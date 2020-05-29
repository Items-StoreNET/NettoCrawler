package de.lif.schule.core.product;

public class ProductData {

    private String name = null;
    private String description = null;
    private String category = null;
    private double price = 0;
    private int grammage = 0;

    public boolean isValid(){
        return name != null && description != null && category != null && price != 0.0
                && grammage != 0 && !name.isEmpty() && !description.isEmpty() && !category.isEmpty();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGrammage(int grammage) {
        this.grammage = grammage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getGrammage() {
        return grammage;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
