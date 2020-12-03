package org.uberfire.ext.widgets.core.client.editors.texteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TextEditorView_TextEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenBundle {
  private static TextEditorView_TextEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new TextEditorView_TextEditorViewBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenCss_style() {
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
        return (".GFVDQLFDBT{margin:" + ("10px"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";resize:" + ("none")  + ";}");
      }
      public java.lang.String betterSetup() {
        return "GFVDQLFDBT";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView_TextEditorViewBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
