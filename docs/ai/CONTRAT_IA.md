# CONTRAT IA — Projet Java Hexagonal (Constitution technique) - docs/ai/CONTRAT_IA.md

## PRÉAMBULE

Ce document constitue la **constitution technique** régissant le fonctionnement de l’IA sur le dépôt.

Objectifs fondamentaux :
- Permettre un workflow industriel où l’Utilisateur fournit uniquement un SHA
- Garantir un travail reproductible, déterministe et audit-ready
- Éliminer toute dépendance à la mémoire interne de l’IA
- Permettre un fonctionnement ONLINE et OFFLINE
- Assurer une traçabilité complète des analyses et corrections

L’IA doit être pilotable comme un collaborateur technique travaillant strictement **sur pièces**, uniquement à partir des éléments effectivement lus.

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

SHA → `docs/ai/MANIFEST_IA.yaml` → `docs/ai/perimetre.yaml` → fichiers

Les URLs Raw `refs/heads/...` stockées dans le dépôt servent uniquement à :
- retrouver les paths
- reconstruire automatiquement les URLs Raw SHA

---

## 4) Reconstruction automatique des URLs Raw SHA

Format canonique :

`https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`

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

Sacralisation RT-LECTURE-GITHUB-02 :

La lecture autorisée sur le dépôt GitHub ou dans le Bundle est exclusivement :
- URL Raw au SHA : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
- SHA unique fourni par l’Utilisateur
- téléchargement binaire du fichier
- lecture locale du fichier téléchargé (octets bruts)
- vérification stricte des génériques

Les génériques doivent impérativement être correctement lus.

Aucun Raw Type ne doit être toléré comme effet d’une mauvaise lecture.

Ordre préférentiel des techniques de lecture GitHub@SHA :
1. lecture par **web tool**
2. pour la jambe de **téléchargement binaire local**, utilisation prioritaire de **`container.download`**
3. si **`container.download`** ne fonctionne pas, bascule automatique vers une autre technique **container** autorisée permettant toujours un téléchargement binaire local puis une lecture locale

Si une technique de lecture échoue, l’IA doit changer d’elle-même de technique, sans attendre une nouvelle instruction de l’Utilisateur, tout en conservant strictement la même méthode contractuelle de lecture :
- URL Raw au SHA
- téléchargement binaire local
- lecture locale des octets bruts
- vérification des génériques

Interdiction spécifique :
- la lecture réseau par shell (`curl`, `wget`, etc. via `container.exec`) ne doit jamais être la technique locale **préférée** pour satisfaire la jambe “téléchargement binaire local” lorsqu’une primitive dédiée **`container.download`** existe et est disponible.

Toute autre forme de lecture (rendu texte, HTML, extraction non binaire, lecture indirecte d’une page GitHub) est interdite et doit être considérée comme non contractuelle.

Règles :
- Relancer automatiquement en cas d’échec (max 3 tentatives)
- Toute lecture doit être traçable
- Interdiction d’utiliser des contenus mémorisés non vérifiés
- Si les génériques ne peuvent pas être lus correctement : demander confirmation
- Aucune mémorisation, aucune consolidation de baseline, aucune analyse, aucun diagnostic, aucun code ne doivent être produits tant que la lecture conforme à RT-LECTURE-GITHUB-02 n’a pas réussi

En cas d’échec persistant :

➡️ Signaler explicitement **"incident de lecture"**  
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
2. Fichier transmis via le chat (coller le contenu ou pièce jointe)

### 6.1 Lecture d’un fichier transmis via le chat (fallback contrôlé)

Objectif : permettre la continuité de travail lorsque GitHub est indisponible et/ou pour valider l’intégrité d’un fichier transmis par l’Utilisateur.

Règles :
- Le fichier transmis via le chat est traité comme une **source `CHAT`**.
- L’IA doit :
  1. lire le contenu **tel quel**,
  2. calculer **taille**, **nombre de lignes** et **SHA-256**,
  3. comparer avec la version correspondante disponible (baseline et/ou bundle OFFLINE et/ou GitHub si lisible) :
     - **texte** : comparaison **ligne à ligne** (CRLF/LF normalisés),
     - **binaire** : comparaison **octet à octet** (rare en chat).

- Si le contenu `CHAT` est **strictement identique** à une source considérée saine (baseline consolidée à jour, ou bundle OFFLINE validé, ou GitHub@SHA lu parfaitement) :
  - le fichier `CHAT` est considéré **intègre**,
  - il peut être **mémorisé** et **écrasé** en baseline,
  - puis la baseline est **consolidée**.

- Si une différence est constatée, ou si l’IA ne peut pas comparer :
  ➜ signaler explicitement **"incident de lecture via le chat"** et **suspendre** analyse/génération.

---

## 7) Hiérarchie des ressources du dépôt

Ordre de priorité :
1. `docs/ai/CONTRAT_IA.md`
2. `docs/ai/perimetre.yaml`
3. `docs/ai/MANIFEST_IA.yaml`
4. `docs/contrats/**`
5. Code source
6. Tests
7. Historique du chat (non fiable)

Avant toute analyse ou génération de code, l’IA DOIT lire :
- `CONTRAT_IA.md`
- `perimetre.yaml`
- les fichiers pertinents au SHA

---

## 8) Baseline consolidée

Propriétés :
- Toujours à jour avec le GitHub
- Une seule version par fichier (la plus récente)
- Conservation strictement ligne par ligne
- Comparaison/mémorisation des fichiers **texte** (Java, XML, YAML, properties, Markdown, etc.) : **ligne à ligne** avec normalisation **CRLF/LF**
- Comparaison des fichiers **binaires** (zip, images, etc.) : **octet à octet**
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

### MODE DIAGNOSTIC (zéro patch)

Sortie :
- analyse des causes racines
- comparaison implémentation vs tests
- options de correction

### MODE CODER (patch)

Activé uniquement si le message contient explicitement : **"coder"**

Sortie :
- code intégrable dans STS
- conforme aux conventions du projet

---

## 10) MODE OFFLINE — Continuité sans GitHub

Activé si GitHub est inaccessible ou illisible.

Source de vérité : bundle versionné contenant :
- `AI_OFFLINE/INDEX.txt`
- `AI_OFFLINE/PROVENANCE.yaml`
- `AI_OFFLINE/CHECKSUMS.sha256`
- `AI_OFFLINE/FILES/**`

Conditions :
- Tous les fichiers de preuve doivent être présents
- Les checksums doivent correspondre
- Le SHA doit être indiqué dans `PROVENANCE.yaml`

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

### 15.1) Règles sacrées des commentaires de bloc dans les ADAPTER UC

Principe :

➡️ Dans un ADAPTER UC, les commentaires de bloc sont contractuels.  
➡️ Ils doivent être relus avant toute génération de code et reproduits sans improvisation.

Précondition absolue avant toute génération de code dans un ADAPTER UC :

L’IA doit relire, dans cet ordre :
1. le contrat local UC concerné ;
2. la méthode cible dans le PORT UC ;
3. la méthode cible dans l’ADAPTER UC ;
4. les méthodes déjà validées du même ADAPTER UC ;
5. si le même ADAPTER UC ne fournit pas assez d’exemples stables, les méthodes déjà validées des autres ADAPTER UC du projet portant un scénario comparable.

Objectif :

➡️ L’IA doit déduire le formalisme de commentaires à partir du code validé réellement lu.  
➡️ L’IA ne doit jamais inventer un style de commentaires.

Règles obligatoires de rédaction dans l’ADAPTER UC :
- uniquement des commentaires de bloc ;
- un commentaire = un bloc logique précis du code immédiatement situé dessous ;
- le commentaire doit décrire ce que fait réellement le bloc ;
- le commentaire doit être rédigé du point de vue du comportement observable côté UC ;
- le commentaire doit citer les constantes réelles du code lorsqu’elles structurent le comportement observable ;
- le commentaire doit citer le type exact d’exception lorsque ce type fait partie du comportement attendu ;
- le commentaire doit distinguer explicitement :
  - l’erreur utilisateur bénigne,
  - la précondition bloquante,
  - la délégation au GATEWAY,
  - la sécurisation technique,
  - la préparation de la réponse utilisateur,
  - le positionnement du message observable,
  - le retour final ;
