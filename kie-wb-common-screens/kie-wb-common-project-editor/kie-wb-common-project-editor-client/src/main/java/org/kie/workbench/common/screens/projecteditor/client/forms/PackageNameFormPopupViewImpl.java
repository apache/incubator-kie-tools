package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.Popup;
import org.uberfire.client.common.popups.errors.ErrorPopup;

public class PackageNameFormPopupViewImpl
        extends Popup
        implements PackageNameFormPopupView {

    private final Widget widget;
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

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    public PackageNameFormPopupViewImpl() {
        widget = uiBinder.createAndBindUi( this );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public Widget getContent() {
        return widget;
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

    @UiHandler("okButton")
    public void ok( ClickEvent clickEvent ) {
        presenter.onOk();
    }

    @Override
    public void showFieldEmptyWarning() {
        ErrorPopup.showMessage( CommonConstants.INSTANCE.PleaseSetAName() );
    }
}
