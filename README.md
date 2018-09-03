# sparkioref

Benchmark the IO performance of Apache Spark (Scala/Python).
Currently supported: csv/json, parquet, [FITS](https://github.com/astrolabsoftware/spark-fits).

## Run the benchmark

Edit the `run_benchmark.sh` file with your data and cluster configuration, and launch it using

```bash
./run_benchmark.sh
```

## Example

Configuration:
- Spark 2.3.1
- HDFS 2.8.4
- Input dataset: N objects (x, y, z)
- 102 cores (6 executors), 200 GB RAM total

_To come_
