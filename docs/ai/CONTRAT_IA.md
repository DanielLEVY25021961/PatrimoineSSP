# docs/ai/CONTRAT_IA.md

# CONTRAT IA — Projet Java Hexagonal (Constitution technique)

<!-- ******************************************************************** -->
<!-- ************************** CONTRAT IA ******************************* -->
<!-- ******************************************************************** -->

## PRÉAMBULE

Ce document constitue la **constitution technique** régissant le fonctionnement de l’IA
sur le dépôt.

Objectifs fondamentaux :

- Permettre un workflow industriel où l’Utilisateur fournit uniquement un SHA
- Garantir un travail reproductible, déterministe et audit-ready
- Éliminer toute dépendance à la mémoire interne de l’IA
- Permettre un fonctionnement ONLINE et OFFLINE
- Assurer une traçabilité complète des analyses et corrections

L’IA doit être pilotable comme un collaborateur technique travaillant strictement **sur pièces**,
uniquement à partir des éléments effectivement lus.

---

## 1) Source de vérité

La source de vérité est déterminée selon l’ordre de priorité suivant :

1. **Baseline consolidée interne** (dernière version validée)
2. **Repo GitHub au SHA fourni**
3. **Bundle OFFLINE validé**

Règles absolues :

- Ne jamais deviner
- Ne jamais supposer
- Ne jamais improviser
- Ne jamais utiliser une version non vérifiée
- Toute analyse doit être reproductible au SHA

---

## 2) Gestion du SHA

- L’Utilisateur fournit un **SHA unique** après chaque commit/push.
- Si aucun nouveau SHA n’est fourni → le SHA courant reste valide.
- L’IA ne doit jamais redemander un SHA déjà valide.
- Toute lecture GitHub doit être effectuée au SHA fourni.

Flux nominal :

1. Correction dans STS  
2. Commit / Push  
3. Transmission du nouveau SHA  
4. Relecture GitHub au SHA  
5. Consolidation de la baseline  

---

## 3) Découverte automatique du projet

Principe fondamental :

➡️ L’IA doit pouvoir retrouver tous les fichiers nécessaires **à partir du dépôt lui-même**.

Algorithme de découverte :

SHA → docs/ai/MANIFEST_IA.yaml → docs/ai/perimetre.yaml → fichiers

Les URLs Raw `refs/heads/...` stockées dans le dépôt servent uniquement à :

- retrouver les paths
- reconstruire automatiquement les URLs Raw SHA

---

## 4) Reconstruction automatique des URLs Raw SHA

Format canonique :

https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}

Règles :

- L’IA doit reconstruire automatiquement ces URLs à partir des paths.
- Interdiction d’utiliser une URL de branche mouvante comme source de vérité.
- L’Utilisateur n’a jamais à fournir d’URLs.
- Les URLs Raw refs/heads servent uniquement à dériver les paths.

Objectif :

➡️ Garantir un fonctionnement “SHA-only”.

---

## 5) Méthode de lecture GitHub autorisée

Lecture autorisée exclusivement :

1. URL Raw SHA  
2. Téléchargement binaire (octets bruts)  
3. Lecture locale  
4. Vérification stricte des génériques (aucun Raw Type)

Règles :

- Relancer automatiquement en cas d’échec (max 3 tentatives)
- Toute lecture doit être traçable
- Interdiction d’utiliser des contenus mémorisés non vérifiés

En cas d’échec persistant :

➡️ Passer en MODE OFFLINE

---

## 6) Gestion des incidents de lecture

Si l’IA ne peut pas lire correctement une ressource :

➡️ Signaler explicitement **"incident de lecture"**  
➡️ Demander l’élément manquant  
➡️ Suspendre toute analyse ou génération  

L’IA ne doit jamais demander d’URLs Raw.

Priorité des demandes :

1. Bundle OFFLINE valide  
2. Recopie du fichier dans le chat  

---

## 7) Hiérarchie des ressources du dépôt

Ordre de priorité :

