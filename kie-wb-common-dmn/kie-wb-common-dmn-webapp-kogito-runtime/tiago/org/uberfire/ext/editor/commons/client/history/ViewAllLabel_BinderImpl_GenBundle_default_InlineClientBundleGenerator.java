package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ViewAllLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenBundle {
  private static ViewAllLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ViewAllLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenCss_style() {
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
        return (".GFVDQLFDJU{width:" + ("400px")  + ";height:" + ("10px")  + ";font-size:" + ("10px")  + ";padding:" + ("20px"+ " " +"10px"+ " " +"15px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";}.GFVDQLFDIU{height:" + ("20px")  + ";font-size:" + ("12px")  + ";}.GFVDQLFDKU{color:" + ("blue")  + ";}");
      }
      public java.lang.String comment() {
        return "GFVDQLFDIU";
      }
      public java.lang.String general() {
        return "GFVDQLFDJU";
      }
      public java.lang.String link() {
        return "GFVDQLFDKU";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.uberfire.ext.editor.commons.client.history.ViewAllLabel_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