- le commentaire de succès ne doit apparaître qu’après préparation complète de la réponse utilisateur ;
- les commentaires courts de fin de méthode doivent rester sobres et littéraux.

Formes obligatoires à reproduire lorsque le scénario s’y prête :
- `Erreur utilisateur bénigne : ...`
- `Si ... : émet MESSAGE_X + LOG + ExceptionY.`
- `Délègue au GATEWAY ...`
- `Une réponse technique null du GATEWAY est une anomalie ...`
- `Retire les null, trie ...`
- `Positionne le message observable ...`
- `retourne ...`

Interdictions absolues :
- inventer un nouveau style de commentaires ;
- écrire des commentaires philosophiques, vagues ou décoratifs ;
- écrire un commentaire qui n’annonce pas exactement ce que fait le bloc juste dessous ;
- écrire un commentaire qui contredit les constantes, messages ou exceptions réellement utilisées ;
- écrire un commentaire plus faible ou plus flou que ceux des méthodes déjà validées ;
- commenter une intention générale de méthode au lieu du bloc concret réellement exécuté.

Règle de contrôle :

Avant de livrer du code ADAPTER UC, l’IA doit vérifier explicitement que les commentaires générés :
1. correspondent au bloc situé juste dessous ;
2. reprennent le vocabulaire déjà validé du projet ;
3. n’introduisent aucune formule nouvelle non lue dans les méthodes de référence ;
4. restent homogènes avec les commentaires déjà validés dans les ADAPTER UC du projet.

### 15.2) Règles sacrées anti-régression de génération de code

Les règles suivantes sont absolues et doivent être relues avant toute génération de code.

#### 15.2.1) Comparaisons de chaînes dans ce projet

Interdiction absolue :
- ne jamais générer `StringUtils.equalsIgnoreCase(...)`.

Règle obligatoire :
- utiliser exclusivement la convention du projet :
  - `Strings.CI.equals(...)`
  - `Strings.CI.compare(...)`

Conséquence :
- si une comparaison insensible à la casse apparaît dans une méthode générée, l’IA doit vérifier qu’elle utilise bien `Strings.CI`.

#### 15.2.2) Commentaires de bloc dans les ADAPTER UC

Interdiction absolue :
- ne jamais écrire un commentaire vague, décoratif, philosophique ou détaché du bloc situé juste dessous.

Règle obligatoire :
- un commentaire de bloc doit décrire exactement le bloc immédiatement dessous ;
- il doit employer les constantes, messages et exceptions réels du code ;
- il doit reprendre le style déjà validé dans les ADAPTER UC du projet.

Conséquence :
- avant toute livraison, l’IA doit comparer ses commentaires générés à ceux des méthodes déjà validées de l’ADAPTER concerné.

#### 15.2.3) Mockito strict

Interdiction absolue :
- ne jamais laisser un `when(...)` qui ne correspond à aucun appel réellement exécuté.

Règle obligatoire :
- chaque stub Mockito doit être justifié par le scénario testé ;
- aucun stub "au cas où" n’est toléré.

Conséquence :
- avant toute livraison de test Mock, l’IA doit relire le scénario ligne à ligne et supprimer tout stub inutilisé.

#### 15.2.4) Constantes de tests

Interdiction absolue :
- ne jamais disperser des littéraux métier dans les tests lorsque la classe suit déjà une convention de constantes.

Règle obligatoire :
- réutiliser les constantes déjà présentes dans la classe de test ;
- si une nouvelle valeur métier est nécessaire, la déclarer d’abord dans la zone des constantes de la classe.

#### 15.2.5) Pureté de la preuve BD dans les tests d’intégration

Interdiction absolue :
- ne pas affaiblir les tests d’intégration Produit à un simple contrôle par `service.count()` lorsque les classes de référence valident la preuve BD avec `JdbcTemplate`.

Règle obligatoire :
- les scénarios importants de Produit (`creer`, `update`, `delete`, absence d’écriture BD, absence de doublon, conservation d’ID) doivent remonter au niveau de preuve BD de `TypeProduit` et `SousTypeProduit`, avec lecture SQL directe via `JdbcTemplate`.

Conséquence :
- avant toute livraison d’un test d’intégration Produit, l’IA doit relire les tests d’intégration validés de `TypeProduit` et `SousTypeProduit` et vérifier que le niveau de preuve BD n’est pas inférieur.


#### 15.2.6) Contrôle canonique des tests d’une méthode de SERVICE GATEWAY

Interdiction absolue :
- ne jamais conclure trop tôt sur la qualité des tests d’une méthode de SERVICE GATEWAY ;
- ne jamais découvrir progressivement de nouvelles lacunes à chaque passe alors que ces lacunes étaient déjà déductibles du contrat et des tests effectivement lus ;
- ne jamais conclure `contrat respecté - complétude des tests assurée - ordre suivi - tests OK` après un contrôle partiel, implicite ou non structuré.

Règle obligatoire :
- avant toute conclusion sur les tests associés à une méthode d’un SERVICE GATEWAY, l’IA doit appliquer un contrôle canonique unique et exhaustif selon l’ordre suivant :
  1. lire d’abord le contrat de la méthode dans le PORT du SERVICE GATEWAY ;
  2. établir la liste exhaustive, ordonnée et explicite de tous les cas du contrat ;
  3. vérifier qu’à chaque cas du contrat correspond un test dédié, clair, pédagogique, documenté en javadoc et en commentaires de bloc, et complet dans ses assertions ;
  4. vérifier que tous les tests portant sur cette même méthode sont rangés dans le même ordre que les cas exposés dans le contrat du PORT ;
  5. vérifier que le `@DisplayName` de chaque test est exactement cohérent avec ce que le test prouve réellement ;
  6. vérifier que le `@Tag` de chaque test est cohérent avec la méthode testée et la zone fonctionnelle ;
  7. vérifier l’absence de doublons fonctionnels inutiles, de tests manquants, de preuves partielles, et d’asymétries entre variantes voisines du contrat ;
  8. vérifier que les assertions couvrent explicitement, lorsque le contrat le prévoit : le type d’exception, le message, la cause propagée, l’appel DAO, l’absence d’appel DAO, les paramètres transmis, l’ordre contractuel, les invariants métier et les invariants techniques ;
  9. produire une matrice explicite `cas du contrat -> test correspondant -> verdict`.

Points de vigilance obligatoires :
- l’IA doit contrôler explicitement les symétries de preuve, par exemple :
  - `message non nul` / `message null` ;
  - `DAO retourne null` / `content retourne null` ;
  - `requête null` / `requête neutre non nulle` ;
  - `cas nominal pur` / `cas nominal enrichi` ;
  - `doublon strict` / `doublon à la casse près` ;
- l’IA doit distinguer :
  - un test réellement distinct couvrant une variante contractuelle utile ;
  - d’un doublon fonctionnel inutile qui ne fait que répéter une preuve déjà acquise ;
- l’IA doit contrôler la complétude probatoire réelle du test, et non la seule présence apparente du scénario dans le fichier.

Conséquence :
- l’IA ne peut conclure `contrat respecté - complétude des tests assurée - ordre suivi - tests OK` que si tous les points ci-dessus sont validés en une seule passe de contrôle ;
- si un seul point manque, l’IA doit livrer en une seule fois la liste exhaustive de toutes les lacunes restantes ;
- toute nouvelle passe ultérieure ne doit pas révéler une lacune qui était déjà détectable lors de la première lecture complète du contrat et des tests.

Sacralisation :
- cette règle constitue la règle durable de contrôle des tests d’une méthode de SERVICE GATEWAY ;
- elle doit être relue avant tout audit, diagnostic, refactoring ou validation finale portant sur des tests de SERVICE GATEWAY ;
- elle s’applique en priorité à toute méthode de PORT GATEWAY et à l’ensemble des classes de tests Mock / Intégration / autres tests associés à cette méthode.

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

L’IA doit pouvoir être pilotée comme un collaborateur technique travaillant exclusivement sur pièces, sans jamais dépendre d’informations implicites.

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

### 22.1) Règle de livraison des fichiers fragiles (`.md`, `.yaml`, `.py`)

Pour tout fichier `.md`, `.yaml` ou `.py`, l’IA ne doit jamais livrer :
- un snippet ;
- un extrait partiel ;
- un bloc isolé ;
- un fragment à recoller ;
- un patch implicite ;
- un `.zip`.

