import SchoolRe.School;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/SHDB";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "12345";

    private static JFrame frame;
    private static JTextArea outputTextArea;
    private static JTextField inputField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            createTableIfNotExists(connection);

            while (true) {
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        addStudent(connection);
                        break;
                    case 2:
                        viewStudents(connection);
                        break;
                    case 3:
                        viewMarks(connection);
                        break;
                    case 4:
                        viewDivision(connection);
                        break;
                    case 5:
                        viewName(connection);
                        break;
                    case 6:
                        viewAttendance(connection);
                        break;
                    case 7:
                        viewGrade(connection);
                        break;
                    case 8:
                        findStudentByGender(connection);
                        break;
                    case 9:
                        viewMinMarks(connection);
                        break;
                    case 10:
                        viewMaxMarks(connection);
                        break;
                    case 11:
                        deleteStudent(connection);
                        break;
                    case 12:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Student Database Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        JButton executeButton = new JButton("Execute");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSQL(inputField.getText());
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void executeSQL(String sql) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            StringBuilder result = new StringBuilder();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(metaData.getColumnName(i)).append(": ").append(resultSet.getString(i)).append(", ");
                }
                result.append("\n");
            }

            outputTextArea.setText(result.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getUserChoice() {
        String choiceString = JOptionPane.showInputDialog(
                "1. Add Student\n" +
                        "2. View Students\n" +
                        "3. View Marks\n" +
                        "4. View Division\n" +
                        "5. View Name\n" +
                        "6. View Attendance\n" +
                        "7. View Grade\n" +
                        "8. Find Student by Gender\n" +
                        "9. View Minimum Marks\n" +
                        "10. View Maximum Marks\n" +
                        "11. Delete Student\n" +
                        "12. Exit\n" +
                        "Enter your choice:"
        );

        if (choiceString == null) {
            System.exit(0);
        }

        try {
            return Integer.parseInt(choiceString);
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }

    private static void createTableIfNotExists(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS shri (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100)," +
                "age INT," +
                "department VARCHAR(50)," +
                "marks INT," +
                "gender VARCHAR(50)," +
                "attendance INT)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewMinMarks(Connection connection) {
        String selectSQL = "SELECT MIN(marks) AS min_marks FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            if (resultSet.next()) {
                int minMarks = resultSet.getInt("min_marks");
                System.out.println("Minimum Marks: " + minMarks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewMaxMarks(Connection connection) {
        String selectSQL = "SELECT MAX(marks) AS max_marks FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            if (resultSet.next()) {
                int maxMarks = resultSet.getInt("max_marks");
                System.out.println("Maximum Marks: " + maxMarks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewAttendance(Connection connection) {
        String selectSQL = "SELECT id, name, attendance FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int attendance = resultSet.getInt("attendance");
                System.out.println("ID: " + id + ", Name: " + name + ", Attendance: " + attendance + "%");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addStudent(Connection connection) {
        try {
            String name = JOptionPane.showInputDialog("Enter student name:");
            int age = Integer.parseInt(JOptionPane.showInputDialog("Enter student age:"));
            String department = JOptionPane.showInputDialog("Enter student department:");
            int marks = Integer.parseInt(JOptionPane.showInputDialog("Enter the marks of the student:"));
            String gender = JOptionPane.showInputDialog("Enter the gender:");
            int attendance = Integer.parseInt(JOptionPane.showInputDialog("Enter student attendance percentage:"));

            if (attendance < 50) {
                JOptionPane.showMessageDialog(null, "Student attendance is below 50%. Student will be detained.");
                return; // Do not proceed with adding the student
            }

            String insertSQL = "INSERT INTO shri (name, age, department, marks, gender, attendance) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, age);
                preparedStatement.setString(3, department);
                preparedStatement.setInt(4, marks);
                preparedStatement.setString(5, gender);
                preparedStatement.setInt(6, attendance);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Student added successfully.");

                    // Calculate and display the grade only if attendance is 50% or more
                    if (attendance >= 50) {
                        School student = new School();
                        student.setName(name);
                        student.setAge(age);
                        student.setDepartment(department);
                        student.setMarks(marks);
                        student.setGender(gender);

                        String grade = student.calculategrade();
                        JOptionPane.showMessageDialog(null, "Grade: " + grade);
                    } else {
                        JOptionPane.showMessageDialog(null, "Student will not be graded due to low attendance.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add student.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewStudents(Connection connection) {
        String selectSQL = "SELECT * FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                School student = new School();
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setAge(resultSet.getInt("age"));
                student.setDepartment(resultSet.getString("department"));
                student.setMarks(resultSet.getInt("marks"));

                System.out.println(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewMarks(Connection connection) {
        String selectSQL = "SELECT marks FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                int marks = resultSet.getInt("marks");
                System.out.println("Marks: " + marks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewDivision(Connection connection) {
        String selectSQL = "SELECT department FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String division = resultSet.getString("department");
                System.out.println("Division: " + division);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewName(Connection connection) {
        String selectSQL = "SELECT name FROM shri";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                System.out.println("Name: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewGrade(Connection connection) {
        try {
            int studentId = Integer.parseInt(JOptionPane.showInputDialog("Enter student ID to view grade:"));

            String selectSQL = "SELECT name, marks FROM shri WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setInt(1, studentId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int marks = resultSet.getInt("marks");

                        // Call the calculateGrade method to get the grade
                        String grade = calculateGrade(marks);

                        // Print the student information along with the calculated grade
                        JOptionPane.showMessageDialog(null, "ID: " + studentId + ", Name: " + name + ", Marks: " + marks + ", Grade: " + grade);
                    } else {
                        JOptionPane.showMessageDialog(null, "No student found with ID: " + studentId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void findStudentByGender(Connection connection) {
        String genderToSearch = JOptionPane.showInputDialog("Enter gender to search for:");
        String selectSQL = "SELECT * FROM shri WHERE gender = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, genderToSearch);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean found = false;
                while (resultSet.next()) {
                    if (!found) {
                        System.out.println("Matching Students:");
                        found = true;
                    }

                    School student = new School();
                    student.setId(resultSet.getInt("id"));
                    student.setName(resultSet.getString("name"));
                    student.setAge(resultSet.getInt("age"));
                    student.setDepartment(resultSet.getString("department"));
                    student.setMarks(resultSet.getInt("marks"));
                    student.setGender(resultSet.getString("gender"));

                    System.out.println(student);
                }

                if (!found) {
                    System.out.println("No students found with gender: " + genderToSearch);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteStudent(Connection connection) {
        try {
            int studentId = Integer.parseInt(JOptionPane.showInputDialog("Enter student ID to delete:"));

            String deleteSQL = "DELETE FROM shri WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setInt(1, studentId);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Student with ID " + studentId + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "No student found with ID " + studentId + ". Deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void displayResult(String result) {
        JFrame resultFrame = new JFrame("Result");
        JTextArea resultTextArea = new JTextArea(result);
        resultTextArea.setEditable(false);
        resultFrame.add(resultTextArea);
        resultFrame.setSize(400, 200);
        resultFrame.setLocationRelativeTo(frame);
        resultFrame.setVisible(true);
    }

    private static String calculateGrade(int marks) {
        if (marks >= 90) {
            return "A";
        } else if (marks >= 80) {
            return "B";
        } else if (marks >= 70) {
            return "C";
        } else if (marks >= 60) {
            return "D";
        } else if (marks >= 50) {
            return "E";
        } else {
            return "F"; // You can customize this part based on your grading scale
        }
    }
}
