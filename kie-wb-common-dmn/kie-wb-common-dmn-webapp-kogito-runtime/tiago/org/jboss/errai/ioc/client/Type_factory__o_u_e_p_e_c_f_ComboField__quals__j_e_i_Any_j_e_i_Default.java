package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.ComboField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_e_c_f_ComboField__quals__j_e_i_Any_j_e_i_Default extends Factory<ComboField> { public Type_factory__o_u_e_p_e_c_f_ComboField__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ComboField.class, "Type_factory__o_u_e_p_e_c_f_ComboField__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ComboField.class, AbstractField.class, Object.class });
  }

  public ComboField createInstance(final ContextManager contextManager) {
    final ComboField instance = new ComboField();
    setIncompleteInstance(instance);
    final Event ComboField_propertyEditorChangeEventEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PropertyEditorChangeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ComboField_propertyEditorChangeEventEvent);
    ComboField_Event_propertyEditorChangeEventEvent(instance, ComboField_propertyEditorChangeEventEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event ComboField_Event_propertyEditorChangeEventEvent(ComboField instance) /*-{
    return instance.@org.uberfire.ext.properties.editor.client.fields.ComboField::propertyEditorChangeEventEvent;
  }-*/;

  native static void ComboField_Event_propertyEditorChangeEventEvent(ComboField instance, Event<PropertyEditorChangeEvent> value) /*-{
    instance.@org.uberfire.ext.properties.editor.client.fields.ComboField::propertyEditorChangeEventEvent = value;
  }-*/;
}