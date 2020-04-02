### Kogito helper scripts

On this directory you can find some python scripts used to help with some repetitive tasks.

Today we have these scripts:

- [manage-kogito-version.py](manage-kogito-version.py)
- [push-staging.py](push-staging.py)


### Managing Kogito images version

The manage-kogito-version script will help when we need to update the current version due a new release or prepare the
master branch for the next release.

#### Script dependencies

The `manage-kogito-version.py` has one dependency that needs to be manually installed:

```bash
$ pip install -U ruamel.yaml
```

Its usage is pretty simple, only one parameter is accepted:

```bash
$ python manage-kogito-version.py --bump-to 1.0.0  
```

The command above will update all the needed files to the version 1.0.0. These changes includes updates on

 - all cekit modules
 - image.yaml file descriptor
 - kogito-imagestream.yaml
 

### Pushing staging images.

Staging images are the release candidates which are pushed mainly after big changes that has direct impact on how
the images will behave and also when new functionality is added.

#### Script dependencies

The `push-stating.py` has a few dependencies that probably needs to be manually installed:

```bash
$ pip install -U docker yaml
```

This script is called as the last step of the `make push-staging` command defined on the [Makefile](../Makefile).

It will look for the current RC images available on [quay.io](https://quay.io/organization/kiegroup) to increase the rc tag 
accordingly then push the new tag so it can be tested by others. 

