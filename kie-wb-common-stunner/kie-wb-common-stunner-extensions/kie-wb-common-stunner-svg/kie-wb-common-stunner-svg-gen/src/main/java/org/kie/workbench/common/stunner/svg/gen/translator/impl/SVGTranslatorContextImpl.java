/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGTranslatorContextImpl implements SVGTranslatorContext {

    private final Document root;
    private final String path;
    private final Optional<StyleSheetDefinition> cssStyleSheet;
    private String viewId;
    private String id;
    private SVGElementTranslator<Element, Object>[] elementTranslators;
    final Set<ViewRefDefinition> viewRefDefinitions = new LinkedHashSet<>();

    public SVGTranslatorContextImpl(final Document root,
                                    final String path,
                                    final StyleSheetDefinition cssStyleSheet) {
        this.cssStyleSheet = Optional.ofNullable(cssStyleSheet);
        this.root = root;
        this.path = path;
    }

    @Override
    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public void setSVGId(final String id) {
        this.id = id;
    }

    @Override
    public String getSVGId() {
        return id;
    }

    @Override
    public Document getRoot() {
        return root;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Optional<StyleSheetDefinition> getGlobalStyleSheet() {
        return cssStyleSheet;
    }

    public void setElementTranslators(final SVGElementTranslator<Element, Object>[] elementTranslators) {
        this.elementTranslators = elementTranslators;
    }

    @Override
    public SVGElementTranslator<Element, Object> getElementTranslator(final String tagName) {
        for (final SVGElementTranslator<Element, Object> translator : elementTranslators) {
            if (translator.getTagName().equals(tagName)) {
                return translator;
            }
        }
        return null;
    }

    @Override
    public void addSVGViewRef(final ViewRefDefinition viewRef) {
        viewRefDefinitions.add(viewRef);
    }
}
