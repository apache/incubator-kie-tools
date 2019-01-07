package org.dashbuilder.renderer.c3.client.exports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface NativeLibraryResources extends ClientBundle {

    NativeLibraryResources INSTANCE = GWT.create(NativeLibraryResources.class);

    @Source("org/uberfire/client/views/static/d3/d3.min.js")
    TextResource d3js();

    @Source("js/c3.js")
    TextResource c3js();

}