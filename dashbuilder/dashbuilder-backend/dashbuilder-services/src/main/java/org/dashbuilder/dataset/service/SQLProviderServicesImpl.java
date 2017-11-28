/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.service;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.sql.SQLDataSourceLocator;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.jboss.errai.bus.server.annotations.Service;

@ApplicationScoped
@Service
public class SQLProviderServicesImpl implements SQLProviderServices {

    SQLDataSourceLocator sqlDataSourceLocator;

    public SQLProviderServicesImpl() {
    }

    @Inject
    public SQLProviderServicesImpl(SQLDataSourceLocator sqlDataSourceLocator) {
        this.sqlDataSourceLocator = sqlDataSourceLocator;
    }

    @Override
    public List<SQLDataSourceDef> getDataSourceDefs() {
        return sqlDataSourceLocator.list();
    }
}
