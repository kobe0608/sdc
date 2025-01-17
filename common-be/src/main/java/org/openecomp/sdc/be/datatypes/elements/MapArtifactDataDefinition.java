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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MapArtifactDataDefinition extends MapDataDefinition<ArtifactDataDefinition> {

    private String parentName;

    public MapArtifactDataDefinition(MapDataDefinition cdt, String parentName) {
        super(cdt);
        this.parentName = parentName;
    }

    @JsonCreator
    public MapArtifactDataDefinition(Map<String, ArtifactDataDefinition> mapToscaDataDefinition) {
        super(mapToscaDataDefinition);
    }

    @JsonValue
    @Override
    public Map<String, ArtifactDataDefinition> getMapToscaDataDefinition() {
        return mapToscaDataDefinition;
    }


    public void setMapToscaDataDefinition(Map<String, ArtifactDataDefinition> mapToscaDataDefinition) {
        this.mapToscaDataDefinition = mapToscaDataDefinition;
    }

}
