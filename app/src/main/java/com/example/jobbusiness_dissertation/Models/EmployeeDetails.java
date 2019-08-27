package com.example.jobbusiness_dissertation.Models;

public class EmployeeDetails {

    private String employee_username,employee_company,employee_email;

    public EmployeeDetails(){


    }
    public EmployeeDetails(String employee_username,String employee_company,String employee_email){
        this.employee_username = employee_username;
        this.employee_company = employee_company;
        this.employee_email = employee_email;

    }

    public String getEmployee_username() {
        return employee_username;
    }

    public void setEmployee_username(String employee_username) {
        this.employee_username = employee_username;
    }

    public String getEmployee_company() {
        return employee_company;
    }

    public void setEmployee_company(String employee_company) {
        this.employee_company = employee_company;
    }

    public String getEmployee_email() {
        return employee_email;
    }

    public void setEmployee_email(String employee_email) {
        this.employee_email = employee_email;
    }
}