Règle obligatoire :
- l’IA doit livrer **uniquement le fichier complet** ;
- au bon format ;
- avec la bonne structure ;
- les bonnes indentations ;
- les bonnes lignes vides ;
- un contenu directement intégrable dans STS.

Forme de livraison obligatoire :
- directement dans le chat ;
- **1 fichier = 1 bloc autonome complet** ;
- chaque fichier doit être précédé de la ligne exacte : `Chemin STS : <chemin exact dans le projet>` ;
- le chemin annoncé doit être le chemin réel exact du fichier dans le dépôt ;
- sous forme d’un bloc de code autonome correspondant à un fichier complet ;
- visible intégralement avant copie ;
- avec le bouton **copier** du bloc comme mode nominal de récupération ;
- sans archive `.zip` ;
- sans fichier joint comme mode nominal ;
- sans lien de téléchargement comme mode nominal.

Interdictions complémentaires :
- un simple lien de téléchargement ne remplace jamais le bloc visible ;
- un fichier joint ne remplace jamais le bloc visible ;
- l’utilisateur doit toujours pouvoir relire le contenu complet dans le chat avant copie ;
- l’IA ne doit jamais rebaptiser le fichier livré ;
- l’IA ne doit jamais remplacer une partie du contenu par `...` ou par une omission implicite ;
- l’IA ne doit jamais intercaler une explication à l’intérieur du fichier livré ;
- l’utilisateur ne doit jamais avoir à reconstruire le résultat final.

Préférence forte de présentation :
- lorsqu’un fichier généré admet naturellement les commentaires sans effet de bord, l’IA doit préférer insérer en tête du fichier un commentaire portant son chemin exact ;
- si le fichier possède déjà un en-tête canonique du projet, l’IA doit le conserver et l’utiliser comme forme prioritaire ;
- cette préférence ne doit jamais casser le format du fichier ni contredire un en-tête déjà validé.

Exception strictement encadrée :
- si un fichier est trop gros pour être livré directement dans le chat, l’IA doit l’indiquer explicitement ;
- dans ce seul cas, l’IA peut livrer un lien de téléchargement du fichier complet ;
- cette exception doit rester exceptionnelle.

Règle de volumétrie :
- l’IA peut et doit livrer en plusieurs fois (plusieurs échanges dans le chat) si l’ensemble des fichiers à livrer est trop lourd ;
- cette fragmentation par échanges n’autorise jamais les snippets : chaque bloc doit rester un fichier complet autonome.

Conséquence :
- pour tout fichier fragile déjà existant dans le dépôt, l’IA doit d’abord le relire intégralement avant de le réécrire ;
- pour tout nouveau fichier fragile, l’IA doit livrer le fichier entier et jamais un extrait.

## 23) Règle de couplage CODE/BUNDLE (Variante A — 2 SHAs)

Définitions :
- **SHA1 (CODE)** : SHA du commit/push contenant les corrections fonctionnelles/techniques (hors bundle).
- **SHA2 (BUNDLE)** : SHA du commit/push ne contenant que le bundle OFFLINE généré pour le **SHA1**.

Règles obligatoires :

1. **Interdiction de modifications hors bundle entre SHA1 et SHA2**
   - Entre le push de **SHA1** et le push de **SHA2**, aucun fichier ne doit être modifié en dehors de `AI_OFFLINE/**` (et éventuellement du zip `AI_OFFLINE_*.zip` stocké sous `AI_OFFLINE/**`).
   - Si un fichier hors `AI_OFFLINE/**` diffère entre **SHA1** et **SHA2** :
     ➜ le SHA2 n’est plus un “bundle de SHA1”  
     ➜ le cycle Variante A est considéré invalide.

2. **Provenance du bundle**
   - Le fichier `AI_OFFLINE/PROVENANCE.yaml` doit référencer explicitement le **SHA1 (CODE)** comme SHA de référence du contenu packagé (le bundle peut être committé au SHA2).
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

➡️ Garantir que le commit/push **SHA2** est strictement un ajout d’artefact OFFLINE pour le **code validé** au **SHA1**, sans altérer le code ni les contrats.

## 24) Règle de rafraîchissement de la baseline (précondition absolue)

Principe :

➡️ La **baseline consolidée** est la **seule source de vérité opérationnelle** pour l’analyse, le diagnostic et la génération de code. Le GitHub au SHA sert uniquement à **rafraîchir** la baseline après chaque commit/push.

Précondition absolue (ANTI-ANALYSE-SANS-BASELINE-A-JOUR) :
- Toute analyse/diagnostic/codage est **interdit** tant que la baseline n’est pas **parfaitement à jour** avec le **SHA courant** fourni par l’Utilisateur.
- Dès qu’un nouveau SHA est fourni, l’IA doit relire au minimum les ressources nécessaires (selon le périmètre) sur GitHub au SHA, puis mettre à jour la baseline.

Mise à jour obligatoire après lecture GitHub réussie :
- Après chaque lecture GitHub au SHA réalisée selon **RT-LECTURE-GITHUB-02** et jugée techniquement parfaite (lecture binaire, contenu non altéré, génériques lisibles, pas de Raw Types), l’IA doit :
  1. **Mémoriser strictement ligne à ligne** le contenu lu depuis GitHub,
  2. **Écraser** la version précédente du même fichier en baseline,
  3. **Consolider** la baseline (une seule version par chemin : la plus récente),
  4. Considérer la baseline comme unique matériau autorisé pour l’analyse.

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
  ➜ à échec persistant : basculer en MODE OFFLINE (bundle) selon le présent contrat

Objectif :

➡️ Garantir que toute analyse/correction est **reproductible**, **audit-ready** et strictement basée sur une baseline **consolidée** et **alignée** sur le SHA fourni.

### Règle d’exploitation de la baseline consolidée (ANTI-RELECTURE-GITHUB-BUNDLE)

Principe :

➡️ Une fois la baseline **rafraîchie** et **consolidée** au **SHA courant** (lecture parfaite + consolidation réalisées), l’IA est autorisée à **analyser / diagnostiquer / coder** uniquement à partir des fichiers de la **baseline**, sans relecture GitHub ni bundle, tant que le SHA courant reste inchangé.

Règle absolue (ANTI-RELECTURE-APRES-BASELINE-A-JOUR) :
- Si la baseline est jugée **parfaitement à jour** au SHA courant (lecture parfaite + consolidation réalisées), l’IA **ne doit pas** relire GitHub/bundle “par prudence” pour travailler : la baseline devient le **matériau unique** de travail opérationnel.
- GitHub au SHA et le bundle OFFLINE ne servent ensuite **qu’à** :
  1. rafraîchir la baseline lors d’un **nouveau SHA**,
  2. résoudre un **incident de lecture**,
  3. satisfaire une **demande explicite** de relecture par l’Utilisateur.

Traçabilité :
- Le bloc **PREUVE DE LECTURE** peut être produit à partir des **preuves déjà établies** lors du rafraîchissement (URL Raw SHA utilisée lors de la lecture, taille, checksum), sans nécessiter une nouvelle lecture GitHub/bundle tant que le SHA courant n’a pas changé.

