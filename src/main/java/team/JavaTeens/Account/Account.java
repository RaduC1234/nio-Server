package team.JavaTeens.Account;

import java.beans.ConstructorProperties;
import java.util.ArrayList;

public class Account {

    private final boolean admin;
    private final String firstName;
    private final String lastName;
    private final String[] middleName;
    private final Auth authentication;
    private final CalendarEvent dateEmployed;
    private final ArrayList<CalendarEvent> workFromHomeDays;
    private final ArrayList<CalendarEvent> absentDays;

    @ConstructorProperties({"admin", "firstName", "lastName" , "middleName", "auth", "employed", "workFromHomeDays" , "absentDays"})
    public Account(boolean admin, String firstName, String lastName, String[] middleName, Auth authentication, CalendarEvent dateEmployed, ArrayList<CalendarEvent> workFromHomeDays, ArrayList<CalendarEvent> absentDays) {

        this.admin = admin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.authentication = authentication;
        this.dateEmployed = dateEmployed;
        this.workFromHomeDays = workFromHomeDays;
        this.absentDays = absentDays;
    }

    public boolean isAdmin() {
        return admin;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String[] getMiddleName() {
        return middleName;
    }
    public Auth getAuthentication() {
        return authentication;
    }
    public CalendarEvent getDateEmployed() {
        return dateEmployed;
    }
    public ArrayList<CalendarEvent> getWorkFromHomeDays() {
        return workFromHomeDays;
    }
    public ArrayList<CalendarEvent> getAbsentDays() {
        return absentDays;
    }


    public static class Auth {

        private final String userName;
        private final String password;

        @ConstructorProperties({"username", "password"})
        public Auth(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }
}
