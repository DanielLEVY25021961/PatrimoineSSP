# docs/contrats/metier/CoucheMetier.md

# Contrat de couche — Couche métier validée

## 1) Statut et objectif

La couche `couche_metier` est une couche `validated_locked`.

Objectif de ce contrat : fournir à l'IA les règles et techniques immuables nécessaires pour relire, contrôler et recoder en autonomie les fichiers métier validés du projet, sans improviser et sans dégrader le formalisme existant.

Ce contrat ne remplace pas les contrats locaux des objets. Il fixe les règles communes de la couche et impose la relecture des classes et tests concernés avant toute analyse ou codage.

## 2) Source de vérité et ordre de lecture

Avant toute action sur un fichier de la couche métier, l'IA doit lire dans cet ordre :

1. `docs/ai/CONTRAT_IA.md` ;
2. `docs/contrats/metier/CoucheMetier.md` ;
3. le contrat local de la classe concernée ;
4. la classe cible ;
5. les interfaces implémentées ;
6. les classes métier dépendantes ;
7. les utilitaires métier appelés ;
8. les tests unitaires et d'intégration concernés.

Ordre local obligatoire : contrat, classe cible, dépendances de la méthode, puis tests concernés.

Si une dépendance utile n'a pas été relue, l'IA doit déclarer la lecture incomplète et relire avant toute conclusion.

## 3) Périmètre validé de la couche métier

### 3.1 Objets métier et interfaces

- `src/main/java/levy/daniel/application/model/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/model/metier/IExportateurJTable.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/ProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/Produit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/CloneContext.java`

### 3.2 Utilitaires métier

- `src/main/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtils.java`

### 3.3 Tests validés

- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitTest.java` : 38 tests ;
- `src/test/java/levy/daniel/application/model/metier/produittype/SousTypeProduitTest.java` : 36 tests ;
- `src/test/java/levy/daniel/application/model/metier/produittype/ProduitTest.java` : 39 tests ;
- `src/test/java/levy/daniel/application/model/metier/produittype/CloneContextTest.java` : 10 tests ;
- `src/test/java/levy/daniel/application/model/metier/produittype/MetierGlobalConformiteTest.java` : 3 tests ;
- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitSousTypeProduitIntegrationTest.java` : 3 tests ;
- `src/test/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtilsTest.java` : 2 tests.

### 3.4 Convertisseurs DTO liés au métier

Les convertisseurs `Metier -> OutputDTO` appartiennent formellement à `couche_dto`, mais ils manipulent directement les objets métier validés. Lors d'une analyse métier impliquant une conversion, l'IA doit aussi relire :

- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOTypeProduit.java` ;
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOSousTypeProduit.java` ;
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOProduit.java` ;
- leurs tests associés.

Cette règle ne reclasse pas les convertisseurs en `couche_metier`. Elle crée un relais obligatoire lorsque le comportement métier observable dépend d'eux.

## 4) Règles communes de structure des objets métier

Les objets métier validés suivent une structure stable :

1. bannière commentaire historique ;
2. `package` ;
3. imports ;
4. Javadoc de classe ;
5. déclaration de classe avec interfaces ;
6. constantes ;
7. attributs ;
8. `Logger` ;
9. constructeurs ;
10. méthodes métier (`hashCode`, `equals`, affichage, comparaison, clonage, relations) ;
11. export CSV / JTable ;
12. getters / setters ;
13. méthodes internes de synchronisation ou d'aide ;
14. commentaire final de classe lorsqu'il existe.

Ne jamais réordonner mécaniquement une classe métier validée. L'ordre existant dans la classe cible est la référence à conserver, sauf demande explicite de l'utilisateur.

## 5) Règles communes de Javadoc et de commentaires

Le formalisme métier historique utilise majoritairement :

- Javadoc HTML avec `<div>`, `<p>`, `<ul>`, `<li>` ;
- commentaires de fin de méthode du type `} // Fin de ...` ou `} // __________________________________________________________________` selon le fichier ;
- commentaires explicatifs internes pour les sections critiques concurrentes ;
- vocabulaire métier explicite : `objet métier`, `parent`, `enfant`, `clone profond`, `snapshot`, `thread-safe`.

