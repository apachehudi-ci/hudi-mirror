/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.util;

import org.apache.hudi.client.common.HoodieFlinkEngineContext;
import org.apache.hudi.common.engine.HoodieEngineContext;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.hudi.hadoop.fs.HadoopFSUtils;
import org.apache.hudi.table.HoodieFlinkTable;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

import static org.apache.hudi.configuration.HadoopConfigurations.getHadoopConf;

/**
 * Utilities for {@link org.apache.hudi.table.HoodieFlinkTable}.
 */
public class FlinkTables {
  private FlinkTables() {
  }

  private static HoodieFlinkTable<?> createTableInternal(HoodieWriteConfig writeConfig, HoodieEngineContext context) {
    HoodieFlinkTable<?> table = HoodieFlinkTable.create(writeConfig, context);
    CommonClientUtils.validateTableVersion(table.getMetaClient().getTableConfig(), writeConfig);
    return table;
  }

  /**
   * Creates the hoodie flink table.
   *
   * <p>This expects to be used by client.
   */
  public static HoodieFlinkTable<?> createTable(Configuration conf, RuntimeContext runtimeContext) {
    HoodieFlinkEngineContext context = new HoodieFlinkEngineContext(
        HadoopFSUtils.getStorageConf(getHadoopConf(conf)),
        new FlinkTaskContextSupplier(runtimeContext));
    HoodieWriteConfig writeConfig = FlinkWriteClients.getHoodieClientConfig(conf, true);
    return createTableInternal(writeConfig, context);
  }

  /**
   * Creates the hoodie flink table.
   *
   * <p>This expects to be used by client.
   */
  public static HoodieFlinkTable<?> createTable(
      HoodieWriteConfig writeConfig,
      org.apache.hadoop.conf.Configuration hadoopConf,
      RuntimeContext runtimeContext) {
    HoodieFlinkEngineContext context = new HoodieFlinkEngineContext(
        HadoopFSUtils.getStorageConfWithCopy(hadoopConf),
        new FlinkTaskContextSupplier(runtimeContext));
    return createTableInternal(writeConfig, context);
  }

  /**
   * Creates the hoodie flink table.
   *
   * <p>This expects to be used by driver.
   */
  public static HoodieFlinkTable<?> createTable(Configuration conf) {
    HoodieWriteConfig writeConfig = FlinkWriteClients.getHoodieClientConfig(conf, true, false);
    return createTableInternal(writeConfig, HoodieFlinkEngineContext.DEFAULT);
  }
}
