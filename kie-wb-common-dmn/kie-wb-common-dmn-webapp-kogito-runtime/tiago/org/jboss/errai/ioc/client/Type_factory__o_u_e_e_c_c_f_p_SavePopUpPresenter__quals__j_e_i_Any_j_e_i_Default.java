package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SavePopUpPresenter> { public Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SavePopUpPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SavePopUpPresenter.class, Object.class });
  }

  public SavePopUpPresenter createInstance(final ContextManager contextManager) {
    final Event<SaveInProgressEvent> _saveInProgressEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SaveInProgressEvent.class }, new Annotation[] { });
    final View _view_0 = (SavePopUpView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default");
    final SavePopUpPresenter instance = new SavePopUpPresenter(_view_0, _saveInProgressEvent_1);
    registerDependentScopedReference(instance, _saveInProgressEvent_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SavePopUpPresenter instance) {
    instance.setup();
  }
}