1. docs/ai/CONTRAT_IA.md
2. docs/ai/perimetre.yaml
3. docs/ai/MANIFEST_IA.yaml
4. docs/contrats/**
5. Code source
6. Tests
7. Historique du chat (non fiable)

Avant toute analyse ou génération de code, l’IA DOIT lire :

- CONTRAT_IA.md
- perimetre.yaml
- les fichiers pertinents au SHA

---

## 8) Baseline consolidée

Propriétés :

- Toujours à jour avec le GitHub
- Une seule version par fichier (la plus récente)
- Conservation strictement ligne par ligne
- Aucun fichier ne peut être perdu ou ignoré
- Modification uniquement sur ordre explicite

Après chaque lecture GitHub :

➡️ Consolidation obligatoire  
➡️ Suppression des versions précédentes  

---

## 9) Modes d’interaction

### MODE LECTURE (zéro code)

Sortie :

- confirmation de lecture
- signatures exactes
- divergences factuelles

---

### MODE DIAGNOSTIC (zéro patch)

Sortie :

- analyse des causes racines
- comparaison implémentation vs tests
- options de correction

---

### MODE CODER (patch)

Activé uniquement si le message contient explicitement : **"coder"**.

Sortie :

- code intégrable dans STS
- conforme aux conventions du projet

---

## 10) MODE OFFLINE — Continuité sans GitHub

Activé si GitHub est inaccessible ou illisible.

Source de vérité : bundle versionné contenant :

- AI_OFFLINE/INDEX.txt
- AI_OFFLINE/PROVENANCE.yaml
- AI_OFFLINE/CHECKSUMS.sha256
- AI_OFFLINE/FILES/**

Conditions :

- Tous les fichiers de preuve doivent être présents
- Les checksums doivent correspondre
- Le SHA doit être indiqué dans PROVENANCE.yaml

Traçabilité obligatoire :

➡️ Lister les fichiers utilisés  
➡️ Mentionner leur checksum  

Interdiction d’analyser ou coder sans bundle valide.

---

## 11) Travail « sur pièces »

L’IA agit uniquement sur des éléments effectivement lus.

Ordre des sources :

1. Baseline
2. GitHub au SHA
3. Bundle OFFLINE

Si aucune source valide n’est accessible :

➡️ Arrêt du processus avec signalement explicite.

---

## 12) Statuts des fichiers

- MEMORISÉ : conservé tel quel
- VALIDÉ : verrouillé
- DEVERROUILLÉ : modifiable

Toute modification nécessite instruction explicite.

---

## 13) Test = Spécification

Les tests décrivent le comportement réel.

En cas d’ambiguïté :

➡️ Priorité aux tests  
➡️ Puis aux documents de contrat  

---

## 14) Règles de génération de code

- Générer du code uniquement si "coder" est explicitement présent
- Fournir du code directement intégrable dans STS
- Ne pas renvoyer plus que demandé
- Proposer une seule stratégie cohérente
- Ne pas revenir en arrière sans justification technique

---

## 15) Règles de qualité

- Code multi-lignes
- Indentation claire
- Accolades obligatoires
- Aucun Raw Type
- Respect strict du style du projet
- Commentaires de bloc uniquement

---

## 16) Règles d’architecture

- Métier pur Java
- Code thread-safe
- Homogénéité entre modules
- Pas de var
- Pas de modérateur friendly

Comparaisons :

- Alignement strict equals / hashCode / compareTo / toString
- Métier : business key uniquement
- JPA : compatible proxies Hibernate

---

## 17) Constantes

Toute String répétitive doit être factorisée dans une constante documentée.

---

## 18) Constructeurs

Chaque classe doit posséder au moins un constructeur documenté.

---

## 19) Logs

- Ne pas analyser la présence de logger inutilisé
- LOG.warn non généré automatiquement
- LOG.fatal uniquement pour erreurs très graves

---

## 20) Homogénéité du projet

- Maintenir un niveau de qualité identique entre modules
- S’aligner sur les méthodes existantes
- Commenter toute logique non triviale

---

## 21) Objectif global

Ce dispositif vise un workflow industriel :

- SHA-only
- reproductible
- déterministe
- audit-ready
- indépendant de la mémoire interne
- capable de fonctionner ONLINE et OFFLINE

L’IA doit pouvoir être pilotée comme un collaborateur technique
travaillant exclusivement sur pièces,
sans jamais dépendre d’informations implicites.

---

## 22) Livraison des corrections — Règle de complétude (ANTI-SNIPPETS)

Toute correction ou proposition de code fournie par l’IA doit être livrée sous la forme d’un **fichier complet directement intégrable dans STS**, sans nécessité d’édition manuelle intermédiaire.

Règles obligatoires :

- L’IA doit fournir soit :
  - la totalité d’un fichier ou d’une classe (ex : `docs/ai/CONTRAT_IA.md`, une classe Java complète),
  - soit la totalité d’une méthode (ex : une méthode `creer()` complète dans un ADAPTER).

- Le contenu fourni doit être :
  - complet,
  - cohérent,
  - prêt à être copié-collé tel quel,
  - compilable lorsque applicable,
  - conforme aux conventions du projet.

Interdictions absolues :

- Fournir des fragments partiels (“snippets”)
- Donner des instructions du type :
  - « insérer ce code ligne X »
  - « remplacer la ligne Y »
  - « ajouter ceci après telle méthode »
  - « modifier tel endroit manuellement »
- Produire un patch nécessitant une reconstruction par l’utilisateur
- Répartir une correction sur plusieurs messages

Principe :

➡️ **L’utilisateur ne doit jamais avoir à reconstituer le code.**  
➡️ **La livraison doit être immédiatement exploitable.**

Cette règle s’applique à tous les modes produisant du code, en particulier le MODE CODER.

## 23) Règle de couplage CODE/BUNDLE (Variante A — 2 SHAs)

Définitions :

- **SHA1 (CODE)** : SHA du commit/push contenant les corrections fonctionnelles/techniques (hors bundle).
- **SHA2 (BUNDLE)** : SHA du commit/push ne contenant que le bundle OFFLINE généré pour le **SHA1**.

Règles obligatoires :

1. **Interdiction de modifications hors bundle entre SHA1 et SHA2**
   - Entre le push de **SHA1** et le push de **SHA2**, aucun fichier ne doit être modifié
     en dehors de `AI_OFFLINE/**` (et éventuellement du zip `AI_OFFLINE_*.zip` stocké sous `AI_OFFLINE/**`).
   - Si un fichier hors `AI_OFFLINE/**` diffère entre **SHA1** et **SHA2** :
     ➜ le SHA2 n’est plus un “bundle de SHA1”  
     ➜ le cycle Variante A est considéré invalide.

2. **Provenance du bundle**
   - Le fichier `AI_OFFLINE/PROVENANCE.yaml` doit référencer explicitement le **SHA1 (CODE)**
     comme SHA de référence du contenu packagé (le bundle peut être committé au SHA2).
   - Si `PROVENANCE.yaml` ne référence pas **SHA1** :
     ➜ signaler explicitement **"incident de lecture"** et suspendre toute analyse/génération.

3. **Contrôle obligatoire par l’IA à réception de SHA2**
   - À réception de **SHA2**, l’IA doit vérifier que :
     - seuls des chemins sous `AI_OFFLINE/**` ont changé entre **SHA1** et **SHA2**,
     - et que `AI_OFFLINE/PROVENANCE.yaml` référence bien **SHA1**.
   - Si l’IA constate une divergence hors `AI_OFFLINE/**` :
     ➜ signaler **"problème grave"**  
     ➜ exiger un nouveau cycle complet (nouveau SHA1 puis nouveau SHA2).

Objectif :

➡️ Garantir que le commit/push **SHA2** est strictement un ajout d’artefact OFFLINE
pour le **code validé** au **SHA1**, sans altérer le code ni les contrats.

## 24) Règle de rafraîchissement de la baseline (précondition absolue)

Principe :

➡️ La **baseline consolidée** est la **seule source de vérité opérationnelle** pour l’analyse,
le diagnostic et la génération de code.
Le GitHub au SHA sert uniquement à **rafraîchir** la baseline après chaque commit/push.

Précondition absolue (ANTI-ANALYSE-SANS-BASELINE-A-JOUR) :

- Toute analyse/diagnostic/codage est **interdit** tant que la baseline n’est pas
  **parfaitement à jour** avec le **SHA courant** fourni par l’Utilisateur.
- Dès qu’un nouveau SHA est fourni, l’IA doit relire au minimum les ressources nécessaires
  (selon le périmètre) sur GitHub au SHA, puis mettre à jour la baseline.

Mise à jour obligatoire après lecture GitHub réussie :

- Après chaque lecture GitHub au SHA réalisée selon **RT-LECTURE-GITHUB-02** et jugée
  techniquement parfaite (lecture binaire, contenu non altéré, génériques lisibles, pas de Raw Types),
  l’IA doit :
  1) **Mémoriser strictement ligne à ligne** le contenu lu depuis GitHub,
  2) **Écraser** la version précédente du même fichier en baseline,
  3) **Consolider** la baseline (une seule version par chemin : la plus récente),
  4) Considérer la baseline comme unique matériau autorisé pour l’analyse.

Critères minimaux de “lecture parfaite” (pour autoriser la mise à jour baseline) :

- Lecture effectuée via URL Raw **au SHA** : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
- Téléchargement binaire (octets bruts) + lecture locale
- Contenu cohérent (non vide, non HTML d’erreur, non tronqué)
- Génériques Java correctement lus lorsque applicable (aucun “Raw Type” dû à une lecture incorrecte)

En cas de lecture imparfaite :

- Si l’IA ne peut pas garantir la lecture correcte (ex : doute sur génériques, contenu altéré, lecture incomplète) :
  ➜ signaler explicitement **"incident de lecture"**
  ➜ suspendre toute analyse/génération
  ➜ relancer automatiquement la lecture jusqu’à succès (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02
  ➜ à échec persistant : basculer en MODE OFFLINE (bundle) selon le présent contrat.

Objectif :

➡️ Garantir que toute analyse/correction est **reproductible**, **audit-ready** et
strictement basée sur une baseline **consolidée** et **alignée** sur le SHA fourni.

## 25) Détection des modifications au nouveau SHA (comparaison GitHub ↔ baseline)

Principe :

➡️ À chaque **nouveau SHA unique** fourni par l’Utilisateur, l’IA doit être capable de
**détecter et constater** les modifications par rapport à la baseline consolidée,
uniquement à partir de lectures GitHub au SHA (RT-LECTURE-GITHUB-02) et/ou du bundle OFFLINE.

Algorithme obligatoire (à réception d’un nouveau SHA) :

1. **Pré-lecture d’ancrage (obligatoire)**
   - Lire au SHA : `docs/ai/MANIFEST_IA.yaml`, `docs/ai/perimetre.yaml`, `docs/ai/CONTRAT_IA.md`.

2. **Découverte du périmètre**
   - Extraire la liste canonique des `paths` depuis `docs/ai/perimetre.yaml`.
   - Reconstruire toutes les URLs Raw SHA : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`.

3. **Lecture + contrôle technique (RT-LECTURE-GITHUB-02)**
   - Pour chaque fichier requis :
     - télécharger en binaire (octets bruts) via Raw SHA,
     - lire localement,
     - vérifier la cohérence (non vide, non HTML d’erreur, non tronqué),
     - vérifier les génériques lorsque applicable (aucun “Raw Type” dû à une lecture incorrecte).

4. **Comparaison stricte avec la baseline**
   - Comparer strictement **ligne à ligne** (ou octets stricts) le contenu lu au SHA
     avec la version correspondante en baseline consolidée.
   - Classer chaque fichier dans l’un des statuts :
     - **INCHANGÉ** : identique à la baseline,
     - **MODIFIÉ** : différence constatée,
     - **INACCESSIBLE** : non lisible au SHA (fichier manquant / erreur persistante).

5. **Signalements obligatoires**
   - Si un fichier attendu devient **INACCESSIBLE** :
     ➜ signaler explicitement **"problème grave"** (périmètre non satisfaisable au SHA),
     ➜ suspendre analyse/génération.
   - Si la lecture d’un fichier est douteuse (ex : génériques illisibles) :
     ➜ signaler explicitement **"incident de lecture"**,
     ➜ relancer automatiquement jusqu’à succès (max 3 tentatives),
     ➜ à échec persistant : basculer en MODE OFFLINE.

6. **Mise à jour de la baseline (obligatoire, après lectures parfaites)**
   - Après lecture parfaite au SHA :
     - mémoriser strictement ligne à ligne dans la baseline,
     - écraser l’ancienne version,
     - consolider la baseline (une seule version par chemin : la plus récente).

Interdiction :

- Il est **interdit** de démarrer une analyse/diagnostic/génération tant que :
  - la comparaison GitHub ↔ baseline n’a pas été effectuée au SHA courant,
  - et la baseline n’a pas été rafraîchie/consolidée lorsque des modifications existent.

Objectif :

➡️ Garantir que l’IA peut, à partir du **SHA seul**, (1) découvrir, (2) lire, (3) comparer,
(4) prouver les modifications, et (5) travailler uniquement sur une baseline à jour.