package org.drools.guvnor.client.editor;

import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@NameToken("TextEditor")
public class TextEditorActivity implements Activity {

    @Inject
    private IOCBeanManager manager;
    private TextEditorPresenter presenter;

    public TextEditorActivity() {
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
            presenter = manager.lookupBean(TextEditorPresenter.class).getInstance();        
            if(presenter instanceof ScreenService) {
                ((ScreenService) presenter).onStart();
            }
            //TODO: Get tab title (or an closable title bar widget).        
            acceptPanel.add("TextEditor", presenter.view);   
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onReveal();
        }  
    }

    /**
    * True - Close the place
    * False - Do not close the place
    */
    public boolean mayClosePlace() {
        if(presenter instanceof ScreenService) {
            return ((ScreenService) presenter).mayClose();
        } 
        
        return true;
    }
    
    public void closePlace() {
        if(presenter == null) {
            return; 
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onClose();
        }  
        presenter = null;
    }
    
    public String getNameToken() {
        return "TextEditor";
    }
}
