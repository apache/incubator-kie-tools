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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.List;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import static org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentation.asJsArray;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentationDRD {

    private String drdName;

    private String drdType;

    private String drdQuestion;

    private String drdAllowedAnswers;

    private String drdDescription;

    private String drdBoxedExpressionImage;

    private JsArray<DMNDocumentationExternalLink> drdExternalLinks;

    private DMNDocumentationDRD() {

    }

    @JsOverlay
    public static DMNDocumentationDRD create() {

        return new DMNDocumentationDRD();
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
    public final String getDrdQuestion() {
        return drdQuestion;
    }

    @JsOverlay
    public final String getDrdAllowedAnswers() {
        return drdAllowedAnswers;
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
    public final JsArray<DMNDocumentationExternalLink> getDrdExternalLinks() {
        return drdExternalLinks;
    }

    @JsOverlay
    public final void setDrdName(String drdName) {
        this.drdName = drdName;
    }

    @JsOverlay
    public final void setDrdType(String drdType) {
        this.drdType = drdType;
    }

    @JsOverlay
    public final void setDrdQuestion(String drdQuestion) {
        this.drdQuestion = drdQuestion;
    }

    @JsOverlay
    public final void setDrdAllowedAnswers(String drdAllowedAnswers) {
        this.drdAllowedAnswers = drdAllowedAnswers;
    }

    @JsOverlay
    public final void setDrdDescription(String drdDescription) {
        this.drdDescription = drdDescription;
    }

    @JsOverlay
    public final void setDrdBoxedExpressionImage(String drdBoxedExpressionImage) {
        this.drdBoxedExpressionImage = drdBoxedExpressionImage;
    }

    @JsOverlay
    public final void setDrdExternalLinks(List<DMNDocumentationExternalLink> drdExternalLinks) {
        this.drdExternalLinks = asJsArray(drdExternalLinks);
    }
}
