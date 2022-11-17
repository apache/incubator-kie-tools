/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.selenium;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SWAPITests extends SWEditorSeleniumBase {

    @Test
    public void testGetNodeIdsAndColor() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        for (String uuid : nodeIds) {
            String backgroundColor = jsHelper.getGetBackgroundColor(uuid);
            String borderColor = jsHelper.getBorderColor(uuid);

            assertThat(backgroundColor).isEqualTo("#ffffff");
            assertThat(borderColor).isEqualTo("#d5d5d5");

            jsHelper.setBackgroundColor(uuid, "#ff00ff");
            jsHelper.setBorderColor(uuid, "#dd00ff");

            backgroundColor = jsHelper.getGetBackgroundColor(uuid);
            borderColor = jsHelper.getBorderColor(uuid);

            assertThat(backgroundColor).isEqualTo("#ff00ff");
            assertThat(borderColor).isEqualTo("#dd00ff");
        }
    }

    @Test
    public void testGetNodeIdsAndLocation() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        List<Object> location1List = jsHelper.getLocation(nodeIds.get(0));
        List<Object> absoluteLocation1List = jsHelper.getAbsoluteLocation(nodeIds.get(0));

        assertThat(location1List.get(0)).isEqualTo(153L);
        assertThat(location1List.get(1)).isEqualTo(267L);

        assertThat(absoluteLocation1List.get(0)).isEqualTo(153L);
        assertThat(absoluteLocation1List.get(1)).isEqualTo(267L);

        List<Object> location2List = jsHelper.getLocation(nodeIds.get(1));
        List<Object> absoluteLocation2List = jsHelper.getAbsoluteLocation(nodeIds.get(1));

        assertThat(location2List.get(0)).isEqualTo(50L);
        assertThat(location2List.get(1)).isEqualTo(50L);

        assertThat(absoluteLocation2List.get(0)).isEqualTo(50L);
        assertThat(absoluteLocation2List.get(1)).isEqualTo(50L);
    }

    @Test
    public void testGetNodeIdsAndDimensions() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        List<Object> dimension1List = jsHelper.getDimension(nodeIds.get(0));

        assertThat(dimension1List.get(0)).isEqualTo(49L);
        assertThat(dimension1List.get(1)).isEqualTo(47L);

        List<Object> dimension2List = jsHelper.getDimension(nodeIds.get(1));

        assertThat(dimension2List.get(0)).isEqualTo(254L);
        assertThat(dimension2List.get(1)).isEqualTo(92L);
    }

    @Test
    public void testGetNodeIdsAndApplyState() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        String uuid = nodeIds.get(0);

        String backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        String borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("#d5d5d5");

        /// Invalid
        jsHelper.applyState(uuid, "invalid");

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(255,0,0)");

        /// Highlight
        jsHelper.applyState(uuid, "highlight");

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(51,102,204)");

        /// Selected
        jsHelper.applyState(uuid, "selected");

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(0,136,206)");

        /// None
        jsHelper.applyState(uuid, "none");

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(213,213,213)");
    }

    @Test
    public void testGetNodeIdsCenterNode() throws Exception {
        // curently centerNode is only applicable if scrollbars are present and correct zoom level set, otherwise it ignores it such as in this case
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();

        assertThat(nodeIds.size()).isEqualTo(2);

        List<Object> location1List = jsHelper.getLocation(nodeIds.get(0));

        assertThat(location1List.get(0)).isEqualTo(153L);
        assertThat(location1List.get(1)).isEqualTo(267L);

        jsHelper.centerNode(nodeIds.get(0));

        location1List = jsHelper.getLocation(nodeIds.get(0));

        assertThat(location1List.get(0)).isEqualTo(153L);
        assertThat(location1List.get(1)).isEqualTo(267L);
    }

    @Test
    public void testGetNodeIdsAndConnections() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        Boolean isConnection = jsHelper.isConnection(nodeIds.get(0), nodeIds.get(1));
        assertThat(isConnection).isEqualTo(true);
    }
}
