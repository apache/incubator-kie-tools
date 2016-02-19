package org.kie.workbench.common.screens.server.management.client.wizard.template;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class NewTemplatePresenter implements WizardPage {

    public boolean hasProcessCapability() {
        return view.getProcessCapabilityCheck();
    }

    public String getTemplateName() {
        return view.getTemplateName();
    }

    public interface View extends UberView<NewTemplatePresenter> {

        String getTitle();

        void clear();

        void addContentChangeHandler( final ContentChangeHandler contentChangeHandler );

        boolean getProcessCapabilityCheck();

        String getTemplateName();

        boolean isRuleCapabilityChecked();

        boolean isProcessCapabilityChecked();

        boolean isPlanningCapabilityChecked();

        void errorOnTemplateName();

        void errorOnTemplateName( String s );

        void noErrorOnTemplateName();

        void errorCapability();

        void noErrorOnCapability();

        void noErrors();

        String getInvalidErrorMessage();

        String getNewServerTemplateWizardTitle();

        String getNewServerTemplateWizardSaveSuccess();

        String getNewServerTemplateWizardSaveError();
    }

    private final View view;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Inject
    public NewTemplatePresenter( final View view,
                                 final Caller<SpecManagementService> specManagementService,
                                 final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent ) {
        this.view = view;
        this.specManagementService = specManagementService;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public String getTitle() {
        return view.getTitle();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        if ( isValid() ) {
            specManagementService.call( new RemoteCallback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( result.equals( Boolean.FALSE ) ) {
                        view.errorOnTemplateName( view.getInvalidErrorMessage() );
                        callback.callback( false );
                    } else {
                        callback.callback( true );
                    }
                }
            } ).isNewServerTemplateIdValid( view.getTemplateName() );
        } else {
            callback.callback( false );
        }
    }

    public void addContentChangeHandler( final ContentChangeHandler contentChangeHandler ) {
        checkNotNull( "contentChangeHandler", contentChangeHandler );
        view.addContentChangeHandler( new ContentChangeHandler() {
            @Override
            public void onContentChange() {
                contentChangeHandler.onContentChange();
                wizardPageStatusChangeEvent.fire( new WizardPageStatusChangeEvent( NewTemplatePresenter.this ) );
            }
        } );
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isValid() {
        boolean hasError = false;
        if ( isTemplateNameValid() ) {
            view.noErrorOnTemplateName();
        } else {
            view.errorOnTemplateName();
            hasError = true;
        }

        if ( isCapabilityValid() ) {
            view.noErrorOnCapability();
        } else {
            view.errorCapability();
            hasError = true;
        }

        return !hasError;
    }

    public boolean isTemplateNameValid() {
        final String templateName = view.getTemplateName();
        return templateName == null ? false : !templateName.trim().isEmpty();
    }

    public boolean isCapabilityValid() {
        if ( view.isPlanningCapabilityChecked() && view.isRuleCapabilityChecked() ) {
            return true;
        }

        if ( view.isProcessCapabilityChecked() || view.isRuleCapabilityChecked() ) {
            return true;
        }

        return false;
    }

    public void clear() {
        view.clear();
    }

    public boolean isRuleCapabilityChecked() {
        return view.isRuleCapabilityChecked();
    }

    public boolean isProcessCapabilityChecked() {
        return view.isProcessCapabilityChecked();
    }

    public boolean isPlanningCapabilityChecked() {
        return view.isPlanningCapabilityChecked();
    }

    public View getView() {
        return this.view;
    }
}
