package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.SaveButton;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButton;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;

public class Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionRecordManager> { public Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VersionRecordManager.class, "Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VersionRecordManager.class, Object.class });
  }

  public VersionRecordManager createInstance(final ContextManager contextManager) {
    final RestorePopUpPresenter _restorePopUpPresenter_2 = (RestorePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    final Event<VersionSelectedEvent> _versionSelectedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { VersionSelectedEvent.class }, new Annotation[] { });
    final SaveButton _saveButton_1 = (SaveButton) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default");
    final Caller<VersionService> _versionService_5 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VersionService.class }, new Annotation[] { });
    final VersionMenuDropDownButton _versionMenuDropDownButton_0 = (VersionMenuDropDownButton) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default");
    final RestoreUtil _restoreUtil_3 = (RestoreUtil) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default");
    final CurrentBranch _currentBranch_6 = (CurrentBranch) contextManager.getInstance("Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable");
    final VersionRecordManager instance = new VersionRecordManager(_versionMenuDropDownButton_0, _saveButton_1, _restorePopUpPresenter_2, _restoreUtil_3, _versionSelectedEvent_4, _versionService_5, _currentBranch_6);
    registerDependentScopedReference(instance, _restorePopUpPresenter_2);
    registerDependentScopedReference(instance, _versionSelectedEvent_4);
    registerDependentScopedReference(instance, _saveButton_1);
    registerDependentScopedReference(instance, _versionService_5);
    registerDependentScopedReference(instance, _versionMenuDropDownButton_0);
    registerDependentScopedReference(instance, _restoreUtil_3);
    registerDependentScopedReference(instance, _currentBranch_6);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onVersionSelectedEventSubscription", CDI.subscribeLocal("org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent", new AbstractCDIEventCallback<VersionSelectedEvent>() {
      public void fireEvent(final VersionSelectedEvent event) {
        instance.onVersionSelectedEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onRestoreSubscription", CDI.subscribeLocal("org.uberfire.ext.editor.commons.version.events.RestoreEvent", new AbstractCDIEventCallback<RestoreEvent>() {
      public void fireEvent(final RestoreEvent event) {
        VersionRecordManager_onRestore_RestoreEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.editor.commons.version.events.RestoreEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((VersionRecordManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final VersionRecordManager instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onVersionSelectedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onRestoreSubscription", Subscription.class)).remove();
  }

  public native static void VersionRecordManager_onRestore_RestoreEvent(VersionRecordManager instance, RestoreEvent a0) /*-{
    instance.@org.uberfire.ext.editor.commons.client.history.VersionRecordManager::onRestore(Lorg/uberfire/ext/editor/commons/version/events/RestoreEvent;)(a0);
  }-*/;
}