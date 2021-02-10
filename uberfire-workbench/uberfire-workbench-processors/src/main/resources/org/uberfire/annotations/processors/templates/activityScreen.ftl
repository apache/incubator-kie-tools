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

<#if getDefaultPositionMethodName??>
import org.uberfire.workbench.model.Position;

</#if>
import org.uberfire.mvp.PlaceRequest;
<#if owningPlace??>
import org.uberfire.mvp.impl.DefaultPlaceRequest;
</#if>

import com.google.gwt.user.client.ui.IsWidget;

<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchScreenProcessor")
@Named("${identifier}")
<#if beanActivatorClass??>
@ActivatedBy(${beanActivatorClass}.class)
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

    <#if hasPresenterInitMethod>
    @PostConstruct
    public void init() {
        ((HasPresenter) realPresenter.${getWidgetMethodName}()).init( realPresenter );
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
    <#if onOpenMethodName??>

    @Override
    public void onOpen() {
        super.onOpen();
        realPresenter.${onOpenMethodName}();
    }
    </#if>
    <#if getTitleWidgetMethodName??>

    @Override
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
    @Override
    public IsWidget getWidget() {
        <#if isWidgetMethodReturnTypeElement>
        return ElementWrapperWidget.getWidget( realPresenter.${getWidgetMethodName}().getElement() );
        <#else>
        return realPresenter.${getWidgetMethodName}();
        </#if>
    }

    <#elseif isWidget>
    @Override
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

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }
}
