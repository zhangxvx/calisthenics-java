package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs<T> {
    private final HashMap<T, List<Job>> jobs = new HashMap<>();

    public void saveJob(T employer, Job job) {
        List<Job> saved = jobs.getOrDefault(employer, new ArrayList<>());
        saved.add(job);
        jobs.put(employer, saved);
    }

    public List<Job> getJobs(T employer) {
        return jobs.get(employer);
    }
}