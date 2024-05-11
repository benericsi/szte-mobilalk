package com.example.rugandroll;

public class ShoppingItem {
    private String name;
    private String description;
    private String price;
    private float rating;
    private final int imageResource;

    public ShoppingItem(String name, String description, String price, float rating, int imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.imageResource = imageResource;
    }

    public String getName() {
            return name;
        }

    public String getDescription() {
            return description;
        }

    public String getPrice() {
            return price;
        }

    public float getRating() {
            return rating;
        }

    public int getImageResource() {
            return imageResource;
        }
}
