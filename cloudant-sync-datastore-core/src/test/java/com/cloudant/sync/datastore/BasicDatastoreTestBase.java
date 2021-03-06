/**
 * Copyright (c) 2013 Cloudant, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.sync.datastore;

import com.cloudant.sync.util.CouchUtils;
import com.cloudant.sync.util.TestUtils;

import org.junit.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

public abstract class BasicDatastoreTestBase extends DatastoreTestBase {


    String documentOneFile = "fixture/document_1.json";
    String documentTwoFile = "fixture/document_2.json";

    byte[] jsonData = null;
    DocumentBody bodyOne = null;
    DocumentBody bodyTwo = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        jsonData = FileUtils.readFileToByteArray(TestUtils.loadFixture(documentOneFile));
        bodyOne = new DocumentBodyImpl(jsonData);

        jsonData = FileUtils.readFileToByteArray(TestUtils.loadFixture(documentTwoFile));
        bodyTwo = new DocumentBodyImpl(jsonData);
    }

    @After
    public void tearDown() throws Exception {
        super.testDown();
    }

    void createTwoDocuments() throws Exception {
        DocumentRevision rev_1Mut = new DocumentRevision();
        rev_1Mut.setBody(bodyOne);
        DocumentRevision rev_1 = datastore.createDocumentFromRevision(rev_1Mut);
        validateNewlyCreatedDocument(rev_1);
        DocumentRevision rev_2Mut = new DocumentRevision();
        rev_2Mut.setBody(bodyTwo);
        DocumentRevision rev_2 = datastore.createDocumentFromRevision(rev_2Mut);
        validateNewlyCreatedDocument(rev_2);
    }

    DocumentRevision[] createThreeDocuments() throws Exception {
        DocumentRevision rev_1Mut = new DocumentRevision();
        rev_1Mut.setBody(bodyOne);
        DocumentRevision rev_1 = datastore.createDocumentFromRevision(rev_1Mut);
        validateNewlyCreatedDocument(rev_1);
        DocumentRevision rev_2Mut = new DocumentRevision();
        rev_2Mut.setBody(bodyTwo);
        DocumentRevision rev_2 = datastore.createDocumentFromRevision(rev_2Mut);
        validateNewlyCreatedDocument(rev_2);
        DocumentRevision rev_3Mut = new DocumentRevision();
        rev_3Mut.setBody(bodyTwo);
        DocumentRevision rev_3 = datastore.createDocumentFromRevision(rev_3Mut);
        validateNewlyCreatedDocument(rev_3);
        DocumentRevision rev_3_a = rev_3;
        rev_3_a.setBody(bodyOne);
        DocumentRevision rev_4 = datastore.updateDocumentFromRevision(rev_3_a);
        Assert.assertNotNull(rev_4);
        return new DocumentRevision[] { rev_1, rev_2, rev_4 };
    }

    void validateNewlyCreatedDocument(DocumentRevision rev) {
        Assert.assertNotNull(rev);
        CouchUtils.validateDocumentId(rev.getId());
        CouchUtils.validateRevisionId(rev.getRevision());
        Assert.assertEquals(1, CouchUtils.generationFromRevId(rev.getRevision()));
        Assert.assertTrue(((DocumentRevision)rev).isCurrent());
        Assert.assertTrue(((DocumentRevision)rev).getParent() == -1L);
    }

    void validateNewlyCreateLocalDocument(DocumentRevision rev) {
        Assert.assertNotNull(rev);
        CouchUtils.validateDocumentId(rev.getId());
        Assert.assertEquals("1-local", rev.getRevision());
        Assert.assertTrue(rev.isCurrent());
    }
}
