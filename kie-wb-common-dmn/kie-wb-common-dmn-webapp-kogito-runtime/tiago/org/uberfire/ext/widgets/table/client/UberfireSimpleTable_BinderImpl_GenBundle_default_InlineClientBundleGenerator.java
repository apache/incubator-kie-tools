package org.uberfire.ext.widgets.table.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class UberfireSimpleTable_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenBundle {
  private static UberfireSimpleTable_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new UberfireSimpleTable_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDDT{padding-top:" + ("10px")  + ";padding-bottom:" + ("10px")  + ";}.GFVDQLFDET{height:" + ("32px")  + ";}.GFVDQLFDFT{float:" + ("left")  + ";text-align:" + ("left")  + ";}.GFVDQLFDCT{text-align:" + ("center")  + ";}")) : ((".GFVDQLFDDT{padding-top:" + ("10px")  + ";padding-bottom:" + ("10px")  + ";}.GFVDQLFDET{height:" + ("32px")  + ";}.GFVDQLFDFT{float:" + ("right")  + ";text-align:" + ("right")  + ";}.GFVDQLFDCT{text-align:" + ("center")  + ";}"));
      }
      public java.lang.String centerToolBar() {
        return "GFVDQLFDCT";
      }
      public java.lang.String dataGridContainer() {
        return "GFVDQLFDDT";
      }
      public java.lang.String horizontalContainer() {
        return "GFVDQLFDET";
      }
      public java.lang.String rightToolBar() {
        return "GFVDQLFDFT";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.uberfire.ext.widgets.table.client.UberfireSimpleTable_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
