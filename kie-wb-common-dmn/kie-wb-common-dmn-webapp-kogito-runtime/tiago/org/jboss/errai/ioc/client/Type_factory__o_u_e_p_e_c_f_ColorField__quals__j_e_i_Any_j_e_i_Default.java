package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.ColorField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_e_c_f_ColorField__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorField> { public Type_factory__o_u_e_p_e_c_f_ColorField__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ColorField.class, "Type_factory__o_u_e_p_e_c_f_ColorField__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ColorField.class, AbstractField.class, Object.class });
  }

  public ColorField createInstance(final ContextManager contextManager) {
    final ColorField instance = new ColorField();
    setIncompleteInstance(instance);
    final Event ColorField_propertyEditorChangeEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PropertyEditorChangeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ColorField_propertyEditorChangeEvent);
    ColorField_Event_propertyEditorChangeEvent(instance, ColorField_propertyEditorChangeEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event ColorField_Event_propertyEditorChangeEvent(ColorField instance) /*-{
    return instance.@org.uberfire.ext.properties.editor.client.fields.ColorField::propertyEditorChangeEvent;
  }-*/;

  native static void ColorField_Event_propertyEditorChangeEvent(ColorField instance, Event<PropertyEditorChangeEvent> value) /*-{
    instance.@org.uberfire.ext.properties.editor.client.fields.ColorField::propertyEditorChangeEvent = value;
  }-*/;
}