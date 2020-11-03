package com.theladders.avital.cc.job;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.util.ArrayList;
import java.util.List;

public class JobRepository {
    final Jobs<Employer> publishedJobs = new Jobs<>();
    final Jobs<JobSeeker> savedJobs = new Jobs<>();

    public void saveJob(Job job, JobSeeker jobSeeker) {
        savedJobs.saveJob(jobSeeker, job);
    }

    public void publishJob(Employer employer, Job job) {
        publishedJobs.saveJob(employer, job);
    }

    public List<Job> getJobs(Employer employer) {
        return new ArrayList<>(publishedJobs.getJobs(employer));
    }

    public List<Job> getJobs(JobSeeker jobSeeker) {
        return new ArrayList<>(savedJobs.getJobs(jobSeeker));
    }
}