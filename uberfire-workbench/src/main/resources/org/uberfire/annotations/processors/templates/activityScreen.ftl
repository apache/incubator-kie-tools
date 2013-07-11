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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import com.google.gwt.user.client.ui.InlineLabel;

<#if hasUberView>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.UberView;

</#if>
import javax.inject.Named;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;

<#if getDefaultPositionMethodName??>
import org.uberfire.workbench.model.Position;

</#if>
import org.uberfire.mvp.PlaceRequest;

<#if getMenuBarMethodName??>
import org.uberfire.workbench.model.menu.Menus;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.workbench.model.toolbar.ToolBar;

</#if>
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchScreenProcessor")
@Named("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchScreenActivity {

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
    public ${className}(final PlaceManager placeManager) {
        super( placeManager );
    }

    <#if hasUberView>
    @PostConstruct
    public void init() {
        ((UberView) realPresenter.${getWidgetMethodName}()).init( realPresenter );
    }

    </#if>
    <#if onStart1ParameterMethodName??>
    @Override
    public void onStart(final PlaceRequest place) {
        super.onStart( place );
        realPresenter.${onStart1ParameterMethodName}( place );
    }

    <#elseif onStart0ParameterMethodName??>
    @Override
    public void onStart(final PlaceRequest place) {
        super.onStart();
        realPresenter.${onStart0ParameterMethodName}();
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
    <#if onRevealMethodName??>
    @Override
    public void onReveal() {
        super.onReveal();
        realPresenter.${onRevealMethodName}();
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
    <#if getDefaultPositionMethodName??>
    @Override
    public Position getDefaultPosition() {
        return realPresenter.${getDefaultPositionMethodName}();
    }
    
    </#if>
    <#if getMenuBarMethodName??>
    @Override
    public Menus getMenus() {
        return realPresenter.${getMenuBarMethodName}();
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
}
