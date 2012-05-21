package org.drools.guvnor.client.mvp;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
public class MyAdminAreaActivity extends Activity {

    @Inject private IOCBeanManager manager;
    private MyAdminAreaPresenter presenter;
    
    public MyAdminAreaActivity() {
    }

    @Override
    public void start(AcceptItem tabbedPanel) {
        //TODO: Get tab title (or an closable title bar widget). 
        //MyAdminAreaPresenter presenter = new MyAdminAreaPresenter();
        presenter = manager.lookupBean(MyAdminAreaPresenter.class).getInstance();
        
        if(presenter instanceof Startable) {
            ((Startable) presenter).onStart();
        }
        //TODO: Provide a base class for Presenter. Implement a getView() method in the base class
        tabbedPanel.add("MyAdminArea", presenter.view);
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
        return "MyAdminArea";
    }
 }
