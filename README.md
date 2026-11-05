# Play Sound Keybind (Fabric, Minecraft 1.21.11)

Mod client-side: tekan 1 tombol (default **K**) -> memutar 1 suara custom, hanya kamu yang dengar.
Tidak perlu dipasang di server.

## Build lewat GitHub Actions
1. Push repo ini ke GitHub (folder `.github/workflows/build.yml` ikut).
2. Tab **Actions** -> run **build** -> download artifact `playsound-jar`.
3. Pakai jar yang **tanpa** `-sources` di `.minecraft/mods` (butuh Fabric Loader + Fabric API).

## Ganti suara
Ganti `src/main/resources/assets/playsound/sounds/custom.ogg`.
Harus format **.ogg (Vorbis)**, bukan mp3. Konversi:
`ffmpeg -i suara.mp3 -vn -ac 1 -ar 44100 -c:a libvorbis -q:a 5 custom.ogg`

## Ganti tombol
Ubah `GLFW.GLFW_KEY_K` di `PlaySoundClient.java`, atau ubah langsung di menu Options > Controls > "Play Sound".

## Kalau build gagal karena versi
Samakan `loader_version`, `loom_version`, `fabric_version` di `gradle.properties` dengan https://fabricmc.net/develop/ untuk Minecraft 1.21.11.
