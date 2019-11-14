/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.imported;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

@Dependent
public class ImportDataObjectModal extends Elemental2Modal<ImportDataObjectModal.View> {

    private final DMNClientServicesProxy client;

    private Consumer<List<DataObject>> dataObjectsConsumer;

    @Inject
    public ImportDataObjectModal(final View view,
                                 final DMNClientServicesProxy client) {
        super(view);
        this.client = client;
    }

    public Consumer<List<DataObject>> getDataObjectsConsumer() {
        return dataObjectsConsumer;
    }

    public void setup(final Consumer<List<DataObject>> dataObjectsConsumer) {
        this.dataObjectsConsumer = dataObjectsConsumer;
        callSuperSetup();
    }

    void callSuperSetup(){
        super.setup();
    }

    public void hide(final List<DataObject> importedDataObjects) {

        if (!Objects.isNull(getDataObjectsConsumer())) {
            getDataObjectsConsumer().accept(importedDataObjects);
        }
        superHide();
    }

    void superHide() {
        super.hide();
    }

    @Override
    public void show() {
        client.loadDataObjects(wrap(getConsumer()));
    }

    ServiceCallback<List<DataObject>> wrap(final Consumer<List<DataObject>> consumer) {
        return new ServiceCallback<List<DataObject>>() {

            @Override
            public void onSuccess(final List<DataObject> items) {
                consumer.accept(items);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                // do nothing.
            }
        };
    }

    Consumer<List<DataObject>> getConsumer() {
        return objects -> {
            getView().clear();
            getView().addItems(objects);
            superShow();
        };
    }

    void superShow() {
        super.show();
    }

    public interface View extends Elemental2Modal.View<ImportDataObjectModal> {

        void addItems(final List<DataObject> dataObjects);

        void clear();
    }
}
