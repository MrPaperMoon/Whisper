# Whisper

![Project Status: WIP](https://shields.io)

Whisper is an decentralized mesh-networking platform based on offline features like bluetooth and wi-fi. Designed to give people a anonymous way to express themselves. By chaining local Bluetooth connections, Whisper routes messages device-to-device, completely bypassing internet firewalls, cellular blackouts, and centralized surveillance.

> 🚧 **WARNING: Work In Progress (WIP)**
> 
> **Whisper is currently in an early experimental phase.** 
> * The core mesh routing, cryptographic schemas, and database formats are highly volatile.
> * The project is not yet production-ready or safe for critical anonymity needs. Feel free to explore the architecture or open an issue to discuss design patterns!

---

## 💡 Core Philosophy & Use Cases

Whisper is engineered for environments where internet access is restricted, compromised, or unavailable (e.g., localized crisis zones, grid failures, or heavy censorship). It operates on three uncompromised pillars:

1. **Absolute Freedom:** Zero reliance on ISP infrastructure, cellular networks, or central servers.
2. **True Anonymity:** No phone numbers, emails, or central registration keys required to communicate.
3. **Spam & Abuse Resistance:** Self-policing networks driven entirely by decentralized peer trust.

---

## 🛡️ Key Features & Architecture

### 1. The Karma Tree (Anti-Abuse Engine)
To prevent the mesh from being ruined by advertising spam, malicious flood attacks, or bot corruption, messages are strictly capped to text-only formats. Trust is managed locally through a decentralized **Karma Tree**:
* **Web of Trust:** Your trust rating is calculated using a 6-degree friend-of-friend contact model.
* **Local Sentiment Hooks:** Users can apply local likes and bans to immediate peers.
* **Multiplied Trust Routing:** An individual’s final network karma is computed as a weighted tree sum of their score combined with their shared contacts' metrics, creating an organic shield against malicious nodes.

### 2. Polynomial Secret Sharing (Malware & Leak Shielding)
To prevent unauthorized text mining or data theft by malicious application clones running on infected devices, data at rest is heavily guarded. 
* **Shamir's Secret Sharing:** Whisper implements threshold cryptography using polynomial interpolation.
* **Split Keys:** Cryptographic keys are split into multiple mathematical shares, ensuring that a compromised layer or clone app cannot reconstruct user identities or historic message logs without meeting the strict recovery threshold.

### 3. Mesh Network
Operating inside the `mesh` and `nearby` modules, the transport layer establishes ad-hoc P2P Bluetooth sockets. Devices act as relays, dynamically receiving, verifying, and hopping text messages across the physical vicinity until they reach their intended destination or expire.

> ⚠️ The physical transport layer currently utilizes **Google's Nearby Connections API** to establish the peer-to-peer mesh. Because this project is an architectural prototype (Proof of Concept) rather than a production-ready global product, it works within specific technical boundaries:
* **Google Play Services Dependency:** Network capabilities are restricted to Android devices running Google API ecosystems.
* **No Raw Bluetooth/Wi-Fi Low-Level APIs:** To keep the initial implementation focused, the project intentionally avoids custom low-level Bluetooth LE or raw Wi-Fi sockets, meaning multiplatform cross-communication (e.g., iOS to Android) is not supported out of the box.
* **Out-of-the-Box Secure Channels:** A major reason for choosing Google’s Nearby framework is that it natively abstract and manage secure communication tunnels between participants. While implementing cross-platform custom transport layer alternatives is entirely possible, doing so manually introduces severe security risks regarding safe node authentication and key exchanges—complex challenges that the Google Nearby framework safely handles for this prototype.

---

## 🏗️ Project Structure

The codebase is organized into isolated functional layers:
* **`app`**: UI presentation layer and dependency injection initialization.
* **`mesh`**: Message propagation engine, routing table validation, and karma tracking.
* **`nearby`**: Google Nearby hardware abstraction layer and P2P connection handling.
* **`entity`**: Pure POJO layer for bussiness operations.
* **`tools`**: Android level utils everyday tools.
* **`utils`**: Pure java/kotlin utilities and extensions.
