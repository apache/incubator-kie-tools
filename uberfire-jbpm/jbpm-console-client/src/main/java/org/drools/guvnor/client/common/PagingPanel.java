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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Cheap paging helper
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class PagingPanel extends HorizontalPanel {

    private PagingCallback callback;
    private int limit = 12;
    private int page = 0;

    private boolean leftBounds = true;
    private boolean rightBounds;

    private Button revBtn;
    private Button ffwBtn;

    public PagingPanel(final PagingCallback callback) {
        setStyleName("bpm-paging-panel");

        this.callback = callback;

        ClickHandler clickHandler = new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                page--;
                rightBounds = false;
                callback.rev();
            }
        };
        revBtn = new Button(
                "<",
                clickHandler
        );

        this.add(revBtn);

        ClickHandler clickHandler2 = new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                page++;
                leftBounds = false;
                callback.ffw();
            }
        };

        ffwBtn = new Button(">",
                clickHandler2
        );

        this.add(ffwBtn);
    }

    public void reset() {
        leftBounds = true;
        rightBounds = false;
        page = 0;
    }

    public List trim(List tmp) {
        List trimmed = new ArrayList();

        int size = tmp.size();
        if (limit >= size) {
            trimmed = tmp;
            rightBounds = true;
        } else {

            // leftbounds
            if (page <= 0) {
                page = 0;
                leftBounds = true;
            }

            int begin = 0;
            int end = 0;

            // rightbounds
            if (page * limit >= size) {
                begin = (page - 1) * limit;
                rightBounds = true;
            } else {
                begin = page * limit;
            }

            if (begin + limit >= size) {
                end = size;
                rightBounds = true;
            } else {
                end = begin + limit;
            }

            // select range
            int i = 0;
            for (Object o : tmp) {
                if (i >= begin && i < end) {
                    trimmed.add(o);
                }
                i++;
            }

        }

        revBtn.setEnabled(!leftBounds);
        ffwBtn.setEnabled(!rightBounds);

        return trimmed;
    }
}
