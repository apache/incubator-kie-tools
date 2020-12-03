package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.exports.AbstractFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.FileExport;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportProducer;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;

public class Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgFileExport> { public Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SvgFileExport.class, "Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SvgFileExport.class, AbstractFileExport.class, Object.class, FileExport.class });
  }

  public SvgFileExport createInstance(final ContextManager contextManager) {
    FileExportProducer producerInstance = contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final SvgFileExport instance = producerInstance.forSvg();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}