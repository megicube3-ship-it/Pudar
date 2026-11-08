# No Lava Cauldron Light (Fabric 1.21.11, client-only)

Cauldron berisi lava tidak memancarkan cahaya sama sekali di layarmu:
- vanilla: luminance state lava cauldron di-set 0 (client), chunk di-relight
- shader (Iris): lava cauldron dibuang dari tabel block-id shader pack
- tekstur lava di dalam cauldron tetap ada

Build: push ke GitHub, workflow `build` menghasilkan artifact `nolavalight` (ambil jar di dalamnya).
Cek `logs/latest.log`, cari baris `[NoLavaLight]`.