Révocation immédiate :
- Si l’IA constate un doute sur la baseline (fichier requis manquant, incohérence, génériques illisibles/douteux, divergence constatée, ou **incident de lecture**) :
  ➜ signaler explicitement **"incident de lecture"**  
  ➜ suspendre toute analyse/génération  
  ➜ relancer la lecture (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02  
  ➜ à échec persistant : basculer en MODE OFFLINE

### Bloc PREUVE DE LECTURE (OBLIGATOIRE)

Toute réponse contenant une **analyse**, un **diagnostic** ou du **code** DOIT commencer par un bloc **PREUVE DE LECTURE** listant au minimum :
- **SHA courant**
- **Source de lecture** de `docs/ai/CONTRAT_IA.md` : `GITHUB_RAW_SHA` / `BUNDLE_OFFLINE` / `CHAT`
- **URL Raw SHA** utilisée (si source = `GITHUB_RAW_SHA`, sinon `N/A`)
- **Chemin local** utilisé (si source = `BUNDLE_OFFLINE` ou `CHAT`, sinon `N/A`)
- **Taille** (en octets) du fichier lu
- **Nombre de lignes** du fichier lu
- **Checksum** du contenu lu (SHA-256)

Interdiction : aucune analyse/diagnostic/génération ne doit être produite tant que ce bloc n’a pas été affiché et que la baseline n’a pas été rafraîchie/consolidée au SHA courant.

## 25) Détection des modifications au nouveau SHA (comparaison GitHub ↔ baseline)

Principe :

➡️ À chaque **nouveau SHA unique** fourni par l’Utilisateur, l’IA doit être capable de **détecter et constater** les modifications par rapport à la baseline consolidée, uniquement à partir de lectures GitHub au SHA (RT-LECTURE-GITHUB-02) et/ou du bundle OFFLINE.

Algorithme obligatoire (à réception d’un nouveau SHA) :

1. **Pré-lecture d’ancrage (obligatoire)**
   - Lire au SHA : `docs/ai/MANIFEST_IA.yaml`, `docs/ai/perimetre.yaml`, `docs/ai/CONTRAT_IA.md`.

2. **Découverte du périmètre**
   - Extraire la liste canonique des `paths` depuis `docs/ai/perimetre.yaml`.
   - Reconstruire toutes les URLs Raw SHA : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`.

3. **Détermination des fichiers requis (performance)**
   - Par défaut, l’IA rafraîchit **uniquement** les fichiers **nécessaires** à la demande courante :
     - pivots (`docs/ai/CONTRAT_IA.md`, `docs/ai/MANIFEST_IA.yaml`, `docs/ai/perimetre.yaml`),
     - fichiers explicitement concernés (PORT/ADAPTER/tests/document de contrat),
     - fichiers strictement indispensables à la compréhension/compilation de la zone (si besoin).
   - Si l’Utilisateur demande explicitement un **rafraîchissement complet**, alors l’IA traite **tous** les `paths` du périmètre.

4. **Lecture + contrôle technique (RT-LECTURE-GITHUB-02)**
   - Pour chaque fichier requis :
     - télécharger en binaire (octets bruts) via Raw SHA,
     - lire localement,
     - vérifier la cohérence (non vide, non HTML d’erreur, non tronqué),
     - vérifier les génériques lorsque applicable (aucun “Raw Type” dû à une lecture incorrecte).

5. **Comparaison stricte avec la baseline**
   - Comparer strictement le contenu lu au SHA avec la version correspondante en baseline consolidée :
     - **Fichiers texte** : comparaison **ligne à ligne** avec normalisation **CRLF/LF**.
     - **Fichiers binaires** : comparaison **octet à octet**.
   - Classer chaque fichier dans l’un des statuts :
     - **INCHANGÉ** : identique à la baseline,
     - **MODIFIÉ** : différence constatée,
     - **INACCESSIBLE** : non lisible au SHA (fichier manquant / erreur persistante).

6. **Signalements obligatoires**
   - Si un fichier attendu devient **INACCESSIBLE** :
     ➜ signaler explicitement **"problème grave"** (périmètre non satisfaisable au SHA),  
     ➜ suspendre analyse/génération.
   - Si la lecture d’un fichier est douteuse (ex : génériques illisibles) :
     ➜ signaler explicitement **"incident de lecture"**,  
     ➜ relancer automatiquement jusqu’à succès (max 3 tentatives),  
     ➜ à échec persistant : basculer en MODE OFFLINE.

7. **Mise à jour de la baseline (obligatoire, après lectures parfaites)**
   - Après lecture parfaite au SHA :
     - mémoriser strictement ligne à ligne dans la baseline,
     - écraser l’ancienne version,
     - consolider la baseline (une seule version par chemin : la plus récente).

Interdiction :
- Il est **interdit** de démarrer une analyse/diagnostic/génération tant que :
  - la comparaison GitHub ↔ baseline n’a pas été effectuée au SHA courant,
  - et la baseline n’a pas été rafraîchie/consolidée lorsque des modifications existent.

Objectif :

➡️ Garantir que l’IA peut, à partir du **SHA seul**, (1) découvrir, (2) lire, (3) comparer, (4) prouver les modifications, et (5) travailler uniquement sur une baseline à jour.

## 26) Méthode d’analyse d’alignement PORT–ADAPTER (procédure + analyse en 7 points)

Objectif : analyser l’alignement **PORT ↔ ADAPTER** **méthode par méthode**, de façon exhaustive et contrôlée, avec une boucle de correction reproductible (SHA-only), sans jamais “sauter” un défaut sérieux.

### 26.1 Principe de progression (gating strict)

- L’IA analyse **une seule méthode à la fois**.
- **Interdiction** de passer à la méthode suivante tant que l’analyse profonde détecte un problème sérieux sur la méthode courante (contrat incorrect, implémentation déficiente, tests incomplets/non alignés, etc.).
- Le **passage à la méthode suivante** est **décidé uniquement par l’Utilisateur**.
- **RAPPEL** : ne pas analyser les **LOG** ni leurs imports.

### 26.2 Analyse profonde en 7 points (checklist obligatoire, méthode par méthode)

Pour chaque méthode analysée (ex : « analyser l’alignement du PORT GATEWAY `ProduitGatewayIService` avec l’ADAPTER GATEWAY `ProduitGatewayJPAService` »), l’IA applique **strictement** les points suivants, **dans l’ordre**, et **ne passe pas au point suivant** tant qu’un problème sérieux subsiste :

1. **Qualité des contrats dans le PORT**
   - Signature, exceptions, préconditions, cohérence des types, cohérence des messages/constantes de contrat.

2. **Qualité du code de l’ADAPTER**
   - Implémentation conforme au contrat du PORT, robustesse, gestion des cas limites, cohérence avec l’architecture.

3. **Javadoc du PORT**
   - Vérifier que chaque javadoc de méthode contient bien les 3 rubriques :
     - **INTENTION TECHNIQUE**
     - **CONTRAT TECHNIQUE**
     - **GARANTIES TECHNIQUES et METIER**
   - Contrôler l’homogénéité du formalisme HTML (`div`, `ul`, `li`, exceptions jetées, etc.).
   - **RAPPEL** : respecter systématiquement la javadoc existante et son formalisme HTML (sauf si elle est fausse).

4. **Javadoc de l’ADAPTER**
   - Contrôler la javadoc (hors `{@inheritDoc}`) des méthodes **privées** de l’ADAPTER : intention/contrat/garanties lorsque pertinent + respect du style HTML du projet.

5. **Commentaires (de bloc) dans l’ADAPTER**
   - Vérifier la présence, la cohérence et l’homogénéité des commentaires de bloc existants ; ne pas les dégrader ; les reproduire dans le code généré.

6. **Détection exhaustive des défauts**
   - Rechercher **absolument tout** ce qui ne va pas en vue d’une correction finale.
   - Examiner les remarques **point par point pour la méthode en cours**, **pas tout d’un coup**.

7. **Alignement et complétude des tests JUnit**
   - Vérifier l’alignement et la complétude des tests relatifs à la méthode :
     - **Test Mock** + **Test d’intégration**
   - Vérifier l’homogénéité du code (PORT/ADAPTER/tests) avec le code similaire existant (parent/enfants).

Précondition absolue :
**lecture stricte** (ligne à ligne, CRLF/LF normalisés) de `docs/ai/CONTRAT_IA.md` et des fichiers nécessaires **avant toute analyse** ou **tout code** (cf. §24, RT-LECTURE-GITHUB-02).

### 26.3 Boucle opérationnelle (7 points)

1. **Détection d’un défaut d’alignement** :
   l’IA produit une première correction **autonome** et **directement intégrable dans STS** (javadoc + méthode complète / classe complète selon le besoin), en précisant **où** l’intégrer (classe + méthode / fichier concerné).

2. L’Utilisateur **applique** la correction dans STS et **rejoue les tests unitaires**.

3. Si tests **verts**, l’Utilisateur **commit/push** et fournit un **nouveau SHA unique**.

3bis. Si tests **KO**, l’Utilisateur demande une **réécriture** de la correction ou du test incriminé.

4. L’IA relit sur GitHub au **SHA** (exclusivement via **RT-LECTURE-GITHUB-02**) et, en l’absence d’incident de lecture, **mémorise** le(s) fichier(s) corrigé(s) en baseline, puis **consolide** la baseline (une seule version par chemin).

5. À partir de là, l’IA **vérifie** la bonne application de la correction et **relance l’analyse** de **la même méthode** directement **depuis la baseline** (sans relecture GitHub/bundle).

5bis. Si l’alignement est parfait : l’IA répond **uniquement** :
`alignement parfait`

5ter. Sinon : l’IA propose une **nouvelle correction autonome** directement intégrable STS → retour au point **2**.

6. Quand **plus aucun défaut** ne subsiste sur la méthode courante : l’IA répond **uniquement** :
`alignement parfait`
→ l’Utilisateur peut décider de passer à la méthode suivante.

### 26.4 Compatibilité avec les modes (LECTURE / DIAGNOSTIC / CODER)

- Cette procédure s’applique aux analyses d’alignement en **MODE DIAGNOSTIC**.
- Toute production de code reste soumise à la règle :
  **MODE CODER uniquement si “coder” est explicitement demandé**.
- En cas de baseline non à jour au SHA courant, ou d’**incident de lecture**, la procédure est **suspendue** jusqu’à restauration d’une baseline saine (cf. §24 et §6).

---

## 27) Gouvernance du `docs/ai/CONTRAT_IA.md` (stabilité et changements batchés)

Objectif : éviter la dérive et les micro-changements continus du CONTRAT, tout en permettant des évolutions contrôlées, auditables et efficaces.

### 27.1 Principe de stabilité

- Par défaut, `docs/ai/CONTRAT_IA.md` est considéré **VALIDÉ** (verrouillé).
- L’IA **ne propose pas** de modifications du CONTRAT “à chaque échange”.
- Les suggestions d’amélioration peuvent être listées en **backlog** (dans le chat), mais **sans** modifier le CONTRAT tant que l’Utilisateur n’a pas explicitement demandé une mise à jour.

### 27.2 Déclenchement explicite d’une mise à jour (gating)

Une mise à jour du CONTRAT n’est autorisée que si l’Utilisateur la demande explicitement (exemples : “MAJ CONTRAT”, “mettre à jour le CONTRAT”, ou message contenant **coder** + demande de consolidation du CONTRAT).

Dans ce cas :
1. L’IA prépare une **proposition consolidée unique** (toutes les modifications nécessaires) au format **fichier complet** `docs/ai/CONTRAT_IA.md` (ANTI-SNIPPETS).
2. L’Utilisateur applique la mise à jour, commit/push, fournit un **nouveau SHA**.
3. L’IA relit au SHA via **RT-LECTURE-GITHUB-02**, puis **consolide** la baseline.
4. Le CONTRAT repasse en statut **VALIDÉ** (verrouillé) jusqu’à la prochaine demande explicite.

### 27.3 Baseline potentiellement “en avance” sur le bundle OFFLINE

- La baseline peut être **en avance** sur le bundle OFFLINE si l’Utilisateur a committé des changements (contrat, code) sans avoir encore produit/committé un nouveau bundle.
- Dans ce cas, la source de vérité reste l’ordre défini en §1 : baseline (si prouvée à jour) > GitHub@SHA > bundle OFFLINE.
- Le bundle OFFLINE est un **artefact** de continuité : il peut être produit ultérieurement (cf. Variante A) sans bloquer la progression, tant que la baseline est prouvée saine au SHA courant.

### 27.4 Exigences de performance (Java)

- Pour les fichiers **texte** (Java et assimilés), la comparaison opérationnelle est **ligne à ligne** avec normalisation **CRLF/LF**.
- L’IA doit privilégier le **rafraîchissement minimal par objectif** (cf. §25) afin d’éviter de relire un périmètre inutilement large quand la tâche porte sur un sous-ensemble (PORT/ADAPTER/tests).

## 28) Règles prioritaires invariantes (sacralisation) — RT-PRIORITAIRE-* / RT-MEMORISATION-* / REGLE-COMMENTAIRE-JAVADOC-07

Cette section sacralise explicitement des règles déjà appliquées dans le workflow, afin d’éliminer toute ambiguïté.

---

### 28.1 RT-PRIORITAIRE-00 (VALIDÉ) — Relecture des règles avant tout code

Avant toute génération de code (MODE CODER), l’IA DOIT relire strictement :
- les règles de travail du présent CONTRAT,
- les règles de qualité et d’architecture,

et NE DOIT JAMAIS deviner / supposer / improviser.

---

### 28.2 RT-PRIORITAIRE-01 (VALIDÉ) — Relecture des fichiers baseline avant tout code

Avant toute génération de code (MODE CODER), l’IA DOIT relire strictement ligne à ligne tous les fichiers de travail nécessaires dans la baseline afin d’en déduire sans hypothèse :
- types de retour des méthodes,
- imports,
- dépendances entre classes,
- contraintes et invariants (y compris listes défensives immutables),
- modificateurs d’accès (public/private/protected),
- signatures exactes et surcharges.

---

### 28.3 RT-PRIORITAIRE-02 (VALIDÉ) — Relecture strictement ligne à ligne

L’IA DOIT relire strictement ligne à ligne les fichiers concernés de la baseline (liste fournie le cas échéant ; sinon toute la baseline) avant toute action nécessitant ces fichiers.

---

### 28.4 RT-PRIORITAIRE-03 (VALIDÉ) — Déduction + preuve de relecture par signatures

Après application de RT-PRIORITAIRE-02, l’IA DOIT déduire (avant tout code) :
- constructeurs,
- méthodes (signatures exactes),
- types de retour,
- modificateurs,
- dépendances,
- contraintes.

Exigence de traçabilité :
- En MODE CODER, l’IA DOIT confirmer la relecture en listant explicitement les signatures exactes (constructeurs/méthodes), types et modificateurs pertinents pour la tâche demandée.
- L’IA ne doit pas oublier les fichiers explicitement cités par l’Utilisateur (ex : `TypeProduitGatewayJPAService.java`).

---

### 28.5 RT-PRIORITAIRE-100 (VALIDÉ) — Intangibilité de la baseline (hors ordre explicite)

Tout fichier déposé/mémorisé dans la baseline DOIT être conservé intégralement, ligne à ligne, sans modification.

Interdiction totale de modifier un fichier baseline tant que l’Utilisateur n’a pas ordonné explicitement de travailler dessus.

---

### 28.6 RT-PRIORITAIRE-101 (VALIDÉ) — Vérification baseline avant toute affirmation factuelle

Avant de répondre, analyser ou coder, l’IA DOIT vérifier dans la baseline les informations factuelles qu’elle avance (imports, signatures, constantes, etc.).

Interdiction d’écrire des hypothèses conditionnelles du type “si l’import X existe…”.

### 28.7 RT-PRIORITAIRE-102 (VALIDÉ) — Baseline unique source de vérité + consolidation

La baseline est l’unique source de vérité.

Toute lecture GitHub au SHA DOIT conduire à une consolidation de la baseline en écrasant toute version précédente (une seule version par path, la plus récente).

Aucun fichier baseline ne doit être oublié / égaré / perdu.

Modification uniquement sur ordre explicite de l’Utilisateur.

---

### 28.8 RT-LECTURE-GITHUB-02 — Clarification “génériques illisibles”

Si l’IA ne peut pas lire correctement les génériques (doute, troncature, rendu altéré, Raw Types possibles) :
- elle DOIT signaler un “incident de lecture”,
- elle DOIT demander confirmation (ou l’élément manquant) avant toute conclusion,
- elle DOIT relancer automatiquement la lecture (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02,
- puis basculer en MODE OFFLINE si échec persistant.

---

### 28.9 RT-MEMORISATION-UNICITE-01 (VALIDÉ) — Unicité de mémorisation

Lors de la mémorisation d’un élément (fichier, règle, chat, etc.), l’IA DOIT effacer les versions précédentes.

Invariant : ne conserver qu’une seule version, toujours la plus récente.

---

### 28.10 RT-MEMORISATION-COURANTE-02 (VALIDÉ) — “mémoriser. baseline” (analyse sans codage)

Commande : “mémoriser. baseline”

- Lire et analyser le fichier transmis (javadoc/code/commentaires),
- Stocker ligne à ligne sans modification dans la baseline en écrasant toute version précédente,
- Ne générer aucun code, aucune suggestion,
- Produire uniquement une synthèse structurée de mémorisation.

---

### 28.11 RT-MEMORISATION-SIMPLE-03 (VALIDÉ) — “mémoriser. baseline. Pas analyser. Pas coder”

Commande : “mémoriser. baseline. Pas analyser. Pas coder”

- Stocker ligne à ligne sans modification dans la baseline en écrasant toute version précédente,
- Aucun traitement immédiat, aucune analyse, aucun code, aucune suggestion,
- Produire uniquement une synthèse structurée de mémorisation.

---

### 28.12 REGLE-COMMENTAIRE-JAVADOC-07 (VALIDÉ) — Javadoc et commentaires (HTML + bloc)

- Les commentaires de l’Utilisateur doivent être reproduits en respectant le formalisme HTML `<div> ... </div>` sans lignes vides, sauf s’ils sont faux.
- Les commentaires ajoutés par l’IA doivent être exclusivement des commentaires de bloc (jamais de commentaires de ligne).
- La javadoc existante doit être conservée (sauf si fausse) et la javadoc ajoutée par l’IA doit respecter le style HTML du projet.

---

### 28.13 RT-PRIORITAIRE-CANAL-LECTURE-01 (VALIDÉ) — Sélection du canal de lecture GitHub@SHA avant “incident de lecture”

Objectif : éviter tout faux “incident de lecture” dû à l’échec d’un canal technique unique alors qu’un autre canal de téléchargement binaire est fonctionnel.

Règle prioritaire invariante :
- Lors d’une lecture GitHub@SHA (RT-LECTURE-GITHUB-02), l’IA DOIT tenter en priorité la lecture par **web tool**.
- Pour la jambe de **téléchargement binaire local**, l’IA DOIT utiliser en priorité la primitive dédiée **`container.download`**.
- Si la lecture par **web tool** échoue, ou si **`container.download`** échoue, l’IA DOIT basculer d’elle-même sur une autre technique **container** autorisée, sans attendre une nouvelle instruction de l’Utilisateur.
- Toutes les techniques doivent strictement respecter la même méthode contractuelle :
  - URL Raw@SHA
  - téléchargement binaire local (octets bruts)
  - lecture locale du fichier téléchargé
  - vérification stricte des génériques (aucun Raw Type)

Règles de tentative :
- L’IA DOIT effectuer au maximum **3 tentatives au total**.
- L’IA DOIT garantir **au moins une tentative par web tool**.
- L’IA DOIT garantir **au moins une tentative par `container.download`** avant de conclure à un “incident de lecture”.
- Si nécessaire, l’IA DOIT garantir **au moins une tentative par autre technique container autorisée** avant de conclure à un “incident de lecture”.

Règle de déclaration d’incident :
- L’IA NE DOIT déclarer **"incident de lecture"** que si :
  - la technique **web tool** a été tentée,
  - la technique **`container.download`** a été tentée,
  - une autre technique **container** a été tentée si nécessaire,
  - les tentatives ont échoué,
  - et que, pour chacune des tentatives, tout contenu manifestement invalide (HTML d’erreur, contenu vide, tronqué, incohérent) est traité comme un échec de tentative.

En cas de succès (par **web tool**, par **`container.download`** ou par une autre technique **container** autorisée) :
- L’IA poursuit la procédure RT-LECTURE-GITHUB-02 :
  - lecture locale du fichier téléchargé,
  - vérification de cohérence,
  - vérification stricte des génériques (aucun Raw Type).
- Si les génériques sont illisibles/douteux :
  - l’IA DOIT demander confirmation avant toute conclusion.

Fallback :
- Le MODE OFFLINE (bundle : PROVENANCE + CHECKSUMS + FILES) n’est autorisé qu’après un **incident de lecture** établi selon la présente règle :
  - web tool tenté,
  - `container.download` tenté,
  - autre technique container tentée si nécessaire,
  - ≤ 3 tentatives.

---
### 28.14 RT-DOWNLOAD-BINAIRE-LOCAL-01 (VALIDÉ) — Primitive locale préférée pour le téléchargement Raw@SHA

Objectif : éliminer les faux incidents de lecture dus à l’usage prioritaire d’une technique réseau shell fragile alors qu’une primitive locale de téléchargement binaire dédiée est disponible.

Règle obligatoire :
- Pour satisfaire la jambe **“téléchargement binaire local”** de RT-LECTURE-GITHUB-02, l’IA DOIT utiliser en priorité **`container.download`**.
- L’usage d’une commande réseau shell (`curl`, `wget`, etc. via `container.exec`) ne peut être qu’un **fallback** et ne doit jamais être la technique locale préférée.
- Toute technique de fallback doit continuer à respecter strictement :
  - l’URL Raw@SHA,
  - le téléchargement binaire local,
  - la lecture locale des octets bruts,
  - la vérification stricte des génériques.

Conséquences :
- L’IA NE DOIT PAS conclure à un **"incident de lecture"** tant que **`container.download`** n’a pas été tenté.
- Si **`container.download`** réussit, la lecture GitHub@SHA est réputée nominalement valide au titre de la jambe locale de téléchargement binaire.
- Si **`container.download`** échoue, l’IA DOIT relancer automatiquement selon la politique de tentatives puis basculer sur une autre technique **container** autorisée avant toute conclusion d’échec définitif.

Interdiction absolue :
- conclure à une indisponibilité GitHub, à un défaut de SHA, ou à une illisibilité d’un fichier alors que seule une technique réseau shell locale a échoué sans tentative préalable de **`container.download`**.

---


### 28.15 RT-LECTURE-LITTERALE-01 (VALIDÉ) — Lecture littérale ancrée sur la méthode exacte de référence

Objectif : éliminer définitivement les erreurs de lecture par reconnaissance de motif, généralisation de style ou relecture “avec une idée en tête”.

Règle prioritaire invariante :
- Avant toute analyse, affirmation, javadoc, test ou génération de code, l’IA DOIT verrouiller la méthode exacte de référence déjà validée dans le même fichier, la même couche, le même objet métier et la même famille de méthode.
- L’IA DOIT effectuer une lecture littérale ancrée sur les lignes exactes de cette méthode de référence.
- Interdiction formelle de relire “avec une idée en tête”, par reconnaissance de motif, par généralisation de style, ou à partir d’une conclusion antérieure non revérifiée.
- Aucune règle de style ne peut être déduite globalement d’une classe, d’une couche, d’un souvenir de lecture ou d’un lot précédent.

Vérification point par point obligatoire :
- présence ou absence de balises HTML de javadoc (`<div>`, `<p>`, `<ul>`, `<li>`),
- vocabulaire exact,
- structure des puces,
- annotations exactes,
- placement du trait final `} // __________________________________________________________________`,
- nombre exact de lignes vides entre méthodes,
- tout autre élément de formalisme visible dans les lignes relues.

