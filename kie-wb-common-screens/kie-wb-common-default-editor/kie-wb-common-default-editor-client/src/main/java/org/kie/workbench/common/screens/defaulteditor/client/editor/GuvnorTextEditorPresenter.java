package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.client.editors.texteditor.TextResourceType;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "GuvnorTextEditor", supportedTypes = {TextResourceType.class}, priority = -1)
public class GuvnorTextEditorPresenter
        extends TextEditorPresenter {

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<DefaultEditorService> defaultEditorService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    @Inject
    private MetadataWidget metadataWidget;

    private boolean isReadOnly;
    private Path path;


    @OnStartup
    public void onStartup(final Path path,
            final PlaceRequest place) {
        super.onStartup(path);

        this.path = path;
        this.isReadOnly = place.getParameter("readOnly", null) == null ? false : true;

        makeMenuBar();

        multiPage.addWidget(super.getWidget(),
                CommonConstants.INSTANCE.EditTabTitle());

        multiPage.addPage(new Page(metadataWidget,
                CommonConstants.INSTANCE.MetadataTabTitle()) {
            @Override
            public void onFocus() {
                metadataWidget.showBusyIndicator(CommonConstants.INSTANCE.Loading());
                metadataService.call(new MetadataSuccessCallback(metadataWidget,
                        isReadOnly),
                        new HasBusyIndicatorDefaultErrorCallback(metadataWidget)).getMetadata(path);
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        });
    }

    private void makeMenuBar() {
        if (isReadOnly) {
            menus = menuBuilder.addRestoreVersion(path).build();
        } else {
            menus = menuBuilder
                    .addSave(
                            new Command() {
                                @Override
                                public void execute() {
                                    onSave();
                                }
                            })
                    .addCopy(path)
                    .addRename(path)
                    .addDelete(path)
                    .build();
        }
    }

    @OnSave
    public void onSave() {
        new SaveOperationService().save(path,
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());
                        defaultEditorService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).save(path,
                                view.getContent(),
                                metadataWidget.getContent(),
                                commitMessage);
                    }
                });
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                busyIndicatorView.hideBusyIndicator();
                view.setDirty(false);
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @IsDirty
    public boolean isDirty() {
        return super.isDirty();
    }

    @OnClose
    public void onClose() {
        super.onClose();
    }

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

}