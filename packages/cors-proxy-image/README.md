<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@kie-tools/cors-proxy](https://github.com/apache/incubator-kie-tools/packages/cors-proxy).

## Additional requirements

- docker

## Build

Enable the image to be built:

```bash
export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name, tags and port can be customized by setting the following environment variables:

```bash
export CORS_PROXY_IMAGE__imageRegistry=<registry>
export CORS_PROXY_IMAGE__imageAccount=<account>
export CORS_PROXY_IMAGE__imageName=<image-name>
export CORS_PROXY_IMAGE__imageBuildTag=<image-tag>
export CORS_PROXY_IMAGE__imagePort=<port>
export CORS_PROXY_IMAGE__imageOrigin=<origin>
export CORS_PROXY_IMAGE__imageVerbose=<verbose>
```

Default values can be found [here](./env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
pnpm @kie-tools/cors-proxy-image... build:prod
```

Then check out the image:

```bash
docker images
```

## Run

Start up a new container with:

```bash
docker run -p 8080:8080 -i --rm docker.io/apache/incubator-kie-cors-proxy:main
```

The service will be up at http://localhost:8080

## Running with an external proxy

When starting the container, pass the `HTTP_PROXY`/`HTTPS_PROXY` environment variable pointing to the URL of your proxy service:

```bash
docker run -p 8080:8080 -i --rm -e HTTPS_PROXY=<YOUR_PROXY_URL> docker.io/apache/incubator-kie-cors-proxy:main
```

While testing you might want to use a local proxy server with a local certificate, like [mitmproxy](https://mitmproxy.org/).

After installing mitmproxy and starting it, run the `cors-proxy` container passing the `HTTPS_PROXY` and `NODE_EXTRA_CA_CERTS` environment variables to configure the proxy. Remember to mount a volume with the certificate inside of the container.

Start the mitmproxy service:

```bash
mitmweb --set listen_port=<PORT> --showhost
```

Run the cors-proxy container:

```bash
docker run --rm -it --network host -e HTTPS_PROXY=http://localhost:<PORT> -e NODE_EXTRA_CA_CERTS=/tmp/certificates/mitmproxy-ca-cert.pem -v ~/.mitmproxy:/tmp/certificates -d -p 8080:8080 docker.io/apache/incubator-kie-cors-proxy:main
```

> Note that we are using `--network host` in this case because the proxy service is running locally and we want the cors-proxy service to reach it.

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
