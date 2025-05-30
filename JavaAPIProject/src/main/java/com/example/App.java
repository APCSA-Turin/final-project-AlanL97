package com.example;
import org.json.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        CollegeList list = new CollegeList();
        Scanner scan = new Scanner(System.in);
        boolean bool = true;
        boolean bool2 = true;
        list.loadData();
        System.out.println("Welcome to the College List App\nYou can search for colleges, view information on colleges, and add them to a college list");
        while (bool) {
            System.out.println("--------------------------------");
            System.out.println("1. Search\n2. View List\n3. Exit");
            System.out.print("Enter an integer: ");
            int choice = scan.nextInt();
            scan.nextLine();
            if (choice == 1) {
                search(list);
            } else if (choice == 2) {
                bool2 = true;
                while (bool2) {
                    if (list.getList().size() == 0) {
                        System.out.println("Your list is empty");
                        break;
                    }
                    list.viewList();
                    System.out.println("--------------------------------");
                    System.out.println("1. View School Info\n2. Remove School\n3. Clear List\n4. Exit");
                    System.out.print("Enter an integer: ");
                    int viewChoice = scan.nextInt();
                    scan.nextLine();
                    if (viewChoice == 1) {
                        System.out.print("Choose a school you would like to view: ");
                        int idx = scan.nextInt();
                        scan.nextLine();
                        list.viewInfo(idx);
                    } else if (viewChoice == 2) {
                        System.out.print("Choose a school you would like to remove: ");
                        int idx2 = scan.nextInt();
                        scan.nextLine();
                        list.removeCollege(idx2);
                    } else if (viewChoice == 3) {
                        list.clear();
                    } else {
                        bool2 = false;
                    }
                }
            } else {
                bool = false;
                list.saveData();
            }
        }
    }

    public static String getAPI(String schoolName) throws Exception {
        /*endpoint is a url (string) that you get from an API website*/
        URL url = new URL("https://api.data.gov/ed/collegescorecard/v1/schools?api_key=xYr2BLdsmRvP3Z7kMGMHIvsdb6n7P4ROCXVIj5o0&school.name=" + schoolName + "&fields=school.name,school.city,school.state,latest.admissions.sat_scores.average.overall,latest.admissions.admission_rate.overall,latest.student.size,latest.cost.tuition.in_state,latest.cost.tuition.out_of_state");
        /*connect to the URL*/
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        /*creates a GET request to the API.. Asking the server to retrieve information for our program*/
        connection.setRequestMethod("GET");
        /* When you read data from the server, it wil be in bytes, the InputStreamReader will convert it to text. 
        The BufferedReader wraps the text in a buffer so we can read it line by line*/
        BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;//variable to store text, line by line
        /*A string builder is similar to a string object but faster for larger strings, 
        you can concatenate to it and build a larger string. Loop through the buffer 
        (read line by line). Add it to the stringbuilder */
        StringBuilder content = new StringBuilder();
        while ((inputLine = buff.readLine()) != null) {
            content.append(inputLine);
        }
        buff.close(); //close the bufferreader
        connection.disconnect(); //disconnect from server 
        return content.toString(); //return the content as a string
    }

    public static void getSchoolInfo(String schoolName, CollegeList list) throws Exception {
        Scanner scan = new Scanner(System.in);
        boolean bool = true;
        String jsonString = getAPI(schoolName);
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("results");
        JSONObject resultsObj = new JSONObject();
        System.out.println("--------------------------------");
        while (bool) {
            for (int i = 0; i < arr.length(); i++) {
                resultsObj = arr.getJSONObject(i);
                String name = resultsObj.getString("school.name");
                System.out.println(name + ", Index: " + i);
            } 
            if (arr.length() == 0) {
                System.out.println("Sorry, we could not find the school you're looking for");
                break;
            }
            System.out.print("Please choose a school to view (index): ");
            int idx = scan.nextInt();
            scan.nextLine();
            System.out.println("--------------------------------");
            resultsObj = arr.getJSONObject(idx);
            System.out.println("School Name: " + resultsObj.getString("school.name"));
            System.out.println("City: " + resultsObj.getString("school.city"));
            System.out.println("State: " + resultsObj.getString("school.state"));
            try {
                System.out.println("Admission Rate: " + resultsObj.getFloat("latest.admissions.admission_rate.overall"));
            } catch (Exception e) {
                System.out.println("Admission Rate: null");
            }
            try {
                System.out.println("In State Tuition: " + resultsObj.getInt("latest.cost.tuition.in_state"));
            } catch (Exception e) {
                System.out.println("In State Tuition: null");
            }
            try {
                System.out.println("Out of State Tuition: " + resultsObj.getInt("latest.cost.tuition.out_of_state"));
            } catch (Exception e) {
                System.out.println("Out of State Tuition: null");
            }
            try {
            System.out.println("Average SAT Score: " + resultsObj.getInt("latest.admissions.sat_scores.average.overall"));
            } catch (Exception e) {
                System.out.println("Average SAT Score: null");
            }
            try {
            System.out.println("Student Size: " + resultsObj.getInt("latest.student.size"));
            } catch (Exception e) {
                System.out.println("Student Size: null");
            }
            System.out.print("Would you like to add this college to your college list? (yes/no): ");
            String add = scan.nextLine();
            System.out.println("--------------------------------");
            if (add.equals("yes")) {
                list.addCollege(resultsObj);
            }
            System.out.println("1. Go Back to Previous Page\n2. Exit");
            System.out.print("Enter an integer: ");
            int choice = scan.nextInt();
            scan.nextLine();
            if (choice == 2) {
                bool = false;
            }
        }
    }

    public static void search(CollegeList list) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------");
        System.out.print("Please enter a school to search for (no spaces): ");
        String name = scanner.nextLine();
        getSchoolInfo(name, list);
    }
}
