Errai Project Release Process
=============================

This document describes the steps that should be followed during every release of
the Errai project. When making a release, please be careful to follow these
steps in order. If something doesn't make sense, if you perform steps not listed
here, or if after the release, it was discovered that something was forgotten,
please update these notes to reflect the correct procedure.

The hope is that most or all of these steps will be automated. But a necessary
first step is to discover everything that needs to be done in order to make a
release successful.

A note on Errai Theory
----------------------

When the Maven Release Plugin goes awry, it's a tedious process to back out its
automated git tags and commits. We prefer to follow the steps manually, committing
after each step as we go.

Release Steps
-------------

1. Run the test suite. Ensure all tests pass. (Can skip this if last CI build is green.)

    ```bash
    mvn -Pintegration-test clean install    
    ```
        
1. Update reference guide with latest content and check in generated docbook.
        
    ```bash
    cd errai-docs
    mvn clean package #this needs a profile in ~/.m2/settings.xml that references the JBoss public maven repo
    git add src
    ```
       
        
1. Ask Maven to update the version number in all the pom.xml files:

   ```bash
   cd {errai_root_dir}
   ./updateVersions.sh a.b.c-SNAPSHOT x.y.z.Final
   ```
  
   Afterward, verify that all subprojects reference the new parent pom's version:
   
   ```bash
   find . -name pom.xml | xargs grep x.y.z | grep SNAP
   ```
       
   (if any are out of sync with the parent version, Maven will not have updated them)

1. Build and package the release. These are the bits that will be uploaded to nexus.
   Expect this to take about 4 minutes, depending on network speed.
        
    ```bash
    mvn clean deploy -Dgwt.compiler.skip=true -DaltDeploymentRepository=jboss-snapshots-repository::default::https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/
    ```

1. Upload the docs

    ```bash
    cd dist
    scripts/upload_docs.sh {version}
    ```
        
    * **NOTE**: In the case it does not work, repeat Step 2 only before retrying.
    * **NOTE2**: Upload both for {version} and `latest`. Remember to pass the `--skip-mkdirs` param when uploading `latest`.
    * **NOTE3**: If you get a "Permission denied" message, make sure that you have your keys on the `~/.ssh` directory and that you've ran `ssh-add -K ${path-to-key}` too.
    
1. Tag and push the release to GitHub

    ```bash
    git commit -a -m "Updated to new version x.y.z.Final"
    git tag x.y.z.Final
    ```
    
 1. Reset all versions to `a.b.c+1-SNAPSHOT` and commit
    ```bash
    cd {errai_root_dir}
    ./updateVersions.sh x.y.z.Final a.b.c+1-SNAPSHOT
    git commit -a -m "Updated to new development version a.b.c+1-SNAPSHOT"
    ```
  
 1. Push the changes:
    ```bash
    git push upstream {branch}
    git push upstream --tags
    ```

1. Browse to nexus (https://repository.jboss.org/nexus/index.html)
    * Find the corresponding staging repository (Sort by repository Update)
    * Select it and click Close (takes about 1 minute)
    * Select it again and click Release
    * After that, it will take about 1 day for the artifacts to show up in Maven Central. Browse to (https://repository.jboss.org/nexus/content/groups/public/org/jboss/errai/) and verify that the artifacts are present.

1. Release the new version on [JIRA](https://issues.jboss.org/projects/ERRAI?selectedItem=com.atlassian.jira.jira-projects-plugin%3Arelease-page&status=unreleased).

1. Update the version number of `errai-tutorial`'s pom.xml:
  https://github.com/errai/errai-tutorial/blob/master/pom.xml

1. Update the website with links to the new version (https://github.com/errai/errai.github.com)

1. (Optional) Publish a release blog entry at http://errai-blog.blogspot.com/

1. (Optional) Tweet about the release!

=== You're done! Congrats! You deserve beer! ===


Relesing org.jboss.errai:wildfly-dist
----

1. `cd errai-cdi/errai-wildfly-dist`

2. Make sure that you have the right permissions on Nexus then run:
```bash
mvn clean deploy
```
3. After that, execute only step 9. of the normal Release Process.
