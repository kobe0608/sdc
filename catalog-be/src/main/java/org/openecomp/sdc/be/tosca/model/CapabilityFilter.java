/*
 * Copyright © 2016-2018 European Support Limited
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

package org.openecomp.sdc.be.tosca.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;



public class CapabilityFilter {

    List<Map<String, List<Object>>> properties;

    public List<Map<String, List<Object>>> getProperties() {
        return properties;
    }

    public void setProperties(List<Map<String, List<Object>>> properties) {
        this.properties = properties;
    }

    public void addProperty(Map<String, List<Object>> property) {
        if(CollectionUtils.isEmpty(properties)) {
            this.properties = new ArrayList<>();
        }

        this.properties.add(property);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CapabilityFilter{");
        sb.append("properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }
}
