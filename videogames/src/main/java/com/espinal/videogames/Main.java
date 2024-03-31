package com.espinal.videogames;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Assignment 2 - Main driver.
 * CSC 311
 * Due 10/18/2021 @ 9:25am
 * @author Jonathan Espinal
 */
public class Main {
    /**
     * Main method.
     * @param args 
     */
    public static void main(String[] args){
        List<VideoGame> videoGames = new ArrayList<>();
        FileWriter fileWriter;
        FileReader fileReader;
        String openedFile;
        String outFile;
        Gson gson = new Gson();
        String json;
        Scanner keyboard = new Scanner(System.in);
        
        int choice;
        boolean running = true;          
        while(running){
            // print menu
            System.out.println("Video Game UI");
            System.out.println("1  - Import DB from JSON");
            System.out.println("2  - Export DB to JSON");
            System.out.println("3  - Add new video game");
            System.out.println("4  - Show all video games");
            System.out.println("5  - Show all video games higher than a given price (user enters price)");
            System.out.println("6  - Show video game with a given title");
            System.out.println("7  - Show average video game price");
            System.out.println("8  - Delete video game by title");
            System.out.println("9  - Delete all video games from DB");
            System.out.println("10 - Exit");
            System.out.println("Enter choice: ");
            
            // take choice and execute
            choice = keyboard.nextInt();
            keyboard.nextLine();
            switch(choice){
                case 1:
                    System.out.println("JSON File Name: ");
                    openedFile = keyboard.next();
                    try {
                        fileReader = new FileReader (openedFile);
                        videoGames = gson.fromJson(fileReader, new TypeToken<ArrayList<VideoGame>>(){}.getType());  
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found.");
                    }
                    for (int i = 0; i < videoGames.size(); i++){
                        insertDbData(videoGames.get(i).getTitle(), videoGames.get(i).getPrice(), videoGames.get(i).getRating());
                    }
                    break;    
                case 2: 
                    System.out.println("Save as: ");
                    outFile = keyboard.next();
                    videoGames = retrieveDbData();
                    json = gson.toJson(videoGames);
                    try {
                        fileWriter = new FileWriter(outFile);
                        fileWriter.write(json);
                        fileWriter.close();
                        System.out.println("File saved: "+outFile);
                    }catch (IOException e){
                            System.err.println("Error creating file.");
                    }
                    break;
                case 3:
                    System.out.println("Enter title: ");
                    String title = keyboard.nextLine();
                    System.out.println("Enter price: ");
                    double price = keyboard.nextDouble();
                    keyboard.nextLine();
                    System.out.println("Enter rating: ");
                    String rating = keyboard.nextLine();
                    insertDbData(title, price, rating);
                    break; 
                case 4:
                    printAllGames(videoGames);
                    break;
                case 5:
                    System.out.println("Enter price: ");
                    double filterPrice = keyboard.nextDouble();
                    keyboard.nextLine();
                    videoGames = retrieveDbData();
                    videoGames.stream().filter(x -> x.getPrice() > filterPrice).forEach(System.out::println);
                    break;
                case 6:
                    System.out.println("Enter title: ");
                    title = keyboard.nextLine();
                    videoGames = retrieveDbData();
                    videoGames.stream().filter(x -> x.getTitle().equals(title)).forEach(System.out::println);
                    break;
                case 7: 
                    videoGames = retrieveDbData();
                    double average = videoGames.stream().mapToDouble(x -> x.getPrice()).average().getAsDouble();
                    System.out.printf("Average Price: %.2f",average);
                    System.out.println();
                    break; 
                case 8:
                    System.out.println("Enter title: ");
                    title = keyboard.nextLine();
                    deleteGameFromDb(title);
                    break;
                case 9: 
                    deleteAllGamesFromDb();
                    break;
                case 10: 
                    running = false;
                    break;
            }
            System.out.println();
        }
    }
    
    /**
     * Inserts video game data into the database.
     * @param title Title of the game.
     * @param price Price of the game.
     * @param esrb_rating ESRB rating of the game.
     */
    public static void insertDbData(String title, double price, String esrb_rating) {
        String databaseURL = "";
        Connection conn = null;

        try {
            databaseURL = "jdbc:ucanaccess://.//videogameDB.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String sql = "UPDATE Games SET price = ?, esrb_rating = ? WHERE title = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDouble(1, price);
            preparedStatement.setString(2, esrb_rating);
            preparedStatement.setString(3, title);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Row updated");
            }
            sql = "INSERT INTO Games (title, price, esrb_rating) VALUES (?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, esrb_rating);
            row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Row inserted");
            }

        } catch (SQLException e) {
        }
    }
    
    /**
     * Retrieves game information from the database as a list.
     * @return The list holding video game objects.
     */
    public static List retrieveDbData() {
        String databaseURL = "";
        Connection conn = null;
        List<VideoGame> videoGames = new ArrayList<>();
        try {
            databaseURL = "jdbc:ucanaccess://.//videogameDB.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(net.ucanaccess.console.Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String tableName = "Games";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select * from " + tableName);
            while (result.next()) {
                String title = result.getString("title");
                double price = result.getDouble("price");
                String rating = result.getString("esrb_rating");
                videoGames.add(new VideoGame(title, price, rating));
            }
        } catch (SQLException except) {
            except.printStackTrace();
        }

        return videoGames;
    }
    
    /**
     * Prints all games in the list.
     * @param videoGames The list of games.
     */
    public static void printAllGames(List<VideoGame> videoGames) {
        videoGames = retrieveDbData();
        System.out.printf("%-30s%-10s%-30s\n", "Title", "Price", "ESRB");
        for (int i = 0; i < videoGames.size(); i++) {
            String title = videoGames.get(i).getTitle();
            double price = videoGames.get(i).getPrice();
            String rating = videoGames.get(i).getRating();
            System.out.printf("%-30s%-10.2f%-30s\n", title, price, rating);
        }
    }
    
    /**
     * Deletes a game record from the database.
     * @param title The title of the game.
     */
    public static void deleteGameFromDb(String title) {
        String databaseURL = "";
        Connection conn = null;

        try {
            databaseURL = "jdbc:ucanaccess://.//videogameDB.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String sql = "DELETE FROM Games WHERE title = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, title);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Row deleted");
            }
        } catch (SQLException e) {
        }
    }
    
    /**
     * Deletes all game records from the database.
     */
    public static void deleteAllGamesFromDb() {
        String databaseURL = "";
        Connection conn = null;

        try {
            databaseURL = "jdbc:ucanaccess://.//videogameDB.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String sql = "DELETE FROM Games";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("All games deleted from database");
            }
        } catch (SQLException e) {
        }
    }
}
