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

package org.drools.guvnor.client.editors.fileexplorer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("File Explorer")
//TODO {manstis} This should not need to re-implement Activity but Errai doesn't detect it if it doesn't
public class FileExplorerActivity extends AbstractStaticScreenActivity implements Activity {

    @Inject
    private IOCBeanManager        manager;

    private FileExplorerPresenter presenter;

    public FileExplorerActivity() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = manager.lookupBean( FileExplorerPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "File Explorer";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

    @Override
    public Position getPreferredPosition() {
        return Position.WEST;
    }

}
