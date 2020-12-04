package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent.ContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView;
import org.kie.workbench.common.widgets.client.cards.CardComponent;

public class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardComponent> { public Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNCardComponent.class, "Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCardComponent.class, BaseCardComponent.class, Object.class, CardComponent.class });
  }

  public DMNCardComponent createInstance(final ContextManager contextManager) {
    final Event<RefreshDecisionComponents> _refreshDecisionComponentsEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDecisionComponents.class }, new Annotation[] { });
    final ContentView _contentView_0 = (DMNCardComponentContentView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard");
    final DMNCardComponent instance = new DMNCardComponent(_contentView_0, _refreshDecisionComponentsEvent_1);
    registerDependentScopedReference(instance, _refreshDecisionComponentsEvent_1);
    registerDependentScopedReference(instance, _contentView_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNCardComponent instance) {
    instance.init();
    instance.init();
  }
}