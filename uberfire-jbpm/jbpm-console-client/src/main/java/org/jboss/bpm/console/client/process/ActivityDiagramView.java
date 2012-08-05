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
package org.jboss.bpm.console.client.process;

import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.process.events.ActivityDiagramResultEvent;
import org.uberfire.client.annotations.WorkbenchEditor;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "ActivityDiagramView")
public class ActivityDiagramView extends ScrollPanel {

    public final static String ID = ActivityDiagramView.class.getName();

    private ProcessDefinitionRef processRef;

    private ProcessInstanceRef instanceRef;

    public ActivityDiagramView() {
        super();
    }

    public void update(ActivityDiagramResultEvent event) {

        List<ActiveNodeInfo> activeNodeInfos = event.getActiveNodeInfo();
        String imageUrl = event.getImageUrl();

        // remove contents
        this.clear();

        String s =
                "<div style='width:1024px; height:768px; background-color:#ffffff;'>" +
                        "<div id=\"imageContainer\" style=\"position:relative;top:-1;left:-1;\">" +
                        "<img src=\"" + imageUrl + "\" style=\"position:absolute;top:0;left:0\" />";
        for (ActiveNodeInfo activeNodeInfo : activeNodeInfos) {

            s += "<div class=\"bpm-graphView-activityImage\" style=\"position:absolute;top:" + (activeNodeInfo.getActiveNode().getY() - 8) + "px;left:" + (activeNodeInfo.getActiveNode().getX() - 8) + "px;width:50px;height:50px; z-index:1000;background-image: url(images/icons/play_red_big.png);background-repeat:no-repeat;\"></div>";
        }
        s += "</div>" +
                "</div>";

        HTML html = new HTML(s);

        this.add(html);
        //TODO: -Rikkola-
//        invalidate();
    }

    /**
     * No overlay. Activity encoded in image
     * @param imageUrl
     */
    public void update(String imageUrl) {
        // remove contents
        this.clear();

        String s =
                "<div style='width:1024px; height:768px; background-color:#ffffff;'>" +
                        "<div id=\"imageContainer\" style=\"position:relative;top:-1;left:-1;\">" +
                        "<img src=\"" + imageUrl + "\" style=\"position:absolute;top:0;left:0\" />";
        s += "</div>" +
                "</div>";

        HTML html = new HTML(s);

        this.add(html);
        //TODO: -Rikkola-
//        invalidate();
    }
}