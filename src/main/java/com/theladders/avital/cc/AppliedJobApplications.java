package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AppliedJobApplications {
    private static final String HTML_HEADER = "<!DOCTYPE html>"
            + "<body>"
            + "<table>"
            + "<thead>"
            + "<tr>"
            + "<th>Employer</th>"
            + "<th>Job</th>"
            + "<th>Job Type</th>"
            + "<th>Applicants</th>"
            + "<th>Date</th>"
            + "</tr>"
            + "</thead>"
            + "<tbody>";
    private static final String HTML_END = "</tbody>"
            + "</table>"
            + "</body>"
            + "</html>";
    private static final String CSV_HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";
    private final LinkedHashMap<JobSeeker, JobApplications> jobApplications = new LinkedHashMap<>();

    public void add(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker) {
        JobApplications saved = jobApplications.getOrDefault(jobSeeker, new JobApplications());
        saved.add(employer, job, applicationTime);
        jobApplications.put(jobSeeker, saved);
    }

    public List<JobApplication> getJobApplications(JobSeeker jobSeeker) {
        JobApplications jobApplications = this.jobApplications.get(jobSeeker);
        return jobApplications.toList();
    }

    public String exportHtml(LocalDate date) {
        String content = "";
        for (Map.Entry<JobSeeker, JobApplications> set : jobApplications.entrySet()) {
            JobSeeker applicant = set.getKey();
            List<JobApplication> appliedOnDate = set.getValue().getMatchedItems(date);
            content = concatTableRow(content, applicant, appliedOnDate);
        }

        return HTML_HEADER + content + HTML_END;
    }

    public String exportCsv(LocalDate date) {
        String result = CSV_HEADER;
        for (Map.Entry<JobSeeker, JobApplications> set : jobApplications.entrySet()) {
            JobSeeker applicant = set.getKey();
            List<JobApplication> appliedOnDate = set.getValue().getMatchedItems(date);
            result = concatCsvRow(result, applicant, appliedOnDate);
        }
        return result;
    }

    private String concatTableRow(String content, JobSeeker applicant, List<JobApplication> appliedOnDate) {
        for (JobApplication job : appliedOnDate) {
            content = content.concat("<tr>" + "<td>" + job.getEmployer().getName() + "</td>" +
                    "<td>" + job.getJob().getName() + "</td>" + "<td>" + job.getJob().getType() + "</td>" +
                    "<td>" + applicant.getName() + "</td>" + "<td>" + job.getApplicationTime().format(JobApplications.DATE_TIME_FORMATTER) + "</td>" +
                    "</tr>");
        }
        return content;
    }

    private String concatCsvRow(String result, JobSeeker applicant, List<JobApplication> appliedOnDate) {
        for (JobApplication job : appliedOnDate) {
            result = result.concat(job.getEmployer().getName() + "," +
                    job.getJob().getName() + "," + job.getJob().getType() + "," +
                    applicant.getName() + "," + job.getApplicationTime().format(JobApplications.DATE_TIME_FORMATTER) +
                    "\n");
        }
        return result;
    }

    public int getCount(String jobName, Employer employer) {
        return (int) jobApplications.entrySet().stream()
                .filter(set -> set.getValue().isMatched(jobName, employer))
                .count();
    }

    List<JobSeeker> find(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobApplication> listPredicate = JobApplications.getPredicate(jobName, from, to);
        return jobApplications.entrySet().stream()
                .filter(set -> set.getValue().isMatched(listPredicate))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

}