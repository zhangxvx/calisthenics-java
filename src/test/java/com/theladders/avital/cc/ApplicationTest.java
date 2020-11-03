package com.theladders.avital.cc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
    Application application;

    private JobApplication createNewJobApplication(String jobName, String jobType, String employerName, String applicationTime) {
        return new JobApplication(new Job(jobName, JobType.valueOf(jobType)), LocalDate.parse(applicationTime), new Employer(employerName));
    }

    private Job createNewJob(final String jobName, final JobType jobType) {
        return new Job(jobName, jobType);
    }

    private List<JobSeeker> expectedJobSeekers(final JobSeeker... jobSeekers) {
        return Arrays.asList(jobSeekers);
    }

    private Resume createResumeBelongNobody() {
        return new Resume(null);
    }


    @Before
    public void setUp() {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() {
        String jobName = "高级前端开发";
        Employer employer = new Employer("");
        application.publishJob(employer, new Job(jobName, JobType.JReq));
        List<Job> jobs = application.getJobs(employer);
        List<Job> expected = new ArrayList<Job>() {{
            add(createNewJob("高级前端开发", JobType.JReq));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        Employer alibaba = new Employer(employerAlibaba);
        Employer tencent = new Employer(employerTencent);

        application.publishJob(alibaba, new Job(seniorJavaDevJob, JobType.JReq));
        application.publishJob(tencent, new Job(juniorJavaDevJob, JobType.JReq));
        List<Job> jobs = application.getJobs(alibaba);
        List<Job> expected = new ArrayList<Job>() {{
            add(createNewJob("高级Java开发", JobType.JReq));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        application.publishJob(alibaba, new Job(seniorJavaDevJob, JobType.ATS));
        List<Job> jobs = application.getJobs(alibaba);
        List<Job> expected = new ArrayList<Job>() {{
            add(createNewJob("高级Java开发", JobType.ATS));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void jobSeekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String jobName = "高级Java开发";
        Employer alibaba = new Employer(employerAlibaba);
        JobSeeker jacky = new JobSeeker(jobSeekerName);

        application.publishJob(alibaba, new Job(jobName, JobType.JReq));
        application.saveJob(new Job(jobName, JobType.JReq), jacky);
        List<Job> savedJobs = application.getJobs(jacky);
        List<Job> expected = new ArrayList<Job>() {{
            add(createNewJob("高级Java开发", JobType.JReq));
        }};

        assertThat(savedJobs, is(expected));
    }

    @Test
    public void jobSeekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        JobSeeker jobSeeker = new JobSeeker(jobSeekerName);
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("2020-01-01"), jobSeeker, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("2020-01-01"), jobSeeker, createResumeBelongNobody());
        List<JobApplication> appliedJobs = application.getJobApplications(jobSeeker);
        List<JobApplication> expected = new ArrayList<JobApplication>() {{
            add(createNewJobApplication("Java开发", "ATS", "Alibaba", "2020-01-01"));
            add(createNewJobApplication("高级Java开发", "ATS", "Alibaba", "2020-01-01"));
        }};

        assertThat(appliedJobs, is(expected));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobSeekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.now(), new JobSeeker(jobSeekerName), createResumeBelongNobody());
    }

    @Test(expected = InvalidResumeException.class)
    public void jobSeekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String resumeApplicantName = "Jacky Chen";

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.now(),
                new JobSeeker(jobSeekerName), new Resume(new JobSeeker(resumeApplicantName)));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker lam = new JobSeeker(jobSeekerLam);
        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.now(), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.now(), lam, createResumeBelongNobody());
        List<JobSeeker> applicants = application.findApplicants(seniorJavaDevJob);

        List<JobSeeker> expected = expectedJobSeekers(jacky, lam);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_application_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"), ho, createResumeBelongNobody());
        List<JobSeeker> applicants = application.findApplicants(null, LocalDate.parse("1999-12-20"));

        List<JobSeeker> expected = expectedJobSeekers(ho);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period_when_period_end_is_given_while_period_start_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"), ho, createResumeBelongNobody());
        List<JobSeeker> applicants = application.findApplicants(null, null, LocalDate.parse("1999-01-01"));

        List<JobSeeker> expected = expectedJobSeekers(jacky);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"), ho, createResumeBelongNobody());
        List<JobSeeker> applicants = application.findApplicants(null, LocalDate.parse("1997-07-01"), LocalDate.parse("1999-12-20"));

        List<JobSeeker> expected = expectedJobSeekers(ho, jacky);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_start_is_given_while_period_end_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"),
                jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.parse("1999-12-20"),
                jacky, new Resume(jacky));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"),
                ho, createResumeBelongNobody());

        List<JobSeeker> applicants = application.findApplicants(seniorJavaDevJob, LocalDate.parse("1999-12-20"));

        List<JobSeeker> expected = expectedJobSeekers(jacky);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_end_is_given_while_period_start_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"), ho, createResumeBelongNobody());

        List<JobSeeker> applicants = application.findApplicants(juniorJavaDevJob, null, LocalDate.parse("1999-01-01"));

        List<JobSeeker> expected = expectedJobSeekers(jacky);

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerWong = "Wong";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        JobSeeker wong = new JobSeeker(jobSeekerWong);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), wong, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"), jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1998-01-01"), ho, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"), lam, createResumeBelongNobody());

        List<JobSeeker> applicants = application.findApplicants(juniorJavaDevJob, LocalDate.parse("1997-01-01"), LocalDate.parse("1999-01-01"));

        List<JobSeeker> expected = expectedJobSeekers(ho, jacky);

        assertThat(applicants, is(expected));
    }

    @Test
    public void should_generator_csv_reports_of_all_jobSeekers_on_a_given_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jackyResume = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"),
                jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.parse("1999-12-20"),
                jacky, new Resume(jacky));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"),
                ho, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"),
                lam, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.parse("1999-12-20"),
                lam, new Resume(lam));

        String csv = application.export(LocalDate.parse("1999-12-20"), ExportType.csv);
        String expected = "Employer,Job,Job Type,Applicants,Date" + "\n" +
                "Alibaba,高级Java开发,JReq,Jacky,1999-12-20" + "\n" +
                "Alibaba,Java开发,ATS,Ho,1999-12-20" + "\n" +
                "Alibaba,Java开发,ATS,Lam,1999-12-20" + "\n" +
                "Alibaba,高级Java开发,JReq,Lam,1999-12-20" + "\n";

        assertThat(csv, is(expected));
    }

    @Test
    public void should_generator_html_reports_of_all_jobSeekers_on_a_given_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1997-07-01"),
                jacky, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.parse("1999-12-20"),
                jacky, new Resume(jacky));
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"),
                ho, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.parse("1999-12-20"),
                lam, createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.parse("1999-12-20"),
                lam, new Resume(lam));

        String csv = application.export(LocalDate.parse("1999-12-20"), ExportType.html);
        String expected = "<!DOCTYPE html>"
                + "<body>"
                + "<table>"
                + "<thead>"
                + "<tr>"
                + "<th>Employer</th>"
                + "<th>Job</th>"
                + "<th>Job Type</th>"
                + "<th>Applicants</th>"
                + "<th>Date</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Jacky</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>Java开发</td>"
                + "<td>ATS</td>"
                + "<td>Ho</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>Java开发</td>"
                + "<td>ATS</td>"
                + "<td>Lam</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Lam</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";

        assertThat(csv, is(expected));
    }

    @Test
    public void should_be_able_to_see_successful_application_of_a_job_for_an_employer() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publishJob(new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.ATS));
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.now(), new JobSeeker(jobSeekerJacky), createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), LocalDate.now(), new JobSeeker(jobSeekerLam), createResumeBelongNobody());
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.now(), new JobSeeker(jobSeekerHo), createResumeBelongNobody());
        application.applyJob(new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.now(), new JobSeeker(jobSeekerHo), createResumeBelongNobody());

        assertThat(application.getSuccessfulApplications(seniorJavaDevJob, new Employer(employerAlibaba)), is(2));
        assertThat(application.getSuccessfulApplications(juniorJavaDevJob, new Employer(employerAlibaba)), is(1));
    }

    @Test
    public void should_be_able_to_see_unsuccessful_applications_of_a_job_for_an_employer() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publishJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.publishJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        try {
            application.applyJob(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), LocalDate.now(), new JobSeeker(jobSeekerJacky), createResumeBelongNobody());
        } catch (RequiresResumeForJReqJobException ignored) {}
        application.applyJob(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), LocalDate.now(), new JobSeeker(jobSeekerLam), createResumeBelongNobody());

        assertThat(application.getUnsuccessfulApplications(seniorJavaDevJob, new Employer(employerAlibaba)), is(1));
        assertThat(application.getUnsuccessfulApplications(juniorJavaDevJob, new Employer(employerAlibaba)), is(0));
    }
}