Règles obligatoires :

- conserver le style du fichier cible ;
- ne pas convertir un formalisme historique validé vers un autre formalisme ;
- ne pas remplacer les Javadocs HTML par une prose compacte ;
- ne pas supprimer les commentaires expliquant les verrous, snapshots ou bidirectionnalités ;
- ne pas introduire de commentaire abstrait inutile du type `vise la branche locale` ;
- décrire le comportement observable et l'intention technique réelle.

## 6) Égalité, hashCode et compareTo

### 6.1 Règles communes

Les règles d'égalité et de comparaison sont métier, pas techniques.

L'IA doit respecter :

- l'insensibilité à la casse des libellés métier ;
- la cohérence Java `equals` / `hashCode` / `compareTo` ;
- la robustesse `null` ;
- l'ordre de verrouillage déterministe ;
- le comportement exact verrouillé par les tests.

### 6.2 Hiérarchie des clés métier

- `TypeProduit.equals(...)` : égalité métier sur `typeProduit` uniquement, avec comparaison insensible à la casse.
- `SousTypeProduit.equals(...)` : égalité métier sur le parent `TypeProduit` + le libellé `sousTypeProduit`, sans s'appuyer aveuglément sur une relation mutable non relue.
- `Produit.equals(...)` : égalité métier sur le parent `SousTypeProduit` + le libellé `produit`. Le grand-parent `TypeProduit` n'est pris en compte que via le parent `SousTypeProduit`.

### 6.3 Comparaison

La comparaison métier respecte la même hiérarchie que l'égalité :

- `TypeProduit` compare le libellé `typeProduit` ;
- `SousTypeProduit` compare d'abord le parent `TypeProduit`, puis le libellé `sousTypeProduit` ;
- `Produit` compare d'abord le parent `SousTypeProduit`, puis le libellé `produit`.

Les comparaisons de chaînes doivent rester insensibles à la casse. Les cas `null` doivent rester déterministes et conformes aux tests.

## 7) Thread-safety et verrouillage

Les objets métier validés sont conçus pour supporter les tests concurrents déjà présents.

Règles obligatoires :

- ne pas supprimer les `synchronized` existants ;
- ne pas remplacer un verrouillage multi-objets par un verrouillage naïf ;
- conserver l'ordre de verrouillage basé sur `System.identityHashCode(...)` lorsqu'il existe ;
- conserver le verrou de classe en cas de collision d'identity hash lorsqu'il existe ;
- prendre des snapshots courts sous verrou puis calculer hors verrou lorsque le code validé le fait ;
- ne jamais appeler `equals(...)`, `contains(...)` ou `remove(Object)` sous verrou lorsque le code validé utilise volontairement une comparaison par identité pour éviter des appels transitifs dangereux ;
- conserver les tests de timeout concurrent comme spécification.

## 8) Relations bidirectionnelles

La couche métier maintient des relations bidirectionnelles :

- `TypeProduit <-> SousTypeProduit` ;
- `SousTypeProduit <-> Produit`.

Règles obligatoires :

- le setter intelligent du côté enfant doit tenir la cohérence avec le parent ;
- le rattachement doit éviter les doublons par identité lorsque le code validé le fait ;
- le détachement doit nettoyer l'ancien parent ;
- le re-parenting doit être cohérent ;
- un parent absent ou `null` doit être traité selon le contrat de la classe cible ;
- ne jamais créer de cycle incohérent entre parents et enfants ;
- ne jamais exposer une collection interne mutable partagée si le getter validé expose un snapshot.

## 9) Clonage

Le clonage métier validé repose sur `CloneContext` pour éviter cycles et duplications.

Règles obligatoires :

