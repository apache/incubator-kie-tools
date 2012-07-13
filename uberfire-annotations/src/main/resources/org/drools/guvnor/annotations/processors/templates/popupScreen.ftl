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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.Identifier;
import org.drools.guvnor.client.mvp.AbstractPopupActivity;
import org.drools.guvnor.client.mvp.BaseService;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractPopupActivity
    implements
    BaseService {

    @Inject
    private ${realClassName} realPresenter;

    @Override
    public boolean onMayClose() {
        <#if onMayCloseMethodName??>
        return realPresenter.${onMayCloseMethodName}();
        <#else>
        return true;
        </#if>
    }

    @Override
    public void onClose() {
        <#if onCloseMethodName??>
        realPresenter.${onCloseMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnClose annotation
        </#if>
    }

    @Override
    public void onReveal() {
        <#if onRevealMethodName??>
        realPresenter.${onRevealMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnReveal annotation
        </#if>
    }

    @Override
    public String getTitle() {
        <#if getTitleMethodName??>
        return realPresenter.${getTitleMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @Title annotation
        return null;
        </#if>
    }

    @Override
    public IsWidget getWidget() {
        <#if getWidgetMethodName??>
        return realPresenter.${getWidgetMethodName}();
        <#elseif isWidget>
        return realPresenter;
        <#else>
        return null;
        </#if>
    }
    
    @Override
    public BaseService getPresenter() {
        return this;
    }

}
