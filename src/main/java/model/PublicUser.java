// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package model;

public class PublicUser {
    private int userId;
    private String organizationName;
    private String contactEmail;
    private String keyword;

    public PublicUser() {}

    public PublicUser(int userId, String organizationName, String contactEmail, String keyword) {
        this.userId = userId;
        this.organizationName = organizationName;
        this.contactEmail = contactEmail;
        this.keyword = keyword;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String o) { this.organizationName = o; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String e) { this.contactEmail = e; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    @Override
    public String toString() { return organizationName; }
}
