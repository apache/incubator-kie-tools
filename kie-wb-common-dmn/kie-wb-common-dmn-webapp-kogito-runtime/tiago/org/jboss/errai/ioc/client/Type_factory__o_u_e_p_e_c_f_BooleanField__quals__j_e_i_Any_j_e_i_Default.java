package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_e_c_f_BooleanField__quals__j_e_i_Any_j_e_i_Default extends Factory<BooleanField> { public Type_factory__o_u_e_p_e_c_f_BooleanField__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BooleanField.class, "Type_factory__o_u_e_p_e_c_f_BooleanField__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BooleanField.class, AbstractField.class, Object.class });
  }

  public BooleanField createInstance(final ContextManager contextManager) {
    final BooleanField instance = new BooleanField();
    setIncompleteInstance(instance);
    final Event BooleanField_propertyEditorChangeEventEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PropertyEditorChangeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, BooleanField_propertyEditorChangeEventEvent);
    BooleanField_Event_propertyEditorChangeEventEvent(instance, BooleanField_propertyEditorChangeEventEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event BooleanField_Event_propertyEditorChangeEventEvent(BooleanField instance) /*-{
    return instance.@org.uberfire.ext.properties.editor.client.fields.BooleanField::propertyEditorChangeEventEvent;
  }-*/;

  native static void BooleanField_Event_propertyEditorChangeEventEvent(BooleanField instance, Event<PropertyEditorChangeEvent> value) /*-{
    instance.@org.uberfire.ext.properties.editor.client.fields.BooleanField::propertyEditorChangeEventEvent = value;
  }-*/;
}