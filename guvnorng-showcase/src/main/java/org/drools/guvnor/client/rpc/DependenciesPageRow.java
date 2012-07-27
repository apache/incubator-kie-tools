/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.rpc;

/**
 * A single row of Dependencies
 * 
 */
public class DependenciesPageRow extends AbstractPageRow {

    private String dependencyPath;
	private String dependencyVersion;
	
	
    public String getDependencyPath() {
		return dependencyPath;
	}
	public void setDependencyPath(String dependencyPath) {
		this.dependencyPath = dependencyPath;
	}
	public String getDependencyVersion() {
		return dependencyVersion;
	}
	public void setDependencyVersion(String dependencyVersion) {
		this.dependencyVersion = dependencyVersion;
	}
}
