package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobApplicationCsvExporter implements JobApplicationExporter {
    private static final String CSV_HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";

    private String concatCsvRow(String result, JobSeeker jobSeeker, List<JobApplication> appliedOnDate) {
        for (JobApplication jobApplication : appliedOnDate) {
            result = result.concat(toCsvRow(jobApplication, jobSeeker));
        }
        return result;
    }

    String toCsvRow(JobApplication application, JobSeeker jobSeeker) {
        return application.publishedJob.toCsvCells() + "," + jobSeeker + "," +
                application.applicationTime.format(JobApplication.DATE_TIME_FORMATTER) + "\n";
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
