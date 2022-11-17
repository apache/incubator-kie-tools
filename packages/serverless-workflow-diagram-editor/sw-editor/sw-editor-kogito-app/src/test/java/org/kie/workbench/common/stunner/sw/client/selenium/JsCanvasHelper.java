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

import org.openqa.selenium.JavascriptExecutor;

import static org.assertj.core.api.Assertions.assertThat;

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
        Object dimension = executor.executeScript(String.format(GET_DIMENSIONS.replace("%s", uuid)));
        assertThat(dimension).isInstanceOf(ArrayList.class);
        return (ArrayList<Object>) dimension;
    }

    public void applyState(String uuid, String state) {
        String command = APPLY_STATE.replace("%1", uuid).replace("%2", state);
        executor.executeScript(String.format(command));
        try {
            Thread.sleep(2000); // Otherwise sometime it applies the state and sometimes it does not finish
        } catch (InterruptedException ex) {
        }
    }

    public void centerNode(String uuid) {
        executor.executeScript(String.format(CENTER_NODE.replace("%s", uuid)));
    }

    public boolean isConnection(String uuid1, String uuid2) {
        final Object connection = executor.executeScript(String.format(IS_CONNECTION.replace("%1", uuid1).replace("%2", uuid2)));
        assertThat(connection).isInstanceOf(Boolean.class);
        return (Boolean) connection;
    }
}