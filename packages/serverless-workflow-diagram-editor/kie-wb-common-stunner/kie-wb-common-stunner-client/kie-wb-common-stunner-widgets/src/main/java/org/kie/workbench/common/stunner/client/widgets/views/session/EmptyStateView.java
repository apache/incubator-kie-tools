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


package org.kie.workbench.common.stunner.client.widgets.views.session;

import com.ait.lienzo.shared.core.types.ColorName;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;

@Dependent
public class EmptyStateView {

    public static final int CAPTION_FONT_SIZE = 20;
    public static final int MESSAGE_FONT_SIZE = 10;
    public static final String TEXT_FONT_FAMILY = "Open Sans,Helvetica,Arial,sans-serif";

    public static final int TEXT_ALPHA = 1;

    public static final String TEXT_STROKE_COLOR = ColorName.TRANSPARENT.toString();
    public static final int TEXT_STROKE_WIDTH = 0;

    //See PF Empty State: https://www.patternfly.org/v3/pattern-library/communication/empty-state/
    public static final String BACKGROUND_FILL_COLOR = "#f5f5f5";
    public static final String CURSOR_FILL_COLOR = "#9c9c9c";
    public static final String TEXT_FILL_COLOR = "#363636";

    private LienzoLayer lienzoLayer;
    private String captionText;
    private String messageText;
    private EmptyStateLayer emptyStateLayer;

    public void init(final LienzoLayer lienzoLayer,
                     final String captionText,
                     final String messageText) {
        this.lienzoLayer = lienzoLayer;
        this.captionText = captionText;
        this.messageText = messageText;
    }

    public void show() {
        createEmptyStateLayer();
    }

    public void hide() {
        destroyEmptyStateLayer();
    }

    @PreDestroy
    public void destroy() {
        destroyEmptyStateLayer();
        lienzoLayer = null;
        captionText = null;
        messageText = null;
    }

    private void createEmptyStateLayer() {
        if (null == emptyStateLayer && lienzoLayer.isReady()) {
            emptyStateLayer = new EmptyStateLayer(captionText, messageText);
            lienzoLayer.add(emptyStateLayer);
        }
    }

    private void destroyEmptyStateLayer() {
        if (null != emptyStateLayer) {
            emptyStateLayer.removeFromParent();
            emptyStateLayer = null;
        }
    }
}
