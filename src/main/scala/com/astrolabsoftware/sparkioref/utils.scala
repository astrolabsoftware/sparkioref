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

// Spark
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object Utils {

  /**
    * Compute the time to execute R.
    * R being typically an action.
    *
    * @param text : (String)
    *   Label to be displayed on the screen
    * @param block : (R)
    *   Action to benchmark.
    */
  def time[R](text: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()

    var dt:Double = (t1 - t0).asInstanceOf[Double] / 1000000000.0

    val unit = "S"

    println("\n" + text + "> Elapsed time:" + " " + dt + " " + unit)

    result
  }

  /**
    * Replicate the same dataset numIt times.
    *
    * @param spark : (SparkSession)
    *   The active spark session.
    * @param df : (DataFrame)
    *   The initial DataFrame to replicate
    * @param catalogFilename : (String)
    *   The catalog filename to replicate
    * @param extension : (String)
    *   File extension (csv, fits, parquet, ...)
    * @param options : (Map[String, String])
    *   Options to pass to the DataFrameReader.
    * @param numIt : (Int)
    *   The number of replication
    * @param ind : (Int)
    *   Internal index used for the recursion. Initialised to 0.
    * @return (DataFrame) initial DataFrame plus the replications.
    */
  def replicateDataSet(spark: SparkSession, df: DataFrame,
      catalogFilename: String, extension: String, options: Map[String, String],
      numIt: Int, ind: Int = 0): DataFrame = {
    if (ind == numIt) {
      df
    } else {
      val df2 = spark.read
        .format(extension)
        .options(options)
        .load(catalogFilename)
        .union(df)
      replicateDataSet(spark, df2, catalogFilename, extension, options, numIt, ind + 1)
    }
  }
}
