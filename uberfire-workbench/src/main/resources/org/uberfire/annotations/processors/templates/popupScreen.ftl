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

import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.PopupPanel;

@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPopupProcessor")
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractPopupActivity {

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

    <#if onStart1ParameterMethodName??>
    @Override
    public void onStart(final PlaceRequest place) {
        realPresenter.${onStart1ParameterMethodName}( place );
    }

    <#elseif onStart0ParameterMethodName??>
    @Override
    public void onStart(final PlaceRequest place) {
        realPresenter.${onStart0ParameterMethodName}();
    }

    </#if>
    <#if onRevealMethodName??>
    @Override
    public void onReveal() {
        realPresenter.${onRevealMethodName}();
    }

    </#if>
    @Override
    public PopupPanel getPopupPanel() {
        <#if getPopupMethodName??>
        return realPresenter.${getPopupMethodName}();
        <#elseif isPopup>
        return realPresenter;
        <#else>
        return null;
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
}
