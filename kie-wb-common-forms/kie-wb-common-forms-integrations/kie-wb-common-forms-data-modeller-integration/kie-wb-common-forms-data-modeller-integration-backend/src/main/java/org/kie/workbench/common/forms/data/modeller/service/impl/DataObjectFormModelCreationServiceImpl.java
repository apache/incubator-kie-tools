package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFormModelCreationService;
import org.uberfire.backend.vfs.Path;

@Service
@Dependent
public class DataObjectFormModelCreationServiceImpl implements DataObjectFormModelCreationService {

    private DataObjectFinderService finderService;

    private DataObjectFormModelHandler formModelHandler;

    @Inject
    public DataObjectFormModelCreationServiceImpl(DataObjectFinderService finderService,
                                                  DataObjectFormModelHandler formModelHandler) {
        this.finderService = finderService;
        this.formModelHandler = formModelHandler;
    }

    @Override
    public List<DataObjectFormModel> getAvailableDataObjects(final Path path) {
        return finderService.getProjectDataObjects(path).stream().map(dataObject -> formModelHandler.createFormModel(dataObject, path)).collect(Collectors.toList());
    }
}
