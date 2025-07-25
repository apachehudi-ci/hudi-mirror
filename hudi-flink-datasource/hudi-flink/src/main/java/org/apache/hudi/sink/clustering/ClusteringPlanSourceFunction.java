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

package org.apache.hudi.sink.clustering;

import org.apache.hudi.adapter.AbstractRichFunctionAdapter;
import org.apache.hudi.adapter.SourceFunctionAdapter;
import org.apache.hudi.avro.model.HoodieClusteringGroup;
import org.apache.hudi.avro.model.HoodieClusteringPlan;
import org.apache.hudi.common.model.ClusteringGroupInfo;
import org.apache.hudi.common.model.ClusteringOperation;
import org.apache.hudi.util.StreamerUtil;

import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flink hudi clustering source function.
 *
 * <P>This function read the clustering plan as {@link ClusteringOperation}s then assign the clustering task
 * event {@link ClusteringPlanEvent} to downstream operators.
 *
 * <p>The clustering instant time is specified explicitly with strategies:
 *
 * <ul>
 *   <li>If the timeline has no inflight instants,
 *   use {@link org.apache.hudi.common.table.timeline.HoodieActiveTimeline#createNewInstantTime() as the instant time;</li>
 *   <li>If the timeline has inflight instants,
 *   use the median instant time between [last complete instant time, earliest inflight instant time]
 *   as the instant time.</li>
 * </ul>
 */
public class ClusteringPlanSourceFunction extends AbstractRichFunctionAdapter implements SourceFunctionAdapter<ClusteringPlanEvent> {

  protected static final Logger LOG = LoggerFactory.getLogger(ClusteringPlanSourceFunction.class);

  /**
   * The clustering plan.
   */
  private final HoodieClusteringPlan clusteringPlan;

  /**
   * Clustering instant time.
   */
  private final String clusteringInstantTime;

  private final Configuration conf;

  public ClusteringPlanSourceFunction(String clusteringInstantTime, HoodieClusteringPlan clusteringPlan, Configuration conf) {
    this.clusteringInstantTime = clusteringInstantTime;
    this.clusteringPlan = clusteringPlan;
    this.conf = conf;
  }

  @Override
  public void open(Configuration parameters) throws Exception {
    // no operation
  }

  @Override
  public void run(SourceContext<ClusteringPlanEvent> sourceContext) throws Exception {
    boolean isPending = StreamerUtil.createMetaClient(conf).getActiveTimeline().isPendingClusteringInstant(clusteringInstantTime);
    if (isPending) {
      for (HoodieClusteringGroup clusteringGroup : clusteringPlan.getInputGroups()) {
        LOG.info("Execute clustering plan for instant {} as {} file slices", clusteringInstantTime, clusteringGroup.getSlices().size());
        sourceContext.collect(new ClusteringPlanEvent(this.clusteringInstantTime, ClusteringGroupInfo.create(clusteringGroup), clusteringPlan.getStrategy().getStrategyParams()));
      }
    } else {
      LOG.warn(clusteringInstantTime + " not found in pending clustering instants.");
    }
  }

  @Override
  public void close() throws Exception {
    // no operation
  }

  @Override
  public void cancel() {
    // no operation
  }
}
