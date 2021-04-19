#!/bin/bash

export version=$1
skip_mkdirs=""

# TODO use getopt
if [ "$2" = "--skip-mkdirs" ]; then
    skip_mkdirs=yes
fi

if [ -z "$version" ]; then
    echo "FAIL: you must specify the Errai release version number as the first command line arg."
    exit 1
fi

set -e

if [ -z "$skip_mkdirs" ]; then

    echo "Making directories..."

    sftp -b - errai@filemgmt.jboss.org <<END
cd /docs_htdocs/errai
mkdir $version
cd $version
mkdir errai
END

    echo "Done!"
fi

echo "Uploading documentation..."

(cd ../errai-docs/target/docbook/publish/en/pdf && mv Reference_Guide.pdf Errai_${version}_Reference_Guide.pdf)

(cd ../errai-docs/target/docbook/publish/en  && scp -rp . errai@filemgmt.jboss.org:/docs_htdocs/errai/$version/errai/reference)

(cd ../errai-docs/src/main/asciidoc && scp -rp author errai@filemgmt.jboss.org:/docs_htdocs/errai/$version/errai/html && scp -rp author errai@filemgmt.jboss.org:/docs_htdocs/errai/$version/errai/reference/html_single)

echo "Done!"

