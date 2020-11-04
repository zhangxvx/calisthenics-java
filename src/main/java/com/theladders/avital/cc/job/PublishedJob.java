package com.theladders.avital.cc.job;

import com.google.common.base.Objects;
import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.jobapplication.export.JobApplicationExporter;

public class PublishedJob {
    private final Job job;
    private final Employer employer;

    public PublishedJob(Job job, Employer employer) {
        this.job = job;
        this.employer = employer;
    }

    public boolean isMatched(String jobName, Employer employer) {
        return job.isMatched(jobName) && this.employer.equals(employer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishedJob that = (PublishedJob) o;
        return Objects.equal(job, that.job) &&
                Objects.equal(employer, that.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(job, employer);
    }

    public void accept(JobApplicationExporter exporter) {
        exporter.addCell(employer.toString());
        job.accept(exporter);
    }

    public boolean isMatched(String jobName) {
        return job.isMatched(jobName);
    }
}