# 🚀 Guide de Déploiement iOS : TestFlight & App Store (Béthel+)

Ce guide vous accompagne pas à pas pour compiler, signer et publier **Béthel+** sur **TestFlight** et l'**App Store**.

---

## 📋 1. Prérequis Apple Developer
1. Un compte [Apple Developer Program](https://developer.apple.com/programs/) actif.
2. Identifiant d'application créé sur le portail Apple : `com.bethelplus.app`.
3. Une fiche d'application créée sur [App Store Connect](https://appstoreconnect.apple.com/) avec le Bundle ID `com.bethelplus.app`.

---

## 🛠️ 2. Configuration & Signature dans Xcode (Sur macOS)

1. Ouvrez le projet dans Xcode :
   ```bash
   cd bethelplus_flutter/ios
   open Runner.xcworkspace
   ```
2. Dans le volet de gauche, sélectionnez le projet **Runner** > Cible **Runner** > Onglet **Signing & Capabilities** :
   - Cochez **Automatically manage signing**.
   - Sélectionnez votre **Team** Apple Developer.
   - Vérifiez que le **Bundle Identifier** est bien `com.bethelplus.app`.

---

## 📦 3. Compilation de l'Archive de Production (IPA)

### Méthode A : En ligne de commande Flutter (Recommandée)
```bash
# Nettoyage et récupération des dépendances
flutter clean
flutter pub get

# Compilation de l'archive release
flutter build ipa --release
```
L'archive générée se trouvera dans :
`build/ios/archive/Runner.xcarchive` ou `build/ios/ipa/bethelplus.ipa`.

### Méthode B : Directement depuis Xcode
1. Dans Xcode, sélectionnez le périphérique **Any iOS Device (arm64)** dans la barre supérieure.
2. Allez dans le menu **Product** > **Archive**.
3. Une fois l'archive terminée, la fenêtre **Organizer** s'ouvre automatiquement.

---

## ☁️ 4. Distribution sur TestFlight & App Store

1. Dans la fenêtre **Organizer** de Xcode :
   - Cliquez sur **Distribute App**.
   - Choisissez **App Store Connect** > **Upload**.
   - Laissez les options par défaut cochées (Upload symbols, manage build number).
   - Cliquez sur **Upload**.
2. Une fois téléversé (environ 5 à 10 minutes de traitement sur les serveurs Apple) :
   - Rendez-vous sur [App Store Connect](https://appstoreconnect.apple.com/) > **Mes apps** > **Béthel+** > Onglet **TestFlight**.
   - Ajoutez vos testeurs internes (immédiat) ou externes (après validation beta Apple).

---

## 🔒 5. Informations Légales & Fiche Store Déjà Préparées
- **Chiffrement (`ITSAppUsesNonExemptEncryption`)** : `false` (aucun document de conformité cryptographique supplémentaire requis).
- **Modes d'arrière-plan** : Audio (méditation & TTS), Background fetch.
- **Autorisations** : Notifications locales & reconnaissance vocale (textes en français déjà configurés dans `Info.plist`).
