package com.theladders.avital.cc;

import java.util.Objects;

public class Job {
    private final String name;
    private final JobType type;

    public Job(String name, JobType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public JobType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(name, job.name) &&
                type == job.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
