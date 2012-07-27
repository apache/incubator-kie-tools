/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.rpc;

import java.io.Serializable;

/**
 * This class contains all the parameters passed in the invocation of the
 * standalone editor. 
 * This object is used to pass those parameters from server to client side.
 */
public class StandaloneEditorInvocationParameters implements Serializable {
    
    static final long serialVersionUID = 530L;
    
    private Asset[] assetsToBeEdited;
    
    private Asset[] activeWorkingSets;
    private Asset[] activeTemporalWorkingSets;

    private boolean temporalAssets;
    
    private boolean hideLHS;
    private boolean hideRHS;
    private boolean hideAttributes;
    private String clientName;

    public StandaloneEditorInvocationParameters() {
    }

    public Asset[] getAssetsToBeEdited() {
        return assetsToBeEdited;
    }

    public void setAssetsToBeEdited(Asset[] assetsToBeEdited) {
        this.assetsToBeEdited = assetsToBeEdited;
    }

    public boolean isHideAttributes() {
        return hideAttributes;
    }

    public void setHideAttributes(boolean hideAttributes) {
        this.hideAttributes = hideAttributes;
    }

    public boolean isHideLHS() {
        return hideLHS;
    }

    public void setHideLHS(boolean hideLHS) {
        this.hideLHS = hideLHS;
    }

    public boolean isHideRHS() {
        return hideRHS;
    }

    public void setHideRHS(boolean hideRHS) {
        this.hideRHS = hideRHS;
    }

    public boolean isTemporalAssets() {
        return temporalAssets;
    }

    public void setTemporalAssets(boolean temporalAssets) {
        this.temporalAssets = temporalAssets;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Asset[] getActiveWorkingSets() {
        return activeWorkingSets;
    }

    public void setActiveWorkingSets(Asset[] activeWorkingSets) {
        this.activeWorkingSets = activeWorkingSets;
    }

    public Asset[] getActiveTemporalWorkingSets() {
        return activeTemporalWorkingSets;
    }

    public void setActiveTemporalWorkingSets(Asset[] activeTemporalWorkingSets) {
        this.activeTemporalWorkingSets = activeTemporalWorkingSets;
    }
    
}
