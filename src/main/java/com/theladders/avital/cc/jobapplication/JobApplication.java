package com.theladders.avital.cc.jobapplication;

import com.google.common.base.Objects;
import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.PublishedJob;
import com.theladders.avital.cc.jobapplication.export.JobApplicationExporter;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.function.Predicate;

public class JobApplication {
    private final PublishedJob publishedJob;
    private final ApplicationInfo applicationInfo;

    public JobApplication(Job job, LocalDate applicationTime, Employer employer) {
        this(job, applicationTime, employer, null);
    }

    public JobApplication(Job job, LocalDate applicationTime, Employer employer, JobSeeker jobSeeker) {
        this.applicationInfo = new ApplicationInfo(jobSeeker, applicationTime);
        this.publishedJob = new PublishedJob(job, employer);
    }

    static Predicate<JobApplication> getPredicate(String jobName, Employer employer) {
        return application -> application.publishedJob.isMatched(jobName, employer);
    }

    public static Predicate<JobApplication> getPredicate(LocalDate date) {
        return jobApplication -> jobApplication.applicationInfo.isMatched(date);
    }

    static Predicate<JobApplication> getPredicate(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobApplication> predicate = application -> true;
        if (jobName != null) {
            predicate = predicate.and(application -> application.publishedJob.isMatched(jobName));
        }
        if (from != null) {
            predicate = predicate.and(application -> application.applicationInfo.isAfter(from));
        }
        if (to != null) {
            predicate = predicate.and(application -> application.applicationInfo.isBefore(to));
        }
        return predicate;
    }

    public void accept(JobApplicationExporter exporter) {
        exporter.startRow();
        publishedJob.accept(exporter);
        applicationInfo.accept(exporter);
        exporter.endRow();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equal(publishedJob, that.publishedJob) &&
                Objects.equal(applicationInfo, that.applicationInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publishedJob, applicationInfo);
    }
}