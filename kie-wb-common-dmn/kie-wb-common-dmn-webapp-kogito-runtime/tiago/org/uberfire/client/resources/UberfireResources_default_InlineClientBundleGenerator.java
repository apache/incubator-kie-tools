package org.uberfire.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class UberfireResources_default_InlineClientBundleGenerator implements org.uberfire.client.resources.UberfireResources {
  private static UberfireResources_default_InlineClientBundleGenerator _instance0 = new UberfireResources_default_InlineClientBundleGenerator();
  private void imagesInitializer() {
    images = com.google.gwt.core.client.GWT.create(org.uberfire.client.resources.images.UberfireImageResources.class);
  }
  private static class imagesInitializer {
    static {
      _instance0.imagesInitializer();
    }
    static org.uberfire.client.resources.images.UberfireImageResources get() {
      return images;
    }
  }
  public org.uberfire.client.resources.images.UberfireImageResources images() {
    return imagesInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.client.resources.images.UberfireImageResources images;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
    }
    return null;
  }-*/;
}
