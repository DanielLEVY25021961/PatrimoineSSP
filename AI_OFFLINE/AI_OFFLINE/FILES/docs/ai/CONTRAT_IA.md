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