- `clone()` délègue au clonage profond avec un nouveau `CloneContext` lorsque le code validé le fait ;
- `deepClone(CloneContext)` doit utiliser le contexte fourni ;
- si un clone existe déjà dans le contexte, il doit être réutilisé ;
- les variantes `cloneWithoutChildren`, `cloneWithoutParentAndChildren` et `cloneWithoutParent` doivent rester des clones partiels, sans rattachement parasite ;
- les clones profonds doivent reconstruire les relations bidirectionnelles de manière cohérente ;
- ne jamais remplacer un clonage profond par une copie superficielle.

## 10) Validité métier

`SousTypeProduit` et `Produit` portent un booléen `valide` recalculé.

Règles obligatoires :

- ne pas sérialiser ni calculer manuellement l'état en dehors du contrat ;
- `SousTypeProduit.valide` dépend du parent et du libellé métier ;
- `Produit.valide` dépend du parent et du libellé métier ;
- les setters intelligents doivent recalculer l'état ;
- les clones partiels doivent recalculer l'état selon leur parent réel.

## 11) CSV et JTable

Les objets métier implémentent `IExportateurCsv` et `IExportateurJTable`.

Règles obligatoires :

- conserver les en-têtes exacts ;
- conserver l'ordre exact des colonnes ;
- conserver le séparateur point-virgule `;` en CSV ;
- conserver la représentation textuelle de `null` dans les CSV lorsque le code validé la produit ;
- conserver `"invalide"` pour les indices de colonne non supportés ;
- conserver les valeurs `null` pour les colonnes valides sans valeur lorsque le code validé les retourne ;
- ne pas modifier l'orthographe historique des en-têtes, même si elle paraît perfectible.

Ordres validés :

| Classe | CSV / JTable |
| --- | --- |
| `TypeProduit` | `idTypeProduit;type de produit;` |
| `SousTypeProduit` | `idSousTypeProduit;type de produit;sous-type de produit;` |
| `Produit` | `idproduit;type de produit;sous-type de produit;produit;` |

## 12) Tests métier : formalisme validé

Les tests métier validés conservent un formalisme historique différent des tests Gateway/UC récents.

Règles obligatoires :

- ne pas imposer mécaniquement le formalisme `ARRANGE / ACT / ASSERT` des tests UC aux tests métier existants ;
- conserver les `@DisplayName` inline déjà validés dans les tests métier tant que l'utilisateur n'a pas demandé leur refonte ;
- conserver les tags existants ;
- conserver les constantes d'affichage et `AFFICHAGE_GENERAL` lorsqu'elles existent ;
- conserver les tests concurrents, y compris leurs timeouts, `ExecutorService`, `Future`, `CountDownLatch` ou équivalents ;
- ne pas réduire les assertions redondantes lorsqu'elles servent de documentation comportementale ;
- ne pas supprimer les `System.out.println(...)` historiques conditionnés lorsqu'ils font partie du formalisme validé.

## 13) Contrats locaux obligatoires

La couche métier est décrite par les contrats locaux suivants :

- `docs/contrats/metier/CoucheMetier.md` ;
- `docs/contrats/metier/TypeProduit.md` ;
- `docs/contrats/metier/SousTypeProduit.md` ;
- `docs/contrats/metier/Produit.md` ;
- `docs/contrats/metier/CloneContext.md` ;
- `docs/contrats/metier/NormalizerUtils.md` ;
- `docs/contrats/metier/ConvertisseursMetierOutputDTO.md`.

## 14) Interdictions absolues

- ne jamais raisonner de mémoire sur une classe métier ;
- ne jamais coder sans relire la classe cible et ses tests ;
- ne jamais modifier une règle d'égalité, de comparaison, de clonage ou de relation bidirectionnelle sans relire tous les tests concernés ;
- ne jamais remplacer une relation bidirectionnelle par une relation unidirectionnelle ;
- ne jamais exposer une collection interne mutable partagée ;
- ne jamais supprimer un verrou ou un snapshot concurrent validé ;
- ne jamais homogénéiser le formalisme métier avec une autre couche sans demande explicite ;
- ne jamais employer la terminologie interdite pour désigner le stockage relationnel ; employer uniquement `stockage`.

