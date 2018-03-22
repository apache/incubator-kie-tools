/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Picture;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.client.lienzo.shape.util.LienzoPictureUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class ServiceTaskShapeViewHandler
        implements ShapeViewHandler<ServiceTask, SVGShapeView<?>> {

    public static final String URL_DATA_IMAGE = "data:image";
    public static final String WID_ICON_ID = "task_service_icon";

    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;

    public ServiceTaskShapeViewHandler(final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
    }

    @Override
    public void handle(final ServiceTask bean,
                       final SVGShapeView<?> view) {
        // Obtain the work item's icon data url.
        final String itemIconData = workItemDefinitionRegistry
                .get()
                .get(bean.getName())
                .getIconData();
        final String iconData = null != itemIconData ? itemIconData : WorkItemDefinitionClientRegistry.DEFAULT_ICON_DATA;
        // Obtain the image element from the svg shape view.
        final SVGPrimitive svgPrimitive = SVGViewUtils.getPrimitive(view, WID_ICON_ID).get();
        // Load the image data.
        final Picture picture = (Picture) svgPrimitive.get();
        if (hasDataChanged(iconData,
                           picture.getURL())) {
            LienzoPictureUtils.forceLoad(picture,
                                         iconData,
                                         view::refresh);
        }
    }

    private static boolean hasDataChanged(final String url1,
                                          final String url2) {
        if (null == url1 && null == url2) {
            return false;
        }
        if (null != url1 && url2 != null) {
            return url1.startsWith(URL_DATA_IMAGE) || url1.equals(url2);
        }
        return true;
    }
}

