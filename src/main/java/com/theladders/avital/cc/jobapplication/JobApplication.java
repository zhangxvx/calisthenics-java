package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Predicate;

public class JobApplication {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Job job;
    private final LocalDate applicationTime;
    private final Employer employer;

    public JobApplication(Job job, LocalDate applicationTime, Employer employer) {
        this.job = job;
        this.applicationTime = applicationTime;
        this.employer = employer;
    }

    static Predicate<JobApplication> getPredicate(String jobName, Employer employer) {
        return application -> application.job.isMatched(jobName) && application.employer.equals(employer);
    }

    public static Predicate<JobApplication> getPredicate(LocalDate date) {
        return jobApplication -> jobApplication.applicationTime.equals(date);
    }

    static Predicate<JobApplication> getPredicate(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return application -> application.job.isMatched(jobName);
        }
        if (jobName == null && to == null) {
            return application -> !from.isAfter(application.applicationTime);
        }
        if (jobName == null && from == null) {
            return application -> !to.isBefore(application.applicationTime);
        }
        if (jobName == null) {
            return application -> !from.isAfter(application.applicationTime) &&
                    !to.isBefore(application.applicationTime);
        }
        if (to != null) {
            return application -> application.job.isMatched(jobName) &&
                    !to.isBefore(application.applicationTime);
        }
        return application -> application.job.isMatched(jobName) &&
                !from.isAfter(application.applicationTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equals(job, that.job) &&
                Objects.equals(applicationTime, that.applicationTime) &&
                Objects.equals(employer, that.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(job, applicationTime, employer);
    }

    String toTableRow(JobSeeker jobSeeker) {
        return "<tr>" + "<td>" + employer + "</td>" +
                job.toTaleCells() +
                "<td>" + jobSeeker + "</td>" +
                "<td>" + applicationTime.format(DATE_TIME_FORMATTER) + "</td>" +
                "</tr>";
    }

    String toCsvRow(JobSeeker jobSeeker) {
        return employer + "," +
                job.toCsvCells() +
                jobSeeker + "," +
                applicationTime.format(DATE_TIME_FORMATTER) +
                "\n";
    }
}
