package com.theladders.avital.cc.jobapplication.export;

import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface JobApplicationExporter {
    void startRow();

    void endRow();

    void addCell(String value);

    String getContent();
}
