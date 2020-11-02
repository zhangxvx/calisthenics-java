package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.HashMap;
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
    private final HashMap<String, JobApplications> jobApplications = new HashMap<>();

    public void add(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker) {
        JobApplications saved = jobApplications.getOrDefault(jobSeeker.getName(), new JobApplications());
        saved.add(employer, job, applicationTime);
        jobApplications.put(jobSeeker.getName(), saved);
    }

    public List<List<String>> getJobApplications(String employerName) {
        JobApplications jobApplications = this.jobApplications.get(employerName);
        return jobApplications.toList();
    }

    public List<String> findMatchedKeys(Predicate<List<String>> listPredicate) {
        return jobApplications.entrySet().stream()
                .filter(set -> set.getValue().isMatched(listPredicate))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }


    private String concatTableRow(String content, String applicant, List<List<String>> appliedOnDate) {
        for (List<String> job : appliedOnDate) {
            content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
        }
        return content;
    }

    public String exportHtml(LocalDate date) {
        String content = "";
        for (Map.Entry<String, JobApplications> set : jobApplications.entrySet()) {
            String applicant = set.getKey();
            List<List<String>> appliedOnDate = set.getValue().getMatchedItems(date);
            content = concatTableRow(content, applicant, appliedOnDate);
        }

        return HTML_HEADER + content + HTML_END;
    }

    public String exportCsv(LocalDate date) {
        String result = CSV_HEADER;
        for (Map.Entry<String, JobApplications> set : jobApplications.entrySet()) {
            String applicant = set.getKey();
            List<List<String>> appliedOnDate = set.getValue().getMatchedItems(date);
            result = concatCsvRow(result, applicant, appliedOnDate);
        }
        return result;
    }

    private String concatCsvRow(String result, String applicant, List<List<String>> appliedOnDate) {
        for (List<String> job : appliedOnDate) {
            result = result.concat(job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + applicant + "," + job.get(2) + "\n");
        }
        return result;
    }

    public int getCount(String jobName, Employer employer) {
        int result = 0;
        for (Map.Entry<String, JobApplications> set : jobApplications.entrySet()) {
            JobApplications jobs = set.getValue();

            result += jobs.getMatchedCount(jobName, employer);
        }
        return result;
    }

    List<String> findApplicationsByJobNameAndFromTime(String jobName, LocalDate from) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    List<String> findApplicationsByJobNameAndToTime(String jobName, LocalDate to) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    List<String> findApplicationsInTimeRange(LocalDate from, LocalDate to) {
        Predicate<List<String>> listPredicate = job -> !from.isAfter(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER)) && !to.isBefore(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    List<String> findApplicationsByToTime(LocalDate to) {
        Predicate<List<String>> listPredicate = job ->
                !to.isBefore(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    List<String> findApplicationsByFromTime(LocalDate from) {
        Predicate<List<String>> listPredicate = job ->
                !from.isAfter(LocalDate.parse(job.get(2), JobApplications.DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    List<String> findApplicationsByJobName(String jobName) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName);
        return findMatchedKeys(listPredicate);
    }

    List<String> find(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return findApplicationsByJobName(jobName);
        }
        if (jobName == null && to == null) {
            return findApplicationsByFromTime(from);
        }
        if (jobName == null && from == null) {
            return findApplicationsByToTime(to);
        }
        if (jobName == null) {
            return findApplicationsInTimeRange(from, to);
        }
        if (to != null) {
            return findApplicationsByJobNameAndToTime(jobName, to);
        }
        return findApplicationsByJobNameAndFromTime(jobName, from);
    }
}