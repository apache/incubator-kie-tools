package org.kie.workbench.common.widgets.client.discussion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CommentLineViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle {
  private static CommentLineViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new CommentLineViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDBU{height:" + ("100%")  + ";min-width:" + ("120px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";}.GFVDQLFDPT{font-size:" + ("12px")  + ";}.GFVDQLFDOT{color:" + ("#06c")  + ";height:" + ("20px")  + ";font-size:" + ("12px")  + ";font-weight:" + ("bold")  + ";}.GFVDQLFDAU{font-size:") + (("8px")  + ";text-align:" + ("left")  + ";}")) : ((".GFVDQLFDBU{height:" + ("100%")  + ";min-width:" + ("120px")  + ";font-size:" + ("10px")  + ";padding:" + ("3px"+ " " +"10px"+ " " +"3px"+ " " +"10px")  + ";margin:" + ("3px"+ " " +"3px"+ " " +"3px"+ " " +"3px")  + ";}.GFVDQLFDPT{font-size:" + ("12px")  + ";}.GFVDQLFDOT{color:" + ("#06c")  + ";height:" + ("20px")  + ";font-size:" + ("12px")  + ";font-weight:" + ("bold")  + ";}.GFVDQLFDAU{font-size:") + (("8px")  + ";text-align:" + ("right")  + ";}"));
      }
      public java.lang.String author() {
        return "GFVDQLFDOT";
      }
      public java.lang.String comment() {
        return "GFVDQLFDPT";
      }
      public java.lang.String date() {
        return "GFVDQLFDAU";
      }
      public java.lang.String general() {
        return "GFVDQLFDBU";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
