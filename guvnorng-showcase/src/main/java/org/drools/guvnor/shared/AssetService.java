/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.shared;


import org.drools.guvnor.shared.common.vo.assets.factmodel.FactModels;
import org.drools.guvnor.vfs.Path;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface AssetService {
    //<V> V loadAsset(Path path, Class<V> type);
    FactModels loadAsset(Path path);
}
