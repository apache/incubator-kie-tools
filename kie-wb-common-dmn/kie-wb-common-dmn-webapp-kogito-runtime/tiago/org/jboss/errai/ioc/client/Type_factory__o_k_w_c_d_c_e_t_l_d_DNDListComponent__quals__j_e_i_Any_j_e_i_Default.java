package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView;

public class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDListComponent> { public Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DNDListComponent.class, "Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DNDListComponent.class, Object.class });
  }

  public DNDListComponent createInstance(final ContextManager contextManager) {
    final View _view_0 = (DNDListComponentView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeList _dataTypeList_1 = (DataTypeList) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default");
    final DNDListComponent instance = new DNDListComponent(_view_0, _dataTypeList_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DNDListComponent instance) {
    DNDListComponent_init(instance);
  }

  public native static void DNDListComponent_init(DNDListComponent instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent::init()();
  }-*/;
}