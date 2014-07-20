/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.image;

import com.google.gwt.core.client.JavaScriptObject;

public final class JSImage extends JavaScriptObject
{
    protected JSImage()
    {
    }

    public static final native JSImage make(String url, ImageLoader self)
    /*-{
		var image = new $wnd.Image();

		image.onload = function() {

			if ('naturalHeight' in image) {
				if (image.naturalHeight + image.naturalWidth == 0) {
					self.@com.ait.lienzo.client.core.image.ImageLoader::onErrorHelper(Ljava/lang/String;)("Image " + url + " did not load completely or 0.");
					return;
				}
			} else if (image.width + image.height == 0) {
				self.@com.ait.lienzo.client.core.image.ImageLoader::onErrorHelper(Ljava/lang/String;)("Image " + url + " did not load completely or 0.");
				return;
			}
			self.@com.ait.lienzo.client.core.image.ImageLoader::onLoadedHelper()();
		};
		image.onerror = function() {

			self.@com.ait.lienzo.client.core.image.ImageLoader::onErrorHelper(Ljava/lang/String;)("Image " + url + " did not load.");
		};
		image.crossOrigin = 'anonymous';

		image.src = url;

		return image;
    }-*/;

    public final native int getWidth()
    /*-{
		return this.width || 0;
    }-*/;

    public final native int getHeight()
    /*-{
		return this.height || 0;
    }-*/;
}