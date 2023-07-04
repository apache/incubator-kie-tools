# kie-patternfly-webjar

This package contains the patternfly 3.18.1 webjar.

It fixes the following vulnerabilities included in the original `org.webjars.bower` webjar:

- `CVE-2022-24785`, `WS-2016-0075`, `CVE-2017-18214` & `CVE-2022-31129`: `moment.js@2.14.1` brought on package tests (via cdnjs links).
- `CVE-2018-17567`: `jekyll.gem` which is brought by the original patternfly build.
