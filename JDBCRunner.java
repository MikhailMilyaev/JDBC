package main.java;

import java.sql.*;
import java.util.Scanner;

public class JDBCRunner {

    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost/";

    private static final String DATABASE_NAME = "Constraint";

    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;
    public static final String USER_NAME = "postgres";
    public static final String DATABASE_PASS = "postgres";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JDBCRunner app = new JDBCRunner();


        while (true) {
            System.out.println("\nВведите команду:");
            System.out.println("1 - Просмотреть список студентов");
            System.out.println("2 - Просмотреть список курсов");
            System.out.println("3 - Просмотреть список оценок");
            System.out.println("4 - Добавить студента");
            System.out.println("5 - Удалить студента");
            System.out.println("6 - Добавить курс");
            System.out.println("7 - Удалить курс");
            System.out.println("8 - Поиск студента по имени");
            System.out.println("9 - Поиск курса по названию");
            System.out.println("10 - Поиск по оценке");
            System.out.println("11 - Сортировка по оценкам");
            System.out.println("12 - Выйти");
            System.out.print("Действие: ");

            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    app.getStudents();
                    break;
                case 2:
                    app.getCourses();
                    break;
                case 3:
                    app.getGrades();
                    break;
                case 4:
                    System.out.print("Введите имя студента: ");
                    String studentName = scanner.nextLine();
                    System.out.print("Введите email студента: ");
                    String studentEmail = scanner.nextLine();
                    app.addStudent(studentName, studentEmail);
                    break;
                case 5:
                    System.out.print("Введите ID студента для удаления: ");
                    int studentIdToDelete = scanner.nextInt();
                    scanner.nextLine();
                    app.deleteStudent(studentIdToDelete);
                    break;
                case 6:
                    System.out.print("Введите название курса: ");
                    String courseTitle = scanner.nextLine();
                    System.out.print("Введите описание курса: ");
                    String courseDescription = scanner.nextLine();
                    app.addCourse(courseTitle, courseDescription);
                    break;
                case 7:
                    System.out.print("Введите ID курса для удаления: ");
                    int courseIdToDelete = scanner.nextInt();
                    scanner.nextLine();
                    app.deleteCourse(courseIdToDelete);
                    break;
                case 8:
                    System.out.print("Введите имя студента для поиска: ");
                    String searchStudentName = scanner.nextLine();
                    app.searchStudentByName(searchStudentName);
                    break;
                case 9:
                    System.out.print("Введите название курса для поиска: ");
                    String searchCourseTitle = scanner.nextLine();
                    app.searchCourseByTitle(searchCourseTitle);
                    break;
                case 10:
                    System.out.print("Введите оценку для поиска: ");
                    int searchGrade = scanner.nextInt();
                    scanner.nextLine();
                    app.searchGrade(searchGrade);
                    break;
                case 11:
                    app.sortGrades();
                    break;
                case 12:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная команда.");
            }
        }
    }

    private Connection connect() {
        Connection connect = null;
        try {
            connect = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connect;
    }

    public void getStudents() {
        String sql = "SELECT * FROM Students";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок студентов:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Имя: " + rs.getString("name") +
                        ", Email: " + rs.getString("email"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getCourses() {
        String sql = "SELECT * FROM Courses";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок курсов:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Название: " + rs.getString("title") +
                        ", Описание: " + rs.getString("description"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getGrades() {
        String sql = "SELECT Students.name, Courses.title, Grades.grade " +
                "FROM Grades " +
                "JOIN Students ON Students.id = Grades.student_id " +
                "JOIN Courses ON Courses.id = Grades.course_id";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок оценок:");
            while (rs.next()) {
                System.out.println("Студент: " + rs.getString("name") +
                        ", Курс: " + rs.getString("title") +
                        ", Оценка: " + rs.getInt("grade"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addStudent(String name, String email) {
        String sql = "INSERT INTO Students (name, email) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("Студент добавлен.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteStudent(int studentId) {
        String sql = "DELETE FROM Students WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
            System.out.println("Студент с ID " + studentId + " удален.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addCourse(String title, String description) {
        String sql = "INSERT INTO Courses (title, description) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
            System.out.println("Курс добавлен.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCourse(int courseId) {
        String sql = "DELETE FROM Courses WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.executeUpdate();
            System.out.println("Курс с ID " + courseId + " удален.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchStudentByName(String studentName) {
        String sql = "SELECT * FROM Students WHERE name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nРезультаты поиска по имени студента:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Имя: " + rs.getString("name") +
                        ", Email: " + rs.getString("email"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchCourseByTitle(String courseTitle) {
        String sql = "SELECT * FROM Courses WHERE title = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseTitle); // Точное совпадение
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nРезультаты поиска по названию курса:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Название: " + rs.getString("title") +
                        ", Описание: " + rs.getString("description"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sortGrades() {
        String sql = "SELECT Students.name, Courses.title, Grades.grade " +
                "FROM Grades " +
                "JOIN Students ON Students.id = Grades.student_id " +
                "JOIN Courses ON Courses.id = Grades.course_id " +
                "ORDER BY Grades.grade DESC"; // Сортировка по убыванию оценки
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок оценок (отсортированный):");
            while (rs.next()) {
                System.out.println("Студент: " + rs.getString("name") +
                        ", Курс: " + rs.getString("title") +
                        ", Оценка: " + rs.getInt("grade"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchGrade(int grade) {
        String sql = "SELECT Students.name, Courses.title, Grades.grade " +
                "FROM Grades " +
                "JOIN Students ON Students.id = Grades.student_id " +
                "JOIN Courses ON Courses.id = Grades.course_id " +
                "WHERE Grades.grade = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grade);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nРезультаты поиска по оценке:");
            while (rs.next()) {
                System.out.println("Студент: " + rs.getString("name") +
                        ", Курс: " + rs.getString("title") +
                        ", Оценка: " + rs.getInt("grade"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}