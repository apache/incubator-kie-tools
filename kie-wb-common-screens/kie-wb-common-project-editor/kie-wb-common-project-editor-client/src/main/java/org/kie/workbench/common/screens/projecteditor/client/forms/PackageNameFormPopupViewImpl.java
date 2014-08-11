package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.popups.KieBaseModal;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.kie.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class PackageNameFormPopupViewImpl
        extends KieBaseModal
        implements PackageNameFormPopupView {

    private Presenter presenter;

    interface PackageNameFormPopupViewImplBinder
            extends
            UiBinder<Widget, PackageNameFormPopupViewImpl> {

    }

    private static PackageNameFormPopupViewImplBinder uiBinder = GWT.create( PackageNameFormPopupViewImplBinder.class );

    @UiField
    TextBox selectedNameTextBox;

    @UiField
    Dropdown nameDropDown;

    public PackageNameFormPopupViewImpl() {
        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.onOk();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return selectedNameTextBox.getText();
    }

    @Override
    public void addItem( final String packageName ) {
        NavLink navLink = new NavLink( packageName );
        navLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                selectedNameTextBox.setText( packageName );
            }
        } );
        nameDropDown.add( navLink );
    }

    @Override
    public void setName( String name ) {
        selectedNameTextBox.setText( name );
    }

    @Override
    public void showFieldEmptyWarning() {
        ErrorPopup.showMessage( CommonConstants.INSTANCE.PleaseSetAName() );
    }
}
