package org.openecomp.sdc.be.dao.cassandra.schema.tables;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import com.datastax.driver.core.DataType;


public class UserAccessEventTableDescriptionTest {

	private UserAccessEventTableDescription createTestSubject() {
		return new UserAccessEventTableDescription();
	}

	
	@Test
	public void testPrimaryKeys() throws Exception {
		UserAccessEventTableDescription testSubject;
		List<ImmutablePair<String, DataType>> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.primaryKeys();
	}

	
	@Test
	public void testClusteringKeys() throws Exception {
		UserAccessEventTableDescription testSubject;
		List<ImmutablePair<String, DataType>> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.clusteringKeys();
	}

	
	@Test
	public void testGetColumnDescription() throws Exception {
		UserAccessEventTableDescription testSubject;
		Map<String, ImmutablePair<DataType, Boolean>> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getColumnDescription();
	}

	
	@Test
	public void testGetKeyspace() throws Exception {
		UserAccessEventTableDescription testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getKeyspace();
	}

	
	@Test
	public void testGetTableName() throws Exception {
		UserAccessEventTableDescription testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getTableName();
	}
}