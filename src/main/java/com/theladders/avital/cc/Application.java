package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {

    private final Jobs<Employer> publishedJobs = new Jobs<>();
    private final Jobs<JobSeeker> savedJobs = new Jobs<>();
    private final AppliedJobApplications appliedJobApplications = new AppliedJobApplications();
    private final JobApplications jobApplications = new JobApplications();

    public void saveJob(Job job, JobSeeker jobSeeker) {
        savedJobs.saveJob(jobSeeker, job);
    }

    public void publishJob(Employer employer, Job job) {
        publishedJobs.saveJob(employer, job);
    }

    public void applyJob(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        validateResume(employer, job, applicationTime, jobSeeker, resume);
        appliedJobApplications.add(employer, job, applicationTime, jobSeeker);
    }

    private void validateResume(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.isJReq() && !resume.isExists()) {
            jobApplications.add(employer, job, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }

        if (job.isJReq() && !resume.isMatched(jobSeeker)) {
            throw new InvalidResumeException();
        }
    }

    public List<JobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedJobApplications.getJobApplications(jobSeeker);
    }

    public List<Job> getJobs(Employer employer) {
        return new ArrayList<>(publishedJobs.getJobs(employer));
    }

    public List<Job> getJobs(JobSeeker jobSeeker) {
        return new ArrayList<>(savedJobs.getJobs(jobSeeker));
    }

    public List<JobSeeker> findApplicants(String jobName) {
        return appliedJobApplications.find(jobName, null, null);
    }

    public List<JobSeeker> findApplicants(String jobName, LocalDate from) {
        return appliedJobApplications.find(jobName, from, null);
    }

    public List<JobSeeker> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return appliedJobApplications.find(jobName, from, to);
    }

    public String export(LocalDate date, ExportType type) {
        if (type == ExportType.csv) {
            return appliedJobApplications.exportCsv(date);
        }
        return appliedJobApplications.exportHtml(date);
    }

    public int getSuccessfulApplications(String jobName, Employer employer) {
        return appliedJobApplications.getCount(jobName, employer);
    }

    public int getUnsuccessfulApplications(String jobName, Employer employer) {
        return jobApplications.getCount(jobName, employer);
    }

}
