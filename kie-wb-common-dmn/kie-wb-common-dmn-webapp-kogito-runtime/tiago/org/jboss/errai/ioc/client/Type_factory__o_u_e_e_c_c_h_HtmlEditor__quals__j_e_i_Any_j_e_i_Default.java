package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteIgnoredEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameIgnoredEvent;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditor;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlResourceType;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.htmleditor.HtmlEditorService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditor> { public Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HtmlEditor.class, "Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HtmlEditor.class, BaseEditor.class, Object.class });
  }

  public HtmlEditor createInstance(final ContextManager contextManager) {
    final HtmlResourceType _htmlResourceType_0 = (HtmlResourceType) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_HtmlResourceType__quals__j_e_i_Any_j_e_i_Default");
    final HtmlEditorPresenter _editor_1 = (HtmlEditorPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default");
    final Caller<HtmlEditorService> _htmlEditorService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { HtmlEditorService.class }, new Annotation[] { });
    final HtmlEditor instance = new HtmlEditor(_htmlResourceType_0, _editor_1, _htmlEditorService_2);
    registerDependentScopedReference(instance, _editor_1);
    registerDependentScopedReference(instance, _htmlEditorService_2);
    setIncompleteInstance(instance);
    final DefaultFileNameValidator BaseEditor_fileNameValidator = (DefaultFileNameValidator) contextManager.getInstance("Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default");
    BaseEditor_DefaultFileNameValidator_fileNameValidator(instance, BaseEditor_fileNameValidator);
    final SaveAndRenameCommandBuilder BaseEditor_saveAndRenameCommandBuilder = (SaveAndRenameCommandBuilder) contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BaseEditor_saveAndRenameCommandBuilder);
    BaseEditor_SaveAndRenameCommandBuilder_saveAndRenameCommandBuilder(instance, BaseEditor_saveAndRenameCommandBuilder);
    final Event BaseEditor_concurrentDeleteIgnoredEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ConcurrentDeleteIgnoredEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_concurrentDeleteIgnoredEvent);
    BaseEditor_Event_concurrentDeleteIgnoredEvent(instance, BaseEditor_concurrentDeleteIgnoredEvent);
    final DownloadMenuItemBuilder BaseEditor_downloadMenuItemBuilder = (DownloadMenuItemBuilder) contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BaseEditor_downloadMenuItemBuilder);
    BaseEditor_DownloadMenuItemBuilder_downloadMenuItemBuilder(instance, BaseEditor_downloadMenuItemBuilder);
    final DeletePopUpPresenter BaseEditor_deletePopUpPresenter = (DeletePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    BaseEditor_DeletePopUpPresenter_deletePopUpPresenter(instance, BaseEditor_deletePopUpPresenter);
    final Event BaseEditor_concurrentRenameIgnoredEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ConcurrentRenameIgnoredEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_concurrentRenameIgnoredEvent);
    BaseEditor_Event_concurrentRenameIgnoredEvent(instance, BaseEditor_concurrentRenameIgnoredEvent);
    final ManagedInstance BaseEditor_menuBuilderManagedInstance = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { BasicFileMenuBuilder.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_menuBuilderManagedInstance);
    BaseEditor_ManagedInstance_menuBuilderManagedInstance(instance, BaseEditor_menuBuilderManagedInstance);
    final Event BaseEditor_concurrentDeleteAcceptedEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ConcurrentDeleteAcceptedEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_concurrentDeleteAcceptedEvent);
    BaseEditor_Event_concurrentDeleteAcceptedEvent(instance, BaseEditor_concurrentDeleteAcceptedEvent);
    final Event BaseEditor_concurrentRenameAcceptedEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ConcurrentRenameAcceptedEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_concurrentRenameAcceptedEvent);
    BaseEditor_Event_concurrentRenameAcceptedEvent(instance, BaseEditor_concurrentRenameAcceptedEvent);
    final Promises BaseEditor_promises = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BaseEditor_promises);
    BaseEditor_Promises_promises(instance, BaseEditor_promises);
    final BasicFileMenuBuilderImpl BaseEditor_menuBuilder = (BasicFileMenuBuilderImpl) contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BaseEditor_menuBuilder);
    BaseEditor_BasicFileMenuBuilder_menuBuilder(instance, BaseEditor_menuBuilder);
    final PlaceManagerImpl BaseEditor_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    BaseEditor_PlaceManager_placeManager(instance, BaseEditor_placeManager);
    final Event BaseEditor_changeTitleNotification = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_changeTitleNotification);
    BaseEditor_Event_changeTitleNotification(instance, BaseEditor_changeTitleNotification);
    final VersionRecordManager BaseEditor_versionRecordManager = (VersionRecordManager) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BaseEditor_versionRecordManager);
    BaseEditor_VersionRecordManager_versionRecordManager(instance, BaseEditor_versionRecordManager);
    final Event BaseEditor_notification = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BaseEditor_notification);
    BaseEditor_Event_notification(instance, BaseEditor_notification);
    thisInstance.setReference(instance, "onRestoreSubscription", CDI.subscribeLocal("org.uberfire.ext.editor.commons.version.events.RestoreEvent", new AbstractCDIEventCallback<RestoreEvent>() {
      public void fireEvent(final RestoreEvent event) {
        instance.onRestore(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.editor.commons.version.events.RestoreEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((HtmlEditor) instance, contextManager);
  }

  public void destroyInstanceHelper(final HtmlEditor instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onRestoreSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final HtmlEditor instance) {
    instance.init();
  }

  native static Event BaseEditor_Event_concurrentDeleteAcceptedEvent(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentDeleteAcceptedEvent;
  }-*/;

  native static void BaseEditor_Event_concurrentDeleteAcceptedEvent(BaseEditor instance, Event<ConcurrentDeleteAcceptedEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentDeleteAcceptedEvent = value;
  }-*/;

  native static PlaceManager BaseEditor_PlaceManager_placeManager(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::placeManager;
  }-*/;

  native static void BaseEditor_PlaceManager_placeManager(BaseEditor instance, PlaceManager value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::placeManager = value;
  }-*/;

  native static Event BaseEditor_Event_notification(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::notification;
  }-*/;

  native static void BaseEditor_Event_notification(BaseEditor instance, Event<NotificationEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::notification = value;
  }-*/;

  native static Event BaseEditor_Event_concurrentDeleteIgnoredEvent(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentDeleteIgnoredEvent;
  }-*/;

  native static void BaseEditor_Event_concurrentDeleteIgnoredEvent(BaseEditor instance, Event<ConcurrentDeleteIgnoredEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentDeleteIgnoredEvent = value;
  }-*/;

  native static SaveAndRenameCommandBuilder BaseEditor_SaveAndRenameCommandBuilder_saveAndRenameCommandBuilder(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::saveAndRenameCommandBuilder;
  }-*/;

  native static void BaseEditor_SaveAndRenameCommandBuilder_saveAndRenameCommandBuilder(BaseEditor instance, SaveAndRenameCommandBuilder<String, DefaultMetadata> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::saveAndRenameCommandBuilder = value;
  }-*/;

  native static BasicFileMenuBuilder BaseEditor_BasicFileMenuBuilder_menuBuilder(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::menuBuilder;
  }-*/;

  native static void BaseEditor_BasicFileMenuBuilder_menuBuilder(BaseEditor instance, BasicFileMenuBuilder value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::menuBuilder = value;
  }-*/;

  native static DeletePopUpPresenter BaseEditor_DeletePopUpPresenter_deletePopUpPresenter(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::deletePopUpPresenter;
  }-*/;

  native static void BaseEditor_DeletePopUpPresenter_deletePopUpPresenter(BaseEditor instance, DeletePopUpPresenter value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::deletePopUpPresenter = value;
  }-*/;

  native static ManagedInstance BaseEditor_ManagedInstance_menuBuilderManagedInstance(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::menuBuilderManagedInstance;
  }-*/;

  native static void BaseEditor_ManagedInstance_menuBuilderManagedInstance(BaseEditor instance, ManagedInstance<BasicFileMenuBuilder> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::menuBuilderManagedInstance = value;
  }-*/;

  native static DownloadMenuItemBuilder BaseEditor_DownloadMenuItemBuilder_downloadMenuItemBuilder(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::downloadMenuItemBuilder;
  }-*/;

  native static void BaseEditor_DownloadMenuItemBuilder_downloadMenuItemBuilder(BaseEditor instance, DownloadMenuItemBuilder value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::downloadMenuItemBuilder = value;
  }-*/;

  native static Event BaseEditor_Event_concurrentRenameIgnoredEvent(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentRenameIgnoredEvent;
  }-*/;

  native static void BaseEditor_Event_concurrentRenameIgnoredEvent(BaseEditor instance, Event<ConcurrentRenameIgnoredEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentRenameIgnoredEvent = value;
  }-*/;

  native static DefaultFileNameValidator BaseEditor_DefaultFileNameValidator_fileNameValidator(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::fileNameValidator;
  }-*/;

  native static void BaseEditor_DefaultFileNameValidator_fileNameValidator(BaseEditor instance, DefaultFileNameValidator value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::fileNameValidator = value;
  }-*/;

  native static VersionRecordManager BaseEditor_VersionRecordManager_versionRecordManager(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::versionRecordManager;
  }-*/;

  native static void BaseEditor_VersionRecordManager_versionRecordManager(BaseEditor instance, VersionRecordManager value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::versionRecordManager = value;
  }-*/;

  native static Event BaseEditor_Event_concurrentRenameAcceptedEvent(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentRenameAcceptedEvent;
  }-*/;

  native static void BaseEditor_Event_concurrentRenameAcceptedEvent(BaseEditor instance, Event<ConcurrentRenameAcceptedEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::concurrentRenameAcceptedEvent = value;
  }-*/;

  native static Promises BaseEditor_Promises_promises(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::promises;
  }-*/;

  native static void BaseEditor_Promises_promises(BaseEditor instance, Promises value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::promises = value;
  }-*/;

  native static Event BaseEditor_Event_changeTitleNotification(BaseEditor instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.BaseEditor::changeTitleNotification;
  }-*/;

  native static void BaseEditor_Event_changeTitleNotification(BaseEditor instance, Event<ChangeTitleWidgetEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.BaseEditor::changeTitleNotification = value;
  }-*/;
}