package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.Closable;
import org.drools.guvnor.client.mvp.ScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
public class MyAdminAreaActivity2 implements Activity {

    @Inject private IOCBeanManager manager;
    private MyAdminAreaPresenter2 presenter;
    
    public MyAdminAreaActivity2() {
    }

    @Override
    public void start(AcceptItem tabbedPanel) {
    }
    
    public void onStop() {
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onClose();
        }       
    }
    
    public boolean mayStop() {
        if(presenter instanceof ScreenService) {
            return ((ScreenService) presenter).mayClose();
        }  
        return true;
    }
    
    public void onRevealPresenter(AcceptItem acceptPanel) {
        if(presenter == null) {
            presenter = manager.lookupBean(MyAdminAreaPresenter2.class).getInstance();        
            if(presenter instanceof ScreenService) {
                ((ScreenService) presenter).onStart();
            }
            //TODO: Get tab title (or an closable title bar widget).        
            acceptPanel.add("MyAdminArea2", presenter.view);   
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onReveal();
        }  
    }
    
    public void onClosePresenter() {
        if(presenter == null) {
            return; 
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onClose();
        }  
        presenter = null;
    }
    
    public String getNameToken() {
        return "MyAdminArea2";
    }
 }
