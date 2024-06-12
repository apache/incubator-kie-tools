/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.uberfire.client.util;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.EditorActivity;

public class JSFunctions {

    private JSFunctions() {
        // Empty
    }

    public static native void nativeRegisterGwtEditorProvider() /*-{

        console.log("registerGWTEditorProvider");

        $wnd.gwtEditorBeans = new Map();
        $wnd.resolveEditor = function (id) {
            return $wnd.gwtEditorBeans.get(id);
        };

        $wnd.GWTEditor = function (instance) {
            this.instance = instance;
        };

        $wnd.GWTEditor.prototype.onOpen = function () {
            this.instance.@org.uberfire.client.mvp.EditorActivity::onOpen()();
        };

        $wnd.GWTEditor.prototype.setContent = function (path, value) {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::setContent(Ljava/lang/String;Ljava/lang/String;)(path, value);
        };

        $wnd.GWTEditor.prototype.getContent = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getContent()();
        };

        $wnd.GWTEditor.prototype.getPreview = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getPreview()();
        };

        $wnd.GWTEditor.prototype.getView = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getWidgetElement()();
        };

         $wnd.GWTEditor.prototype.validate = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::validate()();
        };

        $wnd.GWTEditorSupplier = function (bean) {
            this.bean = bean;
        };

        $wnd.GWTEditorSupplier.prototype.get = function () {
            return new $wnd.GWTEditor(this.bean.@org.jboss.errai.ioc.client.container.SyncBeanDef::newInstance()());
        }

        $wnd.GWTEditor.prototype.undo = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::undo()();
        };

        $wnd.GWTEditor.prototype.redo = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::redo()();
        };
        $wnd.GWTEditor.prototype.searchDomainObject = function (uuid) {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::searchDomainObject(Ljava/lang/String;)(uuid);
        };

    }-*/;

    public static native void nativeRegisterGwtClientBean(final String id, final SyncBeanDef<EditorActivity> bean) /*-{
        $wnd.gwtEditorBeans.set(id, new $wnd.GWTEditorSupplier(bean));
    }-*/;

    public static native void notifyJSReady() /*-{
        if ($wnd.appFormerGwtFinishedLoading) {
            $wnd.appFormerGwtFinishedLoading();
        }
    }-*/;
}
