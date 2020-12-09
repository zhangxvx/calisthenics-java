package com.theladders.avital.cc.job;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.util.ArrayList;
import java.util.List;

public class JobRepository {
    private final Jobs<Employer> publishedJobs = new Jobs<>();
    private final Jobs<JobSeeker> savedJobs = new Jobs<>();

    public List<Job> getJobs(Employer employer) {
        return new ArrayList<>(publishedJobs.getJobs(employer));
    }

    public ArrayList<Job> getJobs(JobSeeker jobSeeker) {
        return new ArrayList<>(savedJobs.getJobs(jobSeeker));
    }

    public void publishJob(Employer employer, Job job) {
        publishedJobs.saveJob(employer, job);
    }

    public void saveJob(Job job, JobSeeker jobSeeker) {
        savedJobs.saveJob(jobSeeker, job);
    }
}