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


package org.uberfire.client.views.pfly.monaco.jsinterop;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "window", name = "monaco")
public class MonacoLanguages {

    public native void register(final JavaScriptObject language);

    public native void setMonarchTokensProvider(final String languageId,
                                                final JavaScriptObject languageDefinition);

    public native void registerCompletionItemProvider(final String languageId,
                                                      final JavaScriptObject completionItemProvider);

    @JsProperty(name = "languages")
    public static native MonacoLanguages get();

    @JsFunction
    public interface ProvideCompletionItemsFunction {

        JavaScriptObject call(final ITextModel model,
                              final Position position);
    }
}
