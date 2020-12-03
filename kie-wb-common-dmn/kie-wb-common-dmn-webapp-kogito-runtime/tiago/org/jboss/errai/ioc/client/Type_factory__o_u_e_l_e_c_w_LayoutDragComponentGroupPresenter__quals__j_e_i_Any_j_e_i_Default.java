package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView;

public class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentGroupPresenter> { public Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutDragComponentGroupPresenter.class, "Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutDragComponentGroupPresenter.class, Object.class });
  }

  public LayoutDragComponentGroupPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (LayoutDragComponentGroupView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default");
    final LayoutDragComponentGroupPresenter instance = new LayoutDragComponentGroupPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}