package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobApplicationCsvExporter implements JobApplicationExporter {
    private static final String CSV_HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";

    private String concatCsvRow(String result, JobSeeker jobSeeker, List<JobApplication> appliedOnDate) {
        for (JobApplication job : appliedOnDate) {
            result = result.concat(job.toCsvRow(jobSeeker));
        }
        return result;
    }

    @Override
    public String export(LocalDate date, Set<Map.Entry<JobSeeker, JobApplications>> entries) {
        String result = CSV_HEADER;
        for (Map.Entry<JobSeeker, JobApplications> set : entries) {
            JobSeeker applicant = set.getKey();
            List<JobApplication> appliedOnDate = set.getValue().getMatchedItems(date);
            result = concatCsvRow(result, applicant, appliedOnDate);
        }
        return result;
    }
}
