# Productization notes for Red Hat OpenShift Serverless Logic Operator
## Introduction

The Kogito Servereless Operator is the upstream project for the Red Hat OpenShift Serverless Logic Operator.

In this document you can find some notes useful if you need to productize the Operator image and its bundle.

In order to build the Operator and its bundle in a Red Hat environment you need to have a [CeKit](https://cekit.io/) 
installed on your machine.

## Operator image


In order to build an operator image you have to execute the following command:

```shell
make -f Makefile.osl container-build
```

This will produce a scratch build, if you would like to release it add the `RELEASE=true` flag and so:

```shell
make -f Makefile.osl container-build RELEASE=true
```

## Bundle image


In order to build a bundle image you have to execute the following command:

```shell
make -f Makefile.osl bundle-build
```

This will produce a scratch build, if you would like to release it add the `RELEASE=true` flag and so:

```shell
make -f Makefile.osl bundle-build RELEASE=true
```

If you would like to release the bundle referring to a particular operator image digest different from the default one reported
into the Makefile, you can do it using these commands:

```shell
make -f Makefile.osl generate-all USE_IMAGE_DIGESTS=true IMAGE_DIGEST=sha256:aae0198cbd4a9d92130437d3869b6da8854ba3a7c229956e172b621aac3261f3

make -f Makefile.osl bundle-build RELEASE=true
```