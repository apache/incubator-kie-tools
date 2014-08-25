package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.uberfire.client.editors.texteditor.TextResourceType;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public class KieTextEditorPresenter
        extends KieEditor {

    protected KieTextEditorView view;

    @Inject
    private Caller<DefaultEditorService> defaultEditorService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    private PlaceManager placeManager;

    private Metadata metadata;

    @Inject
    public KieTextEditorPresenter(KieTextEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {
        super.init(path, place, new TextResourceType());

        view.onStartup(path);

        if (isReadOnly) {
            view.makeReadOnly();
        }
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(
                        new Command() {
                            @Override
                            public void execute() {
                                onSave();
                            }
                        })
                .addCopy(versionRecordManager.getCurrentPath(),
                        fileNameValidator)
                .addRename(versionRecordManager.getCurrentPath(),
                        fileNameValidator)
                .addDelete(versionRecordManager.getCurrentPath())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }

    @Override
    protected Command onValidate() {
        // not used
        return null;
    }

    @Override
    protected void loadContent() {
        defaultEditorService.call(new RemoteCallback<Overview>() {
            @Override
            public void callback(Overview overview) {

                resetEditorPages(overview);

                metadata = overview.getMetadata();

            }
        }).loadOverview(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void save() {
        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());
        new SaveOperationService().save(versionRecordManager.getCurrentPath(),
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        defaultEditorService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).save(versionRecordManager.getCurrentPath(),
                                view.getContent(),
                                metadata,
                                commitMessage);

                    }
                }
        );

    }

    @Override
    protected void onOverviewSelected() {
        updatePreview(view.getContent());
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    public IsWidget getWidget() {
        return super.getWidget();
    }

}