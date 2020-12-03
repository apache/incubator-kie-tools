package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_e_c_f_TextField__quals__j_e_i_Any_j_e_i_Default extends Factory<TextField> { public Type_factory__o_u_e_p_e_c_f_TextField__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextField.class, "Type_factory__o_u_e_p_e_c_f_TextField__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextField.class, AbstractField.class, Object.class });
  }

  public TextField createInstance(final ContextManager contextManager) {
    final TextField instance = new TextField();
    setIncompleteInstance(instance);
    final Event TextField_propertyEditorChangeEventEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PropertyEditorChangeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, TextField_propertyEditorChangeEventEvent);
    TextField_Event_propertyEditorChangeEventEvent(instance, TextField_propertyEditorChangeEventEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event TextField_Event_propertyEditorChangeEventEvent(TextField instance) /*-{
    return instance.@org.uberfire.ext.properties.editor.client.fields.TextField::propertyEditorChangeEventEvent;
  }-*/;

  native static void TextField_Event_propertyEditorChangeEventEvent(TextField instance, Event<PropertyEditorChangeEvent> value) /*-{
    instance.@org.uberfire.ext.properties.editor.client.fields.TextField::propertyEditorChangeEventEvent = value;
  }-*/;
}