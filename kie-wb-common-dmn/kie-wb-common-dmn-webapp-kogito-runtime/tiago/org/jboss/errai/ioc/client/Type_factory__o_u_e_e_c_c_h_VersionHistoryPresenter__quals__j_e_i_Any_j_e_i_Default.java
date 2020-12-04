package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
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
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenterView;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenterView.Presenter;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenterViewImpl;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.VersionService;

public class Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionHistoryPresenter> { public Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VersionHistoryPresenter.class, "Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VersionHistoryPresenter.class, Object.class, Presenter.class, IsWidget.class });
  }

  public VersionHistoryPresenter createInstance(final ContextManager contextManager) {
    final VersionHistoryPresenterView _view_0 = (VersionHistoryPresenterViewImpl) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final Caller<VersionService> _versionService_1 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VersionService.class }, new Annotation[] { });
    final Event<VersionSelectedEvent> _versionSelectedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { VersionSelectedEvent.class }, new Annotation[] { });
    final VersionHistoryPresenter instance = new VersionHistoryPresenter(_view_0, _versionService_1, _versionSelectedEvent_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _versionService_1);
    registerDependentScopedReference(instance, _versionSelectedEvent_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onVersionChangeSubscription", CDI.subscribeLocal("org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent", new AbstractCDIEventCallback<VersionSelectedEvent>() {
      public void fireEvent(final VersionSelectedEvent event) {
        instance.onVersionChange(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((VersionHistoryPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final VersionHistoryPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onVersionChangeSubscription", Subscription.class)).remove();
  }
}