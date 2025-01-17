/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openecomp.sdc.be.datatypes.tosca.ToscaDataDefinition;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AnnotationTypeDataDefinition extends ToscaDataDefinition {

    protected String uniqueId;

    @ToString.Exclude
    protected String type;

    protected String description;

    protected Long creationTime;
    protected Long modificationTime;

    protected String version;
    protected boolean highestVersion;

    public AnnotationTypeDataDefinition(AnnotationTypeDataDefinition other) {
        uniqueId = other.uniqueId;
        type = other.type;
        version = other.version;
        description = other.description;
        creationTime = other.creationTime;
        modificationTime = other.modificationTime;
        highestVersion = other.highestVersion;
    }

}
