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

import org.junit.Test;

import java.util.List;


public class ProductMetadataDataDefinitionTest {

	private ProductMetadataDataDefinition createTestSubject() {
		return new ProductMetadataDataDefinition();
	}

	@Test
	public void testAddContact() throws Exception {
		ProductMetadataDataDefinition testSubject;
		String contact = "";

		// test 1
		testSubject = createTestSubject();
		contact = null;
		testSubject.addContact(contact);

		// test 2
		testSubject = createTestSubject();
		contact = "";
		testSubject.addContact(contact);
	}

	@Test
	public void testGetActualComponentType() throws Exception {
		ProductMetadataDataDefinition testSubject;
		Object obj = null;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getActualComponentType();
	}
}
