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
import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import com.google.gwt.user.client.ui.InlineLabel;

<#if hasUberView>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.HasPresenter;

</#if>
<#if size?has_content>
import org.uberfire.client.annotations.WorkbenchPopup.WorkbenchPopupSize;
import static org.uberfire.client.annotations.WorkbenchPopup.WorkbenchPopupSize.*;
</#if>
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import javax.inject.Named;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;

<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPopupProcessor")
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
public class ${className} extends AbstractPopupActivity {

    @Inject
<#list qualifiers as qualifier>
    ${qualifier}
</#list>
    private ${realClassName} realPresenter;

    @Inject
    //Constructor injection for testing
    public ${className}( final PlaceManager placeManager, final PopupView view ) {
        super( placeManager, view );
    }

    <#if hasUberView>
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
    <#if getTitleWidgetMethodName??>
    @Override
    public IsWidget getTitleDecoration() {
        return realPresenter.${getTitleWidgetMethodName}();
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
        return realPresenter.${getWidgetMethodName}();
    }

    <#elseif isWidget>
    @Override
    public IsWidget getWidget() {
        return realPresenter;
    }

    </#if>
    @Override
    public String getIdentifier() {
        return "${identifier}";
    }
    <#if size?has_content>

    @Override
    public WorkbenchPopupSize getSize() {
        return ${size};
    }
    </#if>
}
