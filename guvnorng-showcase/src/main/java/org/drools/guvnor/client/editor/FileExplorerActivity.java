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

package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
@NameToken("File Explorer")
public class FileExplorerActivity implements Activity {

    private FileExplorerPresenter presenter;
    @Inject
    private IOCBeanManager manager;
    
    public FileExplorerActivity() {
    }

    @Override
    public void start() {
    }
    
    @Override
    public void onStop() {
        if ( presenter != null && presenter instanceof ScreenService ) {
            ((ScreenService) presenter).onClose();
        }
    }
    
    @Override
    public boolean mayStop() {
        if ( presenter != null && presenter instanceof ScreenService ) {
            return ((ScreenService) presenter).mayClose();
        }
        return true;
    }

    @Override
    public Position getPreferredPosition() {
        return Position.WEST;
    }
    
    @Override
    public void revealPlace(AcceptItem acceptPanel) {
        if(presenter == null) {
            presenter = manager.lookupBean(FileExplorerPresenter.class).getInstance();        
            if(presenter instanceof ScreenService) {
                ((ScreenService) presenter).onStart();
            }
            //TODO: Get tab title (or an closable title bar widget).        
            acceptPanel.add("File Explorer", presenter.view);   
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onReveal();
        }  
    }
    
    /**
     * True - Close the place False - Do not close the place
     */
    @Override
    public boolean mayClosePlace() {
        if ( presenter instanceof ScreenService ) {
            return ((ScreenService) presenter).mayClose();
        }

        return true;
    }
    
    @Override
    public void closePlace() {
        if ( presenter == null ) {
            return;
        }

        if ( presenter instanceof ScreenService ) {
            ((ScreenService) presenter).onClose();
        }
        presenter = null;
    }
}
