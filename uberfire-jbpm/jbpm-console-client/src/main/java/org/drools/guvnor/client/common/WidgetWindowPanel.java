/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.Widget;

/**
 * General purpose window panel
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class WidgetWindowPanel {
    // TODO: This needs to be a popup. -Rikkola-
//    private WindowPanel window;

    public WidgetWindowPanel(String title, final Widget widget) {
        this(title, widget, false);
    }

    public WidgetWindowPanel(String title, final Widget widget, boolean overlay) {
//        window = new WindowPanel(title);
//        window.setAnimationEnabled(true);
//        window.setWidget(widget);
//
//        WindowUtil.addMaximizeButton(window, Caption.CaptionRegion.RIGHT);
//        WindowUtil.addMinimizeButton(window, Caption.CaptionRegion.RIGHT);
//
//        window.pack();
//
//        if(overlay)
//        {
//            final int width = Window.getClientWidth()-120;
//            final int height = Window.getClientHeight()-80;
//
//            window.setContentSize(new Dimension(width, height));
//            window.setPopupPosition(60,40);
//
//            window.show();
//        }
//        else
//        {
//            window.center();
//        }
    }

    public void close() {
//        window.hide();
    }
}
