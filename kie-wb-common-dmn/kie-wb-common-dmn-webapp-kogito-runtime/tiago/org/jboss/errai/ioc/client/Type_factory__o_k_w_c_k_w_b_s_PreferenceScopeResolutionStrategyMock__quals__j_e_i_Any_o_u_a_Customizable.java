package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeResolutionStrategyMock;
import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;

public class Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable extends Factory<PreferenceScopeResolutionStrategyMock> { public Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable() {
    super(new FactoryHandleImpl(PreferenceScopeResolutionStrategyMock.class, "Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeResolutionStrategyMock.class, Object.class, PreferenceScopeResolutionStrategy.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Customizable() {
        public Class annotationType() {
          return Customizable.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.Customizable()";
        }
    } });
  }

  public PreferenceScopeResolutionStrategyMock createInstance(final ContextManager contextManager) {
    final PreferenceScopeResolutionStrategyMock instance = new PreferenceScopeResolutionStrategyMock();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}