Règle de conclusion :
- L’IA NE DOIT conclure qu’à partir des éléments matériellement visibles dans la méthode exacte relue.
- Si la méthode exacte de référence n’a pas été relue littéralement ligne à ligne, l’IA DOIT s’arrêter, le signaler, et ne pas livrer.

---

### 28.16 RT-COMPARAISON-TEXTE-CRLF-01 (VALIDÉ) — Normalisation EOL obligatoire avant toute décision de différence réelle

Objectif : éliminer définitivement les faux positifs de comparaison sur les fichiers texte entre STS/chat, GitHub@SHA et bundle OFFLINE.

Règle prioritaire invariante :
- Pour tout fichier texte (`.md`, `.yaml`, `.py`, `.java`, `.xml`, `.properties`, `.sh`, etc.), l’IA DOIT distinguer strictement :
  - l’identité binaire (octet à octet),
  - l’identité textuelle canonique après normalisation EOL,
  - l’écart EOF (présence ou absence d’un saut de ligne final).
- Pour la comparaison de contenu texte, l’IA DOIT construire une vue canonique en remplaçant :
  - `CRLF` par `LF`,
  - puis `CR` isolé par `LF`.
- La comparaison métier DOIT ensuite être réalisée ligne à ligne sur cette vue canonique.
- L’écart EOF DOIT être contrôlé et signalé séparément ; il ne doit jamais être confondu avec une différence de ligne.

