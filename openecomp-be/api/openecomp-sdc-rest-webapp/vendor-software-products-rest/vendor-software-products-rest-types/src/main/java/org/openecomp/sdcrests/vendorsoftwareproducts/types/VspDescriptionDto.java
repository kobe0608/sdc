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

package org.openecomp.sdcrests.vendorsoftwareproducts.types;

import lombok.Data;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.LicenseType;
import org.openecomp.sdc.vendorsoftwareproduct.types.LicensingData;

import javax.validation.constraints.NotNull;

@Data
public class VspDescriptionDto {
    @NotNull
    private String name;
    @NotNull
    private String description;
    private String icon;
    @NotNull
    private String category;
    @NotNull
    private String subCategory;
    @NotNull
    private String vendorName;
    @NotNull
    private String vendorId;            // this will be populated with vlm id
    private String licensingVersion;    // this will be populated with vlm version
    private LicenseType licenseType;
    private LicensingData licensingData;
}
