package com.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JSONFileGenerator {

    public static void main(String[] args) {
        
        
        int numberOfRows = 3_500_000; // Adjust the number of rows as needed
        Scanner scanner = new Scanner(System.in);
        boolean order = true;
        if(order){
            try {
                System.out.println("Select ID generation method:");
                System.out.println("1. Sequential numbers (1 to " + numberOfRows + ")");
                System.out.println("2. Random GUIDs");
                System.out.print("Enter choice (1 or 2): ");
                int idChoice = Integer.parseInt(scanner.nextLine());
    
                System.out.println("Enter the fields and their values. If you want to add multiple values, separate them with commas.");
                System.out.println("Enter 'done' when finished.");
    
                Map<String, List<String>> userFields = new LinkedHashMap<>();
                while (true) {
                    System.out.print("Enter field (or 'done' to finish): ");
                    String field = scanner.nextLine();
                    if ("done".equalsIgnoreCase(field)) {
                        break;
                    }
                    System.out.print("Enter values for field '" + field + "' (separate values with commas, or enter one value): ");
                    String valuesInput = scanner.nextLine();
                    List<String> values = Arrays.asList(valuesInput.split("\\s*,\\s*"));  // Split input by commas
                    userFields.put(field, values);
                }
    
                // Create a list of keys (field names)
                List<String> fieldNames = new ArrayList<>(userFields.keySet());
    
                // Create ObjectMapper instance
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
    
                // Generate the rows of data
                Random random = new Random();
                try (FileWriter file = new FileWriter("data.json")) {
                    file.write("["); // Start of the JSON array
    
                    for (int i = 1; i <= numberOfRows; i++) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id", generateId(idChoice, i));
    
                        // Add other fields
                        for (String field : fieldNames) {
                            List<String> values = userFields.get(field);
                            String selectedValue = values.size() > 1 ? values.get(random.nextInt(values.size())) : values.get(0);
                            row.put(field, selectedValue);
                        }
                        
                        // Convert row to JSON and append to the array
                        String jsonString = objectMapper.writeValueAsString(row);
                        file.write(jsonString);
    
                        if (i < numberOfRows) {
                            file.write(","); // Add a comma between entries except for the last one
                        }
                    }
    
                    file.write("]"); // End of the JSON array
                } catch (IOException e) {
                    System.out.println("Error writing to file: " + e.getMessage());
                }
    
                System.out.println("Successfully wrote JSON data to data.json");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for ID generation choice. Please enter 1 or 2.");
    
            } finally {
                // Ensure scanner is closed
                scanner.close();
            }
        }
        else{
            try{
                System.out.println("Select ID generation method:");
                System.out.println("1. Sequential numbers (1 to " + numberOfRows + ")");
                System.out.println("2. Random GUIDs");
                System.out.print("Enter choice (1 or 2): ");
                int idChoice = Integer.parseInt(scanner.nextLine());

                System.out.println("Enter the fields and their values. If you want to add multiple values, separate them with commas.");
                System.out.println("Enter 'done' when finished.");

                Map<String, List<String>> userFields = new LinkedHashMap<>();
                while (true) {
                    System.out.print("Enter field (or 'done' to finish): ");
                    String field = scanner.nextLine();
                    if ("done".equalsIgnoreCase(field)) {
                        break;
                    }
                    System.out.print("Enter values for field '" + field + "' (separate values with commas, or enter one value): ");
                    String valuesInput = scanner.nextLine();
                    List<String> values = Arrays.asList(valuesInput.split("\\s*,\\s*"));  // Split input by commas, trimming spaces
                    userFields.put(field, values);
                }

                // Create a list of keys (field names)
                List<String> fieldNames = new ArrayList<>(userFields.keySet());
                // Generate the rows of data
                JSONArray jsonArray = new JSONArray();

                // Generate remaining rows
                Random random = new Random();
                for (int i = 1; i <= numberOfRows; i++) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", generateId(idChoice, i));
                    for (String field : fieldNames) {
                        List<String> values = userFields.get(field);
                        // Randomly select a value if there are multiple options
                        String selectedValue = values.size() > 1 ? values.get(random.nextInt(values.size())) : values.get(0);
                        row.put(field, selectedValue);
                    } // Check the map before creating JSONObject

                    JSONObject jsonObject = new JSONObject(row);
                    jsonArray.put(jsonObject);

                    // Periodically write to file to save memory
                    if (i % 100_000 == 0) {
                        appendToFile(jsonArray, "data.json");
                        jsonArray = new JSONArray(); // Reset for the next batch
                    }
                }

                // Final append
                appendToFile(jsonArray, "data.json");
                System.out.println("Successfully wrote JSON data to data.json");
            } 
            finally {
                // Ensure scanner is closed
                scanner.close();
            }
        }
    }
    private static String generateId(int idChoice, int rowNumber) {
        if (idChoice == 1) {
            return String.valueOf(rowNumber); // Sequential ID (1 to numberOfRows)
        } else {
            return UUID.randomUUID().toString(); // Random GUID
        }
    }

    // Method to append to the file
    private static void appendToFile(JSONArray jsonArray, String filename) {
        try (FileWriter file = new FileWriter(filename, true)) {
            file.write(jsonArray.toString(2)); // Indent for readability
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
