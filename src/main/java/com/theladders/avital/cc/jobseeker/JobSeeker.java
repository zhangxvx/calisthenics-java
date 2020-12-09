package com.theladders.avital.cc.jobseeker;

public class JobSeeker implements Comparable<JobSeeker> {
    private final String name;

    public JobSeeker(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(JobSeeker other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSeeker jobSeeker = (JobSeeker) o;

        return name.equals(jobSeeker.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
