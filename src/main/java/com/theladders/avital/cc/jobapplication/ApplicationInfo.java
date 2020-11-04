package com.theladders.avital.cc.jobapplication;

import com.google.common.base.Objects;
import com.theladders.avital.cc.jobapplication.export.JobApplicationExporter;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ApplicationInfo {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate applicationTime;
    private final JobSeeker jobSeeker;

    public ApplicationInfo(JobSeeker jobSeeker, LocalDate applicationTime) {
        this.jobSeeker = jobSeeker;
        this.applicationTime = applicationTime;
    }

    boolean isMatched(LocalDate date) {
        return applicationTime.equals(date);
    }

    boolean isAfter(LocalDate from) {
        return !from.isAfter(applicationTime);
    }

    boolean isBefore(LocalDate to) {
        return !to.isBefore(applicationTime);
    }

    public void accept(JobApplicationExporter exporter) {
        exporter.addCell(jobSeeker.toString());
        exporter.addCell(applicationTime.format(DATE_TIME_FORMATTER));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationInfo that = (ApplicationInfo) o;
        return Objects.equal(applicationTime, that.applicationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(applicationTime);
    }
}