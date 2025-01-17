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
package org.openecomp.sdc.be.datatypes.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.Serializable;
import org.openecomp.sdc.be.datatypes.enums.JsonPresentationFields;
import org.openecomp.sdc.be.datatypes.tosca.ToscaDataDefinition;

public class CINodeFilterDataDefinition extends ToscaDataDefinition implements Serializable {

    @JsonCreator
    public CINodeFilterDataDefinition() {
    }

    public CINodeFilterDataDefinition(CINodeFilterDataDefinition inData) {
        super();
        this.setID(inData.getID());
        this.setName(inData.getName());
        this.setTosca_id(inData.getTosca_id());
        this.setProperties(inData.getProperties());
        this.setCapabilities(inData.getCapabilities());
    }

    public Object getTosca_id() {
        return getToscaPresentationValue(JsonPresentationFields.TOSCA_ID);
    }

    public void setTosca_id(Object tosca_id) {
        setToscaPresentationValue(JsonPresentationFields.TOSCA_ID, tosca_id);
    }

    public ListDataDefinition<RequirementNodeFilterPropertyDataDefinition> getProperties() {
        return (ListDataDefinition<RequirementNodeFilterPropertyDataDefinition>) getToscaPresentationValue(
            JsonPresentationFields.PROPERTIES);
    }

    public void setProperties(ListDataDefinition<RequirementNodeFilterPropertyDataDefinition> properties) {
        setToscaPresentationValue(JsonPresentationFields.PROPERTIES, properties);
    }

    public ListDataDefinition<RequirementNodeFilterCapabilityDataDefinition> getCapabilities() {
        return (ListDataDefinition<RequirementNodeFilterCapabilityDataDefinition>) getToscaPresentationValue(
            JsonPresentationFields.NODE_FILTER_CAPABILITIES);
    }

    public void setCapabilities(ListDataDefinition<RequirementNodeFilterCapabilityDataDefinition> capabilities) {
        setToscaPresentationValue(JsonPresentationFields.NODE_FILTER_CAPABILITIES, capabilities);
    }

    public String getID() {
        return (String) getToscaPresentationValue(JsonPresentationFields.UNIQUE_ID);
    }

    public void setID(String name) {
        setToscaPresentationValue(JsonPresentationFields.UNIQUE_ID, name);
    }

}
