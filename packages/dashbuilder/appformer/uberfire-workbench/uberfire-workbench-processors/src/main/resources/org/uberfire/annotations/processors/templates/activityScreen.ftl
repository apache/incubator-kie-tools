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
import com.google.gwt.user.client.ui.InlineLabel;

<#if hasPresenterInitMethod>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.HasPresenter;

</#if>
<#if needsElementWrapper>
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
</#if>
import javax.inject.Named;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;

<#if getDefaultPositionMethodName??>
import org.uberfire.workbench.model.Position;

</#if>
import org.uberfire.mvp.PlaceRequest;
<#if owningPlace??>
import org.uberfire.mvp.impl.DefaultPlaceRequest;
</#if>

<#if getMenuBarMethodName??>
import org.uberfire.workbench.model.menu.Menus;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.workbench.model.toolbar.ToolBar;

</#if>
import com.google.gwt.user.client.ui.IsWidget;

<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
<#if isDynamic>
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.jboss.errai.ioc.client.api.Shared;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchScreenProcessor")
@Named("${identifier}")
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
public class ${className} extends AbstractWorkbenchScreenActivity {

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
    <#if hasPresenterInitMethod>

    @PostConstruct
    public void init() {
        ((HasPresenter) realPresenter.${getWidgetMethodName}()).init( realPresenter );
    }
    </#if>
    <#if preferredHeight??>

    @Override
    public int preferredHeight() {
       return ${preferredHeight};
    }
    </#if>
    <#if preferredWidth??>

    @Override
    public int preferredWidth() {
       return ${preferredWidth};
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
    <#if onMayCloseMethodName??>

    @Override
    public boolean onMayClose() {
        return realPresenter.${onMayCloseMethodName}();
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
    <#if onLostFocusMethodName??>

    @Override
    public void onLostFocus() {
        super.onLostFocus();
        realPresenter.${onLostFocusMethodName}();
    }
    </#if>
    <#if onFocusMethodName??>

    @Override
    public void onFocus() {
        super.onFocus();
        realPresenter.${onFocusMethodName}();
    }
    </#if>
    <#if owningPlace??>

    @Override
    public PlaceRequest getOwningPlace() {
        return new DefaultPlaceRequest("${owningPlace}");
    }
    </#if>
    <#if getTitleWidgetMethodName??>

    <#if isDynamic>@JsIgnore </#if>@Override
    public IsWidget getTitleDecoration() {
        <#if isTitleWidgetMethodReturnTypeElement>
        return ElementWrapperWidget.getWidget( realPresenter.${getTitleWidgetMethodName}().getElement() );
        <#else>
        return realPresenter.${getTitleWidgetMethodName}();
        </#if>
    }
    </#if>
    <#if getTitleMethodName??>

    @Override
    public String getTitle() {
        return realPresenter.${getTitleMethodName}();
    }

    </#if>
    <#if getWidgetMethodName??>
    <#if isDynamic>@JsIgnore </#if>@Override
    public IsWidget getWidget() {
        <#if isWidgetMethodReturnTypeElement>
        return ElementWrapperWidget.getWidget( realPresenter.${getWidgetMethodName}().getElement() );
        <#else>
        return realPresenter.${getWidgetMethodName}();
        </#if>
    }

    <#elseif isWidget>
    <#if isDynamic>@JsIgnore </#if>@Override
    public IsWidget getWidget() {
        return realPresenter;
    }

    </#if>
    <#if getDefaultPositionMethodName??>

    @Override
    public Position getDefaultPosition() {
        return realPresenter.${getDefaultPositionMethodName}();
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
    <#if getContextIdMethodName??>

    @Override
    public String contextId() {
        return realPresenter.${getContextIdMethodName}();
    }
    </#if>
    @Override
    public String getIdentifier() {
        return "${identifier}";
    }
    <#if isDynamic>

    @Override
    public boolean isDynamic() {
    	return true;
    }
    </#if>
}
