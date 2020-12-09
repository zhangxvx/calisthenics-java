package com.theladders.avital.cc;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.JobRepository;
import com.theladders.avital.cc.jobapplication.InvalidResumeException;
import com.theladders.avital.cc.jobapplication.JobApplication;
import com.theladders.avital.cc.jobapplication.JobApplicationRepository;
import com.theladders.avital.cc.jobapplication.RequiresResumeForJReqJobException;
import com.theladders.avital.cc.jobapplication.export.ExportType;
import com.theladders.avital.cc.jobseeker.JobSeeker;
import com.theladders.avital.cc.resume.Resume;

import java.time.LocalDate;
import java.util.List;

public class Application {
    private final JobApplicationRepository jobApplicationRepository = new JobApplicationRepository();
    private final JobRepository jobRepository = new JobRepository();

    public void saveJob(Job job, JobSeeker jobSeeker) {
        jobRepository.saveJob(job, jobSeeker);
    }

    public void publishJob(Employer employer, Job job) {
        jobRepository.publishJob(employer, job);
    }

    public List<Job> getJobs(Employer employer) {
        return jobRepository.getJobs(employer);
    }

    public List<Job> getJobs(JobSeeker jobSeeker) {
        return jobRepository.getJobs(jobSeeker);
    }

    public void applyJob(Employer employer, Job job, LocalDate applicationTime, JobSeeker jobSeeker, Resume resume) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplicationRepository.applyJob(employer, job, applicationTime, jobSeeker, resume);
    }

    public String export(LocalDate date, ExportType type) {
        if (type == ExportType.csv) {
            return jobApplicationRepository.exportCsv(date);
        }
        return jobApplicationRepository.exportHtml(date);
    }

    public List<JobApplication> getJobApplications(JobSeeker jobSeeker) {
        return jobApplicationRepository.getJobApplications(jobSeeker);
    }

    public List<JobSeeker> findApplicants(String jobName) {
        return jobApplicationRepository.getJobSeekers(jobName, null, null);
    }

    public List<JobSeeker> findApplicants(String jobName, LocalDate from) {
        return jobApplicationRepository.getJobSeekers(jobName, from, null);
    }

    public List<JobSeeker> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return jobApplicationRepository.getJobSeekers(jobName, from, to);
    }

    public int getSuccessfulApplications(String jobName, Employer employer) {
        return jobApplicationRepository.getSuccessfulCount(jobName, employer);
    }

    public int getUnsuccessfulApplications(String jobName, Employer employer) {
        return jobApplicationRepository.getUnsuccessfulCount(jobName, employer);
    }
}
