if [ -z "$1" -o -z "$2" ]; then
    echo "Usage:"
    echo "  $0 <oldversion> <newversion>"
    exit 1
fi

oldversion=$1
newversion=$2

mvn versions:set -DnewVersion=$newversion
mvn clean install -Dgwt.compiler.skip=true -Dmaven.test.skip=true
for proj in errai-demos errai-demos/errai-* errai-bom errai-internal-bom errai-version-master errai-forge-addon; do (cd $proj; mvn versions:set -DnewVersion=$newversion); done

echo "Visual Sanity Check for old version $oldversion and snapshots..."
find . -name pom.xml | xargs grep -3 "$oldversion"
find . -name pom.xml | xargs grep -e -SNAPSHOT
echo "Done sanity check"

echo " !!"
echo " !! Remember to update the versions in reference/src/main/docbook/en/Book_Info.xml"
echo " !!"
