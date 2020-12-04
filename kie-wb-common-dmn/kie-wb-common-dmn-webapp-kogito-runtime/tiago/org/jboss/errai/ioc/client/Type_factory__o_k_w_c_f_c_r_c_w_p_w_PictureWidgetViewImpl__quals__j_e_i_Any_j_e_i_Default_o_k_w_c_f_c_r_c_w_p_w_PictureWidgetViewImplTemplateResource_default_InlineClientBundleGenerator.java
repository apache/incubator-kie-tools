package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource {
  private static Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-forms/kie-wb-common-forms-commons/kie-wb-common-forms-common-rendering/kie-wb-common-forms-common-rendering-client/target/kie-wb-common-forms-common-rendering-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html
      public String getText() {
        return "<div>\n    <div class=\"row\">\n        <div class=\"col-md-12\">\n            <div data-field=\"videoContainer\" class=\"pictureContainer\">\n                <video data-field=\"videoElement\" class=\"videoElement\"></video>\n                <button data-field=\"takePicture\" class=\"btn btn-primary actionButton\" data-i18n-key=\"takePicture\"></button>\n            </div>\n            <div data-field=\"imageContainer\" class=\"pictureContainer\">\n                <img data-field=\"imageElement\"/>\n                <button data-field=\"takeAnotherPicture\" class=\"btn btn-success actionButton\" data-i18n-key=\"newPicture\"></button>\n            </div>\n        </div>\n    </div>\n</div>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".pictureContainer{display:" + ("inline-block")  + ";}.videoElement{display:" + ("block")  + ";}.actionButton{display:" + ("block")  + ";margin-right:" + ("auto")  + ";margin-left:" + ("auto")  + ";margin-top:" + ("5px")  + ";}")) : ((".pictureContainer{display:" + ("inline-block")  + ";}.videoElement{display:" + ("block")  + ";}.actionButton{display:" + ("block")  + ";margin-left:" + ("auto")  + ";margin-right:" + ("auto")  + ";margin-top:" + ("5px")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}
