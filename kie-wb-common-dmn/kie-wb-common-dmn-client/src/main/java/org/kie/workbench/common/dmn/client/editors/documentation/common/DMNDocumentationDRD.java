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

import java.util.List;

import elemental2.core.Array;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import static org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentation.asJsArray;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentationDRD {

    private String drdName;

    private String drdType;

    private String drdDescription;

    private String drdBoxedExpressionImage;

    private Array<DMNDocumentationExternalLink> drdExternalLinks;

    private boolean hasExternalLinks;

    private DMNDocumentationDRD() {

    }

    @JsOverlay
    public static DMNDocumentationDRD create(final String drdName,
                                             final String drdType,
                                             final String drdDescription,
                                             final String drdBoxedExpressionImage,
                                             final List<DMNDocumentationExternalLink> externalLinks,
                                             final boolean hasExternalLinks) {

        final DMNDocumentationDRD drd = new DMNDocumentationDRD();

        drd.drdName = drdName;
        drd.drdType = drdType;
        drd.drdDescription = drdDescription;
        drd.drdBoxedExpressionImage = drdBoxedExpressionImage;
        drd.drdExternalLinks = asJsArray(externalLinks);
        drd.hasExternalLinks = hasExternalLinks;

        return drd;
    }

    @JsOverlay
    public final String getDrdName() {
        return drdName;
    }

    @JsOverlay
    public final String getDrdType() {
        return drdType;
    }

    @JsOverlay
    public final String getDrdDescription() {
        return drdDescription;
    }

    @JsOverlay
    public final String getDrdBoxedExpressionImage() {
        return drdBoxedExpressionImage;
    }

    @JsOverlay
    public final Array<DMNDocumentationExternalLink> getDrdExternalLinks() {
        return drdExternalLinks;
    }

    @JsOverlay
    public final boolean getHasExternalLinks() {
        return hasExternalLinks;
    }
}
