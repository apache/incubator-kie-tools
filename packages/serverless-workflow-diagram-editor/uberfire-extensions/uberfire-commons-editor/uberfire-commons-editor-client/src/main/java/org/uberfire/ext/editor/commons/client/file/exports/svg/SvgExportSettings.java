/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.exports.svg;

/**
 * Contains all attributes used on the SVG Export process.
 */
public class SvgExportSettings {

    private final double width;
    private final double height;
    private final Object context;

    public SvgExportSettings(final double width, final double height, final Object context) {
        this.width = width;
        this.height = height;
        this.context = context;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Object getContext() {
        return context;
    }
}