package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumerationItem> { public Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintEnumerationItem.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintEnumerationItem.class, Object.class });
  }

  public DataTypeConstraintEnumerationItem createInstance(final ContextManager contextManager) {
    final ConstraintPlaceholderHelper _placeholderHelper_1 = (ConstraintPlaceholderHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DataTypeConstraintEnumerationItemView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintEnumerationItem instance = new DataTypeConstraintEnumerationItem(_view_0, _placeholderHelper_1);
    registerDependentScopedReference(instance, _placeholderHelper_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeConstraintEnumerationItem instance) {
    DataTypeConstraintEnumerationItem_setup(instance);
  }

  public native static void DataTypeConstraintEnumerationItem_setup(DataTypeConstraintEnumerationItem instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem::setup()();
  }-*/;
}