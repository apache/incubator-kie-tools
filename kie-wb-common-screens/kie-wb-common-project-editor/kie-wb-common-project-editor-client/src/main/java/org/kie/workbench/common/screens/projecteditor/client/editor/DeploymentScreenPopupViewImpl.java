package org.kie.workbench.common.screens.projecteditor.client.editor;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class DeploymentScreenPopupViewImpl extends BaseModal {

    interface DeploymentScreenPopupWidgetBinder
            extends
            UiBinder<Widget, DeploymentScreenPopupViewImpl> {

    }

    private DeploymentScreenPopupWidgetBinder uiBinder = GWT.create( DeploymentScreenPopupWidgetBinder.class );

    @Inject
    private User identity;

    @UiField
    ControlGroup userNameTextGroup;

    @UiField
    TextBox userNameText;

    @UiField
    HelpInline userNameTextHelpInline;

    @UiField
    ControlGroup passwordTextGroup;

    @UiField
    PasswordTextBox passwordText;

    @UiField
    HelpInline passwordTextHelpInline;

    @UiField
    ControlGroup serverURLTextGroup;

    @UiField
    TextBox serverURLText;
    ;

    @UiField
    HelpInline serverURLTextHelpInline;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if ( isEmpty( userNameText.getText() ) ) {
                userNameTextGroup.setType( ControlGroupType.ERROR );
                userNameTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "Username" ) );

                return;
            }

            if ( isEmpty( passwordText.getText() ) ) {
                passwordTextGroup.setType( ControlGroupType.ERROR );
                passwordTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "Password" ) );

                return;
            }

            if ( isEmpty( serverURLText.getText() ) ) {
                serverURLTextGroup.setType( ControlGroupType.ERROR );
                serverURLTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "ServerURL" ) );

                return;
            }

            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            hide();
        }

        private boolean isEmpty( String value ) {
            if ( value == null || value.isEmpty() ) {
                return true;
            }

            return false;
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public DeploymentScreenPopupViewImpl() {
        setTitle( ProjectEditorResources.CONSTANTS.BuildAndDeploy() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );
    }

    public void configure( Command command ) {
        this.callbackCommand = command;

        // set default values for the fields
        userNameText.setText( identity.getIdentifier() );
        serverURLText.setText( GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" ) );
    }

    public String getUsername() {
        return this.userNameText.getText();
    }

    public String getPassword() {
        return this.passwordText.getText();
    }

    public String getServerURL() {
        return this.serverURLText.getText();
    }

}
