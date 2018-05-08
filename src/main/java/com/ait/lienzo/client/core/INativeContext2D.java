/*
   Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.dom.client.Element;

public interface INativeContext2D {

    void initDeviceRatio();

    void saveContainer(String id);

    void restoreContainer();

    void save();

    void restore();

    void beginPath();

    void closePath();

    void moveTo(double x, double y);

    void lineTo(double x, double y);

    void setGlobalCompositeOperation(String operation);

    void setLineCap(String lineCap);

    void setLineJoin(String lineJoin);

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

    void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color);

    void fillText(String text, double x, double y, double maxWidth);

    void setFillColor(String fill);

    void rect(double x, double y, double w, double h);

    void rotate(double angle);

    void scale(double sx, double sy);

    void setStrokeColor(String color);

    void setStrokeWidth(double width);

    void setImageSmoothingEnabled(boolean enabled);

    void setFillGradient(LinearGradient.LinearGradientJSO grad);

    void setFillGradient(PatternGradient.PatternGradientJSO grad);

    void setFillGradient(RadialGradient.RadialGradientJSO grad);

    void transform(Transform.TransformJSO jso);

    void transform(double d0, double d1, double d2, double d3, double d4, double d5);

    void setTransform(Transform.TransformJSO jso);

    void setTransform(double d0, double d1, double d2, double d3, double d4, double d5);

    void setToIdentityTransform();

    void setTextFont(String font);

    void setTextBaseline(String baseline);

    void setTextAlign(String align);

    void strokeText(String text, double x, double y);

    void setGlobalAlpha(double alpha);

    void translate(double x, double y);

    void setShadow(Shadow.ShadowJSO shadow);

    boolean isSupported(String feature);

    boolean isPointInPath(double x, double y);

    ImageData getImageData(double x, double y, double width, double height);

    ImageData createImageData(double width, double height);

    ImageData createImageData(ImageData data);

    void putImageData(ImageData imageData, double x, double y);

    void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh);

    TextMetrics measureText(String text);

    void drawImage(Element image, double x, double y);

    void drawImage(Element image, double x, double y, double w, double h);

    void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h);

    void resetClip();

    void setMiterLimit(double limit);

    void setLineDash(NFastDoubleArrayJSO dashes);

    void setLineDashOffset(double offset);

    double getBackingStorePixelRatio();

    boolean path(PathPartList.PathPartListJSO list);

    boolean clip(PathPartList.PathPartListJSO list);

    void fill(Path2D.NativePath2D path);

    void stroke(Path2D.NativePath2D path);

    void clip(Path2D.NativePath2D path);

    Path2D.NativePath2D getCurrentPath();

    void setCurrentPath(Path2D.NativePath2D path);
}
