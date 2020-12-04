package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;

public class Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<RenamePopUpPresenter> { public Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RenamePopUpPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RenamePopUpPresenter.class, Object.class });
  }

  public RenamePopUpPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (RenamePopUpView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default");
    final ToggleCommentPresenter _toggleCommentPresenter_1 = (ToggleCommentPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default");
    final RenamePopUpPresenter instance = new RenamePopUpPresenter(_view_0, _toggleCommentPresenter_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _toggleCommentPresenter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final RenamePopUpPresenter instance) {
    instance.setup();
  }
}