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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentationExternalLink {

    private String description;

    private String url;

    private DMNDocumentationExternalLink() {

    }

    @JsOverlay
    public static DMNDocumentationExternalLink create(final String description,
                                                      final String url) {

        final DMNDocumentationExternalLink link = new DMNDocumentationExternalLink();

        link.description = description;
        link.url = url;

        return link;
    }

    @JsOverlay
    public final String getDescription() {
        return description;
    }

    @JsOverlay
    public final String getUrl() {
        return url;
    }
}
