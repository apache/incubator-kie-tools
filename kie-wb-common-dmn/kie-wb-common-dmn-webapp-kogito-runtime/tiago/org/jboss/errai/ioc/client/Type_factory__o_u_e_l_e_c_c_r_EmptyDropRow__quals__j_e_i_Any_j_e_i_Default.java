package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow.View;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

public class Type_factory__o_u_e_l_e_c_c_r_EmptyDropRow__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyDropRow> { public Type_factory__o_u_e_l_e_c_c_r_EmptyDropRow__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EmptyDropRow.class, "Type_factory__o_u_e_l_e_c_c_r_EmptyDropRow__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EmptyDropRow.class, Object.class });
  }

  public EmptyDropRow createInstance(final ContextManager contextManager) {
    final LayoutDragComponentHelper _layoutDragComponentHelper_1 = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (EmptyDropRowView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default");
    final EmptyDropRow instance = new EmptyDropRow(_view_0, _layoutDragComponentHelper_1);
    registerDependentScopedReference(instance, _layoutDragComponentHelper_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final EmptyDropRow instance) {
    instance.post();
  }
}