#!/bin/bash -e

targetUser=kiereleaseuser
remoteUrl=git@github.com:kiereleaseuser/dashbuilder.git
DATE=$(date "+%Y-%m-%d")

# clone the repository and branch of dashbuilder
git clone git@github.com:dashbuilder/dashbuilder.git --branch $baseBranch
cd $WORKSPACE/dashbuilder
prBranch=dashbuilder-upgrade-version-$DATE-$baseBranch
git checkout -b $prBranch $baseBranch
git remote add $targetUser $remoteUrl

# upgrades the version to next development version of dashbuilder
sh scripts/release/update-version.sh $newVersion

# change properties via sed as they don't update automatically

sed -i \
-e "$!N;s/<version.org.uberfire>.*.<\/version.org.uberfire>/<version.org.uberfire>$uberfireDevelVersion<\/version.org.uberfire>/;" \
-e "s/<version.org.jboss.errai>.*.<\/version.org.jboss.errai>/<version.org.jboss.errai>$erraiDevelVerison<\/version.org.jboss.errai>/;P;D" \
pom.xml

# git add and commit the version update changes 
git add .
commitMsg="update to next development version $newVersion"
git commit -m "$commitMsg"

# do a build of dashbuilder
mvn -B -e -U clean install -Dfull -Dgwt.memory.settings="-Xmx3g -Xms1g -Xss1M"

# Raise a PR
source=dashbuilder
git push $targetUser $prBranch
hub pull-request -m "$commitMsg" -b $source:$baseBranch -h $targetUser:$prBranch
