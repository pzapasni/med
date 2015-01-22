""" Skrypt konwertuje pliki tekstowe z polami oddzielonymi tabulatorami to CSV z polami oddzielonymi ';' i obcina ostatnią kolumnę
    Uruchamianie: python data_converter.py file1.txt file2.txt...
"""
import os
import sys

for arg in sys.argv[1:]:
    lines = open(arg).readlines()
    outname = os.path.splitext(arg)[0] + '.csv'
    outfile = open(outname, 'w')
    for line in lines:
        outline = ';'.join(line.split('\t')[:-1])
        outfile.write(outline + '\n')
    outfile.close()