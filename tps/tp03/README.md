File header meaning
first 4 bytes (int):
0 if is not compressed
1 if is compressed by huffman
2 if is compressed by LZW
secound 4 bytes (int):
1 if is b plus tree
2 if is extensible hashing
3 if is external sorting.
third 8 bytes (long):
last movie id
fourth 4 bytes (int):
number of movies