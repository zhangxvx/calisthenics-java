package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();

    List<String> createJob(Job job) {
        return new ArrayList<String>() {{
            add(job.getName());
            add(job.getType().name());
        }};
    }

    public void saveJob(Employer employer, Job job) {
        List<List<String>> saved = jobs.getOrDefault(employer.getName(), new ArrayList<>());
        saved.add(createJob(job));
        jobs.put(employer.getName(), saved);
    }

    public void publishJob(Employer employer, Job job) {
        List<List<String>> alreadyPublished = jobs.getOrDefault(employer.getName(), new ArrayList<>());
        alreadyPublished.add(createJob(job));
        jobs.put(employer.getName(), alreadyPublished);
    }

    public List<List<String>> getJobs(String employerName) {
        return this.jobs.get(employerName);
    }
}