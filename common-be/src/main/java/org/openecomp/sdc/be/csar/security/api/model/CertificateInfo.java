/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation
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

package org.openecomp.sdc.be.csar.security.api.model;

import java.io.File;
import java.security.Key;
import java.security.cert.Certificate;

public interface CertificateInfo {

    String getName();

    File getCertificateFile();

    Certificate getCertificate();

    File getPrivateKeyFile();

    Key getPrivateKey();

    /**
     * Check if the certificate is valid.
     *
     * @return {@code true} if the certificate is valid. {@code false} otherwise.
     * @throws UnsupportedOperationException when the certificate is not supported
     */
    boolean isValid();

}
