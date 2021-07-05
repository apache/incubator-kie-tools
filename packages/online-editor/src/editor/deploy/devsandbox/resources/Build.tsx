/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { HttpMethod, JAVA_RUNTIME_VERSION, Resource, ResourceArgs, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/build.openshift.io/v1";

export interface Build extends Resource {
  status: {
    phase: "New" | "Pending" | "Running" | "Complete" | "Failed" | "Error" | "Cancelled";
  };
}

export interface Builds {
  items: Build[];
}

export interface CreateBuildArgs {
  buildConfigUid: string;
  model: {
    filename: string;
    content: string;
  };
  urls: {
    index: string;
    swaggerUI: string;
    onlineEditor: string;
  };
}

export class CreateBuild extends ResourceFetch {
  private readonly BASE_IMAGE = "quay.io/caponetto/dmn-dev-sandbox-deployment-base:latest";
  private readonly KOGITO_FOLDER = "/tmp/kogito";
  private readonly PROJECT_FOLDER = `${this.KOGITO_FOLDER}/project`;
  private readonly PROJECT_MAIN_RESOURCES = `${this.PROJECT_FOLDER}/src/main/resources`;
  private readonly PROJECT_METAINF_RESOURCES = `${this.PROJECT_MAIN_RESOURCES}/META-INF/resources`;
  private readonly QUARKUS_APP_FOLDER = `${this.PROJECT_FOLDER}/target/quarkus-app`;
  private readonly DEPLOYMENTS_FOLDER = "/deployments";
  private readonly POM_PATH = `${this.PROJECT_FOLDER}/pom.xml`;
  private readonly MVNW_PATH = `${this.KOGITO_FOLDER}/mvnw`;
  private readonly FORM_SCHEMA_GENERATOR_PATH = `${this.KOGITO_FOLDER}/dmn-form-schema-generator.jar`;
  private readonly DATA_PATH = `${this.PROJECT_METAINF_RESOURCES}/data.json`;

  public constructor(protected args: ResourceArgs & CreateBuildArgs) {
    super(args);
  }

  protected method(): HttpMethod {
    return "POST";
  }

  protected requestBody(): string | undefined {
    const diagramPath = `${this.PROJECT_METAINF_RESOURCES}/${this.args.model.filename}`;

    return `
      kind: Build
      apiVersion: build.openshift.io/v1
      metadata:
        annotations:
          openshift.io/build-config.name: ${this.args.resourceName}
          openshift.io/build.number: '1'
          openshift.io/build.pod-name: ${this.args.resourceName}
        name: ${this.args.resourceName}
        namespace: ${this.args.namespace}
        ownerReferences:
          - apiVersion: build.openshift.io/v1
            kind: BuildConfig
            name: ${this.args.resourceName}
            uid: ${this.args.buildConfigUid}
            controller: true
        labels:
          app: ${this.args.resourceName}
          app.kubernetes.io/component: ${this.args.resourceName}
          app.kubernetes.io/instance: ${this.args.resourceName}
          app.kubernetes.io/part-of: ${this.args.resourceName}
          app.kubernetes.io/name: java
          app.openshift.io/runtime: quarkus
          app.openshift.io/runtime-version: ${JAVA_RUNTIME_VERSION}
          openshift.io/build.start-policy: Serial
          buildconfig: ${this.args.resourceName}
          openshift.io/build-config.name: ${this.args.resourceName}
      spec:
        output:
          to:
            kind: ImageStreamTag
            name: ${this.args.resourceName}:latest
        triggeredBy:
          - message: Triggered by Kogito Business Modeler (REST API)
        strategy:
          dockerStrategy:
              noCache: true
        source:
          dockerfile: |
            FROM ${this.BASE_IMAGE}
            ENV MAVEN_OPTS="-Xmx256m -Xms128m" JAVA_OPTS="-Xmx256m -Xms128m"
            RUN echo $'${this.args.model.content}' > '${diagramPath}' \
                && java -jar ${this.FORM_SCHEMA_GENERATOR_PATH} '${diagramPath}' ${this.DATA_PATH} ${this.args.urls.index} ${this.args.urls.onlineEditor} ${this.args.urls.swaggerUI} \
                && ${this.MVNW_PATH} clean package -f ${this.POM_PATH} \
                && cp ${this.QUARKUS_APP_FOLDER}/*.jar ${this.DEPLOYMENTS_FOLDER} \
                && cp -R ${this.QUARKUS_APP_FOLDER}/lib/ ${this.DEPLOYMENTS_FOLDER} \
                && cp -R ${this.QUARKUS_APP_FOLDER}/app/ ${this.DEPLOYMENTS_FOLDER} \
                && cp -R ${this.QUARKUS_APP_FOLDER}/quarkus/ ${this.DEPLOYMENTS_FOLDER} \
                && rm -fr ~/.m2
    `;
  }

  public name(): string {
    return CreateBuild.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/builds`;
  }
}

export class ListBuilds extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected requestBody(): string | undefined {
    return;
  }

  public name(): string {
    return ListBuilds.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/builds`;
  }
}

export class DeleteBuild extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  protected requestBody(): string | undefined {
    return;
  }

  public name(): string {
    return DeleteBuild.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/builds/${this.args.resourceName}`;
  }
}
