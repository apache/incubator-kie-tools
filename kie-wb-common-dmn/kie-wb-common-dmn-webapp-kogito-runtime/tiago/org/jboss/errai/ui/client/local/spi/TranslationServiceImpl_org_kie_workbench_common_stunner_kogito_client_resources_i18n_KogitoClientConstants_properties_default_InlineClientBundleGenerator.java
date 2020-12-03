package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_kogito_client_resources_i18n_KogitoClientConstants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_kogito_client_resources_i18n_KogitoClientConstants_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_kogito_client_resources_i18n_KogitoClientConstants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_kogito_client_resources_i18n_KogitoClientConstants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-kogito-client/7.47.0-SNAPSHOT/kie-wb-common-stunner-kogito-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/kogito/client/resources/i18n/KogitoClientConstants_default.properties
      public String getText() {
        return "#\n# Copyright 2019 Red Hat, Inc. and/or its affiliates.\n#\n# Licensed under the Apache License, Version 2.0 (the \"License\");\n# you may not use this file except in compliance with the License.\n# You may obtain a copy of the License at\n#\n#     http://www.apache.org/licenses/LICENSE-2.0\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\norg.kie.workbench.common.stunner.kogito.client.editor.DownloadDiagram=Download diagram\norg.kie.workbench.common.stunner.kogito.client.editor.ConfirmAction=Confirm action\norg.kie.workbench.common.stunner.kogito.client.editor.OnErrorConfirmUndoLastAction=An error happened [{0}]. Do you want to undo the last action?\norg.kie.workbench.common.stunner.kogito.client.editor.DiagramSaveSuccessful=Diagram saved successfully\norg.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorDefaultTitle=Project Diagram Editor\norg.kie.workbench.common.stunner.kogito.client.editor.DiagramParsingError=An error occurred parsing the diagram. There might be nodes not yet supported by the editor or the XML content can contain errors. You can try to repair it manually. {0}\norg.kie.workbench.common.stunner.kogito.client.editor.Documentation=Documentation\norg.kie.workbench.common.stunner.kogito.client.editor.Print=Print\n\nKogitoClientDiagramService.fileAlreadyExists0=File '{0}' already exists\nDiagramEditorPropertiesDock.title=Properties\nDiagramEditorPreviewAndExplorerDock.title=Explore Diagram\n";
      }
      public String getName() {
        return "getContents";
      }
    }
    ;
  }
  private static class getContentsInitializer {
    static {
      _instance0.getContentsInitializer();
    }
    static com.google.gwt.resources.client.TextResource get() {
      return getContents;
    }
  }
  public com.google.gwt.resources.client.TextResource getContents() {
    return getContentsInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_kogito_client_resources_i18n_KogitoClientConstants_properties::getContents()();
    }
    return null;
  }-*/;
}
