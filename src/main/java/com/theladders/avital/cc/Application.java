package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {

    private final Jobs jobs = new Jobs();
    private final AppliedJobApplications appliedJobApplications = new AppliedJobApplications();
    private final JobApplications jobApplications = new JobApplications();

    public void execute(Command command, final Employer employer, final Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (command == Command.publish) {
            jobs.publishJob(employer, job);
            return;
        }
        if (command == Command.save) {
            jobs.saveJob(employer, job);
            return;
        }
        if (command == Command.apply) {
            applyJob(employer, job, applicationTime, jobSeeker, resume);
        }
    }

    private void applyJob(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        validateResume(employer, job, applicationTime, jobSeeker, resume);
        appliedJobApplications.add(employer, job, applicationTime, jobSeeker);
    }

    private void validateResume(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getType() == JobType.JReq && resume.getApplicantName() == null) {
            jobApplications.add(employer, job, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }

        if (job.getType() == JobType.JReq && !resume.getApplicantName().equals(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
    }

    public List<JobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedJobApplications.getJobApplications(jobSeeker);
    }

    public List<Job> getJobs(Employer employer) {
        return new ArrayList<>(jobs.getJobs(employer));
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
