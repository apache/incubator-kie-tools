package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.annotations.Customizable;
import org.uberfire.ext.editor.commons.client.menu.common.CurrentBranchProducer;
import org.uberfire.ext.editor.commons.version.CurrentBranch;

public class Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable extends Factory<CurrentBranch> { public Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable() {
    super(new FactoryHandleImpl(CurrentBranch.class, "Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CurrentBranch.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Customizable() {
        public Class annotationType() {
          return Customizable.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.Customizable()";
        }
    } });
  }

  public CurrentBranch createInstance(final ContextManager contextManager) {
    CurrentBranchProducer producerInstance = contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final CurrentBranch instance = producerInstance.currentBranchProducer();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }
}