package com.theladders.avital.cc.resume;

import com.theladders.avital.cc.jobseeker.JobSeeker;

public class Resume {
    private final JobSeeker applicant;

    public Resume(JobSeeker applicant) {
        this.applicant = applicant;
    }

    public boolean isMatched(JobSeeker jobSeeker) {
        return applicant.equals(jobSeeker);
    }
}
