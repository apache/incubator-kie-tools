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


package com.ait.lienzo.client.core.image;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import jsinterop.base.Js;

public class ImageBitmapProxy {

    private JsImageBitmap image;

    public ImageBitmapProxy() {
    }

    ImageBitmapProxy(JsImageBitmap imageBitmap) {
        this.image = imageBitmap;
    }

    public void load(final String url,
                     final Runnable callback) {
        assert null == image;

        JsImageBitmap.loadImageBitmap(url, new JsImageBitmapCallback() {
            @Override
            public void onSuccess(JsImageBitmap image) {
                ImageBitmapProxy.this.image = Js.uncheckedCast(image);
                callback.run();
            }

            @Override
            public void onError(Object error) {
                LienzoCore.get().error("Error loading Image. Message: [" + error.toString() + "]");
            }
        });
    }

    public void draw(final Context2D context) {
        context.drawImage(image, 0, 0);
    }

    public void draw(final Context2D context,
                     final ImageClipBounds clipBounds) {
        final double clipX = clipBounds.getClipXPos();
        final double clipY = clipBounds.getClipYPos();
        final int width = getWidth();
        final int height = getHeight();
        final double _clipWide = clipBounds.getClipWide();
        final double clipWide = _clipWide > 0 ? _clipWide : width;
        final double _clipHigh = clipBounds.getClipHigh();
        final double clipHigh = _clipHigh > 0 ? _clipHigh : height;
        final double _destWide = clipBounds.getDestWide();
        final double destWide = _destWide > 0 ? _destWide : width;
        final double _destHigh = clipBounds.getDestHigh();
        final double destHigh = _destHigh > 0 ? _destHigh : height;
        context.drawImage(image, clipX, clipY, clipWide, clipHigh, 0, 0, destWide, destHigh);
    }

    public boolean isLoaded() {
        return null != image;
    }

    public int getWidth() {
        return isLoaded() ? image.getWidth() : 0;
    }

    public int getHeight() {
        return isLoaded() ? image.getHeight() : 0;
    }

    public void destroy() {
        image.close();
    }
}
