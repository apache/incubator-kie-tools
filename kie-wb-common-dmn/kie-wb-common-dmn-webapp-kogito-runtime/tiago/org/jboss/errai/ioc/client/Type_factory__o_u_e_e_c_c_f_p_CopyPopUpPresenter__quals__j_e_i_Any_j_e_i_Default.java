package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;

public class Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopUpPresenter> { public Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CopyPopUpPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CopyPopUpPresenter.class, Object.class });
  }

  public CopyPopUpPresenter createInstance(final ContextManager contextManager) {
    final ToggleCommentPresenter _toggleCommentPresenter_1 = (ToggleCommentPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (View) contextManager.getInstance("Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable");
    final CopyPopUpPresenter instance = new CopyPopUpPresenter(_view_0, _toggleCommentPresenter_1);
    registerDependentScopedReference(instance, _toggleCommentPresenter_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CopyPopUpPresenter instance) {
    instance.setup();
  }
}