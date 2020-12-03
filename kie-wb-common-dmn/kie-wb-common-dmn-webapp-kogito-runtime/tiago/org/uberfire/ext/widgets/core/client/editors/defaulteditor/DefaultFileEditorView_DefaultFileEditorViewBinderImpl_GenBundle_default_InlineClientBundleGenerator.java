package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle {
  private static DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDAT{margin-top:" + ("15px")  + ";}.GFVDQLFDPS{padding-right:" + ("0")  + ";padding-left:" + ("0")  + ";}")) : ((".GFVDQLFDAT{margin-top:" + ("15px")  + ";}.GFVDQLFDPS{padding-left:" + ("0")  + ";padding-right:" + ("0")  + ";}"));
      }
      public java.lang.String editor() {
        return "GFVDQLFDPS";
      }
      public java.lang.String topMargin() {
        return "GFVDQLFDAT";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
