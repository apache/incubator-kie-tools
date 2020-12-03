package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionsListPresenter;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;

public class Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionsListPresenter> { public Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieSelectOptionsListPresenter.class, "Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieSelectOptionsListPresenter.class, ListPresenter.class, Object.class });
  }

  public KieSelectOptionsListPresenter createInstance(final ContextManager contextManager) {
    final ManagedInstance<KieSelectOptionElement> _itemPresenters_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { KieSelectOptionElement.class }, new Annotation[] { });
    final KieSelectOptionsListPresenter instance = new KieSelectOptionsListPresenter(_itemPresenters_0);
    registerDependentScopedReference(instance, _itemPresenters_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}