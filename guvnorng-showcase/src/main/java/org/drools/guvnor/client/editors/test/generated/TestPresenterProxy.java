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

package org.drools.guvnor.client.editors.test.generated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.editors.test.TestPresenter;
import org.drools.guvnor.client.mvp.StaticScreenService;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
//TODO {manstis} This class should be generated. See TestPlace
public class TestPresenterProxy
    implements
    StaticScreenService {

    @Inject
    private TestPresenter realPresenter;

    @Override
    public void onStart() {
        //This may do nothing if the real presenter does not have a @OnStart annotation
        realPresenter.onStart();
    }

    @Override
    public boolean mayClose() {
        //This may do nothing if the real presenter does not have a @MayClose annotation
        return realPresenter.mayClose();
    }

    @Override
    public void onClose() {
        //This may do nothing if the real presenter does not have a @OnClose annotation
        realPresenter.onClose();
    }

    @Override
    public void onReveal() {
        //This may do nothing if the real presenter does not have a @OnReveal annotation
        realPresenter.onReveal();
    }

    @Override
    public void onLostFocus() {
        //This may do nothing if the real presenter does not have a @OnLostFocus annotation
        realPresenter.onLostFocus();
    }

    @Override
    public void onFocus() {
        //This may do nothing if the real presenter does not have a @OnFocus annotation
        realPresenter.onFocus();
    }

    public String getTitle() {
        //This may do nothing if the real presenter does not have a @Title annotation
        return realPresenter.getTitle();
    }

    public IsWidget getView() {
        //This has to be implemented by the real presenter
        return realPresenter.getView();
    }

}