package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String HTML_HEADER = "<!DOCTYPE html>"
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
    public static final String HTML_END = "</tbody>"
            + "</table>"
            + "</body>"
            + "</html>";
    public static final String CSV_HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(Command command, final Employer employer, final Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (command == Command.publish) {
            publishJob(employer, job);
            return;
        }
        if (command == Command.save) {
            saveJob(employer, job);
            return;
        }
        if (command == Command.apply) {
            applyJob(employer, job, applicationTime, jobSeeker, resume);
            return;
        }
    }

    private void applyJob(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        validateResume(employer, job, applicationTime, jobSeeker, resume);
        List<List<String>> saved = this.applied.getOrDefault(jobSeeker.getName(), new ArrayList<>());
        saved.add(createJobApplication(employer, job, applicationTime));
        applied.put(jobSeeker.getName(), saved);
    }

    private ArrayList<String> createJobApplication(Employer employer, Job job, LocalDate applicationTime) {
        return new ArrayList<String>() {{
            add(job.getName());
            add(job.getType().name());
            add(applicationTime.format(DATE_TIME_FORMATTER));
            add(employer.getName());
        }};
    }

    private void validateResume(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getType() == JobType.JReq && resume.getApplicantName() == null) {
            failedApplications.add(createJobApplication(employer, job, applicationTime));
            throw new RequiresResumeForJReqJobException();
        }

        if (job.getType() == JobType.JReq && !resume.getApplicantName().equals(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
    }

    private void saveJob(Employer employer, Job job) {
        List<List<String>> saved = jobs.getOrDefault(employer.getName(), new ArrayList<>());
        saved.add(createJob(job));
        jobs.put(employer.getName(), saved);
    }

    private ArrayList<String> createJob(Job job) {
        return new ArrayList<String>() {{
            add(job.getName());
            add(job.getType().name());
        }};
    }

    private void publishJob(Employer employer, Job job) {
        List<List<String>> alreadyPublished = jobs.getOrDefault(employer.getName(), new ArrayList<>());
        alreadyPublished.add(createJob(job));
        jobs.put(employer.getName(), alreadyPublished);
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

    private List<String> findMatchedKeys(Predicate<List<String>> listPredicate) {
        return applied.entrySet().stream()
                .filter(set -> set.getValue().stream().anyMatch(listPredicate))
                .map(Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> findApplicationsByJobNameAndFromTime(String jobName, LocalDate from) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    private List<String> findApplicationsByJobNameAndToTime(String jobName, LocalDate to) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    private List<String> findApplicationsInTimeRange(LocalDate from, LocalDate to) {
        Predicate<List<String>> listPredicate = job -> !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER)) && !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    private List<String> findApplicationsByToTime(LocalDate to) {
        Predicate<List<String>> listPredicate = job ->
                !to.isBefore(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    private List<String> findApplicationsByFromTime(LocalDate from) {
        Predicate<List<String>> listPredicate = job ->
                !from.isAfter(LocalDate.parse(job.get(2), DATE_TIME_FORMATTER));
        return findMatchedKeys(listPredicate);
    }

    private List<String> findApplicationsByJobName(String jobName) {
        Predicate<List<String>> listPredicate = job -> job.get(0).equals(jobName);
        return findMatchedKeys(listPredicate);
    }

    public String export(LocalDate date, ExportType type) {
        if (type == ExportType.csv) {
            return exportCsv(date);
        }
        return exportHtml(date);
    }

    private String exportHtml(LocalDate date) {
        String content = "";
        for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
            String applicant = set.getKey();
            List<List<String>> jobs1 = set.getValue();
            List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DATE_TIME_FORMATTER))).collect(Collectors.toList());

            content = concatTableRow(content, applicant, appliedOnDate);
        }

        return HTML_HEADER + content + HTML_END;
    }

    private String concatTableRow(String content, String applicant, List<List<String>> appliedOnDate) {
        for (List<String> job : appliedOnDate) {
            content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
        }
        return content;
    }

    private String exportCsv(LocalDate date) {
        String result = CSV_HEADER;
        for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
            String applicant = set.getKey();
            List<List<String>> jobs1 = set.getValue();
            List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DATE_TIME_FORMATTER))).collect(Collectors.toList());

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

    public int getSuccessfulApplications(String jobName, Employer employer) {
        int result = 0;
        for (Entry<String, List<List<String>>> set : this.applied.entrySet()) {
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employer.getName()) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String jobName, Employer employer) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employer.getName())).count();
    }
}
