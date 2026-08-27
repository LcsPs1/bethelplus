# 🚀 Guide GitHub Actions CI/CD — Béthel+ (iOS & Android)

Ce guide explique comment utiliser **GitHub Actions** pour compiler automatiquement et gratuitement :
1. 🍏 **Le fichier `.ipa` pour iOS** (sur un serveur Mac virtuel macOS-14)
2. 🤖 **L'application Android `.apk`** (pour installation directe)
3. 📦 **Le bundle Android `.aab`** (pour publication sur le Google Play Store)

---

## 📁 Workflow Automatique Configuré

Le workflow est configuré dans [`.github/workflows/build_release.yml`](file:///c:/Users/KOBA/bethelplus/.github/workflows/build_release.yml).

Il se déclenche automatiquement :
- À chaque `git push` sur la branche principale (`main` ou `master`).
- Manuellement à tout moment via le bouton **« Run workflow »** dans l'onglet **Actions** de votre dépôt GitHub.

---

## 🔑 1. Configuration des Secrets GitHub (Optionnel / Recommandé)

Si vous souhaitez injecter vos clés d'API lors de la compilation dans le Cloud :

1. Rendez-vous sur votre dépôt GitHub : **Settings** > **Secrets and variables** > **Actions**.
2. Cliquez sur **New repository secret** et ajoutez :
   - `GEMINI_API_KEY` : Votre clé API Google Gemini pour les prières inspirées.
   - `SUPABASE_URL` : L'URL de votre projet Supabase (ex: `https://xxxx.supabase.co`).
   - `SUPABASE_ANON_KEY` : La clé publique anon de Supabase.

---

## ⚡ 2. Déclencher la compilation

### Méthode 1 : Par simple `git push`
```bash
git add .
git commit -m "Build: Mise à jour avec prières dynamiques, iCloud et Supabase"
git push origin main
```

### Méthode 2 : Déclenchement Manuel depuis GitHub
1. Ouvrez votre dépôt sur **GitHub.com**.
2. Cliquez sur l'onglet **Actions** en haut.
3. Dans la barre latérale gauche, sélectionnez **« 🚀 Build & Release (iOS IPA & Android APK/AAB) »**.
4. Cliquez sur le menu déroulant **Run workflow** à droite, puis sur le bouton vert **Run workflow**.

---

## 📥 3. Télécharger les Fichiers Compilés (Artifacts)

Une fois le workflow terminé avec succès (environ 4 à 7 minutes) :
1. Cliquez sur l'exécution du workflow (marquée d'une coche verte ✅).
2. Faites défiler la page vers le bas jusqu'à la section **Artifacts**.
3. Vous trouverez vos 3 fichiers prêts à l'emploi :
   - 🍏 **`BethelPlus-iOS-IPA`** : Contient `BethelPlus_iOS_Release.ipa` pour iOS.
   - 🤖 **`BethelPlus-Android-APK`** : Contient `app-release.apk` installable directement sur tout téléphone Android.
   - 📦 **`BethelPlus-Android-AAB`** : Contient `app-release.aab` prêt pour la console Google Play.
