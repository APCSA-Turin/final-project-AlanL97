package com.example;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CollegeList {
    private ArrayList<JSONObject> list;

    public CollegeList() {
        list = new ArrayList<JSONObject>();
    }

    public void addCollege(JSONObject college) {
        for (int i = 0; i < list.size(); i++) {
            if (college.getString("school.name").equals(list.get(i).getString("school.name"))) {
                System.out.println("This school has already been added");
                System.out.println("--------------------------------");
                return;
            }
        }
        list.add(college);
    }

    public void clear() {
        list = new ArrayList<JSONObject>();
    }

    public void removeCollege(int idx) {
        list.remove(idx);
    }

    public ArrayList<JSONObject> getList() {
        return list;
    }

    public void viewList() throws Exception {
        System.out.println("--------------------------------");
        sort();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getString("school.name") + ", Index: " + i);
        }
        int inState = 0;
        int outOfState = 0;
        int inStateSize = 0;
        int outOfStateSize = 0;
        for (int j = 0; j < list.size(); j++) {
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
        System.out.println("Average In State Tuititon: " + (double) inState/inStateSize);
        System.out.println("Average Out of State Tuition: " + (double) outOfState/outOfStateSize);
    }

    public void viewInfo(int idx) {
        System.out.println("--------------------------------");
        System.out.println("School Name: " + list.get(idx).getString("school.name"));
        System.out.println("City: " + list.get(idx).getString("school.city"));
        System.out.println("State: " + list.get(idx).getString("school.state"));
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

    public void saveData() {
        try (FileWriter writer = new FileWriter("output.txt")) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).write(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                text += line;
            }
            String[] arr = text.split("(?<=})");
            if (arr.length > 1) {
                for (int i = 0; i < arr.length; i++) {
                JSONObject obj = new JSONObject(arr[i]);
                list.add(obj);
            }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sort() {
        for (int i = 0; i < list.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
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
