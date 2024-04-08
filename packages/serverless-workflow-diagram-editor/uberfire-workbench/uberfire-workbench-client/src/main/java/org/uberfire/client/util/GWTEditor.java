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

import java.util.List;

import elemental2.promise.Promise;
import jsinterop.annotations.JsType;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.workbench.model.bridge.Notification;

@JsType
public class GWTEditor {

    private EditorActivity instance;

    public GWTEditor(EditorActivity instance) {
        this.instance = instance;
    }


    public final void onOpen() {
        instance.onOpen();
    }

    public final Promise<Void> applyTheme(String theme) {
        return instance.applyTheme(theme);
    }

    public final Promise<Void> setContent(String path, String value) {
        return instance.setContent(path, value);
    }

    public final Promise<Void> selectStateByName(String name) {
        return instance.selectStateByName(name);
    }

    public final Promise<String> getContent() {
        return instance.getContent();
    }

    public final Promise<String> getContentYAML() {
        return instance.getContentYAML();
    }

    public final Promise<String> getContentJSON() {
        return instance.getContentJSON();
    }

    public final Promise<String>  getPreview() {
        return instance.getPreview();
    }

    public final Promise<List<Notification>> validate() {
        return instance.validate();
    }

    public final Promise<Void> undo() {
        return instance.undo();
    }

    public final Promise<Void> redo() {
        return instance.redo();
    }

    public final Promise<Void> searchDomainObject(String uuid) {
        return instance.searchDomainObject(uuid);
    }
}
