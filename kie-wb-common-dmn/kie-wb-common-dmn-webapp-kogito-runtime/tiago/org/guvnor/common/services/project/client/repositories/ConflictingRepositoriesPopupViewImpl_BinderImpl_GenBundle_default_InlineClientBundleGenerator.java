package org.guvnor.common.services.project.client.repositories;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle {
  private static ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDFR{float:" + ("right")  + ";padding-left:" + ("20px")  + ";}")) : ((".GFVDQLFDFR{float:" + ("left")  + ";padding-right:" + ("20px")  + ";}"));
      }
      public java.lang.String icon() {
        return "GFVDQLFDFR";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
