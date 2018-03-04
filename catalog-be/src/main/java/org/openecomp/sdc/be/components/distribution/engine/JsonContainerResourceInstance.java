/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.be.components.distribution.engine;

import org.openecomp.sdc.be.model.ComponentInstance;

import java.util.List;

public class JsonContainerResourceInstance {
    private String resourceInstanceName;
    private String resourceName;
    private String resourceVersion;
    private String resourceType;
    private String resourceUUID;
    private String resourceInvariantUUID;
    private String resourceCustomizationUUID;
    private String category;
    private String subcategory;
    private List<ArtifactInfoImpl> artifacts;

    public JsonContainerResourceInstance(ComponentInstance resourceInstance, String resourceType, List<ArtifactInfoImpl> artifacts) {
        super();
        this.resourceInstanceName = resourceInstance.getName();
        this.resourceName = resourceInstance.getComponentName();
        this.resourceVersion = resourceInstance.getComponentVersion();
        this.resourceType = resourceType;
        this.resourceUUID = resourceInstance.getComponentUid();
        this.artifacts = artifacts;
        this.resourceCustomizationUUID = resourceInstance.getCustomizationUUID();
    }
    
    public JsonContainerResourceInstance(ComponentInstance resourceInstance, List<ArtifactInfoImpl> artifacts) {
        super();
        this.resourceInstanceName = resourceInstance.getName();
        this.resourceName = resourceInstance.getComponentName();
        this.resourceVersion = resourceInstance.getComponentVersion();
        if(resourceInstance.getOriginType() != null)
            this.resourceType = resourceInstance.getOriginType().getValue();
        this.resourceUUID = resourceInstance.getComponentUid();
        this.artifacts = artifacts;
        this.resourceCustomizationUUID = resourceInstance.getCustomizationUUID();
    }

    public String getResourceInstanceName() {
        return resourceInstanceName;
    }

    public void setResourceInstanceName(String resourceInstanceName) {
        this.resourceInstanceName = resourceInstanceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public String getResoucreType() {
        return resourceType;
    }

    public void setResoucreType(String resoucreType) {
        this.resourceType = resoucreType;
    }

    public String getResourceUUID() {
        return resourceUUID;
    }

    public void setResourceUUID(String resourceUUID) {
        this.resourceUUID = resourceUUID;
    }

    public List<ArtifactInfoImpl> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactInfoImpl> artifacts) {
        this.artifacts = artifacts;
    }

    public String getResourceInvariantUUID() {
        return resourceInvariantUUID;
    }

    public void setResourceInvariantUUID(String resourceInvariantUUID) {
        this.resourceInvariantUUID = resourceInvariantUUID;
    }

    public String getResourceCustomizationUUID() {
        return resourceCustomizationUUID;
    }

    public void setResourceCustomizationUUID(String customizationUUID) {
        this.resourceCustomizationUUID = customizationUUID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    @Override
    public String toString() {
        return "JsonContainerResourceInstance [resourceInstanceName=" + resourceInstanceName + ", resourceName=" + resourceName + ", resourceVersion=" + resourceVersion + ", resoucreType=" + resourceType + ", resourceUUID=" + resourceUUID
                + ", resourceInvariantUUID=" + resourceInvariantUUID + ", resourceCustomizationUUID=" + resourceCustomizationUUID + ", category=" + category + ", subcategory=" + subcategory + ", artifacts=" + artifacts + "]";
    }

}
