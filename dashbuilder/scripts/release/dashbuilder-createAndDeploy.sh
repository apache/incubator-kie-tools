#!/bin/bash -e

# removing dashbuilder artifacts from local maven repo (basically all possible SNAPSHOTs)
if [ -d $MAVEN_REPO_LOCAL ]; then
    rm -rf $MAVEN_REPO_LOCAL/org/dashbuilder/
fi

# clone the repository and branch
git clone git@github.com:dashbuilder/dashbuilder.git --branch $baseBranch

# checkout the release branch 
cd $WORKSPACE/dashbuilder  
git checkout -b $releaseBranch $baseBranch

# upgrades the version to the release/tag version
sh scripts/release/update-version.sh $newVersion

# update files that are not automatically changed with the update-version.sh script
sed -i "$!N;s/<version.org.uberfire>.*.<\/version.org.uberfire>/<version.org.uberfire>$uberfireVersion<\/version.org.uberfire>/;P;D" pom.xml
sed -i "$!N;s/<version.org.jboss.errai>.*.<\/version.org.jboss.errai>/<version.org.jboss.errai>$erraiVersion<\/version.org.jboss.errai>/;P;D" pom.xml
sed -i "$!N;s/<version.org.kie.soup>.*.<\/version.org.kie.soup>/<version.org.kie.soup>$kiesoupVersion<\/version.org.kie.soup>/;P;D" pom.xml

# git add and commit the version update changes 
git add .
commitMsg="update to version $newVersion"
git commit -m "$commitMsg"

if [ "$target" == "community" ]; then
   stagingProfile=15c58a1abc895b
else
   stagingProfile=15c3321d12936e
fi

# build the repos & deploy into local dir (will be later copied into staging repo)
deployDir=$WORKSPACE/deployDir
# (1) do a full build, but deploy only into local dir
# we will deploy into remote staging repo only once the whole build passed (to save time and bandwith)
mvn -B -e -U clean deploy -Dfull -Drelease -T1C -DaltDeploymentRepository=local::default::file://$deployDir -Dmaven.test.failure.ignore=true\
 -Dgwt.memory.settings="-Xmx2g -Xms1g -Xss1M" -Dgwt.compiler.localWorkers=2

# (2) upload the content to remote staging repo
cd $deployDir
mvn -B -e org.sonatype.plugins:nexus-staging-maven-plugin:1.6.5:deploy-staged-repository -DnexusUrl=https://repository.jboss.org/nexus -DserverId=jboss-releases-repository\
 -DrepositoryDirectory=$deployDir -DstagingProfileId=$stagingProfile -DstagingDescription="dashbuilder $newVersion" -DstagingProgressTimeoutMinutes=30

cd $WORKSPACE/dashbuilder
# pushes the release-branches to rhub.com:jboss-integration or github.com:dashbuilder [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script is finished and will be applied]
if [ "$target" == "community" ]; then
   git push origin $releaseBranch
else
   git remote add upstream git@github.com:jboss-integration/dashbuilder.git
   git push upstream $releaseBranch
   git push upstream $baseBranch
fi
