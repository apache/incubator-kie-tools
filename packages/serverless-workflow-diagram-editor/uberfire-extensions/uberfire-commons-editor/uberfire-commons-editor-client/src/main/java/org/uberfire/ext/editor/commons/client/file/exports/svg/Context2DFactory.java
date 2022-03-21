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

import org.uberfire.ext.editor.commons.client.file.exports.jso.svg.C2SContext2D;

/**
 * Create {@link IContext2D} concrete instances to be used on the SVG exporting,
 * abstracting which library implementation is used, allowing to change or use multiple implementations if necessary.
 */
public class Context2DFactory {

    public static IContext2D create(SvgExportSettings settings){
        //choose the implementation, for now just the canvas2svg.
        return new C2SContext2D(settings.getWidth(), settings.getHeight(), settings.getContext());
    }
}