Définition prioritaire de « le même fichier » :
- Pour un fichier texte, « le même fichier » ne signifie pas nécessairement « mêmes octets ».
- Pour un fichier texte, « le même fichier » signifie : **même contenu texte canonique après normalisation EOL**, sauf si le fichier est binaire ou si le présent CONTRAT / le format du fichier lui donne explicitement une sémantique spéciale.

Règle de décision :
- Si l’identité binaire diffère uniquement à cause de `LF` / `CRLF`, mais que le contenu texte canonique est identique, l’IA DOIT qualifier le cas de :
  - **FAUSSE DIFFÉRENCE BINAIRE — EOL ONLY**
- Dans ce cas :
  - le fichier DOIT être considéré comme **INCHANGÉ** sur le plan métier / contenu ;
  - l’IA NE DOIT PAS annoncer un fichier **MODIFIÉ** ;
  - cet écart NE DOIT PAS bloquer la comparaison, la consolidation, le diagnostic ni la génération ;
  - l’IA DOIT signaler séparément l’écart binaire si cela est utile à l’audit.

Cas explicitement visés :
- `docs/ai/CONTRAT_IA.md`
- `docs/ai/perimetre.yaml`
- plus généralement tout fichier texte comparé entre STS/chat, GitHub@SHA et bundle OFFLINE.

