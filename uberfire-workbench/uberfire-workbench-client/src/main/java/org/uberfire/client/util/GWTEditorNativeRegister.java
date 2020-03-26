/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.util;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.Activity;

/**
 *  Call native Javascript to register GWT Editors
 */
@ApplicationScoped
public class GWTEditorNativeRegister {
    
    public native void nativeRegisterGwtEditorProvider() /*-{

        console.log("registerGWTEditorProvider");

        $wnd.gwtEditorBeans = new Map();
        $wnd.resolveEditor = function (id) {
            return $wnd.gwtEditorBeans.get(id);
        };

        $wnd.GWTEditor = function (instance) {
            this.instance = instance;
        };

        $wnd.GWTEditor.prototype.isDirty = function () {
            return this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::isDirty();
        };

        $wnd.GWTEditor.prototype.onOpen = function () {
            this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::onOpen()();
        };

        $wnd.GWTEditor.prototype.setContent = function (path, value) {
            return this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::setContent(Ljava/lang/String;Ljava/lang/String;)(path, value);
        };

        $wnd.GWTEditor.prototype.getContent = function () {
            return this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::getContent()();
        };
        
        $wnd.GWTEditor.prototype.getPreview = function () {
            return this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::getPreview()();
        };        

        $wnd.GWTEditor.prototype.getView = function () {
            return this.instance.@org.uberfire.client.mvp.WorkbenchClientEditorActivity::getWidgetElement()();
        };

        $wnd.GWTEditorSuplier = function (bean) {
            this.bean = bean;
        };

        $wnd.GWTEditorSuplier.prototype.get = function () {
            return new $wnd.GWTEditor(this.bean.@org.jboss.errai.ioc.client.container.SyncBeanDef::newInstance()());
        }

    }-*/;
    
    public native void nativeRegisterGwtClientBean(final String id, final SyncBeanDef<Activity> activityBean) /*-{
        $wnd.gwtEditorBeans.set(id, new $wnd.GWTEditorSuplier(activityBean));
    }-*/;

}
