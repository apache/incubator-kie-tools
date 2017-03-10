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

import java.io.InputStream;

import com.google.gwt.resources.client.DataResource;
import com.google.gwt.safehtml.shared.SafeUri;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.AbstractIconRendererTest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SVGIconRendererTest extends AbstractIconRendererTest<SVGIconRenderer, DataResource, SVGIconRendererView> {

    public static String SVG_DATA_URI_PREFFIX = "data:image/svg+xml;base64,";

    public static String JPG_DATA_URI_PREFFIX = "data:image/jpeg;base64,";

    @Mock
    private DataResource dataResource;

    @Mock
    private SafeUri safeUri;

    @Override
    protected SVGIconRenderer getRendererIncance(SVGIconRendererView view) {
        return new SVGIconRenderer(view);
    }

    @Override
    protected Class<SVGIconRendererView> getViewClass() {
        return SVGIconRendererView.class;
    }

    @Test
    public void testReadSVGContent() throws Exception {

        initDataURI(SVG_DATA_URI_PREFFIX,
                    this.getClass().getResourceAsStream("/images/svg.svg"));

        assertNotNull(renderer.getSVGContent());
    }

    @Test
    public void testReadJPGContent() throws Exception {

        initDataURI(JPG_DATA_URI_PREFFIX,
                    this.getClass().getResourceAsStream("/images/jpg.jpg"));

        assertNull(renderer.getSVGContent());
    }

    protected void initDataURI(final String header,
                               final InputStream imageStream) throws Exception {
        renderer.render(resource);
        when(resource.getResource()).thenReturn(dataResource);
        when(dataResource.getSafeUri()).thenReturn(safeUri);
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(IOUtils.toByteArray(imageStream))));
        when(safeUri.asString()).thenReturn(sb.toString());
    }
}
