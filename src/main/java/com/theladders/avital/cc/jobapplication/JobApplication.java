package com.theladders.avital.cc.jobapplication;

import com.google.common.base.Objects;
import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.PublishedJob;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class JobApplication {
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final LocalDate applicationTime;
    final PublishedJob publishedJob;

    public JobApplication(Job job, LocalDate applicationTime, Employer employer) {
        this.publishedJob = new PublishedJob(job, employer);
        this.applicationTime = applicationTime;
    }

    static Predicate<JobApplication> getPredicate(String jobName, Employer employer) {
        return application -> application.publishedJob.isMatched(jobName, employer);
    }

    public static Predicate<JobApplication> getPredicate(LocalDate date) {
        return jobApplication -> jobApplication.applicationTime.equals(date);
    }

    static Predicate<JobApplication> getPredicate(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobApplication> predicate = application -> true;
        if (jobName != null) {
            predicate = predicate.and(application -> application.publishedJob.isMatched(jobName));
        }
        if (from != null) {
            predicate = predicate.and(application -> !from.isAfter(application.applicationTime));
        }
        if (to != null) {
            predicate = predicate.and(application -> !to.isBefore(application.applicationTime));
        }
        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equal(applicationTime, that.applicationTime) &&
                Objects.equal(publishedJob, that.publishedJob);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(applicationTime, publishedJob);
    }
}