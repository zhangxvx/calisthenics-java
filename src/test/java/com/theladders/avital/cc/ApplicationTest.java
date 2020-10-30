package com.theladders.avital.cc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
    Application application;

    private ArrayList<String> createNewJob(String jobName, String jobType, String employerName, String applicationTime) {
        return new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime);
            add(employerName);
        }};
    }

    private ArrayList<String> createNewJob(final String jobName, final String jobType) {
        return new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }};
    }


    @Before
    public void setUp() throws Exception {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerName = "";
        String jobName = "高级前端开发";
        application.execute(employerName, jobName, null, null, null, Command.publish, JobType.JReq);
        List<List<String>> jobs = application.getJobs(employerName, GettingJobsType.published);
        List<List<String>> expected = new ArrayList<List<String>>() {{
            add(createNewJob("高级前端开发", "JReq"));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerTencent, juniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        List<List<String>> jobs = application.getJobs(employerAlibaba, GettingJobsType.published);
        List<List<String>> expected = new ArrayList<List<String>>() {{
            add(createNewJob("高级Java开发", "JReq"));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        List<List<String>> jobs = application.getJobs(employerAlibaba, GettingJobsType.published);
        List<List<String>> expected = new ArrayList<List<String>>() {{
            add(createNewJob("高级Java开发", "ATS"));
        }};

        assertThat(jobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String jobName = "高级Java开发";
        application.execute(employerAlibaba, jobName, null, null, null, Command.publish, JobType.JReq);
        application.execute(jobSeekerName, jobName, null, null, null, Command.save, JobType.JReq);
        List<List<String>> savedJobs = application.getJobs(jobSeekerName, GettingJobsType.published);
        List<List<String>> expected = new ArrayList<List<String>>() {{
            add(createNewJob("高级Java开发", "JReq"));
        }};

        assertThat(savedJobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerName, null, LocalDate.parse("2020-01-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerName, null, LocalDate.parse("2020-01-01"), Command.apply, JobType.ATS);
        List<List<String>> appliedJobs = application.getJobs(jobSeekerName, GettingJobsType.applied);
        List<List<String>> expected = new ArrayList<List<String>>() {{
            add(createNewJob("Java开发", "ATS", "Alibaba", "2020-01-01"));
            add(createNewJob("高级Java开发", "ATS", "Alibaba", "2020-01-01"));
        }};

        assertThat(appliedJobs, is(expected));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerName, null, LocalDate.now(), Command.apply, JobType.JReq);
    }

    @Test(expected = InvalidResumeException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String resumeApplicantName = "Jacky Chen";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerName, resumeApplicantName, LocalDate.now(), Command.apply, JobType.JReq);
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.now(), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerLam, null, LocalDate.now(), Command.apply, JobType.ATS);
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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
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

        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, resumeApplicantName, LocalDate.parse("1999-12-20"), Command.apply, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);

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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);

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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerWong, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1998-01-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerLam, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);

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

        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, jackyResume, LocalDate.parse("1999-12-20"), Command.apply, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerLam, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerLam, lamResume, LocalDate.parse("1999-12-20"), Command.apply, JobType.JReq);

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

        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerJacky, null, LocalDate.parse("1997-07-01"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, jackyResume, LocalDate.parse("1999-12-20"), Command.apply, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerLam, null, LocalDate.parse("1999-12-20"), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerLam, lamResume, LocalDate.parse("1999-12-20"), Command.apply, JobType.JReq);

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

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerTencent, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.now(), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerLam, null, LocalDate.now(), Command.apply, JobType.ATS);
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerHo, null, LocalDate.now(), Command.apply, JobType.ATS);
        application.execute(employerTencent, juniorJavaDevJob, jobSeekerHo, null, LocalDate.now(), Command.apply, JobType.ATS);

        assertThat(application.getSuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(2));
        assertThat(application.getSuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(1));
    }

    @Test
    public void should_be_able_to_see_unsuccessful_applications_of_a_job_for_an_employer() throws RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.execute(employerAlibaba, seniorJavaDevJob, null, null, null, Command.publish, JobType.JReq);
        application.execute(employerAlibaba, juniorJavaDevJob, null, null, null, Command.publish, JobType.ATS);
        try {
            application.execute(employerAlibaba, seniorJavaDevJob, jobSeekerJacky, null, LocalDate.now(), Command.apply, JobType.JReq);
        } catch (RequiresResumeForJReqJobException ignored) {}
        application.execute(employerAlibaba, juniorJavaDevJob, jobSeekerLam, null, LocalDate.now(), Command.apply, JobType.ATS);

        assertThat(application.getUnsuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(1));
        assertThat(application.getUnsuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(0));
    }
}
