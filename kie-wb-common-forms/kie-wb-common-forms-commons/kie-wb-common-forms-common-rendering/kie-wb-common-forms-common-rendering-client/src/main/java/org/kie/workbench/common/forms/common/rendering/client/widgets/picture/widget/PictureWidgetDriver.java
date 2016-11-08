/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.VideoElement;

public class PictureWidgetDriver extends JavaScriptObject {

    protected PictureWidgetDriver() {
    }

    public static native PictureWidgetDriver create( VideoElement video, CanvasElement canvas, Element image ) /*-{
        return {
            video: video,
            canvas: canvas,
            image: image,
            streaming: false
        };
    }-*/;

    public final native void startStreaming( double width, double height ) /*-{
        if (this.streaming) {
            @org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetDriver::stopStreaming(*)(this);
        }

        this.width = width;
        this.height = height;

        this.streaming = true;

        var settings = {
            video: true,
            audio: false
        };

        var callback = function (mediaStream) {

            var video = this.video;

            this.stream = mediaStream;
            try {
                if (navigator.mozGetUserMedia) {
                    video.mozSrcObject = mediaStream;
                } else {
                    var vendorURL = window.URL || window.webkitURL;
                    video.src = vendorURL.createObjectURL(mediaStream);
                }
                video.play();
            } catch (err) {
                // swallow error
            }

        }.bind(this);

        var errorCallback = function (err) {
            console.log("An error occured! " + err);
        };

        var navigator = $wnd.navigator;

        if (navigator.getUserMedia) {
            navigator.getUserMedia(settings, callback, errorCallback);
        } else if (navigator.webkitGetUserMedia) {
            navigator.webkitGetUserMedia(settings, callback, errorCallback);
        } else if (navigator.mozGetUserMedia) {
            navigator.mozGetUserMedia(settings, callback, errorCallback);
        } else if (navigator.msGetUserMedia) {
            navigator.msGetUserMedia(settings, callback, errorCallback);
        }

        this.video.addEventListener('canplay', function (ev) {
            var video = this.video;

            var width = this.width;

            var height = video.videoHeight / (video.videoWidth / width);

            if (isNaN(height)) {
                height = width / (4 / 3);
            }

            video.setAttribute('width', width);
            video.setAttribute('height', height);

            var canvas = this.canvas;

            canvas.setAttribute('width', width);
            canvas.setAttribute('height', height);

            var image = this.image;
            image.setAttribute('width', width);
            image.setAttribute('height', height);
        }.bind(this), false);

    }-*/;

    public final native String takePicture() /*-{
        var context = this.canvas.getContext('2d');
        if (this.width && this.height) {
            this.canvas.width = this.width;
            this.canvas.height = this.height;
            context.drawImage(this.video, 0, 0, this.width, this.height);

            var data = this.canvas.toDataURL('image/png');

            @org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetDriver::stopStreaming(*)(this);

            return data;
        }
    }-*/;

    public static final void stopStreaming( PictureWidgetDriver driver ) {
        if ( driver != null ) {
            driver.doStopStreaming();
        }
    }

    public final native void doStopStreaming() /*-{
        try {
            this.streaming = false;
            if (this.video) {
                this.video.pause();
                this.video.src = null;
            }
            if (this.stream != null) {
                this.stream.getTracks().forEach(function (track) {
                    track.stop()
                })
                this.stream = null;
            }
        } catch (err) {
            console.log("An error occured! " + err);
        }
    }-*/;

}
