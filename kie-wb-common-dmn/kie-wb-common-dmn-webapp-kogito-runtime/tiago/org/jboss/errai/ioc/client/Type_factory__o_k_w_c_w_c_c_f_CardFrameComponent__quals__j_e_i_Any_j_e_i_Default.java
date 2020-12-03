package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent.View;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView;

public class Type_factory__o_k_w_c_w_c_c_f_CardFrameComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CardFrameComponent> { public Type_factory__o_k_w_c_w_c_c_f_CardFrameComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CardFrameComponent.class, "Type_factory__o_k_w_c_w_c_c_f_CardFrameComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CardFrameComponent.class, Object.class });
  }

  public CardFrameComponent createInstance(final ContextManager contextManager) {
    final View _view_0 = (CardFrameComponentView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default");
    final CardFrameComponent instance = new CardFrameComponent(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CardFrameComponent instance) {
    CardFrameComponent_setup(instance);
  }

  public native static void CardFrameComponent_setup(CardFrameComponent instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent::setup()();
  }-*/;
}