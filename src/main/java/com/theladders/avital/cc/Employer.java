package com.theladders.avital.cc;

import java.util.Objects;

public class Employer {
    private final String name;

    public Employer(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return Objects.equals(name, employer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
