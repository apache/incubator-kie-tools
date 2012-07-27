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

package org.drools.guvnor.server.selector;

import org.drools.*;
import org.drools.compiler.RuleBaseLoader;
import org.drools.repository.AssetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This uses rules to decide if an asset is to be included in a build.
 */
public class RuleBasedSelector implements AssetSelector {

    private static final Logger log = LoggerFactory.getLogger(RuleBasedSelector.class);


    final String ruleFile;
    private RuleBase ruleBase;

    public RuleBasedSelector(String val) {
        this.ruleFile = val;

        InputStream ins = this.getClass().getResourceAsStream(ruleFile);
        InputStreamReader reader = new InputStreamReader(ins);


        try {
            this.ruleBase = RuleBaseLoader.getInstance().loadFromReader(reader);
        } catch (CheckedDroolsException e) {
            log.error("Unable to load rule base.", e);
            throw new RuntimeDroolsException(e);
        } catch (IOException e) {
            log.error("Unable to load rule base.", e);
            throw new RuntimeDroolsException(e);
        }

    }

    public boolean isAssetAllowed(AssetItem asset) {
        return evalRules(asset);
    }

    @SuppressWarnings("rawtypes")
    boolean evalRules(Object asset) {
        StatelessSession session = ruleBase.newStatelessSession();
        StatelessSessionResult result = session.executeWithResults(asset);

        java.util.Iterator objects = result.iterateObjects();
        while (objects.hasNext()) {
            if (objects.next() instanceof Allow) {
                return true;
            }
        }
        return false;
    }

}