Conséquence projet :
- GitHub en `LF` et bundle OFFLINE en `CRLF` ne doivent jamais produire un faux statut **MODIFIÉ** tant que le contenu texte normalisé est identique.

## 29) Gouvernance canonique par couches (sacralisation progressive)

Objectif : permettre à l’IA de découvrir la structure complète de l’application, les contrats applicables et les formalismes associés, de façon **couche par couche**, avec un bootstrap reproductible, audit-ready et indépendant de la mémoire interne.

### 29.1 Nomenclature canonique des couches

Les couches canoniques du projet sont désormais :

1. `couche_ia`
2. `couche_configuration_tests`
3. `couche_metier`
4. `couche_dto`
5. `couche_persistance`
6. `couche_services`
7. `couche_controllers`
8. `couche_vues`

Sous-couches logiques admises :
- `couche_services.gateway`
- `couche_services.uc`
- `couche_persistance.jpa`

### 29.2 Couche bootstrap obligatoire : `couche_ia`

La première couche à lire avant toute action de l’IA est `couche_ia`.

Cette couche est **sacralisée** comme couche de bootstrap et doit être lue avant toute analyse, tout diagnostic, tout code, toute comparaison et toute consolidation.

La couche `couche_ia` comprend au minimum les fichiers suivants :

#### IA / gouvernance
- `docs/ai/CONTRAT_IA.md`
- `docs/ai/MANIFEST_IA.yaml`
- `docs/ai/perimetre.yaml`

#### Outils IA
- `tools/ai/make_offline_bundle.py`
- `tools/ai/pack_reading_list.py`
- `tools/ai/paths.txt`
- `tools/ai/perimetre_resolver.py`
- `tools/ai/resolve_raw_sha.py`

#### Contrats locaux à relire avant action de l’IA
- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`
- `docs/contrats/metier/TypeProduit.md`
- `docs/contrats/metier/SousTypeProduit.md`
- `docs/contrats/metier/Produit.md`
- `docs/contrats/dto/TypeProduitDTO.md`
- `docs/contrats/dto/SousTypeProduitDTO.md`
- `docs/contrats/dto/ProduitDTO.md`
- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`

### 29.3 Obligation de prélecture de `couche_ia`

Avant toute action, l’IA DOIT relire strictement `couche_ia` dans le dépôt au SHA courant ou dans la baseline consolidée si elle est à jour.

Règles :
- aucun nouveau chat ne doit s’appuyer sur la mémoire seule ;
- la découverte des règles, contrats, packs, couches et formalismes doit provenir du dépôt lui-même ;
- `couche_ia` doit être résolue depuis `docs/ai/perimetre.yaml` et bootstrapée via `tools/ai/paths.txt`.

### 29.4 Anticipation obligatoire des couches futures

L’IA ne doit pas raisonner seulement par liste ponctuelle de fichiers déjà rencontrés.

L’IA doit raisonner par **couches canoniques**.

Chaque couche canonique doit pouvoir être décrite par :
- un nom canonique ;
- un statut (`validated_locked`, `validated_unlocked`, `declared`, `reserved`) ;
- un ou plusieurs packs de périmètre ;
- des fichiers pivots ;
- des contrats locaux éventuels ;
- des tests Mock et d’intégration associés ;
- des règles de lecture applicables.

Une couche peut être pré-déclarée même si elle n’est pas encore totalement remplie.

Objectif :
- éviter toute réécriture du modèle IA lors de l’apparition de nouvelles classes ;
- permettre des audits globaux par couche ;
- garantir qu’une IA puisse découvrir la structure complète du projet même dans un nouveau chat.

### 29.5 Audit global par couche

L’Utilisateur doit pouvoir demander à l’IA un audit global d’une couche canonique.

Pour qu’un audit global soit possible, chaque couche doit permettre à l’IA de retrouver, comme un tout cohérent :
- les classes principales ;
- les utilitaires liés ;
- les tests JUnit Mock ;
- les tests JUnit d’intégration ;
- les contrats locaux applicables ;
- les formalismes spécifiques.

### 29.6 Sacralisation opérationnelle

La gouvernance par couches doit être matérialisée dans :
- `docs/ai/MANIFEST_IA.yaml`
- `docs/ai/perimetre.yaml`
- `tools/ai/paths.txt`
- `tools/ai/perimetre_resolver.py`

Règles :
- `couche_ia` doit exister comme pack explicite ;
- `tools/ai/paths.txt` doit contenir le bootstrap minimal complet de `couche_ia` ;
- `docs/ai/perimetre.yaml` doit déclarer les couches canoniques et leurs packs associés ;
- `tools/ai/perimetre_resolver.py` doit pouvoir exposer cette gouvernance par couches comme API canonique.

### 29.7 Sacralisation de `couche_configuration_tests`

La couche canonique `couche_configuration_tests` a vocation à regrouper l’ensemble des fichiers racine, build, CI et configuration de tests permettant à l’IA de comprendre comment construire, tester et valider les couches déjà vertes du projet.

Sous-couche logique admise :
- `couche_configuration_tests.racine_build_ci`

Cette sous-couche comprend au minimum :
- `.gitattributes`
- `.gitignore`
- `pom.xml`
- `README.md`
- `scripts/test_couches_validees.sh`
- `.github/workflows/maven.yml`

Règles :
- le script shell de lancement des tests validés ne doit pas être figé sur un nombre de couches ;
- son nom canonique devient `scripts/test_couches_validees.sh` ;
- ce script doit rester évolutif : il commence par jouer les tests des couches déjà validées, en particulier les services UC, puis il devra intégrer les tests des Controllers et des Vues lorsqu’ils seront créés et validés ;
- l’objectif est de disposer d’un build Maven jouant tous les tests déjà validés et passant verts.

La couche `couche_configuration_tests` doit donc permettre à l’IA de retrouver comme un tout cohérent :
- les règles racine du dépôt ;
- le build Maven ;
- le point d’entrée CI ;
- le script des couches validées ;
- les ressources de test déjà sacralisées.

### 29.8 Sacralisation de `couche_metier`

La couche canonique `couche_metier` a vocation à regrouper l’ensemble des objets métier, interfaces métier, utilitaires métier et tests associés permettant à l’IA de comprendre le noyau fonctionnel pur Java du projet.

Sous-couches logiques admises :
- `couche_metier.objets`
- `couche_metier.utilitaires`
- `couche_metier.tests`

Cette couche comprend au minimum :

#### Objets métier et interfaces liées
- `src/main/java/levy/daniel/application/model/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/model/metier/IExportateurJTable.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/CloneContext.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/Produit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/ProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduitI.java`

#### Utilitaires métier
- `src/main/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtils.java`

#### Tests métier et tests d’utilitaires métier
- `src/test/java/levy/daniel/application/model/metier/produittype/CloneContextTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/MetierGlobalConformiteTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/ProduitTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/SousTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitSousTypeProduitIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtilsTest.java`

#### Contrats locaux métier pivots
- `docs/contrats/metier/TypeProduit.md`
- `docs/contrats/metier/SousTypeProduit.md`
- `docs/contrats/metier/Produit.md`

