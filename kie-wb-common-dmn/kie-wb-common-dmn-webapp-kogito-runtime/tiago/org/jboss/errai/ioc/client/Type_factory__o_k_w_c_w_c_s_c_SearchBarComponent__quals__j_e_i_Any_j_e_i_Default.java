package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent.View;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView;

public class Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SearchBarComponent> { public Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SearchBarComponent.class, "Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SearchBarComponent.class, Object.class });
  }

  public SearchBarComponent createInstance(final ContextManager contextManager) {
    final View _view_0 = (SearchBarComponentView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default");
    final SearchBarComponent instance = new SearchBarComponent(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SearchBarComponent instance) {
    SearchBarComponent_setup(instance);
  }

  public native static void SearchBarComponent_setup(SearchBarComponent instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponent::setup()();
  }-*/;
}