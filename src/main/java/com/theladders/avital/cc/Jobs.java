package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {
    private final HashMap<Employer, List<Job>> jobs = new HashMap<>();

    public void saveJob(Employer employer, Job job) {
        List<Job> saved = jobs.getOrDefault(employer, new ArrayList<>());
        saved.add(job);
        jobs.put(employer, saved);
    }

    public void publishJob(Employer employer, Job job) {
        saveJob(employer, job);
    }

    public List<Job> getJobs(Employer employer) {
        return jobs.get(employer);
    }
}