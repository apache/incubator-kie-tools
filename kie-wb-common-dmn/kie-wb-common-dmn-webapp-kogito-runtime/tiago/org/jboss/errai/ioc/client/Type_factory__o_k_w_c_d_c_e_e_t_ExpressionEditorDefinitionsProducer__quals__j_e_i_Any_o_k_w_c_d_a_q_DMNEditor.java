package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;

public class Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<ExpressionEditorDefinitionsProducer> { private class Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl extends ExpressionEditorDefinitionsProducer implements Proxy<ExpressionEditorDefinitionsProducer> {
    private final ProxyHelper<ExpressionEditorDefinitionsProducer> proxyHelper = new ProxyHelperImpl<ExpressionEditorDefinitionsProducer>("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    public void initProxyProperties(final ExpressionEditorDefinitionsProducer instance) {

    }

    public ExpressionEditorDefinitionsProducer asBeanType() {
      return this;
    }

    public void setInstance(final ExpressionEditorDefinitionsProducer instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public ExpressionEditorDefinitions get() {
      if (proxyHelper != null) {
        final ExpressionEditorDefinitionsProducer proxiedInstance = proxyHelper.getInstance(this);
        final ExpressionEditorDefinitions retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ExpressionEditorDefinitionsProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(ExpressionEditorDefinitionsProducer.class, "Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExpressionEditorDefinitionsProducer.class, Object.class, Supplier.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public ExpressionEditorDefinitionsProducer createInstance(final ContextManager contextManager) {
    final Instance<ExpressionEditorDefinition> _expressionEditorDefinitionBeans_0 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ExpressionEditorDefinition.class }, new Annotation[] { });
    final ExpressionEditorDefinitionsProducer instance = new ExpressionEditorDefinitionsProducer(_expressionEditorDefinitionBeans_0);
    registerDependentScopedReference(instance, _expressionEditorDefinitionBeans_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ExpressionEditorDefinitionsProducer> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}