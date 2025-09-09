import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Petrol Navigator");
            frame.setSize(340, 650);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(null);
            frame.add(mainPanel);

            JLabel petrolLabel = new JLabel("Enter petrol amount (0-0.5 L):");
            petrolLabel.setBounds(50, 200, 200, 20);
            mainPanel.add(petrolLabel);

            JTextField petrolEntry = new JTextField(10); // 10 columns
            petrolEntry.setBounds(50, 230, 200, 30);
            mainPanel.add(petrolEntry);

            JButton submitButton = new JButton("Submit");
            submitButton.setBounds(100, 280, 100, 40);
            submitButton.addActionListener(e -> {
                String input = petrolEntry.getText();
                try {
                    double petrolAmount = Double.parseDouble(input);
                    if (petrolAmount >= 0 && petrolAmount <= 0.5) {
                        displayLowPetrolMessage(mainPanel);
                    } else {
                        // Handle invalid input
                        System.out.println("Invalid input. Petrol amount must be between 0 and 0.5 liters.");
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            });
            mainPanel.add(submitButton);

            frame.setVisible(true);
        });
    }

    private static void displayLowPetrolMessage(JPanel panel) {
        JFrame lowPetrolFrame = new JFrame("Low Petrol Alert");
        lowPetrolFrame.setSize(340, 650);

        JPanel lowPetrolPanel = new JPanel(null);
        lowPetrolFrame.add(lowPetrolPanel);

        JLabel messageLabel = new JLabel("Petrol is low. Finding the nearest petrol bunk...");
        messageLabel.setBounds(50, 50, 300, 20);
        lowPetrolPanel.add(messageLabel);

        JButton showBunksButton = new JButton("Show Nearby Petrol Bunks");
        showBunksButton.setBounds(50, 100, 200, 40);
        showBunksButton.addActionListener(e -> findNearestPetrolBunk(panel));
        lowPetrolPanel.add(showBunksButton);

        lowPetrolFrame.setVisible(true);
    }

    private static void findNearestPetrolBunk(JPanel panel) {
        // Using JFXPanel to embed JavaFX content in Swing
        JFXPanel fxPanel = new JFXPanel();
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().load("https://www.google.com/maps");
            fxPanel.setScene(new Scene(webView));
            fxPanel.setVisible(true);

            // Additional method for finding the nearest petrol bunk
            findAndDisplayNearestPetrolBunk(webView, panel);
        });

        JFrame googleMapsFrame = new JFrame("Google Maps");
        googleMapsFrame.setSize(800, 600);
        googleMapsFrame.getContentPane().add(fxPanel);
        googleMapsFrame.setVisible(true);
    }

    private static void findAndDisplayNearestPetrolBunk(WebView webView, JPanel panel) {
        try {
            // Use a free IP geolocation service to obtain the user's approximate location
            String ipGeolocationURL = "https://ipapi.co/json/";

            Scanner scanner = new Scanner(new URL(ipGeolocationURL).openStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse the JSON response to get the latitude and longitude
            double userLatitude = parseLatitude(response);
            double userLongitude = parseLongitude(response);

            // Build the Google Maps Directions API URL without an API key
            String directionsURL = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=" + userLatitude + "," + userLongitude +
                    "&destination=gas+station" +
                    "&mode=driving";

            // Send a request to the Google Maps Directions API
            scanner = new Scanner(new URL(directionsURL).openStream(), "UTF-8");
            response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse the response to extract information about the nearest petrol bunk
            // Extract the necessary information from the JSON response, e.g., the distance,
            // duration, etc.

            // Open Google Maps with directions in the JFrame
            Platform.runLater(() -> {
                webView.getEngine().executeScript("window.location.href = '" +
                        "https://www.google.com/maps/dir/?api=1&destination=gas+station&destination_address=" +
                        userLatitude + "," + userLongitude + "&travelmode=driving';");
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static double parseLatitude(String response) {
        // Implement the logic to parse the latitude from the JSON response
        // Example: Extract the "latitude" field from the JSON response
        return 37.7749; // Example latitude
    }

    private static double parseLongitude(String response) {
        // Implement the logic to parse the longitude from the JSON response
        // Example: Extract the "longitude" field from the JSON response
        return -122.4194; // Example longitude
    }
}
