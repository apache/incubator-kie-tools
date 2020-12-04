package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponent.ContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView;
import org.kie.workbench.common.widgets.client.cards.CardComponent;

public class Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLCardComponent> { public Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PMMLCardComponent.class, "Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLCardComponent.class, BaseCardComponent.class, Object.class, CardComponent.class });
  }

  public PMMLCardComponent createInstance(final ContextManager contextManager) {
    final ContentView _contentView_0 = (PMMLCardComponentContentView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard");
    final Event<RefreshDecisionComponents> _refreshDecisionComponentsEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDecisionComponents.class }, new Annotation[] { });
    final PMMLCardComponent instance = new PMMLCardComponent(_contentView_0, _refreshDecisionComponentsEvent_1);
    registerDependentScopedReference(instance, _contentView_0);
    registerDependentScopedReference(instance, _refreshDecisionComponentsEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PMMLCardComponent instance) {
    instance.init();
    instance.init();
  }
}