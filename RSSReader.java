import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
* Additional resources:
* https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
* https://www.w3schools.com/java/java_arraylist.asp
* Some inspiration from friends
*/

public class RSSReader {
    private static final int ACTION_MAIN_MENU = -1;
    private static final int ACTION_SHOW_UPDATES = 1;
    private static final int ACTION_ADD_URL = 2;
    private static final int ACTION_REMOVE_URL = 3;
    private static final int ACTION_EXIT = 4;
    private static final String DATA_FILE_NAME = "data.txt";

    public static void main(String[] args) {
        // Reading data from the data file
        File dataFile = new File(DATA_FILE_NAME);
        BufferedReader reader;
        ArrayList<Blog> blogs = new ArrayList<>();
        if (dataFile.exists() && !dataFile.isDirectory()) {
            try {
                reader = new BufferedReader(new FileReader(dataFile));

                String blogInfoString;
                String[] blogInfoArray;
                while ((blogInfoString = reader.readLine()) != null) {
                    blogInfoArray = blogInfoString.split(";");
                    blogs.add(new Blog(blogInfoArray[0], blogInfoArray[1], blogInfoArray[2]));
                }

                reader.close();
            } catch (Exception e) {
                System.out.println("Error: Unable to read the data file!");
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        int action = ACTION_MAIN_MENU;
        // Used for adding/removing URLs
        String inputURL;

        System.out.println("Welcome to RSS Reader!");
        while (action != ACTION_EXIT) {
            switch (action) {
                case ACTION_SHOW_UPDATES:
                    System.out.println("[0] All websites");
                    for (int i = 0; i < blogs.size(); i++) {
                        System.out.printf("[%d] %s\n", i + 1, blogs.get(i).getTitle());
                    }
                    System.out.println("Enter -1 to return.");

                    int input = scanner.nextInt();
                    if (input >= 0 && input <= blogs.size()) {
                        if (input == 0) {
                            for (Blog blog : blogs) {
                                RSSTools.retrieveRssContent(blog.getRSS());
                                System.out.println();
                            }
                        } else {
                            RSSTools.retrieveRssContent(blogs.get(input - 1).getRSS());
                        }
                    }
                    action = ACTION_MAIN_MENU;
                    break;

                case ACTION_ADD_URL:
                    System.out.println("Please enter website URL to add:");
                    scanner.nextLine();

                    inputURL = scanner.nextLine();
                    Blog blogToAdd;
                    try {
                        if (blogAlreadyExists(inputURL, blogs)) {
                            System.out.println(inputURL + " already exists.");
                        } else {
                            blogToAdd = new Blog(RSSTools.extractPageTitle(RSSTools.fetchPageSource(inputURL)), inputURL, RSSTools.extractRssUrl(inputURL));
                            if (blogToAdd.getRSS() == null || blogToAdd.getRSS().isBlank()) {
                                System.out.println("Didn't add " + inputURL + " because it didn't have a RSS link.");
                            } else {
                                blogs.add(blogToAdd);
                                System.out.println("Added " + inputURL + " successfully.");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: Unable to connect to the internet!");
                    } finally {
                        action = ACTION_MAIN_MENU;
                    }
                    break;

                case ACTION_REMOVE_URL:
                    System.out.println("Please enter website URL to remove:");
                    scanner.nextLine();

                    inputURL = scanner.nextLine();
                    for (int i = 0; i < blogs.size(); i++) {
                        if (blogs.get(i).getURL().equals(inputURL)) {
                            blogs.remove(i);
                            System.out.println("Removed " + inputURL + " successfully.");
                            action = ACTION_MAIN_MENU;
                            break;
                        }
                    }

                    if (action != ACTION_MAIN_MENU) {
                        System.out.println("Couldn't find " + inputURL);
                        action = ACTION_MAIN_MENU;
                    }
                    break;

                default:
                    System.out.println("Type a valid number for your desired action:");
                    System.out.println("[1] Show updates");
                    System.out.println("[2] Add URL");
                    System.out.println("[3] Remove URL");
                    System.out.println("[4] Exit");
                    try {
                        action = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        action = ACTION_MAIN_MENU;
                    }
            }
        }

        // Writing data to the data file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));

            for (Blog blog : blogs) {
                writer.write(blog.getTitle() + ";" + blog.getURL() + ";" + blog.getRSS() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error: Unable to write to the data file!");
        }
    }

    private static boolean blogAlreadyExists(String blogUrlString, ArrayList<Blog> blogs) {
        for (Blog blog : blogs) {
            if (blog.getURL().equals(blogUrlString)) {
                return true;
            }
        }

        return false;
    }
}
