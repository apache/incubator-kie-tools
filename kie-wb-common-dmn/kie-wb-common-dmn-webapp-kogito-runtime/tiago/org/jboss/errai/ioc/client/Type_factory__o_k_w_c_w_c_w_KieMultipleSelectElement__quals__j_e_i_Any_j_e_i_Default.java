package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement.View;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView;
import org.kie.workbench.common.widgets.client.widget.KieSelectElementBase;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionsListPresenter;

public class Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieMultipleSelectElement> { public Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElement__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieMultipleSelectElement.class, "Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElement__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieMultipleSelectElement.class, Object.class, KieSelectElementBase.class });
  }

  public KieMultipleSelectElement createInstance(final ContextManager contextManager) {
    final KieSelectOptionsListPresenter _optionsListPresenter_1 = (KieSelectOptionsListPresenter) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default");
    final Elemental2DomUtil _elemental2DomUtil_2 = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (KieMultipleSelectElementView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default");
    final KieMultipleSelectElement instance = new KieMultipleSelectElement(_view_0, _optionsListPresenter_1, _elemental2DomUtil_2);
    registerDependentScopedReference(instance, _optionsListPresenter_1);
    registerDependentScopedReference(instance, _elemental2DomUtil_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final KieMultipleSelectElement instance) {
    instance.init();
  }
}