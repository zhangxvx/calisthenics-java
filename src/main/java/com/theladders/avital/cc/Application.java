package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String employerName, String jobName, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime, Command command, final JobType jobType) throws RequiresResumeForJReqJobException, InvalidResumeException {
        switch (command) {
            case publish:
                List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

                alreadyPublished.add(new ArrayList<String>() {{
                    add(jobName);
                    add(jobType.name());
                }});
                jobs.put(employerName, alreadyPublished);
                break;
            case save: {
                List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());

                saved.add(new ArrayList<String>() {{
                    add(jobName);
                    add(jobType.name());
                }});
                jobs.put(employerName, saved);
                break;
            }
            case apply: {
                if (jobType == JobType.JReq && resumeApplicantName == null) {
                    List<String> failedApplication = new ArrayList<String>() {{
                        add(jobName);
                        add(jobType.name());
                        add(applicationTime.format(DATE_TIME_FORMATTER));
                        add(employerName);
                    }};
                    failedApplications.add(failedApplication);
                    throw new RequiresResumeForJReqJobException();
                }

                if (jobType == JobType.JReq && !resumeApplicantName.equals(jobSeekerName)) {
                    throw new InvalidResumeException();
                }
                List<List<String>> saved = this.applied.getOrDefault(jobSeekerName, new ArrayList<>());

                saved.add(new ArrayList<String>() {{
                    add(jobName);
                    add(jobType.name());
                    add(applicationTime.format(DATE_TIME_FORMATTER));
                    add(employerName);
                }});
                applied.put(jobSeekerName, saved);
                break;
            }
        }
    }

    public List<List<String>> getJobs(String employerName, GettingJobsType type) {
        if (type == GettingJobsType.applied) {
            return applied.get(employerName);
        }

        return jobs.get(employerName);
    }

    public List<String> findApplicants(String jobName) {
        return findApplicants(jobName, null);
    }

    public List<String> findApplicants(String jobName, LocalDate from) {
        return findApplicants(jobName, from, null);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.get(0).equals(jobName));
                if (hasAppliedToThisJob) {
                    result.add(applicant);
                }
            }
            return result;
        } else if (jobName == null && to == null) {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        } else if (jobName == null && from == null) {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        } else if (jobName == null) {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)) && !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        } else if (to != null) {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        } else {
            List<String> result = new ArrayList<String>() {};
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        }
    }

    public String export(LocalDate date, ExportType type) {
        if (type == ExportType.csv) {
            String result = "Employer,Job,Job Type,Applicants,Date" + "\n";
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DATE_TIME_FORMATTER))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    result = result.concat(job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + applicant + "," + job.get(2) + "\n");
                }
            }
            return result;
        } else {
            String content = "";
            for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DATE_TIME_FORMATTER))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
                }
            }

            return "<!DOCTYPE html>"
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
                    + "<tbody>"
                    + content
                    + "</tbody>"
                    + "</table>"
                    + "</body>"
                    + "</html>";
        }
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}
