package com.theladders.avital.cc.jobapplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.jobseeker.JobSeeker;
import com.theladders.avital.cc.resume.Resume;

import java.time.LocalDate;
import java.util.List;

public class JobApplicationRepository {
    private final AppliedJobApplications appliedJobApplications = new AppliedJobApplications();
    private final JobApplications failedJobApplications = new JobApplications();

    private void validateResume(Employer employer, Job job, Resume resume, LocalDate applicationTime, JobSeeker jobSeeker)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.isJReq() && resume == null) {
            failedJobApplications.add(employer, job, applicationTime, jobSeeker);
            throw new RequiresResumeForJReqJobException();
        }
        if (job.isJReq() && !resume.isMatched(jobSeeker)) {
            throw new InvalidResumeException();
        }
    }

    public void applyJob(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        validateResume(employer, job, resume, applicationTime, jobSeeker);
        appliedJobApplications.add(employer, job, applicationTime, jobSeeker);
    }

    public List<JobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedJobApplications.getJobApplications(jobSeeker);
    }

    public List<JobSeeker> getJobSeekers(String jobName, LocalDate from, LocalDate to) {
        return appliedJobApplications.find(jobName, from, to);
    }

    public String exportHtml(LocalDate date) {
        return appliedJobApplications.exportHtml(date);
    }

    public String exportCsv(LocalDate date) {
        return appliedJobApplications.exportCsv(date);
    }

    public int getSuccessfulCount(String jobName, Employer employer) {
        return appliedJobApplications.getCount(jobName, employer);
    }

    public int getUnsuccessfulCount(String jobName, Employer employer) {
        return failedJobApplications.getCount(jobName, employer);
    }
}