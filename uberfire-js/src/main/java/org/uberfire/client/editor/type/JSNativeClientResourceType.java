/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.editor.type;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;

@Dependent
public class JSNativeClientResourceType {

    private JavaScriptObject obj;

    public void build(final JavaScriptObject obj) {
        if (this.obj != null) {
            throw new RuntimeException("Can't build more than once.");
        }
        this.obj = obj;
    }

    public native boolean acceptFileName(String filename) /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.accept(filename);
    }-*/;

    public native String getSimpleWildcardPattern()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.simple_wildcard_pattern;
    }-*/;

    public native String getPriority()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.priority;
    }-*/;

    public native String getSuffix()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.suffix;
    }-*/;

    public native String getPrefix()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.prefix;
    }-*/;

    public native String getDescription()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.description;
    }-*/;

    public native String getShortName()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.short_name;
    }-*/;

    public native String getId()  /*-{
        return this.@org.uberfire.client.editor.type.JSNativeClientResourceType::obj.id;
    }-*/;
}
