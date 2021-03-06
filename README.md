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
- Input dataset: 1,100,000,000 objects (x, y, z)
- 153 cores (9 executors), 300 GB RAM total
- No cache: 100 iterations (data distributed and read)

<p align="center"><img width="800" src="https://github.com/astrolabsoftware/sparkioref/raw/master/pic/benchmark_nocache_billion.png"/>
</p>
