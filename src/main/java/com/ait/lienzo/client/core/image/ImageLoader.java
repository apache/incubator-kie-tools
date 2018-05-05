/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class ImageLoader
{
    public ImageLoader(final String url)
    {
        this(url,
             new Image());
    }

    public ImageLoader(final String url,
                       final Image image)
    {
        final ImageElement element = ImageElement.as(image.getElement());

        image.setVisible(false);

        final String crossOrigin = url.startsWith("http:") || (url.startsWith("https:")) ? "anonymous" : null;

        if (null != crossOrigin)
        {
            setCrossOrigin(element, crossOrigin);
        }

        final HandlerRegistrationManager m_HandlerRegManager = new HandlerRegistrationManager();

        m_HandlerRegManager.register(
                image.addLoadHandler(new LoadHandler()
                {
                    @Override
                    public final void onLoad(final LoadEvent event)
                    {
                        m_HandlerRegManager.removeHandler();
                        doImageElementLoadAndRetry(element, image, crossOrigin, url);
                    }
                })
        );
        m_HandlerRegManager.register(
                image.addErrorHandler(new ErrorHandler()
                {
                    @Override
                    public final void onError(final ErrorEvent event)
                    {
                        RootPanel.get().remove(image);
                        m_HandlerRegManager.removeHandler();

                        onImageElementError("Image " + url + " failed to load");
                    }
                })
        );
        RootPanel.get().add(image);

        if (isValidDataURL(url) && isValidSVG(url))
        {
            image.setUrl(url);
        }
        else
        {
            element.setSrc(url);
        }
    }

    public ImageLoader(final ImageResource resource)
    {
        this(resource,
             new Image());
    }

    public ImageLoader(final ImageResource resource,
                       final Image image)
    {
        final ImageElement element = ImageElement.as(image.getElement());

        image.setVisible(false);

        final HandlerRegistrationManager m_HandlerRegManager = new HandlerRegistrationManager();

        m_HandlerRegManager.register(
                image.addLoadHandler(new LoadHandler()
        {
            @Override
            public final void onLoad(final LoadEvent event)
            {
                onImageElementLoad(element);
                m_HandlerRegManager.removeHandler();
            }
        })
        );
        m_HandlerRegManager.register(
            image.addErrorHandler(new ErrorHandler()
            {
                @Override
                public final void onError(final ErrorEvent event)
                {
                    RootPanel.get().remove(image);
                    m_HandlerRegManager.removeHandler();
                    onImageElementError("Resource " + resource.getName() + " failed to load");
                }
            })
        );
        image.setResource(resource);

        RootPanel.get().add(image);
    }

    private final void doImageElementLoadAndRetry(final ImageElement elem,
                                                  final Image image,
                                                  final String orig,
                                                  final String url)
    {
        final int w = Math.max(image.getWidth(), elem.getWidth());

        final int h = Math.max(image.getHeight(), elem.getHeight());

        if ((w < 1) || (h < 1))
        {
            load(url, orig, new JSImageCallback()
            {
                @Override
                public void onSuccess(final ImageElement e)
                {
                    onImageElementLoad(e);
                }

                @Override
                public void onFailure()
                {
                    RootPanel.get().remove(image);

                    onImageElementError("Image " + url + " failed to load");
                }
            });
        }
        else
        {
            elem.setWidth(w);

            elem.setHeight(h);

            onImageElementLoad(elem);
        }
    }

    public boolean isValidDataURL(final String url)
    {
        if ((url.startsWith("data:")) && (url.length() > 6) && (false == ("data:,".equals(url))))
        {
            return true;
        }
        return false;
    }

    public boolean isValidSVG(final String url)
    {
        return url.toLowerCase().contains("svg+xml");
    }

    private final native void load(String url, String orig, JSImageCallback self)
    /*-{
		var image = new $wnd.Image();
		image.onload = function() {
			if ('naturalHeight' in image) {
				if (image.naturalHeight + image.naturalWidth == 0) {
					self.@com.ait.lienzo.client.core.image.ImageLoader.JSImageCallback::onFailure()();
					return;
				}
			} else if (image.width + image.height == 0) {
				self.@com.ait.lienzo.client.core.image.ImageLoader.JSImageCallback::onFailure()();
				return;
			}
			self.@com.ait.lienzo.client.core.image.ImageLoader.JSImageCallback::onSuccess(Lcom/google/gwt/dom/client/ImageElement;)(image);
		};
		image.onerror = function() {
			self.@com.ait.lienzo.client.core.image.ImageLoader.JSImageCallback::onFailure()();
		};
		if (undefined != orig) {
			image.crossOrigin = orig;
		}
		image.src = url;
    }-*/;

    private final native void setCrossOrigin(ImageElement element, String value)
    /*-{
		element.crossOrigin = value;
    }-*/;

    public abstract void onImageElementLoad(ImageElement elem);
    public abstract void onImageElementError(String message);

    private interface JSImageCallback
    {
        public void onSuccess(ImageElement e);

        public void onFailure();
    }
}
