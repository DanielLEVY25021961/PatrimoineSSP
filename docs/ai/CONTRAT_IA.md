# docs/ai/CONTRAT_IA.md

# Contrat IA — Projet Java Hexagonal (référence unique versionnée)

<!-- ******************************************************************** -->
<!-- ************************** CONTRAT IA ******************************* -->
<!-- ******************************************************************** -->

<!--
CONTRAT_IA.md est le contrat de fonctionnement (la “constitution”)
entre l’Utilisateur et l’IA pour travailler sur le dépôt.

Il a pour objectifs :

- Rappeler le contexte (gros projet, risque d’oubli)
  et l’objectif fondamental : ne jamais dépendre
  de la mémoire interne de l’IA.

- Fixer la source de vérité :
  les fichiers présents dans le dépôt
  (docs/ai + docs/contrats + tools/ai).

- Imposer une discipline de lecture stricte :
  l’IA doit lire les fichiers pertinents AVANT toute analyse
  ou génération de code, et tracer explicitement
  ce qui a été lu.

- Définir la traçabilité du travail :
  utilisation d’un SHA unique,
  d’URLs Raw figées par SHA,
  et d’un outillage dédié
  (perimetre.yaml, scripts tools/ai).

- Servir de garde-fou opérationnel :
  si l’IA ne peut pas lire correctement une ressource,
  elle doit le signaler explicitement
  et ne jamais improviser ni deviner.

En pratique :
ce fichier constitue la pièce maîtresse permettant
de piloter l’IA comme un collaborateur technique
qui ne peut agir que sur pièces,
sur la base exclusive des éléments présents dans le dépôt.
-->


## 1) Source de vérité
- La **seule source de vérité** est le **repo Git** au **SHA unique** fourni.
- Toute discussion, analyse ou correction doit être **reproductible** en relisant les fichiers au SHA.

## 2) Méthode de lecture autorisée (RT-LECTURE-GITHUB-02)
La lecture autorisée est **uniquement** :
1. URL Raw au format :
   `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
2. **Download binaire** (octets bruts)
3. Lecture locale des octets
4. **Vérification des génériques** (jamais de Raw Types)

Règles :
- Si un fichier ne peut pas être lu correctement (ex : génériques illisibles) :
  - signaler **"incident de lecture"**
  - demander une **nouvelle URL raw SHA** ou une **recopie dans le chat**
- En cas d’incident de lecture : relancer automatiquement jusqu’à succès (max 3 tentatives).

## 3) Modes d’interaction (toujours explicites)
### MODE A — LECTURE (zéro code)
Entrées attendues :
- SHA
- liste de fichiers (paths)
- objectif

Sortie :
- confirmation de lecture
- signatures exactes / contrats trouvés
- différences factuelles / divergences

### MODE B — DIAGNOSTIC (zéro patch)
Sortie :
- comparaison “test suppose X / impl fait Y”
- causes racines
- options de correction (test vs impl), impact

### MODE C — CODER (patch)
Uniquement si le message contient : **"coder"**.
Sortie :
- code intégrable (fichiers complets ou méthodes complètes selon demande)
- style conforme (javadoc, commentaires, etc.) si Java

## 4) SHA unique obligatoire
- Toute requête doit fournir **un SHA unique courant**.
- Interdiction d’utiliser `refs/heads/...` comme source de vérité.
- Les URLs `refs/heads/...` ne servent qu’à retrouver le `path`, puis conversion en raw SHA.

## 4 bis) PRIORITÉ ABSOLUE — Sacralisation des URLs Raw du projet

L’objectif fondamental est de garantir que les URLs Raw du projet ne soient jamais perdues, afin d’éviter toute dépendance à la mémoire interne de l’IA ou à l’historique du chat.

Principe général

L’IA doit pouvoir retrouver de manière autonome, déterministe et reproductible l’ensemble des fichiers du périmètre à partir du dépôt lui-même.

Règles obligatoires

Inventaire permanent des URLs Raw

Toutes les URLs Raw (format refs/heads/...) du périmètre doivent être stockées dans un fichier AI versionné du dépôt (ex : docs/ai/perimetre.yaml).

Objectif :
➜ ne plus jamais avoir à redemander les URLs dans le chat.

Stockage du SHA courant

Le SHA unique courant doit être stocké dans un fichier AI versionné accessible à l’IA (ex : fichier dédié ou clé dans perimetre.yaml).

Objectif :
➜ garantir la reproductibilité exacte du contexte.

Transformation automatique en URLs Raw SHA

À chaque nouveau commit/push (nouveau SHA) :

l’IA doit reconstruire automatiquement les URLs au format :

https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}

en remplaçant uniquement la partie refs/heads/... par le SHA.

Objectif :
➜ obtenir des URLs figées, immuables et auditables.

Lecture obligatoire via URLs Raw SHA

Toute analyse, diagnostic ou génération de code doit être précédée :

d’une lecture effective des fichiers via ces URLs Raw SHA reconstruites,

selon la méthode RT-LECTURE-GITHUB-02.

Interdiction d’utiliser :

des URLs de branche mouvante,

des contenus mémorisés,

des approximations ou suppositions.

Gestion des incidents

Si l’IA ne peut pas reconstruire ou lire correctement les URLs SHA :

elle doit signaler explicitement l’échec,

demander les éléments manquants,

et ne jamais improviser.

Résultat attendu

Grâce à ce mécanisme :

le périmètre du projet est auto-décrit par le dépôt lui-même ;

les URLs Raw ne peuvent plus être “perdues” ;

chaque analyse est traçable et reproductible ;

l’IA peut être pilotée comme un collaborateur technique travaillant uniquement sur pièces.

## 4 ter) Hiérarchie des sources et obligation de lecture préalable

Ordre de priorité des références :

1. docs/ai/CONTRAT_IA.md   → Constitution du système IA
2. docs/ai/perimetre.yaml  → Inventaire opérationnel des ressources
3. docs/contrats/**        → Spécifications fonctionnelles
4. Code source             → Implémentation
5. Historique du chat      → Contexte non fiable

Règles impératives :

- Avant toute analyse, diagnostic ou génération de code,
  l’IA DOIT lire :

  a) CONTRAT_IA.md  
  b) perimetre.yaml  
  c) les fichiers pertinents du périmètre au SHA fourni  

- Si une ressource ne peut pas être lue correctement :
  → signaler explicitement "incident de lecture"
  → NE PAS improviser
  → NE PAS supposer
  → NE PAS coder

- Toute réponse doit être basée uniquement
  sur des éléments effectivement lus au SHA.

Objectif : garantir un travail reproductible,
traçable et indépendant de la mémoire interne.

## 5) Statuts des fichiers
- `MEMORISE` : conservé tel quel ; modification uniquement sur demande explicite.
- `VALIDE` : verrouillé ; modification uniquement sur demande explicite de **déverrouillage**.
- `DEVERROUILLE` : modifiable selon la demande.

## 6) “Test = Spec”
- Les tests d’intégration et unitaires décrivent le **contrat réel** (comportement).
- Toute ambiguïté doit être tranchée dans :
  - le test (preuve)
  - le document de contrat (spécification lisible)

## 7) Checklist minimale (à chaque demande)
- [ ] SHA fourni
- [ ] liste des paths fournie
- [ ] mode demandé : LECTURE / DIAG / CODER
- [ ] si CODER : indiquer “fichier complet” ou “méthodes uniquement”
