package com.espinal.videogames;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * VideoGame class. Stores the title, price, and rating of
 * a video game as an object.
 * @author Jonathan Espinal
 */
public class VideoGame {
    
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private double price;
    @SerializedName("esrb")
    private String rating;
    
    /**
     * Parameterized constructor.
     * @param title Title of the game.
     * @param price Price of the game.
     * @param rating ESRB rating of the game.
     */
    public VideoGame(String title, double price, String rating) {
        this.title = title;
        this.price = price;
        this.rating = rating;
    }
    
    /**
     * Gets the current title of the video game.
     * @return The title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the current price of the video game.
     * @return The price.
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets the current rating of the video game.
     * @return The rating.
     */
    public String getRating() {
        return rating;
    }
    
    /**
     * Sets game title.
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets game price
     * @param price double
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Sets game rating.
     * @param rating String  
     */
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    /**
     * Concatenates together all attributes of the video game object as a 
     * string.
     * @return Returns a string representation of the video game.
     */
    @Override
    public String toString() {
        return "VideoGame{" + "title: " + title + ", price: " + price + ", rating: " + rating + '}';
    }
    
    /**
     * hashCode override to hash titles. Auto generated from Netbeans, modified
     * to use String hashcode value instead of Object hashcode value.
     * @return Returns an integer hash representation of the title.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.title.hashCode();
        return hash;
    }
    
    /**
     * Checks if the titles are equal. Auto generated from Netbeans.
     * @param obj The video game object to compare.
     * @return True if equal, False if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VideoGame other = (VideoGame) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }  
}
