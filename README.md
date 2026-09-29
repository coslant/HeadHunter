<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=soft&color=0:FCAF01,50:FE2C6B,100:FC05AC&height=160&section=header&text=HeadHunter&fontSize=60&fontColor=FFFFFF&fontAlignY=42&desc=Spigot%20%2F%20Paper%20%E2%80%A2%201.8%20%E2%86%92%201.21.11&descSize=15&descAlignY=72&animation=fadeIn" alt="HeadHunter" width="100%" />
</p>

<p align="center">
  <a href="https://github.com/coslant/HeadHunter/releases/latest"><img src="https://img.shields.io/github/v/release/coslant/HeadHunter?style=for-the-badge&label=%C4%B0ndir&color=FE2C6B&logo=github" alt="Download" /></a>
  <a href="https://discord.gg/forges"><img src="https://img.shields.io/badge/Discord-Forges%20Studio-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Discord: Forges Studio" /></a>
</p>

<p align="center">
  <a href="https://github.com/coslant/HeadHunter/actions/workflows/build.yml"><img src="https://img.shields.io/github/actions/workflow/status/coslant/HeadHunter/build.yml?branch=main&style=flat-square&label=build" alt="Build" /></a>
  <img src="https://img.shields.io/badge/Minecraft-1.8%20%E2%86%92%201.21.11-FCAF01?style=flat-square" alt="Minecraft 1.8 - 1.21.11" />
  <img src="https://img.shields.io/badge/Spigot%20%7C%20Paper-FE9808?style=flat-square" alt="Spigot | Paper" />
  <img src="https://img.shields.io/badge/Java-8-FD6C29?style=flat-square&logo=openjdk&logoColor=white" alt="Java 8" />
  <a href="LICENSE"><img src="https://img.shields.io/github/license/coslant/HeadHunter?style=flat-square&color=FB0C9E&label=lisans" alt="License" /></a>
</p>

<p align="center">
  <b>Oyuncu öldürünce kurbanın kafası düşer, katilin adı yazılı.</b>
  <br>
  <sub><kbd>EN</kbd> Drops the victim's head with the killer's name when a player is slain.</sub>
</p>

Bir oyuncu öldürüldüğünde kafası yere düşer; üstünde kurbanın gerçek derisi, açıklamasında
katilin adı ve tarih yazar. PvP sunucuları için ganimet ve övünme kaynağı.
Tek jar **1.8 – 1.21.11** arası çalışır.

## ✨ Özellikler

- 💀 Ölen oyuncunun kafası düşer — üstünde **gerçek skini** görünür, varsayılan Steve/Alex değil
- ✍️ Kafanın adı ve açıklaması tamamen ayarlanabilir (katil, kurban, tarih)
- 🇹🇷 Türkçe ilgi eki desteği: `%victim_in%` → *coslant'ın Kafası*
- 🎲 Düşme şansı yüzde olarak ayarlanır (ondalıklı yazabilirsin: `12.5`)
- 🎁 Kafa yere düşsün ya da doğrudan katilin envanterine gitsin
- 📢 İsteğe bağlı sunucu geneli duyuru
- 👹 **Mob kafaları** — iskelet, zombi, creeper, ender ejderha ve daha fazlası (varsayılan kapalı)
- 🌍 Dünya bazında kapatma

## 📥 Kurulum

1. [Releases](https://github.com/coslant/HeadHunter/releases/latest) sayfasından `HeadHunter.jar` dosyasını indir.
2. Sunucunun `plugins/` klasörüne at.
3. Sunucuyu yeniden başlat.
4. `plugins/HeadHunter/config.yml` dosyasından şansı ve yazıları düzenle.

## 🎮 Komutlar

| Komut | Açıklama | Yetki |
|-------|----------|-------|
| `/headhunter reload` | Ayarları yeniden yükler | `headhunter.reload` |

**Alias:** `/kafaavi` · `/hh`

## 🔑 Yetkiler

| Yetki | Açıklama | Varsayılan |
|-------|----------|------------|
| `headhunter.reload` | Configi yeniden yükler | op |

## ⚙️ Ayarlar (config.yml)

| Anahtar | Açıklama |
|---------|----------|
| `enabled` | Plugin açık mı |
| `chance` | Kafa düşme şansı (0 – 100, ondalıklı olabilir) |
| `only-player-kills` | Sadece oyuncu öldürmelerinde mi düşsün |
| `count-suicide` | Kendini öldürme sayılsın mı |
| `give-to-killer` | Kafa doğrudan katile mi verilsin |
| `disabled-worlds` | Bu dünyalarda kafa düşmez |
| `name` / `lore` | Kafanın adı ve açıklaması |
| `date-format` | Tarih biçimi |
| `mob-heads.enabled` / `chance` | Mob kafaları ve şansı |
| `messages.announce` | Duyuru mesajı (boş = duyuru yok) |

### Yer tutucular

| Kalıp | Anlamı |
| :-- | :-- |
| `%victim%` | Ölen oyuncunun adı |
| `%victim_in%` | İlgi ekli hâli — *coslant'ın* |
| `%killer%` | Öldüren oyuncunun adı |
| `%killer_in%` | İlgi ekli hâli |
| `%date%` | Ölüm tarihi |

## 📝 Notlar

- Kafaya kurbanın gerçek derisi gömülür. Bunun için sırasıyla modern `PlayerProfile` API'si (1.18+),
  `GameProfile` (1.8 – 1.17) ve son çare olarak isim eşleştirmesi denenir.
- 1.13 öncesinde kafa `SKULL_ITEM:3`, sonrasında `PLAYER_HEAD` olarak oluşturulur; bu otomatik yapılır.

## 🛠️ Derleme

```bash
mvn clean package
```

Çıktı: `target/HeadHunter.jar`

> 1.8 uyumluluğu için Java 8 hedefiyle derlenir. En sorunsuz derleme **JDK 8, 11 veya 17** iledir.

Her push'ta [GitHub Actions](https://github.com/coslant/HeadHunter/actions/workflows/build.yml) projeyi otomatik derler; etiketlenen sürümler (`v*`) jar'ıyla birlikte [Releases](https://github.com/coslant/HeadHunter/releases) sayfasında yayınlanır.

## 💬 Destek

- 🐛 Hata bildirimi ve öneriler için [Issues](https://github.com/coslant/HeadHunter/issues) sekmesini kullan.
- 💬 Sorular ve destek için [Forges Studio Discord](https://discord.gg/forges) sunucusuna katıl.

## 📄 Lisans

[MIT](LICENSE) — dilediğin gibi kullan, değiştir, dağıt.

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=rect&color=0:FCAF01,50:FE2C6B,100:FC05AC&height=4&section=footer" width="100%" alt="" />
  <br>
  <sub>🔥 <a href="https://github.com/coslant">coslant</a> tarafından geliştirildi · <a href="https://discord.gg/forges">Forges Studio</a></sub>
</p>
