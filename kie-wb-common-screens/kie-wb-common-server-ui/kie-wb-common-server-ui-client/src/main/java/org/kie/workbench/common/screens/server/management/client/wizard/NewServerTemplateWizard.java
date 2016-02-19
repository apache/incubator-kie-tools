package org.kie.workbench.common.screens.server.management.client.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.client.wizard.config.process.ProcessConfigPagePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.template.NewTemplatePresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class NewServerTemplateWizard extends AbstractMultiPageWizard {

    private final NewTemplatePresenter newTemplatePresenter;
    private final NewContainerFormPresenter newContainerFormPresenter;
    private final ProcessConfigPagePresenter processConfigPagePresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;
    private final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    @Inject
    public NewServerTemplateWizard( final NewTemplatePresenter newTemplatePresenter,
                                    final NewContainerFormPresenter newContainerFormPresenter,
                                    final ProcessConfigPagePresenter processConfigPagePresenter,
                                    final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent,
                                    final Caller<SpecManagementService> specManagementService,
                                    final Event<NotificationEvent> notification,
                                    final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent,
                                    final Event<ServerTemplateSelected> serverTemplateSelectedEvent ) {
        this.newTemplatePresenter = newTemplatePresenter;
        this.newContainerFormPresenter = newContainerFormPresenter;
        this.processConfigPagePresenter = processConfigPagePresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
        this.serverTemplateListRefreshEvent = serverTemplateListRefreshEvent;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;

        final ContentChangeHandler changePages = new ContentChangeHandler() {
            @Override
            public void onContentChange() {
                final int currentSelectedPage = getSelectedPage();
                if ( newTemplatePresenter.hasProcessCapability() &&
                        !newContainerFormPresenter.isEmpty() ) {
                    if ( pages.size() != 3 ) {
                        pages.clear();
                        pages.add( NewServerTemplateWizard.this.newTemplatePresenter );
                        pages.add( NewServerTemplateWizard.this.newContainerFormPresenter );
                        pages.add( NewServerTemplateWizard.this.processConfigPagePresenter );
                        view.setPageTitles( getPages() );
                        checkPagesState();
                        view.selectPage( currentSelectedPage );
                    }
                } else if ( pages.size() != 2 ) {
                    pages.clear();
                    pages.add( NewServerTemplateWizard.this.newTemplatePresenter );
                    pages.add( NewServerTemplateWizard.this.newContainerFormPresenter );
                    view.setPageTitles( getPages() );
                    checkPagesState();
                    view.selectPage( currentSelectedPage > 1 ? 1 : currentSelectedPage );
                }
            }
        };

        this.newTemplatePresenter.addContentChangeHandler( changePages );
        this.newContainerFormPresenter.addContentChangeHandler( changePages );

        pages.add( this.newTemplatePresenter );
        pages.add( this.newContainerFormPresenter );
    }

    @Override
    public String getTitle() {
        return newTemplatePresenter.getView().getNewServerTemplateWizardTitle();
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    public void clear() {
        newTemplatePresenter.clear();
        newContainerFormPresenter.clear();
        processConfigPagePresenter.clear();
        pages.clear();
        pages.add( newTemplatePresenter );
        pages.add( newContainerFormPresenter );
    }

    @Override
    public void close() {
        super.close();
        clear();
    }

    @Override
    public void complete() {
        final ServerTemplate newServerTemplate = buildServerTemplate();
        specManagementService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                notification.fire( new NotificationEvent( newTemplatePresenter.getView().getNewServerTemplateWizardSaveSuccess(), NotificationEvent.NotificationType.SUCCESS ) );
                clear();
                NewServerTemplateWizard.super.complete();
                serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh( newServerTemplate.getId() ) );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( newTemplatePresenter.getView().getNewServerTemplateWizardSaveError(), NotificationEvent.NotificationType.ERROR ) );
                NewServerTemplateWizard.this.pageSelected( 0 );
                NewServerTemplateWizard.this.start();
                return false;
            }
        } ).saveServerTemplate( newServerTemplate );
    }

    private ServerTemplate buildServerTemplate() {
        final Collection<String> capabilities = new ArrayList<String>();
        final Map<Capability, ServerConfig> capabilityConfig = new HashMap<Capability, ServerConfig>();
        final Map<Capability, ContainerConfig> capabilityContainerConfig = new HashMap<Capability, ContainerConfig>();
        final Collection<ContainerSpec> containersSpec = new ArrayList<ContainerSpec>();

        if ( newTemplatePresenter.isProcessCapabilityChecked() ) {
            capabilities.add( Capability.PROCESS.toString() );
            if ( !newContainerFormPresenter.isEmpty() ) {
                capabilityContainerConfig.put( Capability.PROCESS, processConfigPagePresenter.buildProcessConfig() );
            }
        }
        capabilityContainerConfig.put( Capability.RULE, new RuleConfig( null, KieScannerStatus.STOPPED ) );

        if ( newTemplatePresenter.isRuleCapabilityChecked() ) {
            capabilities.add( Capability.RULE.toString() );
        }
        if ( newTemplatePresenter.isPlanningCapabilityChecked() ) {
            capabilities.add( Capability.PLANNING.toString() );
        }

        if ( !newContainerFormPresenter.isEmpty() ) {
            containersSpec.add( newContainerFormPresenter.buildContainerSpec( newTemplatePresenter.getTemplateName(), capabilityContainerConfig ) );
        }

        return new ServerTemplate( newTemplatePresenter.getTemplateName(),
                                       newTemplatePresenter.getTemplateName(),
                                       capabilities,
                                       capabilityConfig,
                                       containersSpec );
    }
}
