package org.uberfire.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class UberfireImageResources_default_InlineClientBundleGenerator implements org.uberfire.client.resources.images.UberfireImageResources {
  private static UberfireImageResources_default_InlineClientBundleGenerator _instance0 = new UberfireImageResources_default_InlineClientBundleGenerator();
  private void typeGenericFileInitializer() {
    typeGenericFile = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "typeGenericFile",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage),
      0, 0, 24, 24, false, false
    );
  }
  private static class typeGenericFileInitializer {
    static {
      _instance0.typeGenericFileInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return typeGenericFile;
    }
  }
  public com.google.gwt.resources.client.ImageResource typeGenericFile() {
    return typeGenericFileInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAA1klEQVR4XmNgGAhgYGAgYGRkVGBiYtJALjY1NTVANxcOQAoiIyP/79+///+ZM2dIxj09Pf+NjY3fo5sLB0DJ/SCFlACQA4EOdUA3GwyoYUFaWtqoBfjBqAUEwagFBMGoBQTBqAUY4NOnT/9BemCYqvUByHAHBwewoSCXA/WDazRg1auAbjYYkGrB3r17wYaDLCouLgYZfh6n4SAAsgBUHxMDnj59+n/t2rVgC0AYqHc9qNGAbiYKACoKAHkZVHnPnDkTJwbJt7a2/i8oKAAHCzDM+9HNAgAejJCDgxWkXwAAAABJRU5ErkJggg==";
  private static com.google.gwt.resources.client.ImageResource typeGenericFile;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      typeGenericFile(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("typeGenericFile", typeGenericFile());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'typeGenericFile': return this.@org.uberfire.client.resources.images.UberfireImageResources::typeGenericFile()();
    }
    return null;
  }-*/;
}
