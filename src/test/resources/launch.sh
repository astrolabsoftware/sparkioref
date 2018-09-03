#/bin/bash

npoints=2000

python create_point.py -nrow ${npoints} -coordinates spherical -seed 0 -filename sph_point_${npoints}.fits
python fits2other.py -inputfits sph_point_${npoints}.fits -hdu 1 -out csv
python fits2other.py -inputfits sph_point_${npoints}.fits -hdu 1 -out json
python fits2other.py -inputfits sph_point_${npoints}.fits -hdu 1 -out parquet
