/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.repository.AssetItem;
import org.drools.repository.utils.Validator;

/**
 * This interface indicates that an asset can validate itself, and present errors if requested.
 */
public interface IHasCustomValidator extends Validator
{

    BuilderResult validateAsset(AssetItem asset);

}
