# 🎵 Flyweight Music Streaming Application — MelodyShare

<p align="center">
<img width="1366" height="728" alt="image" src="https://github.com/user-attachments/assets/ec4600b0-543b-47c3-a7ce-5eeaa6390725" />
</p>

**Repository:** `https://github.com/Tharindu714/Flyweight-Music-Streaming-Application.git`

> MelodyShare is a compact, colourful demo that uses the **Flyweight** design pattern to share heavy album data (artwork, artist, album meta) across many `Song` instances. This reduces memory usage while allowing thousands of simultaneous plays.

---

## ✨ Highlights

* 🎯 Demonstrates the **Flyweight Pattern** (Album = flyweight, Song = context, AlbumFactory = flyweight factory).
* 💾 Simulates heavy artwork bytes per album and shows memory savings when albums are shared.
* 🎨 Attractive musical-themed Swing UI with gradient header, song list and live memory stats.
* 🛡️ Anti-pattern rules applied: no per-instance duplication of heavy resources, album objects immutable, and clear separation of responsibilities.

---

## 🚀 Features

* Add single songs reusing albums.
* Auto-generate large batches of songs (e.g., add 1000 songs) to stress test the flyweight pool.
* Live stats: number of song instances, unique albums (flyweights), estimated memory saved (MB).
* Console demo shows `Album` reuse (`s1.album == s2.album`).

---

## 🛠️ Build & Run

1. Clone the repo:

```bash
git clone https://github.com/Tharindu714/Flyweight-Music-Streaming-Application.git
cd Flyweight-Music-Streaming-Application
```

2. Compile & run the single-file demo (Java 8+):

```bash
javac MusicPlayer_Flyweight.java
java MusicPlayer_Flyweight
```

> The app prints a short console demo and opens the MelodyShare GUI.

---

## 🧭 Design Overview

**Classes**

* `Album` — *Flyweight* (intrinsic state): `albumName`, `artist`, `artwork` (simulated heavy byte\[]). Immutable.
* `AlbumFactory` — *FlyweightFactory*: returns shared `Album` instances keyed by album+artist. Simple pool.
* `Song` — *Context/Extrinsic state*: `title`, `playbackPosition`, `playlistName` + reference to `Album`.
* `MusicPlayerFrame` — Swing UI to add songs and view stats.

**Why Flyweight?** Many songs share identical album data (artwork + metadata). Storing that data once per album and reusing it avoids massive duplication and reduces memory pressure.

---

## 📐 UML (PlantUML)
<p align="center">
<img width="1518" height="576" alt="UML" src="https://github.com/user-attachments/assets/bceb1d61-84e1-41b5-9fcd-c375247dfadd" />
</p>
---

## ✅ Anti-patterns avoided (rules applied)

* **No per-instance heavy resources**: artwork stored once per album, not per song.
* **Immutability for shared data**: `Album` is immutable — prevents accidental shared-mutable-state bugs.
* **Avoid global unbounded caches in production**: `AlbumFactory` is simple; in a real system consider `ConcurrentHashMap`, eviction, weak references or LRU to prevent memory leaks.
* **Separation of concerns**: factory handles pooling, songs handle playback details.
* **Thread-safety caution**: the demo is single-threaded Swing; make the factory thread-safe in multi-threaded servers.

---

## 🧪 Example console output (from `main`)

```
s1.album == s2.album ? true
Number of unique Album flyweights: 2
```

---

## 📸 Screenshots / Uploads (placeholders)

* **Question screenshot** (upload here):

<p align="center">
  <img width="796" height="256" alt="Scenario_6" src="https://github.com/user-attachments/assets/862abea2-5904-4c2e-a498-2fec8e9e7378" />
</p>

---

## 🔧 Extending & Production Notes

* Make `AlbumFactory` thread-safe with `ConcurrentHashMap` and consider `WeakReference` values to allow GC of unused albums.
* Replace simulated `byte[]` artwork with real image assets; load lazily and keep thumbnails in memory.
* Add metrics + memory profiling to verify actual savings.

---

## 📮 Contribution

Fork, add features (e.g., async loading, album eviction, real artwork), and send PRs. Include tests showing memory usage improvements.

---

Made with ♫ by Tharindu — enjoy and share! Keep the music playing. 🎧


