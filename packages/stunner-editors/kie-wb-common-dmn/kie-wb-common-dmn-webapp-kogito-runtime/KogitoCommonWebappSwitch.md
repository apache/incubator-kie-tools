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

# Modifications done to switch to kie-wb-common-kogito-webapp-base

## kie-wb-common-dmn-webapp-kogito-common

**pom.xml**:

add:

    <dependency>
         <groupId>org.kie.kogito.stunner.editors</groupId>
         <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
         <scope>provided</scope>
    </dependency>

**DMNWebappKogitoCommon.gwt.xml**:

add:

    <inherits name="org.kie.workbench.common.kogito.webapp.base.KogitoWebappBase" />

**Java sources**:

removed:

_org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds.DummyAuthenticationService
org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds.MockWorkspaceProjectService
org.kie.workbench.common.dmn.webapp.kogito.common.backend.ApplicationScopedProducer
org.kie.workbench.common.dmn.webapp.kogito.common.client.workarounds.IsKogito
org.kie.workbench.common.dmn.webapp.kogito.common.client.workarounds.IsKogitoTest_

add:

_org.kie.workbench.common.dmn.webapp.kogito.common.backend.IOServiceNio2WrapperProviderImpl_

## kie-wb-common-dmn-webapp-kogito-runtime

**pom.xml**:

add:

     <dependency>
      <groupId>org.kie.kogito.stunner.editors</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.kie.kogito.stunner.editors</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
      <classifier>sources</classifier>
    </dependency>

**Java sources**:

removed:

_org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective_
_org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspectiveTest_

modified:

_org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor_

    ...
    @Default
    public class DMNDiagramEditor extends AbstractDMNDiagramEditor implements KogitoScreen {

        private static final PlaceRequest DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(AbstractDMNDiagramEditor.EDITOR_ID);
        ...
        @Override
        public PlaceRequest getPlaceRequest() {
          return DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
        }
        ...
