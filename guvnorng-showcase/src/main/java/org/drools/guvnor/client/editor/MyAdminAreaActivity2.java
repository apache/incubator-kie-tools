package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.Closable;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
public class MyAdminAreaActivity2 extends Activity {

    @Inject private IOCBeanManager manager;
    private MyAdminAreaPresenter2 presenter;
    
    public MyAdminAreaActivity2() {
    }

    @Override
    public void start(AcceptItem tabbedPanel) {
        //TODO: Get tab title (or an closable title bar widget). 
        //MyAdminAreaPresenter presenter = new MyAdminAreaPresenter();
        presenter = manager.lookupBean(MyAdminAreaPresenter2.class).getInstance();
        //TODO: Provide a base class for Presenter. Implement a getView() method in the base class
        tabbedPanel.add("MyAdminArea2", presenter.view);
    }
    
    public void onStop() {
        if(presenter instanceof Closable) {
            ((Closable) presenter).onClose();
        }       
    }
    
    public boolean mayStop() {
        if(presenter instanceof Closable) {
            return ((Closable) presenter).mayClose();
        }  
        return true;
    }
    
    public String getNameToken() {
        return "MyAdminArea2";
    }
 }
