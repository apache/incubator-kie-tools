/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.annotations.processors;

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * A wrapper for ProcessingEnvironment that returns a wrapped Types instance which works around bug 434378 in Eclipse.
 */
public class EclipseWorkaroundProcessingEnvironment implements ProcessingEnvironment {

    private final ProcessingEnvironment realImpl;
    private final Types wrappedTypeUtils;

    public EclipseWorkaroundProcessingEnvironment(ProcessingEnvironment realImpl) {
        this.realImpl = realImpl;
        this.wrappedTypeUtils = new EclipseWorkaroundTypeUtils( realImpl.getTypeUtils() );
    }

    @Override
    public Elements getElementUtils() {
        return realImpl.getElementUtils();
    }

    @Override
    public Filer getFiler() {
        return realImpl.getFiler();
    }

    @Override
    public Locale getLocale() {
        return realImpl.getLocale();
    }

    @Override
    public Messager getMessager() {
        return realImpl.getMessager();
    }

    @Override
    public Map<String, String> getOptions() {
        return realImpl.getOptions();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return realImpl.getSourceVersion();
    }

    @Override
    public Types getTypeUtils() {
        return wrappedTypeUtils;
    }

}
