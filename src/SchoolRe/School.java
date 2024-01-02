package SchoolRe;

import java.sql.*;
import java.util.Scanner;

public class School {
    private int bonusmakrs;
    private int id;
    private  int marks;


    public int getDiv() {
        return Div;
    }

    private String gender;
    private int Div;

    public School(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public School(int marks) {
        this.marks = marks;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public School() {
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
    }

    private String name;
    private int age;
    private String department;

    // Constructors, getters, and setters

    public int getId() {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public String calculategrade()
    {
        if(marks>=90)
        {
            return "A+";

        } else if (marks>=80) {
            return "A";

        } else if (marks>=70) {
            return "B";

        } else if (marks>=60) {
            return "c";
        } else if (marks>=50) {
            return "d";
        }
        else {
            return "f";
        }

    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age + ", Department: " + department+",Marks:"+marks;
    }
}
