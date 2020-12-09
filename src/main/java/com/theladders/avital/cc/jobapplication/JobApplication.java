package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.PublishedJob;
import com.theladders.avital.cc.jobapplication.export.JobApplicationExporter;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;

public class JobApplication {
    private final PublishedJob publishedJob;
    private final ApplicationInfo applicationInfo;

    public JobApplication(Job job, LocalDate applicationTime, Employer employer) {
        this(job, applicationTime, employer, null);
    }

    public JobApplication(Job job, LocalDate applicationTime, Employer employer, JobSeeker jobSeeker) {
        this.publishedJob = new PublishedJob(job, employer);
        this.applicationInfo = new ApplicationInfo(jobSeeker, applicationTime);
    }

    public static Predicate<JobApplication> getPredicate(String jobName, Employer employer) {
        return application -> application.publishedJob.isMatched(jobName, employer);
    }

    public static Predicate<JobApplication> getPredicate(LocalDate date) {
        return application -> application.applicationInfo.isMatched(date);
    }

    public static Predicate<JobApplication> getPredicate(String jobName, LocalDate from, LocalDate to) {
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
        return Objects.equals(publishedJob, that.publishedJob) && Objects.equals(applicationInfo, that.applicationInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishedJob, applicationInfo);
    }
}
