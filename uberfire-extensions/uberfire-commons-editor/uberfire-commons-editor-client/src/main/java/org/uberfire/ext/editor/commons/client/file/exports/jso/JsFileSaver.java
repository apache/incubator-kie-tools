/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports.jso;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.dom.Blob;

/**
 * Provides the JsInterop API for FileSaver js.
 * Provided by the webjar <code>org.webjars.bower.filesaver</code>.
 * @see <a href="https://github.com/eligrey/FileSaver.js">FileSaver.js</a>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JsFileSaver {

    /**
     * Saves the given blob's content into a file.
     * @param blob The blob data.
     * @param fileName The file name.
     * @param disableAutoBOM true if you don't want FileSaver.js to automatically provide Unicode text encoding hints.
     */
    @JsMethod
    public static native void saveAs(Blob blob,
                                     String fileName,
                                     boolean disableAutoBOM);
}
