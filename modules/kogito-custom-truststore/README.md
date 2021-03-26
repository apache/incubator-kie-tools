# Kogito Custom TrustStore Module

This module adds the possibility to override the default Java TrustStore in the JVM process for any Kogito Service.

## How to Use

1. Add the self-signed certificates or your in-house certificates to the default JKS `cacerts` (or you can start a new one from scratch).
[Keystore Explorer](https://keystore-explorer.org/) is a great tool to manipulate JKS

2. Mount your file anywhere in your system using `docker volume`:

```shell
$ docker volume inspect truststores
[
    {
        "CreatedAt": "2021-03-23T12:53:18-03:00",
        "Driver": "local",
        "Labels": null,
        "Mountpoint": "/var/lib/docker/volumes/truststores/_data",
        "Name": "truststores",
        "Options": null,
        "Scope": "local"
    }
]
```

Make sure to move the `cacerts` file to `/var/lib/docker/volumes/truststores/_data` directory.

3. Mount this volume when running your Kogito service:

```shell
$ docker run --rm -it \
    -e CUSTOM_TRUSTSTORE=cacerts \
    -e CUSTOM_TRUSTSTORE_PASSWORD=changeit \
    -p 8080:8080  \
    --mount source=truststores,target=/home/kogito/certs \
    custom-truststore <imagename>
```

You should see the following message in the console if everything went fine:

```log
INFO ---> Configuring custom Java Truststore 'cacerts' in the path /home/kogito/certs/custom-truststore
```

## Key Takeaways

1. Make sure that the path is `/home/kogito/certs/custom-truststore`. The image **WON'T** read the certificate from anywhere else

2. The environment variable `CUSTOM_TRUSTSTORE` will tell the image the name of the desired file to read

3. `CUSTOM_TRUSTSTORE_PASSWORD` is an optional parameter, but it's a good practice to always have it set. The default password for `cacerts` store is `changeit`
