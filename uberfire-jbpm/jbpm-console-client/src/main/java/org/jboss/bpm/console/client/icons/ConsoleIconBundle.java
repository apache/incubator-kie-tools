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
package org.jboss.bpm.console.client.icons;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ConsoleIconBundle extends ClientBundle
{
    @ClientBundle.Source("processIcon.png")
    ImageResource processIcon();

    @ClientBundle.Source("play.png")
    ImageResource instanceIcon();

    @ClientBundle.Source("toolsIcon.png")
    ImageResource settingsIcon();

    @ClientBundle.Source("taskIcon.png")
    ImageResource taskIcon();

    @ClientBundle.Source("userIcon.png")
    ImageResource userIcon();

    @ClientBundle.Source("tool-button-collapse-down.png")
    ImageResource collapseDownIcon();

    @ClientBundle.Source("tool-button-collapse-left.png")
    ImageResource collapseLeftIcon();

    @ClientBundle.Source("errorIcon.png")
    ImageResource errorIcon();

    @ClientBundle.Source("dialog-information.png")
    ImageResource infoIcon();

    @ClientBundle.Source("dialog-warning.png")
    ImageResource warnIcon();

    @ClientBundle.Source("dialog-question.png")
    ImageResource questionIcon();

    @ClientBundle.Source("loading.gif")
    ImageResource loadingIcon();

    @ClientBundle.Source("reload.png")
    ImageResource reloadIcon();

    @ClientBundle.Source("report.png")
    ImageResource reportIcon();

    @ClientBundle.Source("runtime.png")
    ImageResource runtimeIcon();

    @ClientBundle.Source("deployment.png")
    ImageResource deploymentIcon();

    @ClientBundle.Source("jobs.png")
    ImageResource jobsIcon();

    @ClientBundle.Source("ws.png")
    ImageResource webserviceIcon();

    @ClientBundle.Source("arrow_blue.png")
    ImageResource arrowIcon();

    @ClientBundle.Source("piece.png")
    ImageResource pieceIcon();

    @ClientBundle.Source("reportInstance.png")
    ImageResource reportInstanceIcon();

    @ClientBundle.Source("docIcon.png")
    ImageResource docIcon();

    @ClientBundle.Source("filter.png")
    ImageResource filterIcon();

    @ClientBundle.Source("database.gif")
    ImageResource databaseIcon();


    @ClientBundle.Source("red.png")
    ImageResource redIcon();

    @ClientBundle.Source("green.png")
    ImageResource greenIcon();

    @ClientBundle.Source("blue.png")
    ImageResource blueIcon();

    @ClientBundle.Source("yellow.png")
    ImageResource yellowIcon();

    @ClientBundle.Source("grey.png")
    ImageResource greyIcon();
    
    @ClientBundle.Source("large.png")
    ImageResource historySearchIcon();
}
