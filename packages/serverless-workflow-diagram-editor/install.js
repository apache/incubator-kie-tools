/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const buildEnv = require("./env");
const { setup } = require("@kie-tools/maven-config-setup-helper");

const fs = require("fs").promises;
const path = require("path");
const download = require("mvn-artifact-download").default;
const AdmZip = require("adm-zip");

const stunner_lienzo_path = "kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-lienzo/";
const tempPath = stunner_lienzo_path + "target/external-deps";
const resources_path =
  stunner_lienzo_path + "src/main/resources/org/kie/workbench/common/stunner/client/lienzo/resources";

setup(`
    -Drevision=${buildEnv.env.swfDiagramEditor.version}
`);

console.info("[serverless-workflow-diagram-editor] Download maven resources...");

const artifacts = new Map([
  [
    {
      groupId: "org.uberfire",
      artifactId: "uberfire-workbench-client-views-patternfly",
      version: buildEnv.env.swfDiagramEditor.UBERFIRE__version,
    },
    [
      {
        path: "org/uberfire/client/views/static/js/patternfly.min.js",
        type: "js",
      },
      {
        path: "org/uberfire/client/views/static/bootstrap-select/js/bootstrap-select.min.js",
        type: "js",
      },
      {
        path: "org/uberfire/client/views/static/css/patternfly-additions.min.css",
        type: "css",
      },
      {
        path: "org/uberfire/client/views/static/css/patternfly.min.css",
        type: "css",
      },
      {
        path: "org/uberfire/client/views/static/uberfire-patternfly.css",
        type: "css",
      },
    ],
  ],
  [
    {
      groupId: "org.webjars",
      artifactId: "font-awesome",
      version: buildEnv.env.swfDiagramEditor.FONT_AWESOME__version,
    },
    [
      {
        path:
          "META-INF/resources/webjars/font-awesome/" +
          buildEnv.env.swfDiagramEditor.FONT_AWESOME__version +
          "/css/font-awesome.min.css",
        type: "css",
      },
    ],
  ],
  [
    {
      groupId: "org.gwtbootstrap3",
      artifactId: "gwtbootstrap3",
      version: buildEnv.env.swfDiagramEditor.GWTBOOTSTRAP3__version,
    },
    [
      {
        path: "org/gwtbootstrap3/client/resource/js/jquery-1.12.4.min.cache.js",
        type: "js",
      },
      {
        path: "org/gwtbootstrap3/client/resource/js/gwtbootstrap3.js",
        type: "js",
      },
    ],
  ],
  [
    {
      groupId: "org.webjars",
      artifactId: "bootstrap",
      version: buildEnv.env.swfDiagramEditor.BOOTSTRAP__version,
    },
    [
      {
        path:
          "META-INF/resources/webjars/bootstrap/" +
          buildEnv.env.swfDiagramEditor.BOOTSTRAP__version +
          "/js/bootstrap.min.js",
        type: "js",
      },
    ],
  ],
  [
    {
      groupId: "org.webjars",
      artifactId: "animate.css",
      version: buildEnv.env.swfDiagramEditor.ANIMATE_CSS__version,
    },
    [
      {
        path:
          "META-INF/resources/webjars/animate.css/" +
          buildEnv.env.swfDiagramEditor.ANIMATE_CSS__version +
          "/animate.min.css",
        type: "css",
      },
    ],
  ],
]);

processMavenDependencies();

async function prepareFolders() {
  try {
    await fs.mkdir(tempPath, { recursive: true });
    await fs.mkdir(resources_path + "/js", { recursive: true });
    await fs.mkdir(resources_path + "/css", { recursive: true });
  } catch (err) {
    console.error(err);
  }
}

async function processMavenDependencies() {
  await prepareFolders(tempPath);
  artifacts.forEach((resources, artifact) => {
    download(artifact, tempPath).then((jarPath) => {
      var zip = new AdmZip(jarPath);
      resources.forEach((resource) => {
        let fileName = path.basename(resource.path);
        if (resource.type === "js") {
          zip.extractEntryTo(resource.path, resources_path + "/js/", false, true, false, fileName + ".noproc");
        } else if (resource.type === "css") {
          zip.extractEntryTo(resource.path, resources_path + "/css/", false, true);
        }
      });
    });
  });
}
