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
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGridEditorDefinitionsProducer;

public class Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor extends Factory<FunctionSupplementaryGridEditorDefinitionsProducer> { private class Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditorProxyImpl extends FunctionSupplementaryGridEditorDefinitionsProducer implements Proxy<FunctionSupplementaryGridEditorDefinitionsProducer> {
    private final ProxyHelper<FunctionSupplementaryGridEditorDefinitionsProducer> proxyHelper = new ProxyHelperImpl<FunctionSupplementaryGridEditorDefinitionsProducer>("Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor");
    public void initProxyProperties(final FunctionSupplementaryGridEditorDefinitionsProducer instance) {

    }

    public FunctionSupplementaryGridEditorDefinitionsProducer asBeanType() {
      return this;
    }

    public void setInstance(final FunctionSupplementaryGridEditorDefinitionsProducer instance) {
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
        final FunctionSupplementaryGridEditorDefinitionsProducer proxiedInstance = proxyHelper.getInstance(this);
        final ExpressionEditorDefinitions retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FunctionSupplementaryGridEditorDefinitionsProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor() {
    super(new FactoryHandleImpl(FunctionSupplementaryGridEditorDefinitionsProducer.class, "Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FunctionSupplementaryGridEditorDefinitionsProducer.class, Object.class, Supplier.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FunctionGridSupplementaryEditor() {
        public Class annotationType() {
          return FunctionGridSupplementaryEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor()";
        }
    } });
  }

  public FunctionSupplementaryGridEditorDefinitionsProducer createInstance(final ContextManager contextManager) {
    final Instance<ExpressionEditorDefinition> _expressionEditorDefinitionBeans_0 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ExpressionEditorDefinition.class }, new Annotation[] { new FunctionGridSupplementaryEditor() {
        public Class annotationType() {
          return FunctionGridSupplementaryEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor()";
        }
    } });
    final FunctionSupplementaryGridEditorDefinitionsProducer instance = new FunctionSupplementaryGridEditorDefinitionsProducer(_expressionEditorDefinitionBeans_0);
    registerDependentScopedReference(instance, _expressionEditorDefinitionBeans_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FunctionSupplementaryGridEditorDefinitionsProducer> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditorProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}