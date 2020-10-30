package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.Objects;

public class JobApplication {
    private final Job job;
    private final LocalDate applicationTime;
    private final Employer employer;

    public JobApplication(Job job, LocalDate applicationTime, Employer employer) {
        this.job = job;
        this.applicationTime = applicationTime;
        this.employer = employer;
    }

    public Job getJob() {
        return job;
    }

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public Employer getEmployer() {
        return employer;
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
}
