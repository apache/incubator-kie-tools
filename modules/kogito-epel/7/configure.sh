#!/bin/sh
set -e

rpm -i https://download.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm


microdnf clean all
rm -rf /var/cache/yum