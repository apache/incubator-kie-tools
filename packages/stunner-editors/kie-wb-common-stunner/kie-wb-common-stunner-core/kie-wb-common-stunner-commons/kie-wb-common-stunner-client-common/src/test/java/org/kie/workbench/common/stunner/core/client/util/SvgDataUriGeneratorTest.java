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


package org.kie.workbench.common.stunner.core.client.util;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.util.Base64Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SvgDataUriGeneratorTest {

    private static final String SVG_CONTENT = "svg-content";

    private static final String DATA_URI = SvgDataUriGenerator.SVG_DATA_URI_BASE64 +
            Base64Util.encode(SVG_CONTENT.getBytes(),
                              0,
                              SVG_CONTENT.length());

    private static final String SVG_TASK =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<svg id=\"Layer_Main\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                    "     x=\"0px\" y=\"0px\" width=\"448px\" height=\"448px\" viewBox=\"0 0 448 448\">\n" +
                    "  <rect x=\"0px\" y=\"0px\" width=\"448px\" height=\"448px\" rx=\"5\" ry=\"5\" style=\"fill:none; stroke: black; stroke-width: 5;\"/>\n" +
                    "  <g id=\"userTask\" transform=\"scale(0.25,0.25) translate(8,8)\" style=\"opacity:1\">\n" +
                    "    <use xlink:href=\"task-user.svg#Layer_2\"/>\n" +
                    "  </g>\n" +
                    "  <g id=\"scriptTask\" transform=\"scale(0.25,0.25) translate(8,8)\" style=\"opacity:1\">\n" +
                    "    <use xlink:href=\"task-script.svg#Layer_3\"/>\n" +
                    "  </g>\n" +
                    "</svg>";

    private static final String SVG_TASK_SCRIPT_RAW = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<svg id=\"Layer_3\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\"\n" +
            "\t\t xmlns:stunner=\"http://www.kie.org/2017/stunner\"\n" +
            "\t x=\"0px\" y=\"0px\" width=\"448px\" height=\"448px\"\n" +
            "\t viewBox=\"0 0 448 448\" style=\"enable-background:new 0 0 448 448;\" xml:space=\"preserve\">\n" +
            "<path d=\"M197.3,130.2c-2.9-2.9-7.7-2.9-10.6,0l-56.5,56.5c-2.9,2.9-2.9,7.7,0,10.6l56.5,56.5c1.4,1.5,3.4,2.2,5.3,2.2\n" +
            "\tc1.9,0,3.9-0.7,5.3-2.3c2.9-2.9,2.9-7.7,0-10.6L146.2,192l51.1-51.2C200.2,137.9,200.2,133.1,197.3,130.2z\"/>\n" +
            "<path d=\"M261.3,130.2c-2.9-2.9-7.7-2.9-10.6,0c-2.9,2.9-2.9,7.7,0,10.6l51.1,51.1L250.7,243c-2.9,2.9-2.9,7.7,0,10.6\n" +
            "\tc1.4,1.7,3.4,2.4,5.3,2.4c1.9,0,3.9-0.7,5.3-2.2l56.5-56.5c2.9-2.9,2.9-7.7,0-10.6L261.3,130.2z\"/>\n" +
            "<path stunner:shape=\"main-shape\" d=\"M400,32c0,0-247.2,0-272,0c-66,0-64,64-64,64v192H1c0,0-5,128,77,128h242c48,0,64-48,64-80c0-21.8,0-111.6,0-176h64V96\n" +
            "\tC448,96,449,32,400,32z M78,383.9c-9.5,0-16.4-2.8-22.5-9c-12.1-12.5-18-35.3-20.6-54.9h222c0.2,2.7,0.4,5.4,0.7,8.2\n" +
            "\tc2.4,23.4,7.1,41.9,14.3,55.7L78,383.9L78,383.9z M352,336c0,9.9-2.4,24.3-9.1,35c-5.7,9.1-12.5,13-22.9,13c-35,0-32-96-32-96H96V96\n" +
            "\tv-0.1v-0.8c0-4.5,1.6-16.8,8.7-23.9c1.8-1.8,7.2-7.2,23.3-7.2h228.5c-2.1,8.8-3.7,17.8-4.3,25.9c0,0.6-0.1,1.2-0.1,1.8\n" +
            "\tc0,0.3,0,0.6,0,0.9C352,94.8,352,96,352,96v64V336z\"/>\n" +
            "</svg>";

    private static final String SVG_TASK_USER_RAW = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<svg id=\"Layer_2\" version=\"1.1\"" +
            "\t xmlns=\"http://www.w3.org/2000/svg\" x=\"0px\" y=\"0px\" width=\"448px\" height=\"448px\"\n" +
            "\t viewBox=\"0 0 448 448\" enable-background=\"new 0 0 448 448\" xml:space=\"preserve\">\n" +
            "\n" +
            "\t<path d=\"m 16,445.2101 c 0,-4.34143 2.784312,-14.08104 6.001325,-20.99282 C 35.767643,394.6402 77.282944,359.28049 129,333.08362 c 15.51625,-7.85964 28.34689,-13.11968 38.80739,-15.90946 4.12414,-1.0999 7.92174,-2.76026 8.71771,-3.81151 2.36851,-3.12815 4.38894,-10.45484 5.20163,-18.86265 l 0.77327,-8 -3.99285,-3.04543 C 166.30327,274.14645 154.2837,251.67767 148.03953,226.5 c -2.42867,-9.79285 -2.9835,-11.03754 -5.05589,-11.3421 -1.28032,-0.18816 -4.90062,-2.91488 -8.04512,-6.05938 -11.70538,-11.70538 -18.04706,-31.72296 -13.4987,-42.60874 1.56222,-3.7389 6.71475,-7.48007 10.31018,-7.48604 2.69781,-0.004 2.72062,-0.40078 1.16375,-20.21578 -1.98717,-25.29176 1.36528,-46.522533 10.21842,-64.712284 9.09945,-18.695841 24.43672,-31.193596 45.91717,-37.416169 21.154,-6.128003 48.74732,-6.128003 69.90132,0 41.091,11.903461 60.00776,47.146833 56.00439,104.340493 -0.63522,9.075 -1.33103,16.78816 -1.54625,17.14035 -0.21521,0.35219 1.16652,0.9326 3.07051,1.28979 12.44936,2.33551 14.50688,17.58758 5.01707,37.19078 -4.59319,9.4882 -12.13914,17.88705 -16.67906,18.56425 -1.79473,0.26772 -2.52422,1.96101 -4.87454,11.31483 -3.2833,13.06693 -5.46876,18.80522 -11.99514,31.49534 -5.45692,10.61062 -14.91298,23.11388 -19.84007,26.23349 l -3.23695,2.04949 0.64754,6.61084 c 0.82675,8.44045 3.12133,16.98144 5.3592,19.94826 1.18999,1.5776 4.12509,2.95221 9.18571,4.30199 11.00623,2.93562 23.55394,8.13479 40.93693,16.9623 48.81471,24.78933 89.84811,59.65626 104.03216,88.39829 4.0377,8.18183 6.96784,17.73198 6.96784,22.7101 l 0,2.7899 -208,0 -208,0 0,-2.7899 z\"/>\n" +
            "</svg>";

    @Mock
    private SafeUri simpleUri;

    @Mock
    private SafeUri taskUri;

    @Mock
    private SafeUri taskUserUri;

    @Mock
    private SafeUri taskScriptUri;

    private SvgDataUriGenerator tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(simpleUri.asString()).thenReturn(DATA_URI);
        when(taskUri.asString()).thenReturn(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + SVG_TASK);
        when(taskUserUri.asString()).thenReturn(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + SVG_TASK_USER_RAW);
        when(taskScriptUri.asString()).thenReturn(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + SVG_TASK_SCRIPT_RAW);
        this.tested = new SvgDataUriGenerator();
    }

    @Test
    public void testNoProcessing() {
        assertEquals(SVG_CONTENT,
                     tested.generate(simpleUri));
    }

    @Test
    public void testSingle() {
        final String singleSvgContent = tested.generate(taskUri);
        assertTrue(singleSvgContent.contains("<svg id=\"Layer_Main\"")); //
        assertFalse(singleSvgContent.contains("<\\?xml"));
        assertFalse(singleSvgContent.contains("<use"));
    }

    @Test
    public void testComposite() {
        final List<SafeUri> defs = Arrays.asList(taskUserUri,
                                                 taskScriptUri);
        final List<String> defIds = Arrays.asList("Layer_2",
                                                  "Layer_3");
        String compositeSvgContent = tested.generate(taskUri,
                                                     defs,
                                                     defIds);
        assertFalse(compositeSvgContent.contains("<\\?xml"));
        assertEquals(97,
                     compositeSvgContent.indexOf("width=\"448\""));
        assertEquals(109,
                     compositeSvgContent.indexOf("height=\"448\""));
        assertEquals(122,
                     compositeSvgContent.indexOf("viewBox=\"0 0 448 448\""));
        assertTrue(compositeSvgContent.contains("viewBox=\"0 0 448 448\""));
        assertTrue(compositeSvgContent.contains("<svg id=\"Layer_Main\""));
        assertTrue(compositeSvgContent.contains("<svg id=\"Layer_2\""));
        assertTrue(compositeSvgContent.contains("<svg id=\"Layer_3\""));
        assertTrue(compositeSvgContent.contains("<use xlink:href=\"#Layer_2\""));
        assertTrue(compositeSvgContent.contains("<use xlink:href=\"#Layer_3\""));
    }

    @Test
    public void testFiltered() {
        String filteredSvgContent = tested.generate(taskUri,
                                                    Arrays.asList(taskScriptUri,
                                                                  taskUserUri),
                                                    Arrays.asList("Layer_3"));
        assertFalse(filteredSvgContent.contains("<\\?xml"));
        assertEquals(97,
                     filteredSvgContent.indexOf("width=\"448\""));
        assertEquals(109,
                     filteredSvgContent.indexOf("height=\"448\""));
        assertEquals(122,
                     filteredSvgContent.indexOf("viewBox=\"0 0 448 448\""));
        assertTrue(filteredSvgContent.contains("<svg id=\"Layer_Main\""));
        assertTrue(filteredSvgContent.contains("<svg id=\"Layer_2\""));
        assertTrue(filteredSvgContent.contains("<svg id=\"Layer_3\""));
        assertFalse(filteredSvgContent.contains("<use xlink:href=\"#Layer_2\""));
        assertTrue(filteredSvgContent.contains("<use xlink:href=\"#Layer_3\""));
    }

    @Test
    public void testEncodeUTF8() {
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + "%3C?xml",
                     SvgDataUriGenerator.encodeUtf8("<?xml"));
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + "%3Csvg%20id=%22Layer_Main%22%20",
                     SvgDataUriGenerator.encodeUtf8("<svg id=\"Layer_Main\" "));
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_UTF8 + "%3Cuse%20xlink:href=%22%23Layer_3%22",
                     SvgDataUriGenerator.encodeUtf8("<use xlink:href=\"#Layer_3\""));
    }

    @Test
    public void testEncodeBase64() {
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_BASE64 + "PD94bWw=",
                     SvgDataUriGenerator.encodeBase64("<?xml"));
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_BASE64 + "PHN2ZyBpZD0iTGF5ZXJfTWFpbiIg",
                     SvgDataUriGenerator.encodeBase64("<svg id=\"Layer_Main\" "));
        assertEquals(SvgDataUriGenerator.SVG_DATA_URI_BASE64 + "PHVzZSB4bGluazpocmVmPSIjTGF5ZXJfMyI=",
                     SvgDataUriGenerator.encodeBase64("<use xlink:href=\"#Layer_3\""));
    }
}
