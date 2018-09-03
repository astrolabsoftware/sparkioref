/*
 * Copyright 2018 Julien Peloton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.astrolabsoftware.sparkioref

import scala.util.Try
import scala.collection.mutable.ListBuffer
import java.time

// Logger info
import org.apache.log4j.Level
import org.apache.log4j.Logger

import com.astrolabsoftware.sparkioref.Utils._

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

object benchmark {

  // Set to Level.WARN is you want verbosity
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  // Initialise your SparkSession
  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  /**
    * Routine to just check the throughput
    */
  def ioBenchmark(df: DataFrame, loop: Int = 1) : (Long, List[Double]) = {
    // Put the data set in-memory
    df.persist(StorageLevel.MEMORY_ONLY_SER)

    var times = new ListBuffer[Double]
    var count : Long = 0

    for (i <- 1 to loop) {
      val result_and_time = timeit("bench", df.count())
      count = result_and_time._1
      times += result_and_time._2
    }

    (count, times.result())
  }

  /**
    * Usage: benchmark <fn: String> <replication: Int> <loop: Int>
    */
  def main(args : Array[String]): Unit = {

    // Input arguments
    val catalogFilename : String = args(0)
    val replication : Int = Try{args(1).toInt}.getOrElse(0)
    val loop : Int = Try{args(2).toInt}.getOrElse(1)

    // File extension
    val extension : String = Try{args(3).toString}.getOrElse(
      catalogFilename.split('.').reverse(0)
    )

    // DataFrameReader options
    val options = extension match {
      case "csv" => Map("header" -> "true", "columns" -> "RA,DEC,Z_COSMO")
      case "fits" => Map("hdu" -> "1", "columns" -> "RA,DEC,Z_COSMO")
      case "json" => Map("header" -> "true", "columns" -> "RA,DEC,Z_COSMO")
      case "parquet" => Map("columns" -> "RA,DEC,Z_COSMO")
      case _ => Map("columns" -> "RA,DEC,Z_COSMO")
    }

    // Initial data + replication if needed.
    val df = spark.read
      .format(extension)
      .options(options)
      .load(catalogFilename)
    val df_tot = replicateDataSet(spark, df, catalogFilename, extension, options, replication)

    // Select Ra, Dec, Z only
    val df_index = df_tot.select($"RA", $"Dec", ($"Z_COSMO"))

    // Benchmark
    val count_and_times = ioBenchmark(df_index, loop)

    // Create a DataFrame with the recorded times.
    val schema = StructType(
      StructField("Times for " + extension, DoubleType, true) ::
      StructField("Count", LongType, true) ::
      Nil)

    val rdd = spark.sparkContext.parallelize(
      count_and_times._2.zip(
        Stream.continually(count_and_times._1)
      )
    )

    val df_times = spark.createDataFrame(
      rdd.map(x => Row(x._1, x._2)),
      schema
    )

    // Show the result for visual inspection
    df_times.show()

    // Save the result as CSV
    val now : String = time.Instant.now.toString.replace(".", "_").replace(":", "_").toString
    df_times
      .coalesce(1)
      .write.format("csv")
      .option("header", true)
      .save(now + "_" + extension)
  }
}
