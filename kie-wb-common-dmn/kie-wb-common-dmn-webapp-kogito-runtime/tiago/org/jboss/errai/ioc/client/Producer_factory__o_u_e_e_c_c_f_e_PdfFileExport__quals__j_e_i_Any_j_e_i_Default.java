package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.exports.FileExport;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportProducer;
import org.uberfire.ext.editor.commons.client.file.exports.PdfFileExport;

public class Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<PdfFileExport> { public Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PdfFileExport.class, "Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PdfFileExport.class, Object.class, FileExport.class });
  }

  public PdfFileExport createInstance(final ContextManager contextManager) {
    FileExportProducer producerInstance = contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final PdfFileExport instance = producerInstance.forPDF();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}