package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer;

public class Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable extends Factory<PreferenceScopeTypes> { public Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable() {
    super(new FactoryHandleImpl(PreferenceScopeTypes.class, "Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeTypes.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Customizable() {
        public Class annotationType() {
          return Customizable.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.Customizable()";
        }
    } });
  }

  public PreferenceScopeTypes createInstance(final ContextManager contextManager) {
    PreferenceScopeTypesProducer producerInstance = contextManager.getInstance("Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final PreferenceScopeTypes instance = producerInstance.preferenceScopeTypesProducer();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}