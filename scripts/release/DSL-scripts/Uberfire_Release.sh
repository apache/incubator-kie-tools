def ufDeploy=
"""
sh /home/jenkins/workspace/UF_Release-1.0.x/release-scripts/uberfire/scripts/release/DSL-scripts/UF_Deploy.sh
"""

def ufPushTag=
"""
sh /home/jenkins/workspace/UF_Release-1.0.x/release-scripts/uberfire/scripts/release/DSL-scripts/UF_PushTag.sh
"""

def updateVersions=
"""
sh /home/jenkins/workspace/UF_Release-1.0.x/release-scripts/uberfire/scripts/release/DSL-scripts/UF_updateNextVersions.sh
"""


// ******************************************************

job("01.UF_Release-1.0.x") {

  description("This job: <br> releases UF, upgrades the version, builds and deploys, copies artifacts to Nexus, closes the release on Nexus  <br> <b>IMPORTANT: Created automatically by Jenkins job DSL plugin. Do not edit manually! The changes will get lost next time the job is generated.<b>")

  parameters {
    choiceParam("TARGET", ["community", "productized"], "please select if this release is for community <b> community </b> or <br> if it is for building a productization tag <b>productized <br> ******************************************************** <br> ")
    stringParam("BASE_BRANCH", "master", "please edit the name of the base branch <br> i.e. typically <b> master </b> for <b> community </b>or <b> bsync-6.5.x-2016.08.05  </b> for <b> productization </b> <br> ******************************************************** <br> ")
    stringParam("RELEASE_BRANCH", "r1.0.0.Beta5", "please edit the name of the release branch <br> i.e. typically <b> r1.0.0.Beta5 </b> for <b> community </b>or <b> bsync-6.5.x-2016.08.05  </b> for <b> productization </b> <br> ******************************************************** <br> ")
    stringParam("newVersion", "1.0.0.CR1", "please edit the new version that should be used in the poms <br> The version should typically look like <b> 1.0.0.CR1 </b> for <b> community </b> or <b> 1.0.0.20170103-productized </b> for <b> productization </b> <br> ******************************************************** <br> ")
    stringParam("ERRAI_VERSION", "4.0.0.Beta7", "please edit the errai version <br> ******************************************************** <br>")
  }
  
  label("kie-releases")

  logRotator {
    numToKeep(10)
  }

  jdk("jdk1.8")

  customWorkspace("/home/jenkins/workspace/UF_Release-1.0.x")

  wrappers {
    timeout {
      absolute(60)
    }
    timestamps()
    colorizeOutput()
    toolenv("APACHE_MAVEN_3_2_5", "JDK1_8")
  }

  configure { project ->
    project / 'buildWrappers' << 'org.jenkinsci.plugins.proccleaner.PreBuildCleanup' {
      cleaner(class: 'org.jenkinsci.plugins.proccleaner.PsCleaner') {
        killerType 'org.jenkinsci.plugins.proccleaner.PsAllKiller'
        killer(class: 'org.jenkinsci.plugins.proccleaner.PsAllKiller')
        username 'jenkins'
      }
    }
  }

  publishers {
    mailer('mbiarnes@redhat.com', false, false)
  }

  steps {
    environmentVariables {
        envs(MAVEN_OPTS :"-Xms2g -Xmx3g", MAVEN_HOME: "\$APACHE_MAVEN_3_2_5_HOME", MAVEN_REPO_LOCAL: "/home/jenkins/.m2/repository", PATH :"\$MAVEN_HOME/bin:\$PATH")
    }
    shell(ufDeploy)
  }
}

// ******************************************************

