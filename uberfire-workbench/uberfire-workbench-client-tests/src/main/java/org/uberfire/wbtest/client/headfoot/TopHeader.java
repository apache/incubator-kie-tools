package org.uberfire.wbtest.client.headfoot;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Model;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Header that should appear at the top of all tests (except the ones that use {@link HeaderFooterActivator} to disable
 * headers!)
 * <p>
 * Includes a little UI for creating children of the root panel, which comes in handy in a variety of UI tests.
 */
@ApplicationScoped
@Templated
@ActivatedBy( HeaderFooterActivator.class )
public class TopHeader extends Composite implements Header {

    @Inject @Model NewPanelBuilder newPanelBuilder;

    @Inject @Bound @DataField("newPanelPartPlace") TextBox partPlace;
    @Inject @Bound @DataField("newPanelType") TextBox type;
    @Inject @Bound @DataField("newPanelPosition") TextBox position;

    @Inject @DataField Button newPanelButton;

    @Inject PlaceManager placeManager;
    @Inject PanelManager panelManager;

    @PostConstruct
    private void setupNewPanelDefaults() {
        // XXX if we set either the model or widget values in the PostConstruct directly, the values
        // don't get synced when the bean is created (the data bindings are set up after the postconstruct
        // method is invoked)
        Scheduler.get().scheduleFinally( new ScheduledCommand() {
            @Override
            public void execute() {
                newPanelBuilder.setPartPlace( ResizeTestScreenActivity.class.getName() );
                newPanelBuilder.setType( SimpleDnDWorkbenchPanelPresenter.class.getName() );
                newPanelBuilder.setPosition( CompassPosition.WEST.name() );
            }
        } );
    }

    @EventHandler( "newPanelButton" )
    private void newPanelButtonClicked( ClickEvent e ) {
        newPanelBuilder.makePanel( placeManager, panelManager );
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
