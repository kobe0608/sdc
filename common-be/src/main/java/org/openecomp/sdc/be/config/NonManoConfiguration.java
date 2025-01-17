/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.be.config;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Data;

import java.util.Map;
import org.apache.commons.collections.MapUtils;

/**
 * Represents the non-mano configuration yaml.
 */
@Data
public class NonManoConfiguration {
    private Map<String, NonManoFolderType> nonManoKeyFolderMapping;

    /**
     * Gets the non mano folder type based on the non mano artifact type.
     * @param nonManoArtifactType the artifact type
     * @return
     *  The NonManoType for the artifact type
     */
    public NonManoFolderType getNonManoType(final NonManoArtifactType nonManoArtifactType) {
        return nonManoKeyFolderMapping.get(nonManoArtifactType.getType());
    }

    public Map<String, NonManoFolderType> getNonManoKeyFolderMapping() {
        if (MapUtils.isEmpty(nonManoKeyFolderMapping)) {
            return Collections.emptyMap();
        }

        return nonManoKeyFolderMapping.entrySet().stream()
            .filter(entry -> entry.getValue().isValid())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
