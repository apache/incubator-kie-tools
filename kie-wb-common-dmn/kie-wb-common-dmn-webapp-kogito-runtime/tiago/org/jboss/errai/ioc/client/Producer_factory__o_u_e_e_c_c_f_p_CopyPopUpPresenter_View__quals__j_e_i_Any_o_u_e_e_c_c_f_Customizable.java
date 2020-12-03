package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer;
import org.uberfire.ext.editor.commons.client.file.Customizable;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;

public class Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable extends Factory<View> { public Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable() {
    super(new FactoryHandleImpl(View.class, "Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { View.class, UberElement.class, IsElement.class, HasPresenter.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Customizable() {
        public Class annotationType() {
          return Customizable.class;
        }
        public String toString() {
          return "@org.uberfire.ext.editor.commons.client.file.Customizable()";
        }
    } });
  }

  public View createInstance(final ContextManager contextManager) {
    CopyPopUpViewProducer producerInstance = contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final View instance = producerInstance.copyPopUpViewProducer();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }
}