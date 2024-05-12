package com.example.rugandroll;

public class ShoppingItem {
    private String id;
    private String name;
    private String description;
    private String price;
    private float rating;
    private int imageResource;
    private int cartQuantity = 0;

    public ShoppingItem() {
    }

    public ShoppingItem(String name, String description, String price, float rating, int imageResource, int cartQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.imageResource = imageResource;
        this.cartQuantity = cartQuantity;
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
    public int getCartQuantity() {
        return cartQuantity;
    }
    public String _getId() {
        return id;
    }
    public void setId (String id) {
        this.id = id;
    }
}
