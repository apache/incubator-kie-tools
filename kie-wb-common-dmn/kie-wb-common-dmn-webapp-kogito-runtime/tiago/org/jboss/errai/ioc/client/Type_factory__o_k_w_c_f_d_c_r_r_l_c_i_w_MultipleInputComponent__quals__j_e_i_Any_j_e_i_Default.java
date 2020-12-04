package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.EditableColumnGeneratorManagerImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputComponent> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleInputComponent.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleInputComponent.class, Object.class, Presenter.class, IsElement.class });
  }

  public MultipleInputComponent createInstance(final ContextManager contextManager) {
    final EditableColumnGeneratorManager _columnGeneratorManager_1 = (EditableColumnGeneratorManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final MultipleInputComponentView _view_0 = (MultipleInputComponentViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final MultipleInputComponent instance = new MultipleInputComponent(_view_0, _columnGeneratorManager_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultipleInputComponent instance) {
    instance.init();
  }
}