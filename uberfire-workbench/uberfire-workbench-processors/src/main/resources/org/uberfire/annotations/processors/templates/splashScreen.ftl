/*
 * Copyright 2012 JBoss Inc
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import com.google.gwt.user.client.ui.InlineLabel;

<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
<#if hasUberView>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.UberView;

</#if>
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.AbstractSplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import javax.inject.Named;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.client.mvp.IsSplashScreen;

import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
@Generated("org.uberfire.annotations.processors.WorkbenchSplashScreenProcessor")
@Named("${identifier}")
@IsSplashScreen
<#if beanActivatorClass??>
@ActivatedBy(${beanActivatorClass}.class)
</#if>
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractSplashScreenActivity {

    <#if rolesList??>
    private static final Collection<String> ROLES = Arrays.asList(${rolesList});
    <#else>
    private static final Collection<String> ROLES = Collections.emptyList();
    </#if>

    <#if securityTraitList??>
    private static final Collection<String> TRAITS = Arrays.asList(${securityTraitList});
    <#else>
    private static final Collection<String> TRAITS = Collections.emptyList();
    </#if>

    @Inject
    private ${realClassName} realPresenter;

    @Inject
    //Constructor injection for testing
    public ${className}( final PlaceManager placeManager, final SplashView view ) {
        super( placeManager, view );
    }

    <#if hasUberView>
    @PostConstruct
    public void init() {
        ((UberView) realPresenter.${getWidgetMethodName}()).init( realPresenter );
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
        realPresenter.${onCloseMethodName}();
        super.onClose();
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
    <#if getBodyHeightMethodName??>
    @Override
    public Integer getBodyHeight() {
        return realPresenter.${getBodyHeightMethodName}();
    }

    </#if>
    <#if getSplashFilterMethodName??>
    @Override
    public SplashScreenFilter getFilter() {
        return realPresenter.${getSplashFilterMethodName}();
    }

    </#if>
    <#if getInterceptMethodName??>
    @Override
    public Boolean intercept( final PlaceRequest intercepted ) {
        return realPresenter.${getInterceptMethodName}( intercepted );
    }

    </#if>
    @Override
    public boolean isEnabled() {
        <#if isEnabled>
        return true;
        <#else>
        return false;
        </#if>
    }

    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @Override
    public Collection<String> getTraits() {
        return TRAITS;
    }

    @Override
    public String getSignatureId() {
        return "${packageName}.${className}";
    }

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }

}
