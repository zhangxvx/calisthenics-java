package com.theladders.avital.cc;

public class Resume {
    private JobSeeker applicant;

    public Resume(JobSeeker applicant) {
        this.applicant = applicant;
    }

    boolean isMatched(JobSeeker jobSeeker) {
        if (applicant == null) {
            return false;
        }
        return applicant.equals(jobSeeker);
    }

    boolean isExists() {
        return applicant != null;
    }
}
