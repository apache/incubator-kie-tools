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

import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;

import org.uberfire.shared.mvp.PlaceRequest;

<#if getMenuBarMethodName??>
import org.uberfire.client.workbench.widgets.menu.MenuBar;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPerspectiveProcessor")
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchPerspectiveActivity {

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
    <#if getPerspectiveMethodName??>
    @Override
    public PerspectiveDefinition getPerspective() {
        return realPresenter.${getPerspectiveMethodName}();
    }

    </#if>
    <#if getMenuBarMethodName??>
    @Override
    public MenuBar getMenuBar() {
        return realPresenter.${getMenuBarMethodName}();
    }

    </#if>
    <#if getToolBarMethodName??>
    @Override
    public ToolBar getToolBar() {
        return realPresenter.${getToolBarMethodName}();
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
