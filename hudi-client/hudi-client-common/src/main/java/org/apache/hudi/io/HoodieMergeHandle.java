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

package org.apache.hudi.io;

import org.apache.hudi.ApiMaturityLevel;
import org.apache.hudi.PublicAPIClass;
import org.apache.hudi.PublicAPIMethod;
import org.apache.hudi.client.WriteStatus;
import org.apache.hudi.common.model.HoodieBaseFile;
import org.apache.hudi.common.util.Option;
import org.apache.hudi.storage.StoragePath;

import org.apache.avro.Schema;

import java.io.IOException;
import java.util.List;

@PublicAPIClass(maturity = ApiMaturityLevel.EVOLVING)
public interface HoodieMergeHandle<T, I, K, O> {

  /**
   * Called to read the base file, the incoming records, merge the records and write the final base file.
   * @throws IOException
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  void doMerge() throws IOException;

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  HoodieBaseFile baseFileForMerge();

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  void setPartitionFields(Option<String[]> partitionFields);

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  StoragePath getOldFilePath();

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  String getPartitionPath();

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  Schema getWriterSchema();

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  void setPartitionValues(Object[] partitionValues);

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  List<WriteStatus> getWriteStatuses();

  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  List<WriteStatus> close();
}
