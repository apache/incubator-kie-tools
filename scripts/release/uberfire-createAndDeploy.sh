#!/bin/bash -e

# removing Uberfire artifacts from local maven repo (basically all possible SNAPSHOTs)
if [ -d $MAVEN_REPO_LOCAL ]; then
    rm -rf $MAVEN_REPO_LOCAL/org/jboss/errai/
    rm -rf $MAVEN_REPO_LOCAL/org/uberfire/
fi

# clones uberfire branch 
git clone git@github.com:appformer/uberfire.git -b $baseBranch

cd uberfire

# checkout the release branch 
git checkout -b $releaseBranch $baseBranch

if [ "$target" == "community" ]; then
   stagingProfile=15c58a1abc895b
else
   stagingProfile=15c3321d12936e
fi

# upgrades the version to the release/tag version
sh scripts/release/update-version.sh $newVersion

# update files that are not automatically changed with the update-versions-all.sh script
sed -i "$!N;s/<version.org.jboss.errai>.*.<\/version.org.jboss.errai>/<version.org.jboss.errai>$erraiVersion<\/version.org.jboss.errai>/;P;D" pom.xml

# git add and commit the version update changes 
git add .
commitMsg="update to version $newVersion"
git commit -m "$commitMsg"

# build the repos & deploy into local dir (will be later copied into staging repo)
deploy-dir=$WORKSPACE/deploy-dir
# (1) do a full build, but deploy only into local dir
# we will deploy into remote staging repo only once the whole build passed (to save time and bandwith)
mvn -B -e clean deploy -U -Dfull -Drelease -T1C -DaltDeploymentRepository=local::default::file://$deploy-dir -Dmaven.test.failure.ignore=true\
 -Dgwt.memory.settings="-Xmx2g -Xms1g -Xss1M" -Dgwt.compiler.localWorkers=2

# (2) upload the content to remote staging repo
cd $deploy-dir
mvn -B -e org.sonatype.plugins:nexus-staging-maven-plugin:1.6.5:deploy-staged-repository -DnexusUrl=https://repository.jboss.org/nexus -DserverId=jboss-releases-repository\
 -DrepositoryDirectory=$deploy-dir -DstagingProfileId=$stagingProfile -DstagingDescription="uberfire $newVersion" -DstagingProgressTimeoutMinutes=30

# pushes the release-branches to jboss-integration or droolsjbpm [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script is finished and will be applied]
if [ "$target" == "community" ]; then
   cd $WORKSPACE/uberfire
   git push origin $releaseBranch 

else

   cd $WORKSPACE/uberfire
   git remote add upstream git@github.com:jboss-integration/uberfire.git
   git push upstream $releaseBranch 
   git push upstream $baseBranch
   
fi
