package org.uberfire.client.workbench.panels.impl;

/**
 * Minor behavioural alterations to 
 */
public class SimpleWorkbenchPanelViewUnitTestWrapper extends SimpleWorkbenchPanelView {

    private boolean forcedAttachState;

    @Override
    public boolean isAttached() {
        return forcedAttachState;
    }
    
    public void forceAttachedState( boolean attached ) {
        forcedAttachState = attached;
    }
}
