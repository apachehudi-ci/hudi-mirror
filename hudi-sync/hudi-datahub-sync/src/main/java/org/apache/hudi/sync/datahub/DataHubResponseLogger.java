/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.hudi.sync.datahub;

import datahub.client.Callback;
import datahub.client.MetadataWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle responses to requests to Datahub Metastore. Just logs them.
 */
public class DataHubResponseLogger implements Callback {
  private static final Logger LOG = LoggerFactory.getLogger(DataHubResponseLogger.class);

  @Override
  public void onCompletion(MetadataWriteResponse response) {
    LOG.info("Completed DataHub RestEmitter request. Status: {}", (response.isSuccess() ? " succeeded" : " failed"));
    if (!response.isSuccess()) {
      LOG.error("Request failed. {}", response);
    }
    LOG.debug("Response details: {}", response);
  }

  @Override
  public void onFailure(Throwable e) {
    LOG.error("Error during Datahub RestEmitter request", e);
  }

}
