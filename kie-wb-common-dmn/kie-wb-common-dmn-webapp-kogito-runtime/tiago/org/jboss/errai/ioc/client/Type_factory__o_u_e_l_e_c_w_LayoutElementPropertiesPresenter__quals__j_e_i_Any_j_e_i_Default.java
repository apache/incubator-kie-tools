package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesView;

public class Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutElementPropertiesPresenter> { public Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutElementPropertiesPresenter.class, "Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutElementPropertiesPresenter.class, Object.class });
  }

  public LayoutElementPropertiesPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (LayoutElementPropertiesView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default");
    final Event<LayoutElementClearAllPropertiesEvent> _propertyClearAllEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutElementClearAllPropertiesEvent.class }, new Annotation[] { });
    final LayoutEditorCssHelper _cssHelper_1 = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    final Event<LayoutElementPropertyChangedEvent> _propertyChangedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutElementPropertyChangedEvent.class }, new Annotation[] { });
    final LayoutElementPropertiesPresenter instance = new LayoutElementPropertiesPresenter(_view_0, _cssHelper_1, _propertyChangedEvent_2, _propertyClearAllEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _propertyClearAllEvent_3);
    registerDependentScopedReference(instance, _propertyChangedEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}