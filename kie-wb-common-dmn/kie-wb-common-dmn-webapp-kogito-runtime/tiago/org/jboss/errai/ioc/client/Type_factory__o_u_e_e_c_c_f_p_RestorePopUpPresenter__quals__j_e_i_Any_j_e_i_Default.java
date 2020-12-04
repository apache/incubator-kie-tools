package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

public class Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<RestorePopUpPresenter> { public Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RestorePopUpPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RestorePopUpPresenter.class, Object.class });
  }

  public RestorePopUpPresenter createInstance(final ContextManager contextManager) {
    final RestoreUtil _restoreUtil_4 = (RestoreUtil) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (RestorePopUpView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default");
    final BusyIndicatorView _busyIndicatorView_1 = (BusyIndicatorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default");
    final Event<RestoreEvent> _restoreEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RestoreEvent.class }, new Annotation[] { });
    final Caller<VersionService> _versionService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VersionService.class }, new Annotation[] { });
    final RestorePopUpPresenter instance = new RestorePopUpPresenter(_view_0, _busyIndicatorView_1, _versionService_2, _restoreEvent_3, _restoreUtil_4);
    registerDependentScopedReference(instance, _restoreUtil_4);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _busyIndicatorView_1);
    registerDependentScopedReference(instance, _restoreEvent_3);
    registerDependentScopedReference(instance, _versionService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final RestorePopUpPresenter instance) {
    instance.setup();
  }
}