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
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * {@link PerspectiveEditorComponentGroupProvider} holding all the chart displayer components
 */
@ApplicationScoped
public class PerspectiveEditorReportingGroupProvider implements PerspectiveEditorComponentGroupProvider {

    private SyncBeanManager beanManager;
    private Constants i18n = Constants.INSTANCE;

    @Inject
    public PerspectiveEditorReportingGroupProvider(SyncBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public String getName() {
        return i18n.drag_group_name_reporting();
    }

    @Override
    public LayoutDragComponentGroup getComponentGroup() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(getName());
        group.addLayoutDragComponent(i18n.drag_component_name_barchart(), lookupDisplayerComponent(BarChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_piechart(), lookupDisplayerComponent(PieChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_linechart(), lookupDisplayerComponent(LineChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_areachart(), lookupDisplayerComponent(AreaChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_bubblechart(), lookupDisplayerComponent(BubbleChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_meterchart(), lookupDisplayerComponent(MeterChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_mapchart(), lookupDisplayerComponent(MapChartDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_metric(), lookupDisplayerComponent(MetricDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_table(), lookupDisplayerComponent(TableDragComponent.class));
        group.addLayoutDragComponent(i18n.drag_component_name_filter(), lookupDisplayerComponent(SelectorDragComponent.class));
        return group;
    }

    private DisplayerDragComponent lookupDisplayerComponent(Class dragClass) {
        SyncBeanDef<DisplayerDragComponent> displayerBeanDef = beanManager.lookupBean(dragClass);
        return displayerBeanDef.newInstance();
    }
}
