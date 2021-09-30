/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package ${packageName};

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.inject.Named;
import org.uberfire.workbench.model.PerspectiveDefinition;
<#if isTemplate>
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import java.util.HashMap;
import java.util.Map;
import org.uberfire.client.annotations.Perspective;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.uberfire.client.mvp.IsTemplatePerspective;
</#if>
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;

import org.uberfire.mvp.PlaceRequest;

<#if getMenuBarMethodName??>
import org.uberfire.workbench.model.menu.Menus;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.workbench.model.toolbar.ToolBar;

</#if>
<#if isTemplate>
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.TemplatedActivity;
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

</#if>
<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
<#if isDynamic>
import jsinterop.annotations.JsType;
import org.jboss.errai.ioc.client.api.Shared;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPerspectiveProcessor")
@Named("${identifier}")
<#if isTemplate>
@IsTemplatePerspective
</#if>
<#if beanActivatorClass??>
@ActivatedBy(${beanActivatorClass}.class)
</#if>
<#if isDynamic>
@JsType
</#if>
<#list qualifiers as qualifier>
${qualifier}
</#list>
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchPerspectiveActivity<#if isTemplate> implements TemplatedActivity</#if> {

<#if isTemplate>
    private static final String UF_PERSPECTIVE_COMPONENT = "uf-perspective-component";

</#if>
    @Inject
<#list qualifiers as qualifier>
    ${qualifier}
</#list>
    private ${realClassName} realPresenter;

    @Inject
    //Constructor injection for testing
    public ${className}(<#if isDynamic>@Shared </#if>final PlaceManager placeManager) {
        super( placeManager );
    }

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }

<#if isDefault>
    @Override
    public boolean isDefault() {
        return true;
    }

</#if>
<#if !isTransient>
    @Override
    public boolean isTransient() {
        return false;
    }

</#if>
<#if onStartup1ParameterMethodName??>
    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup( place );
        realPresenter.${onStartup1ParameterMethodName}( place );
    }

<#elseif onStartup0ParameterMethodName??>
    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup( place );
        realPresenter.${onStartup0ParameterMethodName}();
    }

</#if>
<#if onCloseMethodName??>
    @Override
    public void onClose() {
        super.onClose();
        realPresenter.${onCloseMethodName}();
    }

</#if>
<#if onShutdownMethodName??>
    @Override
    public void onShutdown() {
        super.onShutdown();
        realPresenter.${onShutdownMethodName}();
    }

</#if>
<#if onOpenMethodName??>
    @Override
    public void onOpen() {
        super.onOpen();
        realPresenter.${onOpenMethodName}();
    }

</#if>
<#if getPerspectiveMethodName??>
    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        return realPresenter.${getPerspectiveMethodName}();
    }

</#if>
<#if getMenuBarMethodName??>
    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        realPresenter.${getMenuBarMethodName}(menusConsumer);
    }

</#if>
<#if getToolBarMethodName??>
    @Override
    public ToolBar getToolBar() {
        return realPresenter.${getToolBarMethodName}();
    }    
</#if>
<#if isTemplate>
    @Override
    public HTMLElement getRootElement() {
        return realPresenter.getElement();
    }

    @Override
    public HTMLElement resolvePosition( NamedPosition position ) {
        final String fieldName = position.getName();
        <#if defaultPanel??>
        if ( fieldName.equals( "${defaultPanel.fieldName}" ) ) {
            DOMUtil.addCSSClass( realPresenter.${defaultPanel.fieldName}, UF_PERSPECTIVE_COMPONENT );
            return realPresenter.${defaultPanel.fieldName};
        }
        </#if>
        <#list wbPanels as wbPanel>
        if ( fieldName.equals( "${wbPanel.fieldName}" ) ) {
            DOMUtil.addCSSClass( realPresenter.${wbPanel.fieldName}, UF_PERSPECTIVE_COMPONENT );
            return realPresenter.${wbPanel.fieldName};
        }
        </#list>
        return null;
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( TemplatedWorkbenchPanelPresenter.class.getName() );
        p.setName( realPresenter.getClass().getName() );

        <#if defaultPanel??>
        PanelDefinition panelDefinition = new PanelDefinitionImpl( "${defaultPanel.panelType}" );
            <#list defaultPanel.wbParts as wbPart>
                <#if (wbPart.parameters?size > 0)>
        Map properties = new HashMap<String,String>();
                    <#list wbPart.parameters?keys as key>
        properties.put("${key}","${ wbPart.parameters[key]}");
                    </#list>
        panelDefinition.addPart(
            new PartDefinitionImpl(new DefaultPlaceRequest( "${wbPart.partName}", properties  ) ) );
                <#else>
        panelDefinition.addPart(
            new PartDefinitionImpl(new DefaultPlaceRequest( "${wbPart.partName}" ) ) );
                </#if>
            </#list>
        p.getRoot().appendChild( new NamedPosition( "${defaultPanel.fieldName}" ), panelDefinition );
        </#if>

        <#list wbPanels as wbPanel>
        PanelDefinition panelDefinition${wbPanel_index} = new PanelDefinitionImpl( "${wbPanel.panelType}" );
            <#list wbPanel.wbParts as wbPart>
                <#if (wbPart.parameters?size > 0)>
        Map properties${wbPanel_index} = new HashMap<String,String>();
                    <#list wbPart.parameters?keys as key>
        properties${wbPanel_index}.put( "${key}","${ wbPart.parameters[key]}" );
                    </#list>
        panelDefinition${wbPanel_index}.addPart(
                new PartDefinitionImpl( new DefaultPlaceRequest( "${wbPart.partName}", properties${wbPanel_index} ) ) );
                <#else>
        panelDefinition${wbPanel_index}.addPart(
                new PartDefinitionImpl( new DefaultPlaceRequest( "${wbPart.partName}" ) ) );
                </#if>
            </#list>
        p.getRoot().appendChild( new NamedPosition( "${wbPanel.fieldName}" ), panelDefinition${wbPanel_index} );
        </#list>
        return p;
    }
</#if>
<#if isDynamic>
    @Override
    public boolean isDynamic() {
    	return true;
    }
</#if>
}
