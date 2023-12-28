### Kogito helper scripts

On this directory you can find some python scripts used to help with some repetitive tasks.

Today we have these scripts:

- [build-product-image.sh](build-product-image.sh)
- [common.py](common.py)
- [list-images.py](list-images.py)
- [manage-kogito-version.py](manage-kogito-version.py)
- [push-local-registry.sh](push-local-registry.sh)
- [push-staging.py](push-staging.py)
- [run-bats.sh](run-bats.sh)
- [update-repository.py](update-repository.py)



### Build Product Image Script

Script should not be used to build community images. Handled by `make build-prod` command.

To switch the `build_engine` do the following:

```bash
make BUILD_ENGINE=osbs build-prod
```


It receives the Product image name to build the images.

Example: 

```bash 
cekit --verbose --redhat --descriptor logic-data-index-ephemeral-rhel8-image.yaml build docker
```

The product image name must respect the community image name:

 - rhpam-$(kogito_image_name)-rhel8


### Common script

The `common.py` script defines some common functions for the scripts.


### List Images Script

Utilitary script used to retrieve all images that can be built on this repo, there is possible to retrieve
the community image list:

```bash
$ python list-images.py
```

And the product image list by using the `--prod` flag:

```bash
$ python list-images.py --prod
```


### Managing Kogito images version script

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
 - *-image.yaml files descriptor for each container image
 - kogito-imagestream.yaml
 - tests files for default values
 

### Pushing Images to a local registry

This script will help you while building images and test in a local OpenShift Cluster. It requires you to already have
images built in your local registry with the tag following the patter: X.Z, e.g. 0.10:

```text
quay.io/kiegroup/kogito-jobs-service-ephemeral:0.10
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
- *-image.yaml files descriptor for each container image
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
If there is no need to update the tag, there is the option to override it, just set the flag "-o".


### Update tests script

The `update-repository` script allows you to change some build & test information in the repository.

#### Script dependencies

The `update-repository.py` has some dependencies that needs to be manually installed:

```bash
$ pip install -U ruamel.yaml
```

#### Usage

##### Update repository url

```bash
$ python update-repository.py --repo-url 'https://maven-repository.mirror.com/public'
```

This will add this repository as an extra repository for artifacts to be retrieved from into the behave tests, next to the default JBoss repository.

You can also completely replace the main Jboss repository:

```bash
$ python update-repository.py --repo-url 'https://maven-repository.mirror.com/public' --replace-jboss-repo
```

##### Update artifacts version

```bash
$ python update-repository.py --artifacts-version 1.0.0
```

This will set the default artifacts version.

##### Update quarkus version

```bash
$ python update-repository.py --quarkus-platform-version 3.2.9.Final
```

This will set the image quarkus version to 3.2.9.Final.

##### Update Examples URI and Ref

```bash
$ python update-repository.py --examples-uri https://github.com/<yournamespace>/kogito-examples --examples-ref 1.0.0
```

This will update the examples uri and/or the ref for the tests.
