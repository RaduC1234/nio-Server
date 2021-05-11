package team.JavaTeens.Account;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.ConstructorProperties;
import java.util.ArrayList;

public class Account {

    private boolean admin = false;
    private String firstName = null;
    private String lastName = null;
    private String[] middleName = null;
    private Auth authentication = null;
    private CalendarEvent dateEmployed = null;
    private ArrayList<CalendarEvent> workFromHomeDays = null;
    private ArrayList<CalendarEvent> absentDays = null;

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

    public Account() {

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

    public Account setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }
    public Account setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public Account setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    public Account setMiddleName(String[] middleName) {
        this.middleName = middleName;
        return this;
    }
    public Account setAuthentication(Auth authentication) {
        this.authentication = authentication;
        return this;
    }
    public Account setDateEmployed(CalendarEvent dateEmployed) {
        this.dateEmployed = dateEmployed;
        return this;
    }
    public Account setWorkFromHomeDays(ArrayList<CalendarEvent> workFromHomeDays) {
        this.workFromHomeDays = workFromHomeDays;
        return this;
    }
    public Account setAbsentDays(ArrayList<CalendarEvent> absentDays) {
        this.absentDays = absentDays;
        return this;
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