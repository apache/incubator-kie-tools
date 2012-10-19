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
import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.workbench.annotations.ResourceType;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.PlaceManager;

<#if getDefaultPositionMethodName??>
import org.uberfire.client.workbench.Position;

</#if>
import org.uberfire.shared.mvp.PlaceRequest;

<#if getMenuBarMethodName??>
import org.uberfire.client.workbench.widgets.menu.MenuBar;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;

</#if>
import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchEditorProcessor")
@ResourceType(${fileTypes})
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchEditorActivity {

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
    <#if onStart2ParametersMethodName??>
    @Override
    public void onStart(final Path path, 
                        final PlaceRequest place) {
        super.onStart( path, place );
        realPresenter.${onStart2ParametersMethodName}( path, place );
    }

    <#elseif onStart1ParameterMethodName??>
    @Override
    public void onStart(final Path path,
                        final PlaceRequest place) {
        super.onStart( path );
        realPresenter.${onStart1ParameterMethodName}( path );
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
    <#if getTabWidgetMethodName??>
    @Override
    public IsWidget getTabWidget() {
        return realPresenter.${getTabWidgetMethodName}();
    }

    <#elseif getTabTitleMethodName??>
    @Override
    public IsWidget getTabWidget() {
        return new InlineLabel(realPresenter.${getTabTitleMethodName}());
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
    <#if isDirtyMethodName??>
    @Override
    public boolean isDirty() {
        return realPresenter.${isDirtyMethodName}();
    }
    
    </#if>
    <#if onSaveMethodName??>
    @Override
    public void onSave() {
        super.onSave();
        realPresenter.${onSaveMethodName}();
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
