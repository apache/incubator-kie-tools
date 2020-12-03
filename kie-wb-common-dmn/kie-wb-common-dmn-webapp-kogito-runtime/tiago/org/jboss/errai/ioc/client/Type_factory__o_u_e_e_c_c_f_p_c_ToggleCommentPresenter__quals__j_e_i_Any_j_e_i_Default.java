package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView;

public class Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleCommentPresenter> { public Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ToggleCommentPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ToggleCommentPresenter.class, Object.class });
  }

  public ToggleCommentPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (ToggleCommentView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default");
    final ToggleCommentPresenter instance = new ToggleCommentPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ToggleCommentPresenter instance) {
    instance.setup();
  }
}