package com.theladders.avital.cc.jobapplication.export;

public class JobApplicationHtmlExporter implements JobApplicationExporter {
    private static final String HTML_HEADER = "<!DOCTYPE html><body><table><thead><tr><th>Employer</th><th>Job</th><th>Job Type</th><th>Applicants</th><th>Date</th></tr></thead><tbody>";
    private static final String HTML_END = "</tbody></table></body></html>";
    private String content = HTML_HEADER;

    @Override
    public void startRow() {
        content += "<tr>";
    }

    @Override
    public void endRow() {
        content += "</tr>";
    }

    @Override
    public void addCell(String value) {
        content += "<td>" + value + "</td>";
    }

    @Override
    public String getContent() {
        return content + HTML_END;
    }
}