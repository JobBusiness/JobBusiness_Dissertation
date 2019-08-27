package com.example.jobbusiness_dissertation.Models;

public class JobSeekerDetails {


   private String jobseeker_username;
   private String jobseeker_email;


   // For reading the value
    public JobSeekerDetails(){



    }
    //For initialise the object
    public JobSeekerDetails(String jobseeker_username, String jobseeker_email){
        this.jobseeker_username= jobseeker_username;
        this.jobseeker_email= jobseeker_email;

    }

    public String getJobseeker_username() {
        return jobseeker_username;
    }

    public void setJobseeker_username(String jobseeker_username) {
        this.jobseeker_username = jobseeker_username;
    }

    public String getJobseeker_email() {
        return jobseeker_email;
    }

    public void setJobseeker_email(String jobseeker_email) {
        this.jobseeker_email = jobseeker_email;
    }

    @Override
   public String toString() {
        return "JobSeekerDetails{" +
                "jobseeker_username='" + jobseeker_username + '\'' +
                '}';
    }
}
