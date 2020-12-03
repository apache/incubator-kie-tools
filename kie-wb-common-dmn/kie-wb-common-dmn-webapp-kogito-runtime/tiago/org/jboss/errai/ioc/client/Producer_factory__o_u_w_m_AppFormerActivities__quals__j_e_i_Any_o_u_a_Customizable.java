package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.annotations.Customizable;
import org.uberfire.workbench.model.AppFormerActivities;
import org.uberfire.workbench.model.AppFormerActivitiesProducer;

public class Producer_factory__o_u_w_m_AppFormerActivities__quals__j_e_i_Any_o_u_a_Customizable extends Factory<AppFormerActivities> { public Producer_factory__o_u_w_m_AppFormerActivities__quals__j_e_i_Any_o_u_a_Customizable() {
    super(new FactoryHandleImpl(AppFormerActivities.class, "Producer_factory__o_u_w_m_AppFormerActivities__quals__j_e_i_Any_o_u_a_Customizable", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppFormerActivities.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Customizable() {
        public Class annotationType() {
          return Customizable.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.Customizable()";
        }
    } });
  }

  public AppFormerActivities createInstance(final ContextManager contextManager) {
    AppFormerActivitiesProducer producerInstance = contextManager.getInstance("Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final AppFormerActivities instance = producerInstance.appFormerActivitiesProducer();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}