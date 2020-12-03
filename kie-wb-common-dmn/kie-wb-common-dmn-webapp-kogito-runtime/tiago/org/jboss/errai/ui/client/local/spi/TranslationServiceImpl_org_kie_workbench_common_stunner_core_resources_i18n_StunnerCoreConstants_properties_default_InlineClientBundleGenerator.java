package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-core-common/7.47.0-SNAPSHOT/kie-wb-common-stunner-core-common-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/core/resources/i18n/StunnerCoreConstants_default.properties
      public String getText() {
        return "#\n# Copyright 2017 Red Hat, Inc. and/or its affiliates.\n#\n# Licensed under the Apache License, Version 2.0 (the \"License\");\n# you may not use this file except in compliance with the License.\n# You may obtain a copy of the License at\n#\n#   http://www.apache.org/licenses/LICENSE-2.0\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n#######################\n# Core messages\n#######################\norg.kie.workbench.common.stunner.core.error=Error\norg.kie.workbench.common.stunner.core.warn=Warn\norg.kie.workbench.common.stunner.core.info=Info\norg.kie.workbench.common.stunner.core.reason=Reason/s\norg.kie.workbench.common.stunner.core.delete=Delete\norg.kie.workbench.common.stunner.core.edit=Edit\norg.kie.workbench.common.stunner.core.print=Print\norg.kie.workbench.common.stunner.core.reset=Reset\norg.kie.workbench.common.stunner.core.increase=Increase\norg.kie.workbench.common.stunner.core.decrease=Decrease\norg.kie.workbench.common.stunner.core.fit=Fit\norg.kie.workbench.common.stunner.core.areYouSure=Are you sure?\norg.kie.workbench.common.stunner.core.element_uuid=Identifier\norg.kie.workbench.common.stunner.core.command.success=Command execution success\norg.kie.workbench.common.stunner.core.command.fail=Command execution failed\norg.kie.workbench.common.stunner.core.rule.success=Validation success\norg.kie.workbench.common.stunner.core.rule.fail=Validation failed\norg.kie.workbench.common.stunner.core.rule.element=[{0}]\\n {1}\norg.kie.workbench.common.stunner.core.rule.property=Property '{0}' {1}.\norg.kie.workbench.common.stunner.core.client.mediator.zoomArea=Select an area to jump into\n\n\n#######################\n# Rule Violation Types\n#######################\norg.kie.workbench.common.stunner.core.rule.violations.ContainmentRuleViolation=The '{0}' cannot accepts the containment for the candidate roles '{1}'\norg.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation=The '{0}' does not accepts docking the candidate roles '{1}'\norg.kie.workbench.common.stunner.core.rule.violations.ConnectionRuleViolation=The connection is not allowed for the edge with role '{0}'. The permitted connections are '{1}'\norg.kie.workbench.common.stunner.core.rule.violations.CardinalityMinRuleViolation=The diagram must contain a minimum of {1} occurrences for any of the following roles '{0}'. Currently found {2} occurrence/s\norg.kie.workbench.common.stunner.core.rule.violations.CardinalityMaxRuleViolation=The diagram must contain a maximum of {1} occurrences for any of the following roles '{0}'. Currently found {2} occurrence/s\norg.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMinRuleViolation=The node with roles '{0}', can have a minimum of {3} occurrences for {1} edge/s for '{2}' direction. Currently found {4} occurrences\norg.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMaxRuleViolation=The node with roles '{0}', can have a maximum of {3} occurrences for {1} edge/s for '{2}' direction. Currently found {4} occurrences\norg.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation={0} operation not allowed\norg.kie.workbench.common.stunner.core.rule.violations.EmptyConnectionViolation=Both connections must be present for the connector '{0}'. Actual [source='{1}', target='{2}']\n\n#######################\n# Toolbox\n#######################\norg.kie.workbench.common.stunner.core.client.toolbox.createNewConnector=Create\norg.kie.workbench.common.stunner.core.client.toolbox.createNewNode=Create\norg.kie.workbench.common.stunner.core.client.toolbox.morphInto=Convert into\n\n#######################\n# Toolbar\n#######################\norg.kie.workbench.common.stunner.core.client.toolbox.CopySelection=Copy selection\norg.kie.workbench.common.stunner.core.client.toolbox.CutSelection=Cut selection\norg.kie.workbench.common.stunner.core.client.toolbox.PasteSelection=Paste selection\norg.kie.workbench.common.stunner.core.client.toolbox.ClearShapes=Clear shape states\norg.kie.workbench.common.stunner.core.client.toolbox.ClearDiagram=Clear diagram\norg.kie.workbench.common.stunner.core.client.toolbox.DeleteSelection=Delete selection [DEL]\norg.kie.workbench.common.stunner.core.client.toolbox.ExportJPG=Download as JPG\norg.kie.workbench.common.stunner.core.client.toolbox.ExportPDF=Download as PDF\norg.kie.workbench.common.stunner.core.client.toolbox.ExportPNG=Download as PNG\norg.kie.workbench.common.stunner.core.client.toolbox.ExportSVG=Download as SVG\norg.kie.workbench.common.stunner.core.client.toolbox.ExportBPMN=Download as BPMN\norg.kie.workbench.common.stunner.core.client.toolbox.Redo=Redo [Ctrl+Shift+z]\norg.kie.workbench.common.stunner.core.client.toolbox.Save=Save\norg.kie.workbench.common.stunner.core.client.toolbox.SwitchGrid=Switch grid\norg.kie.workbench.common.stunner.core.client.toolbox.Undo=Undo [Ctrl+z]\norg.kie.workbench.common.stunner.core.client.toolbox.Validate=Validate\norg.kie.workbench.common.stunner.core.client.toolbox.VisitGraph=Play\norg.kie.workbench.common.stunner.core.client.toolbox.PerformAutomaticLayout=Perform automatic layout\n\norg.kie.workbench.common.stunner.core.client.toolbox.ConfirmClearDiagram=Clearing the diagram will remove all objects from the canvas permanently. You will not be able to undo this action. Proceed with caution.\n\n########################\n#ClientDiagramService\n########################\norg.kie.workbench.common.stunner.core.client.diagram.load.fail.unsupported=Diagram cannot be loaded. There are unsupported elements: '{0}'\norg.kie.workbench.common.stunner.core.client.diagram.automatic.layout.performed=The diagram had no layout information and has been laid out automatically. Please save changes in order to keep this layout.";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_properties::getContents()();
    }
    return null;
  }-*/;
}
