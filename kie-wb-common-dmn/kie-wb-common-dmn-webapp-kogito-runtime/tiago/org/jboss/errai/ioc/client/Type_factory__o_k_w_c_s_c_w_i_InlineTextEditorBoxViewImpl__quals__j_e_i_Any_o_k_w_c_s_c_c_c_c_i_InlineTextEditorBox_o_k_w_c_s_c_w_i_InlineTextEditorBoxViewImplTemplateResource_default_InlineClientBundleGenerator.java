package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox_o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource {
  private static Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox_o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox_o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-widgets/7.47.0-SNAPSHOT/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html
      public String getText() {
        return "<div data-field=\"editNameBox\" tabindex=\"-1\">\n    <div class=\"inlineNameEditBoxNameBox\" role=\"textbox\" contenteditable=\"true\" data-field=\"nameField\"></div>\n</div>\n";
      }
      public String getName() {
        return "getContents";
      }
    }
    ;
  }
  private static class getContentsInitializer {
    static {
      _instance0.getContentsInitializer();
    }
    static com.google.gwt.resources.client.TextResource get() {
      return getContents;
    }
  }
  public com.google.gwt.resources.client.TextResource getContents() {
    return getContentsInitializer.get();
  }
  private void getStyleInitializer() {
    getStyle = new com.google.gwt.resources.client.CssResource() {
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
        return "getStyle";
      }
      public String getText() {
        return (".inlineNameEditBoxNameBox{position:" + ("absolute")  + ";content:" + ("\"\\A \"")  + ";white-space:" + ("pre-line")  + ";outline:" + ("none")  + ";overflow:" + ("hidden")  + ";overflow-wrap:" + ("break-word")  + ";height:" + ("auto")  + ";border:" + ("none")  + ";resize:" + ("none")  + ";background-color:" + ("transparent")  + ";font-weight:") + (("normal")  + ";line-height:" + ("1.2")  + ";-webkit-border-radius:" + ("7px")  + ";-moz-border-radius:" + ("7px")  + ";border-radius:" + ("7px")  + ";-webkit-user-modify:" + ("read-write-plaintext-only")  + ";}[data-text]:empty:before{content:" + ("attr(data-text)")  + ";color:" + ("#888")  + ";font-style:" + ("italic")  + ";}");
      }
    }
    ;
  }
  private static class getStyleInitializer {
    static {
      _instance0.getStyleInitializer();
    }
    static com.google.gwt.resources.client.CssResource get() {
      return getStyle;
    }
  }
  public com.google.gwt.resources.client.CssResource getStyle() {
    return getStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  private static com.google.gwt.resources.client.CssResource getStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
      getStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
        resourceMap.put("getStyle", getStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}
