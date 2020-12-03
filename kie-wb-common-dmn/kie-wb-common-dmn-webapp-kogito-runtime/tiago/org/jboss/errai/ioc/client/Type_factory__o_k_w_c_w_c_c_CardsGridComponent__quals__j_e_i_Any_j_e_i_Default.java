package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent.View;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponentView;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent;

public class Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CardsGridComponent> { public Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CardsGridComponent.class, "Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CardsGridComponent.class, Object.class });
  }

  public CardsGridComponent createInstance(final ContextManager contextManager) {
    final ManagedInstance<CardFrameComponent> _cardFrameInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CardFrameComponent.class }, new Annotation[] { });
    final View _view_0 = (CardsGridComponentView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default");
    final CardsGridComponent instance = new CardsGridComponent(_view_0, _cardFrameInstances_1);
    registerDependentScopedReference(instance, _cardFrameInstances_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CardsGridComponent instance) {
    CardsGridComponent_init(instance);
  }

  public native static void CardsGridComponent_init(CardsGridComponent instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.CardsGridComponent::init()();
  }-*/;
}