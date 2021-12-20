if [ -z "$1" ]; then
    echo "Usage:"
    echo "  $0 <new_version>"
    exit 1
fi

new_version=$1

mvn versions:set -DnewVersion=$new_version
for proj in errai-bom appformer-bom kie-wb-common-bom drools-wb-bom; do (cd $proj; mvn versions:set -DnewVersion=$new_version); done
mvn clean install -B -ntp -Dgwt.compiler.skip=true -Dmaven.test.skip=true
mvn versions:commit
