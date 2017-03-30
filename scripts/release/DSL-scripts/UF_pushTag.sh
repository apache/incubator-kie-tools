# clone the repository and the release-branch

if [ "$TARGET" == "productized" ]; then   
   git clone git@github.com:jboss-integration/uberfire.git --branch $RELEASE_BRANCH
else 
   git clone git@github.com:uberfire/uberfire.git --branch $RELEASE_BRANCH
fi

commitMSG="Tagging $TAG"

# pushes the TAG to jboss-integration or droolsjbpm [IMPORTANT: "push -n" (--dryrun) should be replaced by "push" when script is ready]
if [ "$TARGET" == "productized" ]; then
   cd $WORKSPACE/uberfire
   git tag -a $TAG -m "$commitMSG"
   git remote add upstream git@github.com:jboss-integration/uberfire.git
   git push upstream $TAG
else
   cd $WORKSPACE/uberfire
   git tag -a $TAG -m "$commitMSG"
   git push origin $TAG
fi
