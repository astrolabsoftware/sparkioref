# Copyright 2018 Julien Peloton
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
import pandas as pd
import pylab as pl
import sys

import argparse

def addargs(parser):
    """ Parse command line arguments for visualisation.py """

    ## Arguments
    parser.add_argument(
        '-inputCSV', dest='inputCSV',
        required=True,
        nargs='+',
        help='Path to one or several CSV files')


if __name__ == "__main__":
    """
    Histogram the benchmark
    """
    parser = argparse.ArgumentParser(
        description="""
        Histogram the benchmark
        """)
    addargs(parser)
    args = parser.parse_args(None)

    colors = {"fits": "C0", "csv": "C1", "parquet": "C2"}

    # Initialisation
    df = pd.read_csv(args.inputCSV[0])
    extension = df.axes[1][0].split(" ")[-1]

    mean = df[1:].mean()["Times for {}".format(extension)]
    ax = df[1:].hist(
        "Times for {}".format(extension),
        label=extension + " ({:.3f} s)".format(mean))
    pl.axvline(mean, color=colors[extension], ls='--')

    # If several files
    for index, file in enumerate(args.inputCSV[1:]):
        df_other = pd.read_csv(file)
        assert(df_other["Count"][0] == df["Count"][0])

        extension = df_other.axes[1][0].split(" ")[-1]
        mean = df_other[1:].mean()["Times for {}".format(extension)]

        df_other[1:].hist(
            "Times for {}".format(extension),
            ax=ax, label=extension + " ({:.3f} s)".format(mean))
        pl.axvline(mean, color=colors[extension], ls='--')

    pl.legend()
    pl.title("Benchmark")
    pl.xlabel("time (second)")
    pl.savefig("benchmark.pdf")
    pl.show()
