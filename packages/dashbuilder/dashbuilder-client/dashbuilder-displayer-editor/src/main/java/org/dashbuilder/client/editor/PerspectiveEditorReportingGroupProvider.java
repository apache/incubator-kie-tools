/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.editor;

import org.dashbuilder.client.editor.resources.i18n.Constants;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.RendererManager;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;

import static org.dashbuilder.displayer.DisplayerType.AREACHART;
import static org.dashbuilder.displayer.DisplayerType.BARCHART;
import static org.dashbuilder.displayer.DisplayerType.BUBBLECHART;
import static org.dashbuilder.displayer.DisplayerType.LINECHART;
import static org.dashbuilder.displayer.DisplayerType.MAP;
import static org.dashbuilder.displayer.DisplayerType.METERCHART;
import static org.dashbuilder.displayer.DisplayerType.METRIC;
import static org.dashbuilder.displayer.DisplayerType.PIECHART;
import static org.dashbuilder.displayer.DisplayerType.SELECTOR;
import static org.dashbuilder.displayer.DisplayerType.TABLE;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * {@link PerspectiveEditorComponentGroupProvider} holding all the chart displayer components
 */
@ApplicationScoped
public class PerspectiveEditorReportingGroupProvider implements PerspectiveEditorComponentGroupProvider {

    private SyncBeanManager beanManager;
    private Constants i18n = Constants.INSTANCE;
    private RendererManager rendererManager;

    @Inject
    public PerspectiveEditorReportingGroupProvider(SyncBeanManager beanManager, 
                                                   RendererManager rendererManager) {
        this.beanManager = beanManager;
        this.rendererManager = rendererManager;
    }

    @Override
    public String getName() {
        return i18n.drag_group_name_reporting();
    }

    @Override
    public LayoutDragComponentGroup getComponentGroup() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(getName());
        addComponent(BARCHART, group, i18n.drag_component_name_barchart(), BarChartDragComponent.class);
        addComponent(PIECHART, group, i18n.drag_component_name_piechart(), PieChartDragComponent.class);
        addComponent(LINECHART, group, i18n.drag_component_name_linechart(), LineChartDragComponent.class);
        addComponent(AREACHART, group, i18n.drag_component_name_areachart(), AreaChartDragComponent.class);
        addComponent(BUBBLECHART, group, i18n.drag_component_name_bubblechart(), BubbleChartDragComponent.class);
        addComponent(METERCHART, group, i18n.drag_component_name_meterchart(), MeterChartDragComponent.class);
        addComponent(MAP, group, i18n.drag_component_name_mapchart(), MapChartDragComponent.class);
        addComponent(METRIC, group, i18n.drag_component_name_metric(), MetricDragComponent.class);
        addComponent(TABLE, group, i18n.drag_component_name_table(), TableDragComponent.class);
        addComponent(SELECTOR, group, i18n.drag_component_name_filter(), SelectorDragComponent.class);
        return group;
    }

    private void addComponent(DisplayerType type, LayoutDragComponentGroup group, String name, Class dragClass) {
        if (rendererManager.isTypeSupported(type)) {
            group.addLayoutDragComponent(name, lookupDisplayerComponent(dragClass));
        }
    }
    
    protected DisplayerDragComponent lookupDisplayerComponent(Class dragClass) {
        SyncBeanDef<DisplayerDragComponent> displayerBeanDef = beanManager.lookupBean(dragClass);
        return displayerBeanDef.newInstance();
    }
    
}
