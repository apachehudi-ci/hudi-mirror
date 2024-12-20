#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

$CONFLUENT_HOME/bin/connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties &
sleep 30
curl -X POST -H "Content-Type:application/json" -d @/opt/bundle-validation/kafka/config-sink.json http://localhost:8083/connectors &
sleep 30
curl -X DELETE http://localhost:8083/connectors/hudi-sink &
sleep 10

# validate
numCommits=$(ls /tmp/hudi-kafka-test/.hoodie/timeline/*.commit | wc -l)
if [ $numCommits -gt 0 ]; then
  exit 0
else
  exit 1
fi
