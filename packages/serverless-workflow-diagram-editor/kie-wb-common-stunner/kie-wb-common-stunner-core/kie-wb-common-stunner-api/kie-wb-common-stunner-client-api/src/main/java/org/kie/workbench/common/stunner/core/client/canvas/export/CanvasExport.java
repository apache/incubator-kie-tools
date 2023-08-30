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


package org.kie.workbench.common.stunner.core.client.canvas.export;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

/**
 * Provides client side canvas exporting features.
 * <p>
 * Rather than using <code>Layer.toDataURL()</code>, which
 * results in the canvas raw image data, this component
 * provides canvas image exporting capabilities as well, but
 * it performs additional operations (like transform,
 * layer filtering, adding backgrounds) that results
 * in a clean and nicer canvas exported image.
 * @param <H> The canvas handler type.
 */
public interface CanvasExport<H extends CanvasHandler> {

    public enum URLDataType {
        PNG("image/png"),
        JPG("image/jpeg"),
        SVG("image/svg+xml");

        private final String mimeType;

        URLDataType(final String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }
    }

    String toImageData(H canvasHandler,
                       CanvasURLExportSettings settings);

    /**
     * Draw the canvas content returning an {@link IContext2D} that represents the drawn content on the context.
     * @param canvasHandler
     * @return
     */
    IContext2D toContext2D(H canvasHandler,
                           CanvasExportSettings settings);
}
