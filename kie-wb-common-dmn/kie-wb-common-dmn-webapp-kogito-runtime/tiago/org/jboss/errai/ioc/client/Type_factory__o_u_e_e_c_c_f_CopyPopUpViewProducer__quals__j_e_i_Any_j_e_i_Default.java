package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;

public class Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopUpViewProducer> { public Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CopyPopUpViewProducer.class, "Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CopyPopUpViewProducer.class, Object.class });
  }

  public CopyPopUpViewProducer createInstance(final ContextManager contextManager) {
    final CopyPopUpViewProducer instance = new CopyPopUpViewProducer();
    setIncompleteInstance(instance);
    final ManagedInstance CopyPopUpViewProducer_copyPopUpViewInstance = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { View.class }, new Annotation[] { });
    registerDependentScopedReference(instance, CopyPopUpViewProducer_copyPopUpViewInstance);
    CopyPopUpViewProducer_ManagedInstance_copyPopUpViewInstance(instance, CopyPopUpViewProducer_copyPopUpViewInstance);
    final ManagedInstance CopyPopUpViewProducer_fallbackCopyPopUpViewInstance = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CopyPopUpView.class }, new Annotation[] { new FallbackImplementation() {
        public Class annotationType() {
          return FallbackImplementation.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.FallbackImplementation()";
        }
    } });
    registerDependentScopedReference(instance, CopyPopUpViewProducer_fallbackCopyPopUpViewInstance);
    CopyPopUpViewProducer_ManagedInstance_fallbackCopyPopUpViewInstance(instance, CopyPopUpViewProducer_fallbackCopyPopUpViewInstance);
    setIncompleteInstance(null);
    return instance;
  }

  native static ManagedInstance CopyPopUpViewProducer_ManagedInstance_copyPopUpViewInstance(CopyPopUpViewProducer instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer::copyPopUpViewInstance;
  }-*/;

  native static void CopyPopUpViewProducer_ManagedInstance_copyPopUpViewInstance(CopyPopUpViewProducer instance, ManagedInstance<View> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer::copyPopUpViewInstance = value;
  }-*/;

  native static ManagedInstance CopyPopUpViewProducer_ManagedInstance_fallbackCopyPopUpViewInstance(CopyPopUpViewProducer instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer::fallbackCopyPopUpViewInstance;
  }-*/;

  native static void CopyPopUpViewProducer_ManagedInstance_fallbackCopyPopUpViewInstance(CopyPopUpViewProducer instance, ManagedInstance<CopyPopUpView> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer::fallbackCopyPopUpViewInstance = value;
  }-*/;
}