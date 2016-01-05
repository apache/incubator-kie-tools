/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.editor;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;
import org.uberfire.client.plugin.JSNativePlugin;

@Dependent
public class JSNativeEditor extends JSNativePlugin {

    private String resourceType;

    public void build( final JavaScriptObject obj ) {
        super.build( obj );
        if ( hasStringProperty( obj, "resourceType" ) ) {
            resourceType = getResourceType( obj );
        }
    }

    private static native String getResourceType( final JavaScriptObject o ) /*-{
        return o.resourceType;
    }-*/;

    public void onConcurrentUpdate() {
        if ( hasMethod( obj, "on_concurrent_update" ) ) {
            executeOnConcurrentUpdate( obj );
        }
    }

    private static native void executeOnConcurrentUpdate( final JavaScriptObject o ) /*-{
        o.on_concurrent_update();
    }-*/;

    public void onConcurrentDelete() {
        if ( hasMethod( obj, "on_concurrent_delete" ) ) {
            executeOnConcurrentDelete( obj );
        }
    }

    private static native void executeOnConcurrentDelete( final JavaScriptObject o ) /*-{
        o.on_concurrent_delete();
    }-*/;

    public void onConcurrentRename() {
        if ( hasMethod( obj, "on_concurrent_rename" ) ) {
            executeOnConcurrentRename( obj );
        }
    }

    private static native void executeOnConcurrentRename( final JavaScriptObject o ) /*-{
        o.on_concurrent_rename();
    }-*/;

    public void onConcurrentCopy() {
        if ( hasMethod( obj, "on_concurrent_copy" ) ) {
            executeOnConcurrentCopy( obj );
        }
    }

    private static native void executeOnConcurrentCopy( final JavaScriptObject o ) /*-{
        o.on_concurrent_copy();
    }-*/;

    public void onRename() {
        if ( hasMethod( obj, "on_rename" ) ) {
            executeOnRename( obj );
        }
    }

    private static native void executeOnRename( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onDelete() {
        if ( hasMethod( obj, "on_delete" ) ) {
            executeOnDelete( obj );
        }
    }
    private static native void executeOnDelete( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onCopy() {
        if ( hasMethod( obj, "on_copy" ) ) {
            executeOnCopy( obj );
        }
    }

    private static native void executeOnCopy( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onUpdate() {
        if ( hasMethod( obj, "on_update" ) ) {
            executeOnUpdate( obj );
        }
    }

    private static native void executeOnUpdate( final JavaScriptObject o ) /*-{
        o.on_update();
    }-*/;

    public String getResourceType() {
        return resourceType;
    }

}
