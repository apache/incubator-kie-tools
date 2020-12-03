package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_properties {
  private static TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT.jar!/org/uberfire/client/views/pfly/resources/i18n/Constants_default.properties
      public String getText() {
        return "Actions=Actions\nApplyLabel=Apply\nCancelLabel=Cancel\nFromLabel=From\nToLabel=To\nCustomRangeLabel=Custom\nWeekLabel=W\nSundayShort=Su\nMondayShort=Mo\nTuesdayShort=Tu\nWednesdayShort=We\nThursdayShort=Th\nFridayShort=Fr\nSaturdayShort=Sa\nJanuary=January\nFebruary=February\nMarch=March\nApril=April\nMay=May\nJune=June\nJuly=July\nAugust=August\nSeptember=September\nOctober=October\nNovember=November\nDecember=December\nMenu=Menu\nHome=Home\n\nErrorPopupView.PopupTitle=Error\nErrorPopupView.ShowDetailLabel=Show Details\nErrorPopupView.CloseDetailLabel=Close Details\nErrorPopupView.Close=Close\n\nConfirmPopup.Cancel=Cancel";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_properties::getContents()();
    }
    return null;
  }-*/;
}
