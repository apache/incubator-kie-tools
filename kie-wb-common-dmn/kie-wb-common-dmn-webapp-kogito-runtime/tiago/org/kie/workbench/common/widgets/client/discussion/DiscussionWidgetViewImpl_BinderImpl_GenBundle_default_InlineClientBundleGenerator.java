package org.kie.workbench.common.widgets.client.discussion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class DiscussionWidgetViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle {
  private static DiscussionWidgetViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new DiscussionWidgetViewImpl_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style() {
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
        return (".GFVDQLFDLR{text-align:" + ("center")  + ";}.GFVDQLFDJR{padding-top:" + ("20px")  + ";}.GFVDQLFDKR{max-height:" + ("360px")  + ";}");
      }
      public java.lang.String comment() {
        return "GFVDQLFDJR";
      }
      public java.lang.String commentScroll() {
        return "GFVDQLFDKR";
      }
      public java.lang.String textAlign() {
        return "GFVDQLFDLR";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
