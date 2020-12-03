package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.SecretTextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_e_c_f_SecretTextField__quals__j_e_i_Any_j_e_i_Default extends Factory<SecretTextField> { public Type_factory__o_u_e_p_e_c_f_SecretTextField__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SecretTextField.class, "Type_factory__o_u_e_p_e_c_f_SecretTextField__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SecretTextField.class, AbstractField.class, Object.class });
  }

  public SecretTextField createInstance(final ContextManager contextManager) {
    final SecretTextField instance = new SecretTextField();
    setIncompleteInstance(instance);
    final Event SecretTextField_propertyEditorChangeEventEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PropertyEditorChangeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, SecretTextField_propertyEditorChangeEventEvent);
    SecretTextField_Event_propertyEditorChangeEventEvent(instance, SecretTextField_propertyEditorChangeEventEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event SecretTextField_Event_propertyEditorChangeEventEvent(SecretTextField instance) /*-{
    return instance.@org.uberfire.ext.properties.editor.client.fields.SecretTextField::propertyEditorChangeEventEvent;
  }-*/;

  native static void SecretTextField_Event_propertyEditorChangeEventEvent(SecretTextField instance, Event<PropertyEditorChangeEvent> value) /*-{
    instance.@org.uberfire.ext.properties.editor.client.fields.SecretTextField::propertyEditorChangeEventEvent = value;
  }-*/;
}