Règles :
- `couche_metier` doit être auditée comme un tout cohérent, et non fichier par fichier isolé ;
- les objets métier racines `TypeProduit`, `SousTypeProduit` et `Produit` constituent les pivots documentaires de la couche ;
- les utilitaires métier liés, notamment `NormalizerUtils`, appartiennent à `couche_metier` ;
- les tests métier et les tests utilitaires métier appartiennent également à `couche_metier` ;
- les contrats locaux métier doivent permettre à l’IA de retrouver :
  - le rôle métier de l’objet,
  - ses invariants,
  - ses relations avec les autres objets métier,
  - les comportements observables,
  - les règles d’égalité, de comparaison, de mutabilité et de clonage lorsque pertinent,
  - les tests qui verrouillent ces comportements.

La couche `couche_metier` doit permettre à l’IA de retrouver comme un tout cohérent :
- les objets métier principaux ;
- les interfaces métier liées ;
- les utilitaires métier liés ;
- les tests unitaires métier ;
- les tests d’intégration métier ;
- les contrats locaux métier applicables.

### 29.9 Sacralisation de `couche_dto`

La couche canonique `couche_dto` a vocation à regrouper l’ensemble des DTO, des convertisseurs DTO et des tests associés permettant à l’IA de comprendre le contrat d’échange entre couches applicatives, en particulier entre CONTROLLER et SERVICE UC.

Sous-couches logiques admises :
- `couche_dto.objets`
- `couche_dto.convertisseurs`
- `couche_dto.tests`

Cette couche comprend au minimum :

#### DTO et convertisseurs DTO
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOSousTypeProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOTypeProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ProduitDTO.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/SousTypeProduitDTO.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/TypeProduitDTO.java`

#### Tests DTO et convertisseurs DTO
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOSousTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ProduitDTOTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/SousTypeProduitDTOTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/TypeProduitDTOTest.java`

#### Contrats locaux DTO pivots
- `docs/contrats/dto/TypeProduitDTO.md`
- `docs/contrats/dto/SousTypeProduitDTO.md`
- `docs/contrats/dto/ProduitDTO.md`

Règles :
- les DTO ne relèvent pas de `couche_persistance` ;
- les DTO servent de contrat d’échange entre couches applicatives, notamment CONTROLLER ↔ SERVICE UC ;
- les convertisseurs DTO appartiennent à `couche_dto` ;
- les tests DTO et convertisseurs DTO appartiennent à `couche_dto` ;
- `couche_dto` doit être auditée comme un tout cohérent, et non fichier par fichier isolé ;
- les pivots documentaires initiaux de la couche sont `TypeProduitDTO`, `SousTypeProduitDTO` et `ProduitDTO`.

Les contrats locaux DTO doivent permettre à l’IA de retrouver :
- le rôle du DTO ;
- la structure `InputDTO` / `OutputDTO` lorsque applicable ;
- les invariants de structure ;
- les contraintes de nullité ;
- les règles d’égalité / hashCode ;
- les règles sur les collections embarquées ;
- les interactions avec les convertisseurs ;
- les tests qui verrouillent ces comportements.

La couche `couche_dto` doit permettre à l’IA de retrouver comme un tout cohérent :
- les DTO principaux ;
- les convertisseurs DTO liés ;
- les tests unitaires DTO ;
- les tests unitaires des convertisseurs DTO ;
- les contrats locaux DTO applicables.

### 29.10 Sacralisation de `couche_persistance`

La couche canonique `couche_persistance` a vocation à regrouper l’ensemble des composants de persistance du domaine `produittype`, en particulier les interfaces transverses de persistance, les convertisseurs JPA ↔ métier, les entités JPA, les DAO JPA et les tests associés.

Sous-couche logique admise :
- `couche_persistance.jpa`

Cette couche comprend au minimum :

#### Interfaces transverses de persistance
- `src/main/java/levy/daniel/application/persistence/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/persistence/metier/IExportateurJTable.java`

#### Entités JPA / convertisseurs
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java`

#### DAO JPA
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPA.java`

#### Tests persistance
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetierTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPARattachementDetachementTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPATest.java`

#### Contrat local persistance pivot
- `docs/contrats/persistance/CouchePersistance.md`

Règles :
- `couche_persistance` porte les composants de persistance et jamais la logique UC ;
- la duplication éventuelle d’interfaces transverses entre `couche_metier` et `couche_persistance` est admise lorsqu’elle sert l’autonomie stricte des couches ;
- les convertisseurs JPA ↔ métier appartiennent à `couche_persistance` ;
- les DAO JPA appartiennent à `couche_persistance` ;
- les tests de persistance appartiennent à `couche_persistance` ;
- la couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La couche `couche_persistance` doit permettre à l’IA de retrouver comme un tout cohérent :
- les interfaces transverses de persistance ;
- les convertisseurs JPA ↔ métier ;
- les entités JPA ;
- les DAO JPA ;
- les tests de persistance ;
- le contrat local de couche applicable.

### 29.11 Sacralisation de `couche_services.gateway`

La sous-couche logique `couche_services.gateway` a vocation à regrouper l’ensemble des services techniques d’accès au stockage du domaine `produittype`, leurs exceptions Gateway, leur socle de pagination, leurs tests associés et leurs contrats locaux.

Cette sous-couche comprend au minimum :

#### PORTS Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

#### ADAPTERS Gateway JPA
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`

#### Exceptions Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliLibelleBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNonPersistent.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParentNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGateway.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGatewayNonPersistent.java`

#### Pagination transverse Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/DirectionTri.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/RequetePage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/ResultatPage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/TriSpec.java`

#### Tests Gateway et pagination
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/DirectionTriTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/RequetePageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/ResultatPageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/TriSpecTest.java`

#### Contrats locaux Gateway pivots
- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`

Règles :
- `couche_services.gateway` manipule les objets métier et jamais les DTO ;
- `couche_services.gateway` ne produit aucun message utilisateur et ne porte aucune logique UC ;
- les exceptions `exceptionsgateway` appartiennent à `couche_services.gateway` ;
- les exceptions `exceptionsservices` n’appartiennent pas à `couche_services.gateway` et relèvent du périmètre UC ;
- la pagination liée aux recherches paginées des Gateways appartient à `couche_services.gateway` ;
- les tests Mock et d’intégration Gateway appartiennent à la même sous-couche logique ;
- les contrats locaux JPA Gateway doivent exister pour `Produit`, `SousTypeProduit` et `TypeProduit` ;
- la sous-couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La sous-couche `couche_services.gateway` doit permettre à l’IA de retrouver comme un tout cohérent :
- les PORTS Gateway ;
- les ADAPTERS Gateway JPA ;
- les exceptions Gateway ;
- le socle de pagination transverse ;
- les tests Mock Gateway ;
- les tests d’intégration Gateway ;
- les contrats locaux Gateway applicables.

### 29.12 Sacralisation de `couche_services.uc`

La sous-couche logique `couche_services.uc` a vocation à regrouper l’ensemble des services métier de cas d’usage du domaine `produittype`, leurs exceptions de SERVICE UC, leurs tests associés et leurs contrats locaux.

Cette sous-couche comprend au minimum :

#### PORTS UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/cu/ProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/SousTypeProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/TypeProduitICuService.java`

#### ADAPTERS UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuService.java`

#### Exceptions services UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionDoublon.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionNonPersistant.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionStockageVide.java`

#### Tests services UC métier
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceMockTest.java`

#### Contrats locaux UC pivots
- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`

Règles :
- `couche_services.uc` manipule les DTO applicatifs lorsque le contrat de service l’exige ;
- `couche_services.uc` orchestre le métier et délègue les opérations techniques de stockage à `couche_services.gateway` ;
- `couche_services.uc` est responsable du message observable côté appelant via `getMessage()` ;
- les exceptions `exceptionsservices` appartiennent à `couche_services.uc` ;
- les exceptions `exceptionsgateway` n’appartiennent pas à `couche_services.uc` et relèvent du périmètre Gateway ;
- les tests Mock et d’intégration UC appartiennent à la même sous-couche logique ;
- la sous-couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La sous-couche `couche_services.uc` doit permettre à l’IA de retrouver comme un tout cohérent :
- les PORTS UC ;
- les ADAPTERS UC ;
- les exceptions de SERVICE UC ;
- les tests Mock UC ;
- les tests d’intégration UC ;
- les contrats locaux UC applicables.