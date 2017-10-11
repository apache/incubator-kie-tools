#!/bin/bash -e

# clone the repository and the release-branch

if [ "$target" == "productized" ]; then
   git clone git@github.com:jboss-integration/uberfire.git --branch $releaseBranch
else 
   git clone git@github.com:appformer/uberfire.git --branch $releaseBranch
fi

commitMsg="Tagging $tag"

# pushes the TAG to jboss-integration or droolsjbpm [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script is ready]
if [ "$target" == "productized" ]; then
   cd $WORKSPACE/uberfire
   git tag -a $tag -m "$commitMsg"
   git remote add gerrit ssh://jb-ip-tooling-jenkins@code.engineering.redhat.com/droolsjbpm-uberfire
   git push gerrit $tag
else
   cd $WORKSPACE/uberfire
   git tag -a $tag -m "$commitMsg"
   git push origin $tag
fi