## 15) AUTONOMIE-COUCHE-METIER-01 — Recodage autonome de la couche métier

### 15.1 Principe

La couche métier validée doit pouvoir être recodée par l'IA avec une autonomie réelle. Cette autonomie ne signifie pas une réécriture libre : elle signifie que les contrats IA décrivent assez précisément les fichiers validés pour empêcher l'IA d'improviser.

L'objectif attendu est un recodage quasiment à l'identique sur :

- la structure générale ;
- les Javadocs utiles ;
- les commentaires de bloc ;
- les constantes ;
- les signatures ;
- l'ordre des méthodes ;
- les choix de synchronisation ;
- les règles de clonage ;
- les relations bidirectionnelles ;
- les sorties CSV/JTable ;
- les tests de référence.

### 15.2 Ce qui peut varier sans rompre l'autonomie

L'utilisateur accepte qu'une IA autonome puisse produire quelques différences mineures non fonctionnelles, par exemple un nom local ou une constante interne lorsque ce nom n'est pas verrouillé par le contrat, le code validé ou les tests.

En revanche, l'IA ne doit pas changer :

- un nom public ;
- une constante publique ;
- une valeur littérale observable ;
- un commentaire critique ;
- une Javadoc expliquant un comportement métier ou concurrent ;
- une méthode interne nécessaire à la sûreté ;
- une stratégie d'identité, de verrouillage, de snapshot ou de relation bidirectionnelle.

### 15.3 Inventaire autonome des fichiers métier validés

| Fichier | Contrat local d'autonomie | Tests de verrouillage |
| --- | --- | --- |
| `TypeProduit.java` | `TypeProduit.md` | `TypeProduitTest.java`, `TypeProduitSousTypeProduitIntegrationTest.java`, `MetierGlobalConformiteTest.java` |
| `SousTypeProduit.java` | `SousTypeProduit.md` | `SousTypeProduitTest.java`, `TypeProduitSousTypeProduitIntegrationTest.java`, `MetierGlobalConformiteTest.java` |
| `Produit.java` | `Produit.md` | `ProduitTest.java`, `MetierGlobalConformiteTest.java` |
| `CloneContext.java` | `CloneContext.md` | `CloneContextTest.java` |
| `NormalizerUtils.java` | `NormalizerUtils.md` | `NormalizerUtilsTest.java` |
| Convertisseurs métier vers OutputDTO | `ConvertisseursMetierOutputDTO.md` | les trois tests `ConvertisseurMetierToOutputDTO...Test.java` |

### 15.4 Règles transverses de reproduction du formalisme métier

Les objets métier validés utilisent un formalisme historique qui doit être conservé :

- bannière historique de classe ;
- sections `CONSTANTES`, `ATTRIBUTS`, `METHODES` ;
- Javadocs HTML en `<div>`, `<p>`, `<ul>`, `<li>` ;
- commentaires de fin de méthode lorsqu'ils existent ;
- commentaires internes décrivant les snapshots, les verrous et les relations bidirectionnelles ;
- usage explicite du vocabulaire `objet métier`, `parent`, `enfant`, `clone profond`, `snapshot`, `verrouillage déterministe`.

Interdiction : remplacer ce formalisme par un style compact ou moderne sous prétexte de simplification.

### 15.5 Règles transverses de synchronisation

Pour les trois objets métier hiérarchiques :

- les lectures de champs mutables doivent se faire par snapshot court sous verrou lorsque le code validé le fait ;
- les comparaisons entre deux objets métier doivent verrouiller dans un ordre déterministe basé sur `System.identityHashCode(...)` ;
- le cas de collision d'`identityHashCode` doit rester protégé par un verrou de départ unique lorsque le code validé le prévoit ;
- les appels susceptibles de déclencher un verrou d'un autre objet doivent être faits hors du verrou courant lorsque le code validé le prévoit ;
- les opérations internes par identité ne doivent jamais être remplacées par `contains(...)`, `remove(Object)` ou une logique fondée sur `equals(...)`.

