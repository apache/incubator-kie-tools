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

# SWF Editor - Kogito Webapp

The Serverless Workflow Diagram Editor's webapp bundle for all kogito channels.

It results in a client side web application that can be just deployed in any web server.

## Building

- Build Stunner and its dependencies:
  - Change to the `kie-tools/packages/serverless-workflow-diagram-editor/` root folder
  - run `mvn clean install -DskipTests`
- Build the webapp:
  - Change to the `kie-tools/packages/serverless-workflow-diagram-editor/` root folder
  - Dev: `mvn -T 8C clean install -DskipTests=true`
  - Production: `mvn -T 8C clean install -DskipTests=true`

## Running in an application server

Following commands assume the use of Widfly:

- Copy the generated file `sw-editor-kogito-app/target/sw-editor-kogito-app.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080/test.html`

## Running in DevMode

Change to the `kie-tools/packages/serverless-workflow-diagram-editor/` root folder

- set compilationLevel of j2cl-maven-plugin to `BUNDLE_JAR` in `kie-tools/packages/serverless-workflow-diagram-editor/sw-editor/sw-editor-kogito-app/pom.xml`
- run `mvn clean org.kie.j2cl.tools:j2cl-maven-plugin:watch` in the `kie-tools/packages/serverless-workflow-diagram-editor` root folder
- once the build is finished (there will be `Build Complete: ready for browser refresh` in the terminal window), open another terminal in `kie-tools/packages/serverless-workflow-diagram-editor/sw-editor/sw-editor-kogito-app/target/sw-editor-kogito-app/org.kie.workbench.common.stunner.sw.KogitoSWEditor` folder
- start a local web server by: `python3 -m SimpleHTTPServer 8001` or `http-server -p 8001` or any other web server
- Navigate to `http://localhost:8001/test.html`

In any question, please follow the j2cl-maven-plugin documentation

## Usage

- Editor's test page

Navigate to the test page in your root context path, eg: `http://localhost:8080/test.html`. The UI provides some buttons for creating new workflows, opening an existing workflow and exporting the actual workflow.

- Editor's Js API:

        // For creating a workflows
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().setContent("", "")

        // For loading a workflows (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().setContent("", raw)

        // Get the actual workflows' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().getContent()
