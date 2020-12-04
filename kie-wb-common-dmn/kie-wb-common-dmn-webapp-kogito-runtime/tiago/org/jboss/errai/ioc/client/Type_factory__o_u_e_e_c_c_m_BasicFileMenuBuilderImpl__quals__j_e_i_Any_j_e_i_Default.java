package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicFileMenuBuilderImpl> { public Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BasicFileMenuBuilderImpl.class, "Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BasicFileMenuBuilderImpl.class, Object.class, BasicFileMenuBuilder.class, HasLockSyncMenuStateHelper.class });
  }

  public BasicFileMenuBuilderImpl createInstance(final ContextManager contextManager) {
    final DeletePopUpPresenter _deletePopUpPresenter_0 = (DeletePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    final BusyIndicatorView _busyIndicatorView_3 = (BusyIndicatorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default");
    final CopyPopUpPresenter _copyPopUpPresenter_1 = (CopyPopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    final RenamePopUpPresenter _renamePopUpPresenter_2 = (RenamePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notification_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final RestoreVersionCommandProvider _restoreVersionCommandProvider_5 = (RestoreVersionCommandProvider) contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default");
    final BasicFileMenuBuilderImpl instance = new BasicFileMenuBuilderImpl(_deletePopUpPresenter_0, _copyPopUpPresenter_1, _renamePopUpPresenter_2, _busyIndicatorView_3, _notification_4, _restoreVersionCommandProvider_5);
    registerDependentScopedReference(instance, _busyIndicatorView_3);
    registerDependentScopedReference(instance, _copyPopUpPresenter_1);
    registerDependentScopedReference(instance, _renamePopUpPresenter_2);
    registerDependentScopedReference(instance, _notification_4);
    registerDependentScopedReference(instance, _restoreVersionCommandProvider_5);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onEditorLockInfoSubscription", CDI.subscribeLocal("org.uberfire.client.mvp.UpdatedLockStatusEvent", new AbstractCDIEventCallback<UpdatedLockStatusEvent>() {
      public void fireEvent(final UpdatedLockStatusEvent event) {
        BasicFileMenuBuilderImpl_onEditorLockInfo_UpdatedLockStatusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.mvp.UpdatedLockStatusEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BasicFileMenuBuilderImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final BasicFileMenuBuilderImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onEditorLockInfoSubscription", Subscription.class)).remove();
  }

  public native static void BasicFileMenuBuilderImpl_onEditorLockInfo_UpdatedLockStatusEvent(BasicFileMenuBuilderImpl instance, UpdatedLockStatusEvent a0) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl::onEditorLockInfo(Lorg/uberfire/client/mvp/UpdatedLockStatusEvent;)(a0);
  }-*/;
}