package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JobApplications {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final List<JobApplication> jobApplications = new ArrayList<>();

    void add(Employer employer, Job job, LocalDate applicationTime) {
        jobApplications.add(new JobApplication(job, applicationTime, employer));
    }

    int getCount(String jobName, Employer employer) {
        Predicate<JobApplication> predicate =
                application -> application.getJob().getName().equals(jobName) && application.getEmployer().equals(employer);
        return (int) jobApplications.stream().filter(predicate).count();
    }

    public boolean isMatched(String jobName, Employer employer) {
        Predicate<JobApplication> predicate =
                application -> application.getJob().getName().equals(jobName) && application.getEmployer().equals(employer);
        return jobApplications.stream().anyMatch(predicate);
    }

    public boolean isMatched(Predicate<JobApplication> listPredicate) {
        return jobApplications.stream().anyMatch(listPredicate);
    }

    public List<JobApplication> getMatchedItems(LocalDate date) {
        Predicate<JobApplication> predicate = job -> job.getApplicationTime().equals(date);
        return jobApplications.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<JobApplication> toList() {
        return jobApplications;
    }

    static Predicate<JobApplication> getPredicate(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return application -> application.getJob().getName().equals(jobName);
        }
        if (jobName == null && to == null) {
            return application -> !from.isAfter(application.getApplicationTime());
        }
        if (jobName == null && from == null) {
            return application -> !to.isBefore(application.getApplicationTime());
        }
        if (jobName == null) {
            return application -> !from.isAfter(application.getApplicationTime()) &&
                    !to.isBefore(application.getApplicationTime());
        }
        if (to != null) {
            return application -> application.getJob().getName().equals(jobName) &&
                    !to.isBefore(application.getApplicationTime());
        }
        return application -> application.getJob().getName().equals(jobName) &&
                !from.isAfter(application.getApplicationTime());
    }
}