package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;

public class Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorDefinitions> { public Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExpressionEditorDefinitions.class, "Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExpressionEditorDefinitions.class, ArrayList.class, AbstractList.class, AbstractCollection.class, Object.class, Collection.class, Iterable.class, List.class, Cloneable.class, RandomAccess.class, Serializable.class });
  }

  public ExpressionEditorDefinitions createInstance(final ContextManager contextManager) {
    ExpressionEditorDefinitionsProducer producerInstance = contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final ExpressionEditorDefinitions instance = producerInstance.get();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}