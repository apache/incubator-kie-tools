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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.SVGPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Color;

class EmptyStateLayer extends Layer {

    private SVGPath cursor;
    private Text caption;
    private Text message;

    EmptyStateLayer(final String captionText,
                    final String messageText) {
        initializeCursor();
        initializeCaption(captionText);
        initializeMessage(messageText);

        setTransformable(false);
        setListening(false);
    }

    @Override
    public void drawWithoutTransforms(final Context2D context,
                                      final double alpha,
                                      final BoundingBox bounds) {

        drawBackground(context);
        updateCaption();
        updateMessage();
        updateCursor();
        super.drawWithoutTransforms(context, alpha, bounds);
    }

    private void initializeCursor() {
        cursor = new SVGPath("M283.25 260.75c4.75 4.5 6 11.5 3.5 17.25-2.5 6-8.25 10-14.75 " +
                                     "10h-95.5l50.25 119c3.5 8.25-0.5 17.5-8.5 21l-44.25 18.75c-8.25 " +
                                     "3.5-17.5-0.5-21-8.5l-47.75-113-78 78c-3 3-7 4.75-11.25 4.75-2 " +
                                     "0-4.25-0.5-6-1.25-6-2.5-10-8.25-10-14.75v-376c0-6.5 4-12.25 " +
                                     "10-14.75 1.75-0.75 4-1.25 6-1.25 4.25 0 8.25 1.5 11.25 4.75z");
        cursor.setFillColor(Color.fromColorString(EmptyStateView.CURSOR_FILL_COLOR));
        add(cursor);
    }

    private void initializeCaption(final String captionText) {
        caption = new Text(captionText);
        caption.setFontFamily(EmptyStateView.TEXT_FONT_FAMILY);
        caption.setFontSize(EmptyStateView.CAPTION_FONT_SIZE);
        caption.setAlpha(EmptyStateView.TEXT_ALPHA);
        caption.setFillColor(EmptyStateView.TEXT_FILL_COLOR);
        caption.setStrokeColor(EmptyStateView.TEXT_STROKE_COLOR);
        caption.setStrokeWidth(EmptyStateView.TEXT_STROKE_WIDTH);
        add(caption);
    }

    private void initializeMessage(final String messageText) {
        message = new Text(messageText);
        message.setFontFamily(EmptyStateView.TEXT_FONT_FAMILY);
        message.setFontSize(EmptyStateView.MESSAGE_FONT_SIZE);
        message.setAlpha(EmptyStateView.TEXT_ALPHA);
        message.setFillColor(EmptyStateView.TEXT_FILL_COLOR);
        message.setStrokeColor(EmptyStateView.TEXT_STROKE_COLOR);
        message.setStrokeWidth(EmptyStateView.TEXT_STROKE_WIDTH);
        add(message);
    }

    private void drawBackground(final Context2D context) {
        context.save();
        context.setFillColor(Color.fromColorString(EmptyStateView.BACKGROUND_FILL_COLOR));

        context.fillRect(0, 0, getWidth(), this.getHeight());

        context.restore();
    }

    private void updateCursor() {
        BoundingBox captionBB = caption.getBoundingBox();
        double captionCenterY = captionBB.getHeight() / 2d;

        double cursorTargetWidth = 40;
        double cursorTargetHeight = 62.4;
        double cursorCenterX = cursorTargetWidth / 2d;

        double centerX = getWidth() / 2d;
        double centerY = getHeight() / 2d;

        BoundingBox cursorBB = cursor.getBoundingBox();
        final double[] cursorScale = getScaleFactor(cursorBB.getWidth(),
                                                    cursorBB.getHeight(),
                                                    cursorTargetWidth,
                                                    cursorTargetHeight);

        cursor.setScale(cursorScale[0], cursorScale[1]);
        cursor.setX(centerX - cursorCenterX);
        cursor.setY(centerY - captionCenterY - 25 - cursorTargetHeight);
    }

    private void updateCaption() {
        BoundingBox captionBB = this.caption.getWrapper().getBoundingBox();
        double captionCenterX = captionBB.getWidth() / 2d;
        double captionCenterY = captionBB.getHeight() / 2d;

        double centerX = getWidth() / 2d;
        double centerY = getHeight() / 2d;

        caption.setX(centerX - captionCenterX);
        caption.setY(centerY + captionCenterY);
    }

    private void updateMessage() {
        BoundingBox captionBB = caption.getBoundingBox();
        double captionCenterY = captionBB.getHeight() / 2d;

        BoundingBox messageBB = message.getBoundingBox();
        double messageCenterX = messageBB.getWidth() / 2d;

        double centerX = getWidth() / 2d;
        double centerY = getHeight() / 2d;

        message.setX(centerX - messageCenterX);
        message.setY(centerY + captionCenterY + 25 + messageBB.getHeight());
    }

    private static double[] getScaleFactor(final double width,
                                           final double height,
                                           final double targetWidth,
                                           final double targetHeight) {
        return new double[]{
                width > 0 ? targetWidth / width : 1,
                height > 0 ? targetHeight / height : 1};
    }
}