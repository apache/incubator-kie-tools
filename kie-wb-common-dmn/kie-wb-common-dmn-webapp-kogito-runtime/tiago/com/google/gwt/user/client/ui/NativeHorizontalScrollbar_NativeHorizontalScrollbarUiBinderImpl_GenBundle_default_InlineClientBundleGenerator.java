package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle {
  private static NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDNT{position:" + ("relative")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDMT{position:" + ("absolute")  + ";right:" + ("0")  + ";bottom:" + ("0")  + ";width:" + ("100%")  + ";height:" + ("100px")  + ";overflow:" + ("auto")  + ";overflow-x:" + ("scroll")  + ";overflow-y:" + ("hidden")  + ";}.GFVDQLFDLT{height:") + (("1px")  + ";}")) : ((".GFVDQLFDNT{position:" + ("relative")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDMT{position:" + ("absolute")  + ";left:" + ("0")  + ";bottom:" + ("0")  + ";width:" + ("100%")  + ";height:" + ("100px")  + ";overflow:" + ("auto")  + ";overflow-x:" + ("scroll")  + ";overflow-y:" + ("hidden")  + ";}.GFVDQLFDLT{height:") + (("1px")  + ";}"));
      }
      public java.lang.String content() {
        return "GFVDQLFDLT";
      }
      public java.lang.String scrollable() {
        return "GFVDQLFDMT";
      }
      public java.lang.String viewport() {
        return "GFVDQLFDNT";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
