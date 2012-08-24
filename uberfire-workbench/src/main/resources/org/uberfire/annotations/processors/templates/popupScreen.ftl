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

import org.uberfire.client.annotations.Identifier;
import org.uberfire.client.mvp.AbstractPopupActivity;

import com.google.gwt.user.client.ui.PopupPanel;

@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPopupProcessor")
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractPopupActivity {

    @Inject
    private ${realClassName} realPresenter;

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
