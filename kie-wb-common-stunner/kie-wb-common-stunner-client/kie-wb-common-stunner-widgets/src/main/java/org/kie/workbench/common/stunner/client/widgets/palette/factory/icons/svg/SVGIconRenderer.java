/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.svg;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.resources.client.DataResource;
import org.jboss.errai.common.client.logging.util.Console;
import org.jboss.errai.common.client.util.Base64Util;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.AbstractIconRenderer;

@Dependent
public class SVGIconRenderer extends AbstractIconRenderer<DataResource, SVGIconRendererView> {

    public static final String SVG_DATA_URI_START = "data:image/svg+xml;base64,";

    @Inject
    public SVGIconRenderer(SVGIconRendererView view) {
        super(view);
    }

    public String getSVGContent() {
        String dataUri = iconResource.getResource().getSafeUri().asString();

        if (dataUri.startsWith(SVG_DATA_URI_START)) {
            try {
                String content = dataUri.substring(SVG_DATA_URI_START.length());

                return new String(Base64Util.decode(content));
            } catch (Exception ex) {
                Console.warn("[SVGIconRenderer] Impossible to get SVG content for '" + dataUri + "'");
                Console.warn("[SVGIconRenderer] Error: " + ex.getMessage());
            }
        }

        return null;
    }
}
