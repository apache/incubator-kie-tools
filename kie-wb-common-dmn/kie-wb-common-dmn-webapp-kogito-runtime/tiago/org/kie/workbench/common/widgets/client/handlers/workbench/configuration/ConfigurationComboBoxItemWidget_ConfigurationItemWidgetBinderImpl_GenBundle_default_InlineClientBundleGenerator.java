package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle {
  private static ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "style";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDNR{margin:" + ("10px")  + ";}.GFVDQLFDOR{height:" + ("32px")  + ";}.GFVDQLFDPR{float:" + ("left")  + ";text-align:" + ("left")  + ";margin-left:" + ("10px")  + ";}.GFVDQLFDMR{text-align:" + ("center")  + ";}")) : ((".GFVDQLFDNR{margin:" + ("10px")  + ";}.GFVDQLFDOR{height:" + ("32px")  + ";}.GFVDQLFDPR{float:" + ("right")  + ";text-align:" + ("right")  + ";margin-right:" + ("10px")  + ";}.GFVDQLFDMR{text-align:" + ("center")  + ";}"));
      }
      public java.lang.String center() {
        return "GFVDQLFDMR";
      }
      public java.lang.String container() {
        return "GFVDQLFDNR";
      }
      public java.lang.String horizontalContainer() {
        return "GFVDQLFDOR";
      }
      public java.lang.String right() {
        return "GFVDQLFDPR";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style style;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      style(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("style", style());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'style': return this.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
