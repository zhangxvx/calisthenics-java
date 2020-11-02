package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JobApplications {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final List<List<String>> jobApplications = new ArrayList<>();

    void add(Employer employer, Job job, LocalDate applicationTime) {
        jobApplications.add(createJobApplication(employer, job, applicationTime));
    }

    int getCount(String jobName, Employer employer) {
        return (int) jobApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employer.getName())).count();
    }

    public boolean isMatched(Predicate<List<String>> listPredicate) {
        return jobApplications.stream().anyMatch(listPredicate);
    }

    public List<List<String>> getMatchedItems(LocalDate date) {
        return jobApplications.stream().filter(job -> job.get(2).equals(date.format(DATE_TIME_FORMATTER))).collect(Collectors.toList());
    }

    public int getMatchedCount(String jobName, Employer employer) {
        return jobApplications.stream().anyMatch(job -> job.get(3).equals(employer.getName()) && job.get(0).equals(jobName)) ? 1 : 0;
    }

    public ArrayList<String> createJobApplication(Employer employer, Job job, LocalDate applicationTime) {
        return new ArrayList<String>() {{
            add(job.getName());
            add(job.getType().name());
            add(applicationTime.format(DATE_TIME_FORMATTER));
            add(employer.getName());
        }};
    }

    public List<List<String>> toList() {
        return jobApplications;
    }
}