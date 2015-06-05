package org.uberfire.client.views.pfly.modal;

import javax.enterprise.context.Dependent;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.Attributes;
import org.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A modal dialog that floats above the workbench. Each instance can only be shown once.
 */
@Dependent
public class Bs3Modal extends Modal {

    private final ModalBody body = new ModalBody();

    private final ModalFooter footer = new ModalFooter();

    /**
     * Used for enforcing the "only show one time" rule.
     */
    boolean hasBeenShown;

    public Bs3Modal() {
        this.add( body );
        this.add( footer );

        this.setDataBackdrop( ModalBackdrop.STATIC );
        this.setFade( true );
        this.getElement().setAttribute( Attributes.ROLE, "dialog" );
        this.getElement().setAttribute( Attributes.TABINDEX, "-1" );
        this.addStyleName( WorkbenchResources.INSTANCE.CSS().modal() );

        final Button close = new Button( "OK" );
        close.setDataDismiss( ButtonDismiss.MODAL );
        close.addStyleName( "btn-primary" );
        footer.add( close );
    }

    /**
     * Shows this modal dialog above the current workbench.
     * @param afterShown the action to perform once the dialog has been shown. Not null. Use {@link Commands#DO_NOTHING} if you don't have an "after show" action.
     * @param afterClosed the action to perform once the dialog has been dismissed. Not null. Use {@link Commands#DO_NOTHING} if you don't have an "after close" action.
     */
    public void show( final Command afterShown,
                      final Command afterClosed ) {

        checkNotNull( "afterShown", afterShown );
        checkNotNull( "afterClosed", afterClosed );
        this.addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( final ModalShownEvent showEvent ) {
                if ( afterShown != null ) {
                    afterShown.execute();
                }
            }
        } );
        this.addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( final ModalHiddenEvent hiddenEvent ) {
                if ( afterClosed != null ) {
                    afterClosed.execute();
                }
            }
        } );
        this.show();
    }

    @Override
    public void show() {
        if ( hasBeenShown ) {
            throw new IllegalStateException( "This modal has already been shown. Create a new instance if you want to show another modal." );
        }
        super.show();
    }

    /**
     * Replaces the contents within the main body area of the modal. By default, the main body area is empty.
     * @param content the new content for the main body area.
     */
    public void setContent( IsWidget content ) {
        body.clear();
        body.add( content );
    }

    public void setModalTitle( final String title ) {
        this.setTitle( SafeHtmlUtils.htmlEscape( title ) );
    }

    /**
     * Replaces the current contents of the footer area with the given widget. By default (if you do not call this
     * method), the footer contains an OK button that dismisses the dialog when clicked.
     * @param content the new content for the footer area.
     */
    public void setFooterContent( IsWidget content ) {
        footer.clear();
        footer.add( content );
    }

    /**
     * Sets the pixel height of the main content container.
     */
    public void setBodyHeight( int height ) {
        body.setHeight( height + "px" );
    }

}
