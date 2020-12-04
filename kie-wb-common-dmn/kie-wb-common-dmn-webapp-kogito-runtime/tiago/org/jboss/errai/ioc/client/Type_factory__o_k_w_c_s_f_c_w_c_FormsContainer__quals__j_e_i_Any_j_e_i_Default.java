package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerView;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;

public class Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsContainer> { public Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsContainer.class, "Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormsContainer.class, Object.class, IsElement.class });
  }

  public FormsContainer createInstance(final ContextManager contextManager) {
    final ManagedInstance<FormDisplayer> _displayersInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { FormDisplayer.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Event<FormFieldChanged> _formFieldChangedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FormFieldChanged.class }, new Annotation[] { });
    final FormsContainerView _view_0 = (FormsContainerViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormsContainer instance = new FormsContainer(_view_0, _displayersInstance_1, _formFieldChangedEvent_2);
    registerDependentScopedReference(instance, _displayersInstance_1);
    registerDependentScopedReference(instance, _formFieldChangedEvent_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormsContainer) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormsContainer instance, final ContextManager contextManager) {
    instance.destroyAll();
  }
}