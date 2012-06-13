package org.drools.guvnor.client.editors.admin1;

import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@NameToken("MyAdminArea")
public class MyAdminAreaActivity implements Activity {

    @Inject
    private IOCBeanManager manager;
    private MyAdminAreaPresenter presenter;

    public MyAdminAreaActivity() {
    }

    @Override
    public void start() {
    }

    @Override
    public Position getPreferredPosition() {
        return Position.SELF;
    }
    
    public void onStop() {
        if(presenter !=null && presenter instanceof ScreenService) {
            ((ScreenService) presenter).onClose();
        }       
    }
    
    public boolean mayStop() {
        if(presenter !=null && presenter instanceof ScreenService) {
            return ((ScreenService) presenter).mayClose();
        }  
        return true;
    }
    
    public void revealPlace(AcceptItem acceptPanel) {
        if(presenter == null) {
            presenter = manager.lookupBean(MyAdminAreaPresenter.class).getInstance();        
            if(presenter instanceof ScreenService) {
                ((ScreenService) presenter).onStart();
            }
            //TODO: Get tab title (or an closable title bar widget).        
            acceptPanel.add("MyAdminArea", presenter.view);   
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onReveal();
        }  
    }

    /**
    * True - Close the place
    * False - Do not close the place
    */
    @Override
    public boolean mayClosePlace() {
        if(presenter instanceof ScreenService) {
            return ((ScreenService) presenter).mayClose();
        } 
        
        return true;
    }
    
    @Override
    public void closePlace() {
        if(presenter == null) {
            return; 
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onClose();
        }  
        presenter = null;
    }

    @Override
    public void hide() {
        //TODO: -Rikkola-
    }

    @Override
    public void show() {
        //TODO: -Rikkola-
    }
}
