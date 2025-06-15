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
    //Main entry point for the program
    public static void main(String[] args) throws Exception
    {
        //declares and initializes a CollegeList object
        CollegeList list = new CollegeList();
        Scanner scan = new Scanner(System.in);
        boolean bool = true;
        boolean bool2 = true;
        list.loadData();
        System.out.println("Welcome to the College List App\nYou can search for colleges, view information on colleges, and add them to a college list");
        //while loop so the program continuously runs unless the user enters 3 to exit
        while (bool) {
            //prints a line asking for the user to input an integer
            System.out.println("--------------------------------");
            System.out.println("1. Search\n2. View List\n3. Exit");
            System.out.print("Enter an integer: ");
            //takes in user input in the form of an integer
            int choice = scan.nextInt();
            scan.nextLine();
            //if the user inputs 1, calls search()
            if (choice == 1) {
                search(list);
            } else if (choice == 2) {
                bool2 = true;
                //while loop so the user is always on the college list menu unless the user enters 4 to exit
                while (bool2) {
                    //if the list is empty, prints a line telling the user that their list is empty and breaks, returning the user to the main menu
                    if (list.getList().size() == 0) {
                        System.out.println("Your list is empty");
                        break;
                    }
                    //calls viewList() on list
                    list.viewList();
                    System.out.println("--------------------------------");
                    System.out.println("1. View School Info\n2. Remove School\n3. Clear List\n4. Exit");
                    System.out.print("Enter an integer: ");
                    //takes in user input in the form of an integer
                    int viewChoice = scan.nextInt();
                    scan.nextLine();
                    //if the user inputs 1, asks the user to choose a school in their list to view
                    if (viewChoice == 1) {
                        System.out.print("Choose a school you would like to view: ");
                        int idx = scan.nextInt();
                        scan.nextLine();
                        //calls viewInfo on list with the user's input as the index to view a school's info
                        list.viewInfo(idx);
                    //if the user inputs 2, asks the user to choose a school in their list to remove
                    } else if (viewChoice == 2) {
                        System.out.print("Choose a school you would like to remove: ");
                        int idx2 = scan.nextInt();
                        scan.nextLine();
                        //calls removeCollege() on list with the user's input as the index to remove a college
                        list.removeCollege(idx2);
                    //if the user inputs 3, calls clear() on list
                    } else if (viewChoice == 3) {
                        list.clear();
                    //if the user inputs 4, bool2 is set to false, stopping the while loop and returning the user to the main menu
                    } else {
                        bool2 = false;
                    }
                }
            //if the user inputs 3, bool is set to false and saveData() is called on list. The while loop stops and the program ends
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

    //prints a list of school names from a search and allows the user to view information on a specific college from the search result
    public static void getSchoolInfo(String schoolName, CollegeList list) throws Exception {
        Scanner scan = new Scanner(System.in);
        boolean bool = true;
        //declares and initializes a String with the string returned from calling getAPI()
        String jsonString = getAPI(schoolName);
        //declares and initializes a JSONObject with the String jsonString
        JSONObject obj = new JSONObject(jsonString);
        //declares and initializes a JSONArray of the JSONObject obj with the key "results"
        JSONArray arr = obj.getJSONArray("results");
        //declares and initializes a new JSONObject
        JSONObject resultsObj = new JSONObject();
        System.out.println("--------------------------------");
        //while loop so the user can view multiple schools from one search result
        while (bool) {
            //iterates through every element in arr
            for (int i = 0; i < arr.length(); i++) {
                //sets resultsObj to the JSONObject in arr at index i
                resultsObj = arr.getJSONObject(i);
                //sets a String to the String in resultsObj with the key "school.name"
                String name = resultsObj.getString("school.name");
                //prints the name of the school at index i with the index
                System.out.println(name + ", Index: " + i);
            } 
            //if arr is empty, prints a line telling the user that their school could not be found and break, ending the for loop and the method
            if (arr.length() == 0) {
                System.out.println("Sorry, we could not find the school you're looking for");
                break;
            }
            System.out.print("Please choose a school to view (index): ");
            //takes in user input in the form of an integer
            int idx = scan.nextInt();
            scan.nextLine();
            System.out.println("--------------------------------");
            //sets resultsObj to the JSONObject in arr at index idx, the user's input
            resultsObj = arr.getJSONObject(idx);
            //prints school information such as school name, city, state, admission rate, etc. by calling resultsObj.getString() with the corresponding key
            System.out.println("School Name: " + resultsObj.getString("school.name"));
            System.out.println("City: " + resultsObj.getString("school.city"));
            System.out.println("State: " + resultsObj.getString("school.state"));
            //uses try catch because sometimes a college's info is null, so a try catch prevents an Exception
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
            //prints a line asking if the user would like to add this college to their list
            System.out.print("Would you like to add this college to your college list? (yes/no): ");
            //takes in user input in the form of a String
            String add = scan.nextLine();
            System.out.println("--------------------------------");
            //if the user inputs yes, calls addCollege() on list with resultsObj as the argument
            if (add.equals("yes")) {
                list.addCollege(resultsObj);
            }
            //prints a line asking the user if they want to go back to the search page
            System.out.println("1. Go Back to Previous Page\n2. Exit");
            System.out.print("Enter an integer: ");
            int choice = scan.nextInt();
            scan.nextLine();
            //if the user inputs 2, bool is set to false, stopping the while loop and ending the method
            if (choice == 2) {
                bool = false;
            }
        }
    }

    //allows the user to search for colleges by taking in an input from the user in form of a String
    public static void search(CollegeList list) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------");
        System.out.print("Please enter a school to search for (no spaces): ");
        String name = scanner.nextLine();
        //calls getSchoolInfo() with the user's input as an argument
        getSchoolInfo(name, list);
    }
}
