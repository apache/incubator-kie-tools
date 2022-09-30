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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class SWAPITests {

    private static final Logger LOG = LoggerFactory.getLogger(SWAPITests.class);

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"SWDiagramEditor\").get().setContent(\"\", '%s')";
    private static final String CONTENT_REGULAR_CHAR = "\\\"";
    private static final String CONTENT_EXECUTOR_CHAR = "&quot;";

    private static final String INDEX_HTML = "target/sw-editor-kogito-app/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String DIAGRAM_PANEL = "qe-static-workbench-panel-view";
    private static final String CANVAS_PANEL = "canvas-panel";
    private static final Boolean HEADLESS = Boolean.valueOf(System.getProperty("org.kie.sw.editor.browser.headless"));

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

    /**
     * Selenium web driver
     */
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().useMirror().setup();
    }

    @Before
    public void openSWEditor() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(HEADLESS);
        driver = new FirefoxDriver(firefoxOptions);

        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        final WebElement designer = waitOperation()
                .until(presenceOfElementLocated(className(DIAGRAM_PANEL)));
        assertThat(designer)
                .as("Diagram panel is a prerequisite for all tests. " +
                            "its absence is indicator of designer load fail.")
                .isNotNull();
    }

    @Test
    public void testGetNodeIdsAndColor() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        for (String string : nodeIds) {
            Object backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
            Object borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

            assertThat(backgroundColorResult).isInstanceOf(String.class);
            assertThat(borderColorResult).isInstanceOf(String.class);

            String backgroundColor = (String) backgroundColorResult;
            String borderColor = (String) borderColorResult;

            assertThat(backgroundColor).isEqualTo("#ffffff");
            assertThat(borderColor).isEqualTo("#d5d5d5");

            String command1 = SET_BACKGROUND_COLOR.replace("%1", string).replace("%2", "#ff00ff");
            String command2 = SET_BORDER_COLOR.replace("%1", string).replace("%2", "#dd00ff");

            ((JavascriptExecutor) driver).executeScript(String.format(command1));
            ((JavascriptExecutor) driver).executeScript(String.format(command2));

            backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
            borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

            assertThat(backgroundColorResult).isInstanceOf(String.class);
            assertThat(borderColorResult).isInstanceOf(String.class);

            backgroundColor = (String) backgroundColorResult;
            borderColor = (String) borderColorResult;

            assertThat(backgroundColor).isEqualTo("#ff00ff");
            assertThat(borderColor).isEqualTo("#dd00ff");
        }
    }

    @Test
    public void testGetNodeIdsAndLocation() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        Object location1 = ((JavascriptExecutor) driver).executeScript(String.format(GET_LOCATION.replace("%s", nodeIds.get(0))));
        Object absolutelocation1 = ((JavascriptExecutor) driver).executeScript(String.format(GET_ABSOLUTE_LOCATION.replace("%s", nodeIds.get(0))));

        assertThat(location1).isInstanceOf(ArrayList.class);
        assertThat(absolutelocation1).isInstanceOf(ArrayList.class);

        List<Object> location1List = (ArrayList<Object>) location1;
        List<Object> absoluteLocation1List = (ArrayList<Object>) absolutelocation1;

        assertThat(location1List.get(0)).isEqualTo(172.5d);
        assertThat(location1List.get(1)).isEqualTo(227L);

        assertThat(absoluteLocation1List.get(0)).isEqualTo(172.5d);
        assertThat(absoluteLocation1List.get(1)).isEqualTo(227L);

        Object location2 = ((JavascriptExecutor) driver).executeScript(String.format(GET_LOCATION.replace("%s", nodeIds.get(1))));
        Object absolutelocation2 = ((JavascriptExecutor) driver).executeScript(String.format(GET_ABSOLUTE_LOCATION.replace("%s", nodeIds.get(1))));

        assertThat(location2).isInstanceOf(ArrayList.class);
        assertThat(absolutelocation2).isInstanceOf(ArrayList.class);

        List<Object> location2List = (ArrayList<Object>) location2;
        List<Object> absoluteLocation2List = (ArrayList<Object>) absolutelocation2;

        assertThat(location2List.get(0)).isEqualTo(70L);
        assertThat(location2List.get(1)).isEqualTo(50L);

        assertThat(absoluteLocation2List.get(0)).isEqualTo(70L);
        assertThat(absoluteLocation2List.get(1)).isEqualTo(50L);
    }

    @Test
    public void testGetNodeIdsAndDimensions() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        Object dimension1 = ((JavascriptExecutor) driver).executeScript(String.format(GET_DIMENSIONS.replace("%s", nodeIds.get(0))));

        assertThat(dimension1).isInstanceOf(ArrayList.class);

        List<Object> dimension1List = (ArrayList<Object>) dimension1;

        assertThat(dimension1List.get(0)).isEqualTo(49L);
        assertThat(dimension1List.get(1)).isEqualTo(47L);

        Object dimension2 = ((JavascriptExecutor) driver).executeScript(String.format(GET_DIMENSIONS.replace("%s", nodeIds.get(1))));

        assertThat(dimension2).isInstanceOf(ArrayList.class);

        List<Object> dimension2List = (ArrayList<Object>) dimension2;

        assertThat(dimension2List.get(0)).isEqualTo(254L);
        assertThat(dimension2List.get(1)).isEqualTo(92L);
    }

    @Test
    public void testGetNodeIdsAndApplyState() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        String string = nodeIds.get(0);
        Object backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
        Object borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

        assertThat(backgroundColorResult).isInstanceOf(String.class);
        assertThat(borderColorResult).isInstanceOf(String.class);

        String backgroundColor = (String) backgroundColorResult;
        String borderColor = (String) borderColorResult;

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("#d5d5d5");

        String command1 = APPLY_STATE.replace("%1", string).replace("%2", "invalid");

        ((JavascriptExecutor) driver).executeScript(String.format(command1));

        backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
        borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

        assertThat(backgroundColorResult).isInstanceOf(String.class);
        assertThat(borderColorResult).isInstanceOf(String.class);

        backgroundColor = (String) backgroundColorResult;
        borderColor = (String) borderColorResult;

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(255,0,0)");

        command1 = APPLY_STATE.replace("%1", string).replace("%2", "highlight");

        ((JavascriptExecutor) driver).executeScript(String.format(command1));

        backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
        borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

        assertThat(backgroundColorResult).isInstanceOf(String.class);
        assertThat(borderColorResult).isInstanceOf(String.class);

        backgroundColor = (String) backgroundColorResult;
        borderColor = (String) borderColorResult;

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(51,102,204)");

        command1 = APPLY_STATE.replace("%1", string).replace("%2", "selected");

        ((JavascriptExecutor) driver).executeScript(String.format(command1));

        backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
        borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

        assertThat(backgroundColorResult).isInstanceOf(String.class);
        assertThat(borderColorResult).isInstanceOf(String.class);

        backgroundColor = (String) backgroundColorResult;
        borderColor = (String) borderColorResult;

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(0,136,206)");

        command1 = APPLY_STATE.replace("%1", string).replace("%2", "none");

        ((JavascriptExecutor) driver).executeScript(String.format(command1));
        Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish

        backgroundColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BACKGROUND_COLOR.replace("%s", string)));
        borderColorResult = ((JavascriptExecutor) driver).executeScript(String.format(GET_BORDER_COLOR.replace("%s", string)));

        assertThat(backgroundColorResult).isInstanceOf(String.class);
        assertThat(borderColorResult).isInstanceOf(String.class);

        backgroundColor = (String) backgroundColorResult;
        borderColor = (String) borderColorResult;

        assertThat(backgroundColor).isEqualTo("#ffffff");
        assertThat(borderColor).isEqualTo("rgb(213,213,213)");
    }

    @Test
    public void testGetNodeIdsCenterNode() throws Exception {
        // curently centerNode is only applicable if scrollbars are present and correct zoom level set, otherwise it ignores it such as in this case
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        Object location1 = ((JavascriptExecutor) driver).executeScript(String.format(GET_LOCATION.replace("%s", nodeIds.get(0))));
        assertThat(location1).isInstanceOf(ArrayList.class);

        List<Object> location1List = (ArrayList<Object>) location1;

        assertThat(location1List.get(0)).isEqualTo(172.5d);
        assertThat(location1List.get(1)).isEqualTo(227L);

        ((JavascriptExecutor) driver).executeScript(String.format(CENTER_NODE.replace("%s", nodeIds.get(0))));

        location1 = ((JavascriptExecutor) driver).executeScript(String.format(GET_LOCATION.replace("%s", nodeIds.get(0))));
        assertThat(location1).isInstanceOf(ArrayList.class);

        location1List = (ArrayList<Object>) location1;

        assertThat(location1List.get(0)).isEqualTo(172.5d);
        assertThat(location1List.get(1)).isEqualTo(227L);
    }

    @Test
    public void testGetNodeIdsAndConecctions() throws Exception {
        final String expected = loadResource("ProcessTransactionsExample.sw.json");
        setContent(expected);
        waitCanvasPanel();

        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_NODE_IDS));
        assertThat(result).isInstanceOf(ArrayList.class);

        List<String> nodeIds = (ArrayList<String>) result;
        assertThat(nodeIds.size()).isEqualTo(2);

        final Object connection = ((JavascriptExecutor) driver).executeScript(String.format(IS_CONNECTION.replace("%1", nodeIds.get(0)).replace("%2", nodeIds.get(1))));
        assertThat(connection).isInstanceOf(Boolean.class);
        System.out.println("Magnet Size: " + connection);
        Boolean isConnection = (Boolean) connection;
        assertThat(connection).isEqualTo(true);
    }

    private void waitCanvasPanel() {
        final WebElement canvasPanelDiv = waitOperation()
                .until(visibilityOfElementLocated(className(CANVAS_PANEL)));
        assertThat(canvasPanelDiv)
                .as("Once content is set canvas panel visibility is a prerequisite" +
                            "for further test execution.")
                .isNotNull();
    }

    private void setContent(final String xml) {
        try {
            String content = String.format(SET_CONTENT_TEMPLATE, xml);
            content = content.replace(CONTENT_REGULAR_CHAR, CONTENT_EXECUTOR_CHAR);
            ((JavascriptExecutor) driver).executeScript(content);
        } catch (Exception e) {
            LOG.error("Exception during JS execution. Ex: {}", e.getMessage());
        }
    }

    /**
     * Use this for loading serverless workflow definitions placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10000).getSeconds());
    }
}
