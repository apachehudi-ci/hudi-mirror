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

package org.apache.hudi.metadata;

import org.apache.hudi.common.engine.HoodieEngineContext;
import org.apache.hudi.common.model.HoodieFailedWritesCleaningPolicy;
import org.apache.hudi.common.table.HoodieTableConfig;
import org.apache.hudi.common.table.HoodieTableVersion;
import org.apache.hudi.common.util.Option;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.hudi.storage.StorageConfiguration;

/**
 * Factory class for generating SparkHoodieBackedTableMetadataWriter based on the table version.
 */
public class SparkMetadataWriterFactory {

  public static HoodieTableMetadataWriter create(StorageConfiguration<?> conf, HoodieWriteConfig writeConfig, HoodieEngineContext context,
                                                 Option<String> inflightInstantTimestamp, HoodieTableConfig tableConfig) {
    if (tableConfig.getTableVersion().lesserThan(HoodieTableVersion.EIGHT)) {
      return SparkHoodieBackedTableMetadataWriterTableVersionSix.create(conf, writeConfig, context, inflightInstantTimestamp);
    } else {
      return SparkHoodieBackedTableMetadataWriter.create(conf, writeConfig, context, inflightInstantTimestamp);
    }
  }

  public static HoodieTableMetadataWriter create(StorageConfiguration<?> conf, HoodieWriteConfig writeConfig, HoodieFailedWritesCleaningPolicy failedWritesCleaningPolicy,
                                                 HoodieEngineContext context, Option<String> inflightInstantTimestamp, HoodieTableConfig tableConfig) {
    if (tableConfig.getTableVersion().lesserThan(HoodieTableVersion.EIGHT)) {
      return new SparkHoodieBackedTableMetadataWriterTableVersionSix(conf, writeConfig, failedWritesCleaningPolicy, context, inflightInstantTimestamp);
    } else {
      return new SparkHoodieBackedTableMetadataWriter(conf, writeConfig, failedWritesCleaningPolicy, context, inflightInstantTimestamp);
    }
  }

  public static HoodieTableMetadataWriter createWithStreamingWrites(StorageConfiguration<?> conf, HoodieWriteConfig writeConfig, HoodieFailedWritesCleaningPolicy failedWritesCleaningPolicy,
                                                                    HoodieEngineContext context, Option<String> inflightInstantTimestamp) {
    return new SparkHoodieBackedTableMetadataWriter(conf, writeConfig, failedWritesCleaningPolicy, context, inflightInstantTimestamp, true);
  }

  public static HoodieTableMetadataWriter create(StorageConfiguration<?> conf, HoodieWriteConfig writeConfig, HoodieEngineContext context, HoodieTableConfig tableConfig) {
    return create(conf, writeConfig, context, Option.empty(), tableConfig);
  }
}
