package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider;
import org.kie.workbench.common.widgets.client.assets.dropdown.AbstractKieAssetsDropdown;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdown;
import org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdown;
import org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdown.View;
import org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView;

public class Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAssetsDropdown> { public Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNAssetsDropdown.class, "Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNAssetsDropdown.class, KogitoKieAssetsDropdown.class, AbstractKieAssetsDropdown.class, Object.class, KieAssetsDropdown.class });
  }

  public DMNAssetsDropdown createInstance(final ContextManager contextManager) {
    final View _view_0 = (KogitoKieAssetsDropdownView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default");
    final DMNAssetsDropdownItemsProvider _dataProvider_1 = (DMNAssetsDropdownItemsProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default");
    final DMNAssetsDropdown instance = new DMNAssetsDropdown(_view_0, _dataProvider_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dataProvider_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNAssetsDropdown instance) {
    instance.init();
  }
}