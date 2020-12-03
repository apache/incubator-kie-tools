package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class VersionMenuItemLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenBundle {
  private static VersionMenuItemLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new VersionMenuItemLabel_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDFU{width:" + ("95%")  + ";height:" + ("40px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";}.GFVDQLFDGU{width:" + ("95%")  + ";height:" + ("40px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";background-color:") + (("lightblue")  + ";}.GFVDQLFDEU{clear:" + ("both")  + ";height:" + ("20px")  + ";font-size:" + ("12px")  + ";padding-right:" + ("10px")  + ";}.GFVDQLFDHU{font-weight:" + ("bold")  + ";font-size:" + ("12px")  + ";}.GFVDQLFDCU{color:" + ("lightgray")  + ";}.GFVDQLFDDU{color:" + ("white")  + ";}")) : ((".GFVDQLFDFU{width:" + ("95%")  + ";height:" + ("40px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";}.GFVDQLFDGU{width:" + ("95%")  + ";height:" + ("40px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";background-color:") + (("lightblue")  + ";}.GFVDQLFDEU{clear:" + ("both")  + ";height:" + ("20px")  + ";font-size:" + ("12px")  + ";padding-left:" + ("10px")  + ";}.GFVDQLFDHU{font-weight:" + ("bold")  + ";font-size:" + ("12px")  + ";}.GFVDQLFDCU{color:" + ("lightgray")  + ";}.GFVDQLFDDU{color:" + ("white")  + ";}"));
      }
      public java.lang.String author() {
        return "GFVDQLFDCU";
      }
      public java.lang.String authorSelected() {
        return "GFVDQLFDDU";
      }
      public java.lang.String comment() {
        return "GFVDQLFDEU";
      }
      public java.lang.String normal() {
        return "GFVDQLFDFU";
      }
      public java.lang.String selected() {
        return "GFVDQLFDGU";
      }
      public java.lang.String version() {
        return "GFVDQLFDHU";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.uberfire.ext.editor.commons.client.history.VersionMenuItemLabel_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
