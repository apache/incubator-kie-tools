package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionView;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;

public class Type_factory__o_k_w_c_w_c_w_KieSelectOptionElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionElement> { public Type_factory__o_k_w_c_w_c_w_KieSelectOptionElement__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieSelectOptionElement.class, "Type_factory__o_k_w_c_w_c_w_KieSelectOptionElement__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieSelectOptionElement.class, ListItemPresenter.class, Object.class });
  }

  public KieSelectOptionElement createInstance(final ContextManager contextManager) {
    final KieSelectOptionView _view_0 = (KieSelectOptionView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default");
    final KieSelectOptionElement instance = new KieSelectOptionElement(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}