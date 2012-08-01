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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoadingOverlay
{

    static PopupPanel p = null;

    public static void on(Widget parent, boolean loading)
    {
        if(parent !=null && loading)
        {
            int left = parent.getAbsoluteLeft();
            int top = parent.getAbsoluteTop();

            int width = parent.getOffsetWidth();
            int height = parent.getOffsetHeight();

            if(width>100 & height>100) // workaround hidden panels
            {
                p = new PopupPanel();
                p.setStylePrimaryName("bpm-loading-overlay");
                p.setWidget(new Image("images/loading_lite.gif"));
                p.setPopupPosition(left+(width/2)-15, top+(height/2)-15);
                p.show();
            }

        }
        else
        {
            if(p!=null)
            {
                p.hide();
                p = null;
            }
        }
    }
}
