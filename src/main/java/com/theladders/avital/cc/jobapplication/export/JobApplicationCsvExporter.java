package com.theladders.avital.cc.jobapplication.export;

public class JobApplicationCsvExporter implements JobApplicationExporter {
    private static final String CSV_HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";
    private String content = CSV_HEADER;

    @Override
    public void startRow() {
    }

    @Override
    public void endRow() {
        content = content.replaceAll(",$", "\n");
    }

    @Override
    public void addCell(String value) {
        content += value + ",";
    }

    @Override
    public String getContent() {
        return content;
    }
}
