package com.theladders.avital.cc;

public class JobSeeker implements Comparable<JobSeeker>{
    private String name;

    public JobSeeker(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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

        return name != null ? name.equals(jobSeeker.name) : jobSeeker.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
