package com.theladders.avital.cc.resurme;

import com.theladders.avital.cc.jobseeker.JobSeeker;

public class Resume {
    private final JobSeeker applicant;

    public Resume(JobSeeker applicant) {
        this.applicant = applicant;
    }

    public boolean isMatched(JobSeeker jobSeeker) {
        if (applicant == null) {
            return false;
        }
        return applicant.equals(jobSeeker);
    }

    public boolean isExists() {
        return applicant != null;
    }
}
