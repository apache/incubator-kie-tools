package org.kie.workbench.common.forms.data.modeller.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DataObjectFormModelCreationService {

    List<DataObjectFormModel> getAvailableDataObjects(Path path);
}