### 15.6 Règles transverses de relation bidirectionnelle

Les relations validées sont intelligentes et bidirectionnelles :

- `TypeProduit <-> SousTypeProduit` ;
- `SousTypeProduit <-> Produit`.

Le setter du côté enfant est le point canonique de rattachement/détachement quand le code validé l'utilise. Les méthodes internes `internalAdd...` et `internalRemove...` ne remplacent pas le setter canonique : elles servent à mettre à jour la collection interne sans boucle infinie et sans réentrance parasite.

### 15.7 Règles transverses de clonage profond

Le clonage profond métier repose sur `CloneContext` :

- `clone()` appelle une méthode privée de clonage profond qui crée un nouveau contexte ;
- `deepClone(CloneContext)` refuse un contexte `null` lorsque le code validé le fait ;
- le clone partiel est créé et enregistré dans le contexte avant de cloner les relations ;
- les relations bidirectionnelles sont reconstruites via les setters canoniques ;
- les variantes `cloneWithoutChildren`, `cloneWithoutParentAndChildren`, `cloneWithoutParent` doivent rester partielles et recalculer la validité lorsqu'elle existe.

### 15.8 Règles transverses des mauvaises instances

Les interfaces métier peuvent recevoir des implémentations non validées. Les classes validées traitent explicitement ces mauvaises instances.

L'IA doit conserver :

- les méthodes privées `traiterMauvaiseInstance...` ;
- le retour sans effet sur paramètre `null` ;
- le `LOG.fatal(...)` conditionné par `LOG.isFatalEnabled()` ;
- l'`IllegalStateException` avec message construit depuis la constante historique ;
- la distinction entre mauvaise instance parent, enfant, petit-enfant et élément de liste.

### 15.9 Règles transverses CSV/JTable

Les chaînes CSV et JTable sont des sorties historiques observables. L'IA doit recopier les valeurs exactes depuis les contrats locaux enrichis ou depuis le code validé :

- `TypeProduit` : `idTypeProduit;type de produit;` ;
- `SousTypeProduit` : `idSousTypeProduit;type de produit;sous-type de produit;` ;
- `Produit` : `idproduit;type de produit;sous-type de produit;produit;`.

La chaîne `invalide` est le retour historique des indices de colonnes non supportés.

### 15.10 Tests globaux et d'intégration validés

Les tests transverses suivants verrouillent les invariants globaux de la couche métier et doivent être relus dès qu'une relation bidirectionnelle, un clonage profond ou un algorithme thread-safe transverse est touché.

`MetierGlobalConformiteTest.java` :

1. `testInvariantMetierGlobalCoherenceLiens()` ;
2. `testCloneProfondCompletProduit()` ;
3. `testAntiDeadlockSousTypeProduitProduit()`.

`TypeProduitSousTypeProduitIntegrationTest.java` :

1. `testIntegrationBidirectionnelleNominale()` ;
2. `testIntegrationReParentingNominal()` ;
3. `testSetSousTypeProduitsThreadSafe()`.

Ces noms de tests sont contractuels pour l'autonomie de recodage : ils indiquent les scénarios transverses que l'IA doit préserver en plus des tests locaux listés dans `TypeProduit.md`, `SousTypeProduit.md` et `Produit.md`.

### 15.11 Contrôle avant livraison

Avant de livrer une correction de la couche IA destinée à rendre la couche métier autonome, l'IA doit reconfronter les contrats corrigés aux fichiers validés suivants :

- classes métier ;
- interfaces métier ;
- utilitaires ;
- convertisseurs relais métier/DTO ;
- tests métier et tests convertisseurs.

La livraison doit indiquer explicitement les fichiers relus, les fichiers modifiés, les contrôles effectués et les limites éventuelles restantes.
