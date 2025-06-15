package com.example;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CollegeList {
    //declares a private JSONObject ArrayList
    private ArrayList<JSONObject> list;

    //constructor, intializes list to an empty ArrayList
    public CollegeList() {
        list = new ArrayList<JSONObject>();
    }

    //add a JSONObject to list
    public void addCollege(JSONObject college) {
        //iterates through list
        for (int i = 0; i < list.size(); i++) {
            //if college is already in list, returns void
            if (college.getString("school.name").equals(list.get(i).getString("school.name"))) {
                System.out.println("This school has already been added");
                System.out.println("--------------------------------");
                return;
            }
        }
        //adds college to list
        list.add(college);
    }

    //sets list to a new ArrayList, clearing list
    public void clear() {
        list = new ArrayList<JSONObject>();
    }

    //removes a college at index idx
    public void removeCollege(int idx) {
        list.remove(idx);
    }

    //returns list
    public ArrayList<JSONObject> getList() {
        return list;
    }

    //returns the names of all colleges in list with an average of in state and out of state tuition to give the user an idea of how expensive their colleges are
    public void viewList() throws Exception {
        System.out.println("--------------------------------");
        //calls sort() to sort the list by alphabetical order
        sort();
        //iterates through list, printing the name of each school in list
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getString("school.name") + ", Index: " + i);
        }
        //declares and initializes integers for calculating the average of tuition
        int inState = 0;
        int outOfState = 0;
        int inStateSize = 0;
        int outOfStateSize = 0;
        //iterates through each element in list
        for (int j = 0; j < list.size(); j++) {
            //uses try catch because sometimes the tuition is null so try catch prevents an exception
            try {
                inState += list.get(j).getInt("latest.cost.tuition.in_state");
                inStateSize++;
            } catch (Exception e) {
            }
            try {
                outOfState += list.get(j).getInt("latest.cost.tuition.out_of_state");
                outOfStateSize++;
            } catch (Exception e) {
            }
        }
        //prints the average of in state and out of state tuition
        System.out.println("Average In State Tuititon: " + (double) inState/inStateSize);
        System.out.println("Average Out of State Tuition: " + (double) outOfState/outOfStateSize);
    }

    //prints information of a specific college in list at index idx
    public void viewInfo(int idx) {
        //prints information such as school name, city, state, admission rate, etc.
        System.out.println("--------------------------------");
        System.out.println("School Name: " + list.get(idx).getString("school.name"));
        System.out.println("City: " + list.get(idx).getString("school.city"));
        System.out.println("State: " + list.get(idx).getString("school.state"));
        //uses try catch becomes some information is null, try catch prevents an exception
        try {
            System.out.println("Admission Rate: " + list.get(idx).getFloat("latest.admissions.admission_rate.overall"));
        } catch (Exception e) {
            System.out.println("Admission Rate: null");
        }
        try {
            System.out.println("In State Tuition: " + list.get(idx).getInt("latest.cost.tuition.in_state"));
        } catch (Exception e) {
            System.out.println("In State Tuition: null");
        }
        try {
            System.out.println("Out of State Tuition: " + list.get(idx).getInt("latest.cost.tuition.out_of_state"));
        } catch (Exception e) {
            System.out.println("Out of State Tuition: null");
        }
        try {
        System.out.println("Average SAT Score: " + list.get(idx).getInt("latest.admissions.sat_scores.average.overall"));
        } catch (Exception e) {
            System.out.println("Average SAT Score: null");
        }
        try {
        System.out.println("Student Size: " + list.get(idx).getInt("latest.student.size"));
        } catch (Exception e) {
            System.out.println("Student Size: null");
        }
    }

    //saves the information in list to a text file
    public void saveData() {
        try (FileWriter writer = new FileWriter("output.txt")) {
            //iterates through list, saving each element in list to the text file output.txt in JSON text
            for (int i = 0; i < list.size(); i++) {
                list.get(i).write(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //loads the information from the text file output.txt
    public void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                //adds all the information to the String text
                text += line;
            }
            //splits text by the delimiter }, using positive lookbehind. learned from https://stackoverflow.com/questions/3481828/how-do-i-split-a-string-in-java
            String[] arr = text.split("(?<=})");
            //checks if arr contains a String with }
            if (arr[0].indexOf("}") > -1) {
                //iterates through each element in arr
                for (int i = 0; i < arr.length; i++) {
                    //declares and initializes a JSONObject with the String in arr at index i
                    JSONObject obj = new JSONObject(arr[i]);
                    //adds obj to list
                    list.add(obj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //sorts list by alphabetical order
    public void sort() {
        //uses selection sort to sort
        for (int i = 0; i < list.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                //uses compareTo to find with school is alphabetically higher
                if (list.get(j).getString("school.name").compareTo(list.get(maxIndex).getString("school.name")) < 0) {
                    maxIndex = j;
                }
            }
            JSONObject temp = list.get(i);
            list.set(i, list.get(maxIndex));
            list.set(maxIndex, temp);
        }
    }
}
