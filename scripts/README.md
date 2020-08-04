### Kogito helper scripts

On this directory you can find some python scripts used to help with some repetitive tasks.

Today we have these scripts:

- [manage-kogito-version.py](manage-kogito-version.py)
- [push-local-registry.sh](push-local-registry.sh)
- [push-staging.py](push-staging.py)
- [update-maven-artifacts.py](update-maven-artifacts.py)
- [update-tests.py](update-tests.py)
- [common.py](common.py)

### Managing Kogito images version

The manage-kogito-version script will help when we need to update the current version due a new release.

#### Script dependencies

The `manage-kogito-version.py` has one dependency that needs to be manually installed:

```bash
$ pip install -U ruamel.yaml
```

This script has also a dependency on `common.py`.

#### Usage

Its default behavior is pretty simple:

```bash
$ python manage-kogito-version.py --bump-to 1.0.0  
```

This will set images' version, artifacts reference version and examples reference to 1.0.0.

You can also set a custom version for artifacts and/or a custom reference to the kogito-examples repository:

```bash
$ python manage-kogito-version.py --bump-to 0.10.2 --artifacts-version 0.10.5 --examples-ref 0.10.x
```

The command above will update all the needed files to the given version(s).  
These changes include updates on

 - all cekit modules
 - image.yaml file descriptor
 - kogito-imagestream.yaml
 - tests files for default values
 

### Pushing Images to a local registry

This script will help you while building images and test in a local OpenShift Cluster. It requires you to already have
images built in your local registry with the tag following the patter: X.Z, e.g. 0.10:

```text
quay.io/kiegroup/kogito-jobs-service:0.10
```

The [Makefile](../Makefile) has an option to do it, it can be invoked as the following sample:

```bash
$ make push-local-registry REGISTRY=docker-registry-default.apps.test.cloud NS=test-1
```

Where **NS** stands for the namespace where the images will be available.

To execute the script directly:

```bash
$ /bin/sh scripts/push-local-registry.sh my_registry_address 0.10 my_namespace
```

### Pushing staging images

Staging images are the release candidates which are pushed mainly after big changes that has direct impact on how
the images will behave and also when new functionality is added.

The script updates the version on:

- all cekit modules
- image.yaml file descriptor
- kogito-imagestream.yaml

#### Script dependencies

The `push-staging.py` has a few dependencies that probably needs to be manually installed:

```bash
$ pip install -U docker yaml
$ pip install -U ruamel.yaml
```

#### Usage

This script is called as the last step of the `make push-staging` command defined on the [Makefile](../Makefile).

It will look for the current RC images available on [quay.io](https://quay.io/organization/kiegroup) to increase the rc tag 
accordingly then push the new tag so it can be tested by others. 

### Updating Kogito Images Service artifacts

The `update-maven-artifacts` script will help in fetching the artifacts from the Maven repository and update them into service modules (`module.yaml` will be updated)

#### Script dependencies

The `update-maven-artifacts.py` has some dependencies that needs to be manually installed:

```bash
$ pip install -U ruamel.yaml
$ pip install -U elementpath
```

#### Usage

Its default behavior is pretty simple:

```bash
$ python update-maven-artifacts.py
```

##### Update Maven artifact repository url

This script also accepts `--repo-url` as argument in specifying the Maven repository from where to fetch the artifacts

```bash
$ python update-maven-artifacts.py --repo-url='https://maven-repository.mirror.com/public'
```

If no argument is given, it takes the JBoss repository as the default value.

The command will update the needed files with the new URL:

- kogito-data-index/module.yaml
- kogito-jobs-service/module.yaml
- kogito-management-console/module.yaml
- kogito-trusty/module.yaml
- kogito-explainability/module.yaml

### Update tests script

The `update-tests` script allows you to change some information in order to perform some testing.

#### Script dependencies

The `update-tests.py` has some dependencies that needs to be manually installed:

```bash
$ pip install -U ruamel.yaml
```

#### Usage

##### Update repository url

```bash
$ python update-tests.py --repo-url 'https://maven-repository.mirror.com/public'
```

This will add this repository as an extra repository for artifacts to be retrieved from into the behave tests, next to the default JBoss repository.

You can also completely replace the main Jboss repository:

```bash
$ python update-tests.py --repo-url 'https://maven-repository.mirror.com/public' --replace-jboss-repo
```

##### Update artifacts version

```bash
$ python update-tests.py --artifacts-version 1.0.0
```

This will set the default artifacts version to 1.0.0 into the behave tests.

##### Update Examples URI and Ref

```bash
$ python update-tests.py --examples-uri https://github.com/<yournamespace>/kogito-examples --examples-ref 1.0.0
```

This will update the examples uri and/or the ref for the tests.

### Common script

The `common.py` script defines some common functions for the scripts.
