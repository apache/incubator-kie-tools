package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.menu.common.CurrentBranchProducer;
import org.uberfire.ext.editor.commons.version.CurrentBranch;

public class Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CurrentBranchProducer> { public Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CurrentBranchProducer.class, "Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CurrentBranchProducer.class, Object.class });
  }

  public CurrentBranchProducer createInstance(final ContextManager contextManager) {
    final CurrentBranchProducer instance = new CurrentBranchProducer();
    setIncompleteInstance(instance);
    final Instance CurrentBranchProducer_currentBranch = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { CurrentBranch.class }, new Annotation[] { });
    registerDependentScopedReference(instance, CurrentBranchProducer_currentBranch);
    CurrentBranchProducer_Instance_currentBranch(instance, CurrentBranchProducer_currentBranch);
    setIncompleteInstance(null);
    return instance;
  }

  native static Instance CurrentBranchProducer_Instance_currentBranch(CurrentBranchProducer instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.menu.common.CurrentBranchProducer::currentBranch;
  }-*/;

  native static void CurrentBranchProducer_Instance_currentBranch(CurrentBranchProducer instance, Instance<CurrentBranch> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.common.CurrentBranchProducer::currentBranch = value;
  }-*/;
}