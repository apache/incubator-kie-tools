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

import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
<#if hasUberView>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.UberView;
</#if>

import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.mvp.AbstractScreenActivity;
<#if getDefaultPositionMethodName??>
import org.uberfire.client.workbench.Position;
</#if>
<#if getMenuBarMethodName??>
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
</#if>

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchScreenProcessor")
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractScreenActivity {

    @Inject
    private ${realClassName} realPresenter;

    <#if hasUberView>
    @PostConstruct
    public void init() {
        ((UberView) realPresenter.${getWidgetMethodName}()).init( realPresenter );
    }

    </#if>
    <#if onStartMethodName??>
    @Override
    public void onStart() {
        realPresenter.${onStartMethodName}();
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
        realPresenter.${onCloseMethodName}();
    }

    </#if>
    <#if onRevealMethodName??>
    @Override
    public void onReveal() {
        realPresenter.${onRevealMethodName}();
    }

    </#if>
    <#if onLostFocusMethodName??>
    @Override
    public void onLostFocus() {
        realPresenter.${onLostFocusMethodName}();
    }

    </#if>
    <#if onFocusMethodName??>
    @Override
    public void onFocus() {
        realPresenter.${onFocusMethodName}();
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
    public WorkbenchMenuBar getMenuBar() {
        return realPresenter.${getMenuBarMethodName}();
    }
    
    </#if>
    <#if rolesList??>
    @Override
    public String[] getRoles() {
        return new String[]{${rolesList}};
    }
    </#if>
    <#if securityTraitList??>
    @Override
    public String[] getTraitTypes() {
        return new String[]{${securityTraitList}};
    }
    </#if>
}