job("02.UF_release-pushTags-1.0.x") {

  description("This job: <br> creates and pushes the tags for <br> community (droolsjbpm) or product (jboss-integration) <br> IMPORTANT: Created automatically by Jenkins job DSL plugin. Do not edit manually! The changes will get lost next time the job is generated.")

  parameters {
    choiceParam("TARGET", ["community", "productized"], "please select if this release is for community <b> community </b> or <br> if it is for building a productization tag <b>productized <br> ******************************************************** <br> ")
    stringParam("RELEASE_BRANCH", "r0.9.0.Final", "please edit the name of the release branch <br> i.e. typically <b> r0.9.0.Final </b> for <b> community </b>or <b> bsync-6.5.x-2016.08.05  </b> for <b> productization </b> <br> ******************************************************** <br> ")
    stringParam("TAG_NAME", "Please enter the name of the tag", "The tag should typically look like <b> 0.9.0.Final </b> for <b> community </b> or <b> sync-6.5.0.2016.08.05 </b> for <b> productization </b> <br> ******************************************************** <br> ")
  };

  label("kie-releases")

  logRotator {
    numToKeep(10)
  }

  jdk("jdk1.8")

  wrappers {
    timeout {
      absolute(30)
    }
    timestamps()
    colorizeOutput()
    preBuildCleanup()
    toolenv("APACHE_MAVEN_3_2_3", "JDK1_8")
  }

  configure { project ->
    project / 'buildWrappers' << 'org.jenkinsci.plugins.proccleaner.PreBuildCleanup' {
      cleaner(class: 'org.jenkinsci.plugins.proccleaner.PsCleaner') {
        killerType 'org.jenkinsci.plugins.proccleaner.PsAllKiller'
        killer(class: 'org.jenkinsci.plugins.proccleaner.PsAllKiller')
        username 'jenkins'
      }
    }
  }

  publishers {
    mailer('mbiarnes@redhat.com', false, false)
  }

  steps {
    environmentVariables {
        envs(MAVEN_OPTS :"-Xms2g -Xmx3g -XX:MaxPermSize=512m", MAVEN_HOME: "\$APACHE_MAVEN_3_2_3_HOME", MAVEN_REPO_LOCAL: "/home/jenkins/.m2/repository", PATH :"\$MAVEN_HOME/bin:\$PATH")
    }
    shell(ufPushTags)
  }
}

// ******************************************************

job("03.UF_release-updateNV-1.0.x") {

  description("This job: <br> updates the UF and UF-extensions repositories to a new developmenmt version <br> for 0.7.x, 0.8.x or 0.9.x branches <br> IMPORTANT: Created automatically by Jenkins job DSL plugin. Do not edit manually! The changes will get lost next time the job is generated.")
 
  parameters {
    stringParam("newVersion", "1.0.1-SNAPSHOT", "Edit the new UF version")
    stringParam("BASE_BRANCH", "master", "please edit the name of the base branch <br> ******************************************************** <br> ")
  }

  label("kie-releases")

  logRotator {
    numToKeep(10)
  }

  jdk("jdk1.8")

  wrappers {
    timeout {
      absolute(30)
    }
    timestamps()
    colorizeOutput()
    preBuildCleanup()
    toolenv("APACHE_MAVEN_3_2_5", "JDK1_8")
  }

  configure { project ->
    project / 'buildWrappers' << 'org.jenkinsci.plugins.proccleaner.PreBuildCleanup' {
      cleaner(class: 'org.jenkinsci.plugins.proccleaner.PsCleaner') {
        killerType 'org.jenkinsci.plugins.proccleaner.PsAllKiller'
        killer(class: 'org.jenkinsci.plugins.proccleaner.PsAllKiller')
        username 'jenkins'
      }
    }
  }

  publishers {
    mailer('mbiarnes@redhat.com', false, false)
  }

  steps {
    environmentVariables {
        envs(MAVEN_OPTS :"-Xms2g -Xmx3g", MAVEN_HOME: "\$APACHE_MAVEN_3_2_5_HOME", MAVEN_REPO_LOCAL: "/home/jenkins/.m2/repository", PATH :"\$MAVEN_HOME/bin:\$PATH")
    }
    shell(updateVersion)
  }
}

// *************************
// *************************

nestedView("UF_Releases-1.0.x") {
    views {
        listView("UF_1.0.x") {
            jobs {
                name("01.UF_release-deploy-1.0.x")
                name("02.UF_release-pushTag-1.0.x")
                name("03.UF_release-updateNV-1.0.x")
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
    }
}
