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

package com.cloudant.sync.replication;

import com.cloudant.common.CouchTestBase;
import com.cloudant.mazha.CouchClient;
import com.cloudant.mazha.CouchConfig;
import com.cloudant.sync.datastore.DatastoreExtended;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.android.encryption.HelperKeyProvider;
import com.cloudant.sync.sqlite.SQLDatabase;
import com.cloudant.sync.util.TestUtils;

import org.junit.After;
import org.junit.Before;

import java.net.URISyntaxException;

public abstract class ReplicationTestBase extends CouchTestBase {

    public String datastoreManagerPath = null;

    protected DatastoreManager datastoreManager = null;
    protected DatastoreExtended datastore = null;
    protected SQLDatabase database = null;
    protected DatastoreWrapper datastoreWrapper = null;

    protected CouchClientWrapper remoteDb = null;
    protected CouchClient couchClient = null;

    private long dbSuffix = System.currentTimeMillis();

    @Before
    public void setUp() throws Exception {
        this.createDatastore();
        this.createRemoteDB();
    }

    @After
    public void tearDown() throws Exception {
        datastore.close();
        TestUtils.deleteDatabaseQuietly(database);
        cleanUpTempFiles();
    }


    private void createDatastore() throws Exception {
        datastoreManagerPath = TestUtils.createTempTestingDir(this.getClass().getName());
        datastoreManager = new DatastoreManager(this.datastoreManagerPath);

        //If SQLCipher parameter is enabled, run all tests with a SQLCipher-based datastore and passphrase
        if(Boolean.valueOf(System.getProperty("test.sqlcipher.passphrase"))) {
            //Open datastore with directory and helper class that provides a test key
            datastore = (DatastoreExtended) datastoreManager.openDatastore(getClass().getSimpleName(), new HelperKeyProvider());
        } else {
            datastore = (DatastoreExtended) datastoreManager.openDatastore(getClass().getSimpleName());
        }
        datastoreWrapper = new DatastoreWrapper(datastore);
    }

    private void createRemoteDB() {
        remoteDb = new CouchClientWrapper(super.getCouchConfig(getDbName()));
        remoteDb.createDatabase();
        couchClient = remoteDb.getCouchClient();
    }

    private void cleanUpTempFiles() {
        TestUtils.deleteTempTestingDir(datastoreManagerPath);
        CouchClientWrapperDbUtils.deleteDbQuietly(remoteDb);
    }

    String getDbName() {
        String dbName = getClass().getSimpleName()+ dbSuffix;
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return dbName.replaceAll(regex, replacement).toLowerCase();
    }

    PullReplication createPullReplication() throws URISyntaxException {
        PullReplication pullReplication = new PullReplication();
        CouchConfig couchConfig = this.getCouchConfig(this.getDbName());
        pullReplication.source = couchConfig.getRootUri();
        pullReplication.target = this.datastore;
        return pullReplication;
    }

    PushReplication createPushReplication() throws URISyntaxException {
        PushReplication pushReplication = new PushReplication();
        CouchConfig couchConfig = this.getCouchConfig(this.getDbName());
        pushReplication.target = couchConfig.getRootUri();
        pushReplication.source = this.datastore;
        return pushReplication;
    }


}
