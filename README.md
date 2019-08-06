Kogito
------

**Kogito** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

<p align="center"><img width=55% height=55% src="docs/kogito.png"></p>

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/kiegroup/kogito-tooling.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/blob/master/LICENSE-ASL-2.0.txt)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)

Quick Links
-----------

**Homepage:** http://kogito.kie.org

**Wiki:** https://github.com/kiegroup/kogito-tooling/wiki

**JIRA:** https://issues.jboss.org/projects/KOGITO

**jBPM:** https://www.jbpm.org/

**Drools:** https://www.drools.org/


Getting Started
---------------

This module contains a number of examples that you can take a look at and try out yourself.
 Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself (either locally or on Kubernetes):
- jBPM + Quarkus Hello World: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-quarkus-helloworld/README.md)
- jBPM + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-quarkus-example/README.md)
- jBPM + Spring Boot: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-springboot-example/README.md)
- jBPM + Drools + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/onboarding-example/readme.md) - Onboarding example combining one process and two decision services
- Polyglot Drools with GraalVM: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-polyglot-example/README.md)
- Drools + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-quarkus-example/README.md)
- Drools + Quarkus with Unit: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-quarkus-unit-example/README.md)

Building from source
--------------------

Check out the source:
```
git clone git@github.com:kiegroup/kogito-tooling.git
```

> If you don't have a GitHub account use this command instead:
> ```
> git clone https://github.com/kiegroup/kogito-tooling.git
> ```

Build with Yarn:
```bash
cd kogito-tooling
yarn run init

#prod
yarn run build:prod

# dev
yarn run build # skips integration tests and production packing
yarn run build:fast # skips lint and unit tests
```

> Final artifacts will be on `packages/*/dist` directories.

Known issues
--------------------
- Flicker when opening or restoring DMN/BPMN editors.
- Unable to detect 'dirty' state on DMN/BPMN editors.
- Copy/paste diagram parts between DMN/BPMN editors.


Contributing to Kogito
--------------------

All contributions are welcome! Before you start please read the [Developing Drools and jBPM](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md) guide.


Guides
--------------------

Here are some of the most notable ones for quick reference:

- [Quarkus - Using Kogito to add business automation capabilities to an application](https://quarkus.io/guides/kogito-guide) - This guide demonstrates how your Quarkus application can use Kogito to add business automation to power it up with business processes and rules.
- [Quarkus - Getting Started](https://quarkus.io/get-started/) - Quarkus Getting Started guide