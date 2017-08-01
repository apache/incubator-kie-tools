#!/bin/bash -e

# clone the repository and the release-branch

if [ "$target" == "productized" ]; then
   git clone git@github.com:jboss-integration/uberfire.git --branch $releaeBranch
else 
   git clone git@github.com:appformer/uberfire.git --branch $releaseBranch
fi

commitMsg="Tagging $tag"

# pushes the TAG to jboss-integration or droolsjbpm [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script is ready]
if [ "$target" == "productized" ]; then
   cd $WORKSPACE/uberfire
   git tag -a $tag -m "$commitMsg"
   git remote add upstream git@github.com:jboss-integration/uberfire.git
   git push upstream $tag
else
   cd $WORKSPACE/uberfire
   git tag -a $tag -m "$commitMsg"
   git push origin $tag
fi
