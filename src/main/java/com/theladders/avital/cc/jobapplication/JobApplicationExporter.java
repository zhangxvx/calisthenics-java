package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface JobApplicationExporter {
    String export(LocalDate date, Set<Map.Entry<JobSeeker, JobApplications>> entries);
}
