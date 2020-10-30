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
        Job job = new Job(jobName, JobType.valueOf(jobType));
        LocalDate applicationTime1 = LocalDate.parse(applicationTime);
        Employer employer = new Employer(employerName);
        return new JobApplication(job, applicationTime1, employer);
    }

    private ArrayList<String> createNewJob(final String jobName, final String jobType) {
        return new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }};
    }

    private Job createNewJob(final String jobName, final JobType jobType) {
        return new Job(jobName, jobType);
    }

    @Before
    public void setUp() throws Exception {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() throws RequiresResumeForJReqJobException, InvalidResumeException {
        Employer employer = new Employer("");
        String jobName = "高级前端开发";
        application.execute(Command.publish, employer, new Job(jobName, JobType.JReq), null, null, new JobSeeker(null));
        List<Job> jobs = application.getJobs(employer);

        List<Job> expected = new ArrayList<Job>() {{
            add(createNewJob("高级前端开发", JobType.JReq));
        }};
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() throws RequiresResumeForJReqJobException, InvalidResumeException {
        Employer alibaba = new Employer("Alibaba");
        Employer tencent = new Employer("Tencent");
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        application.execute(Command.publish, alibaba, new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.publish, tencent, new Job(juniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        List<Job> jobs = application.getJobs(alibaba);

        List<Job> expected = Arrays.asList( createNewJob("高级Java开发", JobType.JReq));
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String seniorJavaDevJob = "高级Java开发";
        Employer alibaba = new Employer("Alibaba");

        application.execute(Command.publish, alibaba, new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        List<Job> jobs = application.getJobs(alibaba);

        List<Job> expected = Arrays.asList(createNewJob("高级Java开发", JobType.ATS));
        assertThat(jobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String jobName = "高级Java开发";
        //TODO fixbug
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(jobName, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.save, new Employer(jobSeekerName), new Job(jobName, JobType.JReq), null, null, new JobSeeker(null));
        Employer employer = new Employer(jobSeekerName);
        List<Job> jobs = application.getJobs(employer);

        List<Job> expected = Arrays.asList(createNewJob("高级Java开发", JobType.JReq));
        assertThat(jobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("2020-01-01"), new JobSeeker(jobSeekerName));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("2020-01-01"), new JobSeeker(jobSeekerName));
        Employer employer = new Employer(jobSeekerName);

        List<JobApplication> jobs_new = application.getJobsApplication_new(GettingJobsType.applied, employer);

        List<JobApplication> expected_new = new ArrayList<JobApplication>() {{
            add(createNewJobApplication("Java开发", "ATS", "Alibaba", "2020-01-01"));
            add(createNewJobApplication("高级Java开发", "ATS", "Alibaba", "2020-01-01"));
        }};
        assertThat(jobs_new, is(expected_new));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, LocalDate.now(), new JobSeeker(jobSeekerName));
    }

    @Test(expected = InvalidResumeException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String resumeApplicantName = "Jacky Chen";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), resumeApplicantName, LocalDate.now(), new JobSeeker(jobSeekerName));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerLam));
        List<String> applicants = application.findApplicants(seniorJavaDevJob);

        List<String> expected = new ArrayList<String>() {{
            add("Lam");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_application_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));
        List<String> applicants = application.findApplicants(null, LocalDate.parse("1999-12-20"));

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period_when_period_end_is_given_while_period_start_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));
        List<String> applicants = application.findApplicants(null, null, LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));
        List<String> applicants = application.findApplicants(null, LocalDate.parse("1997-07-01"), LocalDate.parse("1999-12-20"));

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_start_is_given_while_period_end_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String resumeApplicantName = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), resumeApplicantName, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));

        List<String> applicants = application.findApplicants(seniorJavaDevJob, LocalDate.parse("1999-12-20"));

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_end_is_given_while_period_start_is_not() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));

        List<String> applicants = application.findApplicants(juniorJavaDevJob, null, LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

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

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerWong));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1998-01-01"), new JobSeeker(jobSeekerHo));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerLam));

        List<String> applicants = application.findApplicants(juniorJavaDevJob, LocalDate.parse("1997-01-01"), LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void should_generator_csv_reports_of_all_jobseekers_on_a_given_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jackyResume = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), jackyResume, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerLam));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), lamResume, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerLam));

        String csv = application.export(LocalDate.parse("1999-12-20"), ExportType.csv);
        String expected = "Employer,Job,Job Type,Applicants,Date" + "\n" + "Alibaba,Java开发,ATS,Ho,1999-12-20" + "\n" + "Alibaba,Java开发,ATS,Lam,1999-12-20" + "\n" + "Alibaba,高级Java开发,JReq,Lam,1999-12-20" + "\n" + "Alibaba,高级Java开发,JReq,Jacky,1999-12-20" + "\n";

        assertThat(csv, is(expected));
    }

    @Test
    public void should_generator_html_reports_of_all_jobseekers_on_a_given_date() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jackyResume = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1997-07-01"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), jackyResume, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerHo));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerLam));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), lamResume, LocalDate.parse("1999-12-20"), new JobSeeker(jobSeekerLam));

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
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Jacky</td>"
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

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerJacky));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerLam));
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerHo));
        application.execute(Command.apply, new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerHo));

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

        application.execute(Command.publish, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, null, new JobSeeker(null));
        application.execute(Command.publish, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, null, new JobSeeker(null));
        try {
            application.execute(Command.apply, new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq), null, LocalDate.now(), new JobSeeker(jobSeekerJacky));
        } catch (RequiresResumeForJReqJobException ignored) {}
        application.execute(Command.apply, new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS), null, LocalDate.now(), new JobSeeker(jobSeekerLam));

        assertThat(application.getUnsuccessfulApplications(seniorJavaDevJob, new Employer(employerAlibaba)), is(1));
        assertThat(application.getUnsuccessfulApplications(juniorJavaDevJob, new Employer(employerAlibaba)), is(0));
    }
}
