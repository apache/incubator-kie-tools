/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.exports.svg;

import java.util.Map;
import java.util.Optional;

import elemental2.dom.CanvasGradient;
import elemental2.dom.Element;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.ImageData;
import elemental2.dom.TextMetrics;

/**
 * Represents a canvas context 2D that is used to wrap calls to canvas used to generate SVG files based on canvas.
 */
public interface IContext2D {

    String getSerializedSvg();

    void setFillStyle(String fillStyleColor);

    void setStrokeStyle(String fillStyleColor);

    void setLineWidth(double var1);

    void setLineCap(String lineCap);

    void setLineJoin(String lineJoin);

    void setImageSmoothingEnabled(boolean enabled);

    void setFont(String font);

    void setTextBaseline(String baseline);

    void setTextAlign(String align);

    void setGlobalAlpha(double alpha);

    void setShadowColor(String color);

    void setShadowOffsetX(double color);

    void setShadowOffsetY(double color);

    void setShadowBlur(int color);

    void setMiterLimit(double limit);

    void setLineDashOffset(double offset);

    void addAttributes(Map<String, String> attributes);

    void saveGroup(Map<String, String> attributes);

    void saveStyle();

    void restoreGroup();

    void restoreStyle();

    void save();

    void restore();

    void beginPath();

    void closePath();

    void moveTo(double x, double y);

    void lineTo(double x, double y);

    void setGlobalCompositeOperation(String operation);

    void quadraticCurveTo(double cpx, double cpy, double x, double y);

    void arc(double x, double y, double radius, double startAngle, double endAngle);

    void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise);

    void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac);

    void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea);

    void arcTo(double x1, double y1, double x2, double y2, double radius);

    void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

    void clearRect(double x, double y, double w, double h);

    void clip();

    void fill();

    void stroke();

    void fillRect(double x, double y, double w, double h);

    void fillText(String text, double x, double y);

    void rect(double x, double y, double w, double h);

    void rotate(double angle);

    void scale(double sx, double sy);

    void transform(double d0, double d1, double d2, double d3, double d4, double d5);

    void setTransform(double d0, double d1, double d2, double d3, double d4, double d5);

    void strokeText(String text, double x, double y);

    void translate(double x, double y);

    boolean isPointInPath(double x, double y);

    void putImageData(ImageData imageData, double x, double y);

    void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh);

    void resetClip();

    void setLineDash(double[] dashes);

    TextMetrics measureText(String text);

    HTMLCanvasElement createImageData(ImageData data);

    ImageData getImageData(double x, double y, double width, double height);

    ImageData createImageData(double width, double height);

    void drawImage(Element image, double x, double y);

    void drawImage(Element image, double x, double y, double w, double h);

    void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h);

    CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);

    CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);
}
