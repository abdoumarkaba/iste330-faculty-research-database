// Author: Developer | Date: 2026-03-30 | ISTE 330

package model;

import java.util.List;

public class Faculty {
    private int facultyId;
    private String firstName;
    private String lastName;
    private String building;
    private String officeNumber;
    private String email;
    private List<String> keywords;

    public Faculty() {}

    public Faculty(int facultyId, String firstName, String lastName,
                   String building, String officeNumber, String email) {
        this.facultyId = facultyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.building = building;
        this.officeNumber = officeNumber;
        this.email = email;
    }

    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    @Override
    public String toString() { return firstName + " " + lastName; }
}
