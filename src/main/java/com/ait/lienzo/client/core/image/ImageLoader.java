/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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
    private final Image m_image;

    public ImageLoader(final String url)
    {
        m_image = new Image();

        m_image.setVisible(false);

        RootPanel.get().add(m_image);

        if (isValidDataURL(url))
        {
            RootPanel.get().remove(m_image);

            ImageElement.as(m_image.getElement()).setSrc(url);

            onLoad(ImageElement.as(m_image.getElement()));
        }
        else
        {
            if (url.startsWith("http:") || (url.startsWith("https:")))
            {
                setCrossOrigin(ImageElement.as(m_image.getElement()), "anonymous");
            }
            m_image.addLoadHandler(new LoadHandler()
            {
                @Override
                public void onLoad(LoadEvent event)
                {
                    RootPanel.get().remove(m_image);

                    ImageLoader.this.onLoad(ImageElement.as(m_image.getElement()));
                }
            });
            m_image.addErrorHandler(new ErrorHandler()
            {
                @Override
                public void onError(ErrorEvent event)
                {
                    RootPanel.get().remove(m_image);

                    ImageLoader.this.onError("Image " + url + " failed to load");
                }
            });
            m_image.setUrl(url);
        }
    }

    public boolean isValidDataURL(String url)
    {
        if ((url.startsWith("data:")) && (url.length() > 6) && (false == ("data:,".equals(url))))
        {
            return true;
        }
        return false;
    }

    public ImageLoader(final ImageResource resource)
    {
        m_image = new Image();

        m_image.setVisible(false);

        RootPanel.get().add(m_image);

        m_image.addLoadHandler(new LoadHandler()
        {
            @Override
            public void onLoad(LoadEvent event)
            {
                RootPanel.get().remove(m_image);

                ImageLoader.this.onLoad(ImageElement.as(m_image.getElement()));
            }
        });
        m_image.addErrorHandler(new ErrorHandler()
        {
            @Override
            public void onError(ErrorEvent event)
            {
                RootPanel.get().remove(m_image);

                ImageLoader.this.onError("Resource " + resource.getName() + " failed to load");
            }
        });
        m_image.setResource(resource);
    }

    private final native void setCrossOrigin(ImageElement element, String value)
    /*-{
        element.crossOrigin = value;
    }-*/;

    public abstract void onLoad(ImageElement image);

    public abstract void onError(String message);
}
