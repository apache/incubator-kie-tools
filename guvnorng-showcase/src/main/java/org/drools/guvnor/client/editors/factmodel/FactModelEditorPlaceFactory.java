/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.editors.factmodel;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.mvp.IPlaceRequestFactory;
import org.drools.guvnor.client.workbench.annotations.SupportedFormat;
import org.drools.guvnor.vfs.Path;

/**
 * 
 */
@ApplicationScoped
@SupportedFormat("model.drl")
public class FactModelEditorPlaceFactory
    implements
    IPlaceRequestFactory<FactModelEditorPlace> {

    private static final String               FACTORY_NAME = "FactModelEditorPlace";

    private static final FactModelEditorPlace PLACE        = new FactModelEditorPlace();

    @Override
    public String getFactoryName() {
        return FACTORY_NAME;
    }

    @Override
    public FactModelEditorPlace makePlace(Path path) {
        final FactModelEditorPlace place = new FactModelEditorPlace();
        place.addParameter( "path",
                            path.toURI() );
        return place;
    }

    @Override
    public FactModelEditorPlace makePlace() {
        return PLACE;
    }

}
