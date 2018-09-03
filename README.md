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
- Input dataset: 370,000,000 objects (x, y, z)
- 153 cores (9 executors), 300 GB RAM total
- No cache: 10 iterations
- Data in memory: 100 iterations (data read and put in cache at the 1st iteration)

<p align="center"><img width="800" src="https://github.com/astrolabsoftware/sparkioref/raw/master/pic/benchmark_370million_nocache.png"/>
<img width="800" src="https://github.com/astrolabsoftware/sparkioref/raw/master/pic/benchmark_370million.png"/>
</p>
