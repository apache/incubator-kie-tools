
import org.kie.jenkins.jobdsl.model.Folder
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.Utils

/////////////////////////////////////////////////////////////////
// This file is used for generating Jenkins jobs
// Its placement and structure is a "convention" in kiegroup
// For kie-tools we currently generate just pullrequest job,
// to run sonar with each pull request, see: .ci/jenkins/Jenkinsfile
//
// For more details see:
//  - https://github.com/kiegroup/kogito-pipelines/blob/main/docs/jenkins.md
// Or the same file in other repositories like:
//  - https://github.com/kiegroup/kogito-runtimes/tree/main/.ci/jenkins/dsl
/////////////////////////////////////////////////////////////////

Map getMultijobPRConfig() {
    return [
        parallel: true,
        jobs : [
            [
                id: 'kie-tools-stunner-editors',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    ENABLE_SONARCLOUD: Utils.isMainBranch(this),
                ]
            ]
        ]
    ]
}

// PR checks
setupMultijobPrDefaultChecks()

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createPerRepoPRJobs(this, Folder.PULLREQUEST) { jobFolder -> return getMultijobPRConfig() }
}