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

/**
 * Thrown when there is a conflict updating a datastore, for example trying
 * to update a document but passing in an incorrect revision ID.
 *
 * @api_public
 */
public class ConflictException extends DocumentException {
    public ConflictException(String s) {
        super(s);
    }

    public ConflictException(Exception e){
        super(e);
    }

    public ConflictException(String message, Exception e){
        super(message,e);
    }

    public ConflictException(){

    }
}
