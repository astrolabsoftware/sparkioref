#!/bin/bash
# Copyright 2018 Julien Peloton
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

## SBT Version
SBT_VERSION=2.11.8
SBT_VERSION_SPARK=2.11

## Package version
VERSION=0.1.0

packages="com.github.astrolabsoftware:spark-fits_2.11:0.6.0"

# Package it
sbt ++${SBT_VERSION} package

# Parameters
loop=10
ext=fits

for ext in "fits" "csv" "parquet"; do
  # X, 2X, 5X, 10X GB
  for replication in 0 1 4 9; do
    UC_EXT=`echo $ext | awk '{print toupper($0)}'`
    fitsfn="hdfs://134.158.75.222:8020//user/julien.peloton/LSST1Y${UC_EXT}"
    spark-submit \
      --master spark://134.158.75.222:7077 \
      --driver-memory 4g --executor-memory 30g --executor-cores 17 --total-executor-cores 102 \
      --class com.astrolabsoftware.sparkioref.benchmark \
      --packages ${packages} \
      target/scala-${SBT_VERSION_SPARK}/sparkioref_${SBT_VERSION_SPARK}-${VERSION}.jar \
      $fitsfn $replication $loop $ext
    wait
  done
done
