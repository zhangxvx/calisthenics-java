package com.theladders.avital.cc.jobapplication.export;

public interface JobApplicationExporter {
    void startRow();

    void endRow();

    void addCell(String value);

    String getContent();
}
