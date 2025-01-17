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

package org.openecomp.sdc.be.datatypes.elements;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PolicyTypeDataDefinition extends ToscaTypeDataDefinition {

    private String uniqueId;
    private String version;
    private String derivedFrom;
    private List<String> targets;
    private Map<String, String> metadata;
    private String description;

    /**
     * Timestamp of data type creation
     */
    private Long creationTime;

    /**
     * Timestamp of the data type last update
     */
    private Long modificationTime;
    private boolean highestVersion;

    public PolicyTypeDataDefinition(PolicyTypeDataDefinition p) {
        super(p);
        this.uniqueId = p.uniqueId;
        this.version = p.version;
        this.targets = p.targets;
        this.metadata = p.metadata;
        this.derivedFrom = p.derivedFrom;
        this.description = p.description;
        this.creationTime = p.creationTime;
        this.modificationTime = p.modificationTime;
        this.highestVersion = p.highestVersion;
    }

    @Override
    public String toString() {
        return "PolicyTypeDataDefinition [uniqueId=" + uniqueId + ", type=" + getType() + ", name=" + getName() + ", icon=" + getIcon() + ", version="
            + version
            + ", targets=" + targets + ", metadata=" + metadata + ", description=" + description + ", creationTime="
            + creationTime + ", modificationTime=" + modificationTime + "]";
    }

}
