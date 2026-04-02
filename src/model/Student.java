// Author: Developer | Date: 2026-03-30 | ISTE 330

package model;

import java.util.List;

public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> keywords;

    public Student() {}

    public Student(int studentId, String firstName, String lastName, String email) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    @Override
    public String toString() { return firstName + " " + lastName; }
}
