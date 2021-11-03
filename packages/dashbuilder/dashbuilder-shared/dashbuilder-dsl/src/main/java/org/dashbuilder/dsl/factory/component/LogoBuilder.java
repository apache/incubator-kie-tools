/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

public class LogoBuilder extends ExternalComponentBuilder {

    public static final String LOGO_ID = "logo-provided";

    public static final String LOGO_URL_PROP = "src";
    public static final String LOGO_WIDTH_PROP = "width";
    public static final String LOGO_HEIGHT_PROP = "height";

    LogoBuilder(String src) {
        super(LOGO_ID);
        src(src);
    }

    public static LogoBuilder create(String src) {
        return new LogoBuilder(src);
    }

    public LogoBuilder src(String src) {
        componentProperty(LOGO_URL_PROP, src);
        return this;
    }

    public LogoBuilder width(String width) {
        componentProperty(LOGO_WIDTH_PROP, width);
        return this;
    }

    public LogoBuilder height(String height) {
        componentProperty(LOGO_HEIGHT_PROP, height);
        return this;
    }

}