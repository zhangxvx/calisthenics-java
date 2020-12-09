package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JobApplications {
    private final List<JobApplication> jobApplications = new ArrayList<>();

    public void add(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker) {
        jobApplications.add(new JobApplication(job, applicationTime, employer, jobSeeker));
    }

    public int getCount(String jobName, Employer employer) {
        Predicate<JobApplication> predicate = JobApplication.getPredicate(jobName, employer);
        return (int) jobApplications.stream().filter(predicate).count();
    }

    public boolean isMatched(String jobName, Employer employer) {
        Predicate<JobApplication> predicate = JobApplication.getPredicate(jobName, employer);
        return jobApplications.stream().anyMatch(predicate);
    }

    public boolean isMatched(Predicate<JobApplication> listPredicate) {
        return jobApplications.stream().anyMatch(listPredicate);
    }

    public List<JobApplication> getMatchedItems(LocalDate date) {
        Predicate<JobApplication> predicate = JobApplication.getPredicate(date);
        return jobApplications.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<JobApplication> toList() {
        return jobApplications;
    }
}