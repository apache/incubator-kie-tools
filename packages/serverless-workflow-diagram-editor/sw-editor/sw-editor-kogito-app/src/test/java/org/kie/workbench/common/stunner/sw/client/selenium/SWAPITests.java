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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class SWAPITests extends SWEditorSeleniumBase {

    @Test
    public void testGetNodeIdsAndColor() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        for (String uuid : nodeIds) {
            String backgroundColor = jsHelper.getGetBackgroundColor(uuid);
            String borderColor = jsHelper.getBorderColor(uuid);
            ;

            assertThat(backgroundColor).isEqualTo("#ffffff");
            assertThat(borderColor).isEqualTo("#d5d5d5");

            jsHelper.setBackgroundColor(uuid, "#ff00ff");
            jsHelper.setBorderColor(uuid, "#dd00ff");

            backgroundColor = jsHelper.getGetBackgroundColor(uuid);
            borderColor = jsHelper.getBorderColor(uuid);
            ;

            assertThat(backgroundColor).isEqualTo("#ff00ff");
            assertThat(borderColor).isEqualTo("#dd00ff");
        }
    }

    @Test
    public void testGetNodeIdsAndLocation() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        List<Object> location1List = jsHelper.getLocation(nodeIds.get(0));
        List<Object> absoluteLocation1List = jsHelper.getAbsoluteLocation(nodeIds.get(0));
        ;

        assertThat(location1List.get(0)).isEqualTo(153L);
        assertThat(location1List.get(1)).isEqualTo(267L);

        assertThat(absoluteLocation1List.get(0)).isEqualTo(153L);
        assertThat(absoluteLocation1List.get(1)).isEqualTo(267L);

        List<Object> location2List = jsHelper.getLocation(nodeIds.get(1));
        List<Object> absoluteLocation2List = jsHelper.getAbsoluteLocation(nodeIds.get(1));
        ;

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

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
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

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        String uuid = nodeIds.get(0);

        String backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        String borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("#d5d5d5");

        /// Invalid
        jsHelper.applyState(uuid, "invalid");
        Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(255,0,0)");

        /// Highlight
        jsHelper.applyState(uuid, "highlight");
        Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(51,102,204)");

        /// Selected
        jsHelper.applyState(uuid, "selected");
        Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish

        backgroundColor = jsHelper.getGetBackgroundColor(uuid);
        borderColor = jsHelper.getBorderColor(uuid);

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(0,136,206)");

        /// None
        jsHelper.applyState(uuid, "none");
        Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish

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

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
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

        JsCanvasHelper jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);
        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(2);

        Boolean isConnection = jsHelper.isConnection(nodeIds.get(0), nodeIds.get(1));
        assertThat(isConnection).isEqualTo(true);
    }

    public class JsCanvasHelper {

        private static final String GET_NODE_IDS = "return canvas.getNodeIds();";
        private static final String GET_BACKGROUND_COLOR = "return canvas.getBackgroundColor('%s')";
        private static final String GET_BORDER_COLOR = "return canvas.getBorderColor('%s')";
        private static final String SET_BACKGROUND_COLOR = "return canvas.setBackgroundColor('%1','%2');";
        private static final String SET_BORDER_COLOR = "return canvas.setBorderColor('%1', '%2');";
        private static final String GET_LOCATION = "return canvas.getLocation('%s');";
        private static final String GET_ABSOLUTE_LOCATION = "return canvas.getAbsoluteLocation('%s')";
        private static final String GET_DIMENSIONS = "return canvas.getDimensions('%s')";
        private static final String APPLY_STATE = "return canvas.applyState('%1','%2');";
        private static final String CENTER_NODE = "return canvas.centerNode('%s');";
        private static final String IS_CONNECTION = "return canvas.isConnected('%1', '%2');";

        private JavascriptExecutor executor;

        public JsCanvasHelper(JavascriptExecutor executor) {
            this.executor = executor;
        }

        public List<String> getNodeIds() {
            final Object result = executor.executeScript(String.format(GET_NODE_IDS));
            assertThat(result).isInstanceOf(ArrayList.class);
            return (ArrayList<String>) result;
        }

        public String getBorderColor(String uuid) {
            Object borderColorResult = executor.executeScript(String.format(GET_BORDER_COLOR.replace("%s", uuid)));
            assertThat(borderColorResult).isInstanceOf(String.class);
            return (String) borderColorResult;
        }

        public String getGetBackgroundColor(String uuid) {
            Object backgroundColorResult = executor.executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", uuid)));
            assertThat(backgroundColorResult).isInstanceOf(String.class);
            return (String) backgroundColorResult;
        }

        public void setBackgroundColor(String uuid, String color) {
            String command = SET_BACKGROUND_COLOR.replace("%1", uuid).replace("%2", color);
            executor.executeScript(String.format(command));
        }

        public void setBorderColor(String uuid, String color) {
            String command = SET_BORDER_COLOR.replace("%1", uuid).replace("%2", color);
            executor.executeScript(String.format(command));
        }

        public List<Object> getLocation(String uuid) {
            Object location = executor.executeScript(String.format(GET_LOCATION.replace("%s", uuid)));
            assertThat(location).isInstanceOf(ArrayList.class);
            return (ArrayList<Object>) location;
        }

        public List<Object> getAbsoluteLocation(String uuid) {
            Object absoluteLocation = executor.executeScript(String.format(GET_ABSOLUTE_LOCATION.replace("%s", uuid)));
            assertThat(absoluteLocation).isInstanceOf(ArrayList.class);
            return (ArrayList<Object>) absoluteLocation;
        }

        public List<Object> getDimension(String uuid) {
            Object dimension = ((JavascriptExecutor) driver).executeScript(String.format(GET_DIMENSIONS.replace("%s", uuid)));
            assertThat(dimension).isInstanceOf(ArrayList.class);
            return (ArrayList<Object>) dimension;
        }

        public void applyState(String uuid, String state) {
            String command = APPLY_STATE.replace("%1", uuid).replace("%2", state);
            executor.executeScript(String.format(command));
        }

        public void centerNode(String uuid) {
            executor.executeScript(String.format(CENTER_NODE.replace("%s", uuid)));
        }

        public boolean isConnection(String uuid1, String uuid2) {
            final Object connection = ((JavascriptExecutor) driver).executeScript(String.format(IS_CONNECTION.replace("%1", uuid1).replace("%2", uuid2)));
            assertThat(connection).isInstanceOf(Boolean.class);
            return (Boolean) connection;
        }
    }
}
