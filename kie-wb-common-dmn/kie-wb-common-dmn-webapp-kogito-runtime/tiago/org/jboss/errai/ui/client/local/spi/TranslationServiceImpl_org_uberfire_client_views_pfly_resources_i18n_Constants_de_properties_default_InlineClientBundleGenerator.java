package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_de_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_de_properties {
  private static TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_de_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_de_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT.jar!/org/uberfire/client/views/pfly/resources/i18n/Constants_de.properties
      public String getText() {
        return "Actions=Aktionen\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nJanuary=Januar\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nFebruary=Februar\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nMarch=März\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nApril=April\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nMay=Mai\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nJune=Juni\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nJuly=Juli\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nAugust=August\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nSeptember=September\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nOctober=Oktober\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nNovember=November\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/CommonConstants, author jdimanos\nDecember=Dezember\n# translation auto-copied from project KIE webapp distributions, version 6.0.0, document org.kie/kie-wb-webapp/org/kie/workbench/client/resources/i18n/AppConstants, author jdimanos\nHome=Home\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_de_properties::getContents()();
    }
    return null;
  }-*/;
}
