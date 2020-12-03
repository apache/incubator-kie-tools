package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement.View;
import org.kie.workbench.common.widgets.client.widget.KieSelectElementBase;
import org.kie.workbench.common.widgets.client.widget.KieSelectElementView;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionsListPresenter;

public class Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectElement> { public Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieSelectElement.class, "Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieSelectElement.class, Object.class, KieSelectElementBase.class, IsElement.class });
  }

  public KieSelectElement createInstance(final ContextManager contextManager) {
    final View _view_0 = (KieSelectElementView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default");
    final KieSelectOptionsListPresenter _optionsListPresenter_1 = (KieSelectOptionsListPresenter) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default");
    final KieSelectElement instance = new KieSelectElement(_view_0, _optionsListPresenter_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _optionsListPresenter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final KieSelectElement instance) {
    instance.init();
  }
}