# docs/contrats/cu/ProduitICuService.md

# Contrat comportemental — ProduitICuService (SERVICE METIER UC)

## 1) Port concerné

- `levy.daniel.application.model.services.produittype.cu.ProduitICuService`

## 2) Objet du contrat

Décrire le **comportement réel attendu et observable** du SERVICE METIER UC
rattaché au port `ProduitICuService`.

Ce document ne décrit pas la technique de persistance.

Il décrit le comportement **côté UC** :
- orchestration applicative ;
- messages utilisateur ;
- exceptions observables ;
- résultats retournés ;
- traçabilité ;
- cohérence avec les tests Mock et Intégration.

## 3) Rôle du SERVICE UC

Le SERVICE METIER UC :
- reçoit des `InputDTO` depuis la couche appelante ;
- valide les préconditions applicatives observables ;
- retrouve les parents persistants nécessaires lorsque le scénario l’exige ;
- convertit les `InputDTO` en objets métier lorsque nécessaire ;
- délègue les opérations techniques au `GATEWAY` ;
- convertit les objets métier retournés en `OutputDTO` lorsque nécessaire ;
- produit des messages utilisateur via `getMessage()` ;
- émet les LOGs nécessaires à la traçabilité ;
- retourne un résultat exploitable par la couche appelante.

### Règles structurantes

- le `GATEWAY` ne produit pas de message utilisateur ;
- le `SERVICE UC` est responsable du **message observable** côté appelant ;
- les messages récurrents doivent être **factorisés dans le PORT** ;
- le comportement observable décrit dans le PORT doit rester cohérent avec l’ADAPTER et avec les tests.

## 4) Source de vérité de ce contrat

Ce contrat est rattaché explicitement au port `ProduitICuService`.

Ordre d’interprétation :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/ProduitICuService.md`
3. le code du PORT `ProduitICuService`
4. le code de l’ADAPTER `ProduitCuService`
5. les tests JUnit Mock et Intégration

En cas d’ambiguïté :
- priorité au **comportement réellement verrouillé par les tests** ;
- puis au **présent contrat** ;
- puis au code.

## 5) Méthodes PORT déjà normalisées servant de référence

Les méthodes suivantes sont considérées comme **références de formalisme UC**
dans le PORT `ProduitICuService` :
- `ProduitDTO.OutputDTO creer(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### Règle absolue

Toute nouvelle remise au carré du PORT UC doit s’aligner sur le formalisme
des méthodes déjà normalisées dans les ports `TypeProduitICuService`
et `SousTypeProduitICuService`.

## 6) Statut du contrat local Produit

Le présent contrat local Produit constitue la référence documentaire
du chantier UC Produit.

Conséquence :
- toute correction future doit maintenir l’homogénéité avec `TypeProduit` ;
- toute correction future doit maintenir l’homogénéité avec `SousTypeProduit` ;
- aucune régression documentaire ou comportementale n’est acceptable ;
- toute évolution doit rester synchronisée entre PORT, ADAPTER et tests.

## 7) Séquence de lecture obligatoire avant toute correction

Avant toute rédaction, analyse, correction ou génération de code concernant
une méthode UC Produit, l’IA doit relire, dans cet ordre :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/ProduitICuService.md`
3. les méthodes déjà normalisées de `TypeProduitICuService`
4. les méthodes déjà normalisées de `SousTypeProduitICuService`
5. la méthode UC cible dans le PORT `ProduitICuService`
6. la méthode correspondante dans l’ADAPTER `ProduitCuService`
7. les tests Mock
8. les tests d’Intégration

### Interdictions absolues

- ne jamais inventer un nouveau style ;
- ne jamais dégrader une méthode déjà normalisée ;
- ne jamais se baser sur une ancienne réponse de chat plutôt que sur les fichiers relus ;
- ne jamais contourner le contrat local au motif qu’une méthode “semble simple”.

## 8) Formalisme javadoc obligatoire dans le PORT UC

### 8.1 Structure obligatoire

Toute méthode UC du PORT doit comporter, dans sa javadoc, **dans cet ordre exact** :
1. une **phrase d’ouverture** ;
2. une rubrique **INTENTION DE SERVICE UC (scénario nominal)** ;
3. une rubrique **CONTRAT DE SERVICE UC** ;
4. une rubrique **GARANTIES METIER, UTILISATEUR et TRAÇABILITE** ;
5. un bloc détaillé `@param / @return / @throws` lorsque applicable.

Aucune de ces rubriques ne doit manquer.

### 8.2 Habillage HTML obligatoire pour la lisibilité

Afin d’éviter les rubriques invisibles et d’obtenir un rendu homogène dans la javadoc,
la partie descriptive doit être encapsulée dans une structure HTML explicite :
- un bloc racine `<div> ... </div>` ;
- des paragraphes `<p> ... </p>` pour la phrase d’ouverture ;
- un paragraphe `<p> ... </p>` pour **chaque titre de rubrique** ;
- des listes `<ul><li> ... </li></ul>` pour les points de scénario,
  de contrat et de garanties.

### 8.3 Phrase d’ouverture

La phrase d’ouverture doit :
- commencer par un verbe d’action clair ;
- nommer le type principal manipulé lorsque c’est pertinent ;
- résumer la finalité observable de la méthode ;
- rester courte et descriptive.

### 8.4 Rubrique INTENTION DE SERVICE UC (scénario nominal)

Cette rubrique décrit le **déroulé nominal** du point de vue orchestration UC.

Elle doit :
- partir de ce que reçoit ou fait la couche UC ;
- expliciter les validations utiles ;
- expliciter la délégation au `GATEWAY` ou à une autre méthode UC ;
- expliciter la préparation de la réponse finale ;
- se terminer par la restitution d’une réponse exploitable.

### 8.5 Rubrique CONTRAT DE SERVICE UC

Cette rubrique décrit les **cas observables**.

Elle doit :
- énumérer les cas d’entrée problématique lorsque la méthode a des paramètres ;
- préciser, pour chaque cas, le triplet :
  - retour observable,
  - message utilisateur,
  - LOG / exception / absence de LOG / absence d’exception ;
- expliciter la délégation lorsqu’elle existe ;
- expliciter les cas métier ;
- expliciter le cas nominal de succès ;
- expliciter le comportement en cas de panne technique remontée.

Le contrat doit être **falsifiable par les tests**.

### 8.6 Rubrique GARANTIES METIER, UTILISATEUR et TRAÇABILITE

Cette rubrique décrit ce que la méthode garantit sur la qualité de la réponse.

Elle doit comporter, selon le cas, des garanties du type :
- le message retourné par `getMessage()` reflète l’issue observable ;
- le message de succès n’est positionné qu’après traitement complet ;
- le DTO ou la liste retournée correspond à l’état métier effectivement accessible ;
- aucun résultat partiel incohérent n’est exposé à l’appelant.

### 8.7 Bloc `@param / @return / @throws`

Le bloc de tags doit être complet et détaillé.

#### `@param`

Chaque paramètre doit préciser :
- son type ;
- son rôle métier/technique ;
- ce qu’il représente dans le scénario UC.

#### `@return`

Le retour doit préciser :
- le type réellement retourné ;
- le cas nominal ;
- les cas où `null` est autorisé.

#### `@throws`

Les exceptions doivent être détaillées par catégorie :
- exception de validation observable ;
- exception métier ;
- exception technique ;
- `IllegalStateException` en cas d’incohérence finale de réponse ;
- `Exception` de garde si l’implémentation le prévoit encore.

## 9) Spécificité structurante de Produit

Le `Produit` porte une contrainte supplémentaire par rapport à `TypeProduit` :

- un `Produit` n’est pas autonome ;
- il doit être rattaché à un `SousTypeProduit` parent ;
- ce parent doit exister dans le stockage ;
- ce parent doit être **persistant** ;
- le SERVICE UC doit donc vérifier ce point avant toute création,
  modification ou recherche nécessitant ce rattachement.

Conséquence observable côté UC :
- l’absence de parent exploitable n’est pas un succès partiel ;
- le SERVICE UC doit positionner un message utilisateur déterministe ;
- le SERVICE UC doit refuser d’exposer une réponse incohérente
  reposant sur un parent absent ou non persistant.

## 10) Contrat spécifique de `creer(...)`

Signature cible :
- `ProduitDTO.OutputDTO creer(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 10.1 Scénario nominal attendu

Le scénario nominal de `creer(...)` est :

1. recevoir un `ProduitDTO.InputDTO` ;
2. valider les préconditions visibles côté appelant ;
3. vérifier l’absence de doublon fonctionnel ;
4. retrouver le `SousTypeProduit` parent persistant ;
5. convertir l’`InputDTO` en objet métier ;
6. rattacher explicitement le parent persistant ;
7. déléguer la création au `GATEWAY` ;
8. récupérer l’objet métier réellement créé ;
9. convertir la réponse en `OutputDTO` ;
10. positionner le message de succès ;
11. retourner la réponse finale.

### 10.2 Cas observables attendus

- si `pInputDTO == null` :
  - retourne `null`,
  - positionne `getMessage()` à `MESSAGE_CREER_NULL`,
  - n’émet ni LOG ni exception ;

- si `pInputDTO.getProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_CREER_NOM_BLANK`,
  - émet un LOG,
  - lève une exception de validation ;

- si le libellé du parent est blank :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si le doublon est détecté :
  - positionne `getMessage()` à `MESSAGE_DOUBLON + libellé`,
  - émet un LOG,
  - lève une `ExceptionDoublon` ;

- si le parent n’existe pas ou n’est pas persistant :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si une anomalie technique survient pendant :
  - le contrôle d’unicité,
  - la vérification du parent,
  - la création via le `GATEWAY`,
  - la conversion finale,

  alors le SERVICE UC :
  - positionne un message circonstancié,
  - émet un LOG,
  - propage une exception conforme à l’implémentation ;

- en cas de succès :
  - retourne un `ProduitDTO.OutputDTO` non null,
  - positionne `getMessage()` à `MESSAGE_CREER_OK`
    uniquement après préparation complète de la réponse.

### 10.3 Garanties spécifiques de `creer(...)`

- aucun succès ne doit être exposé si le parent n’est pas persistant ;
- aucun succès ne doit être exposé si le `GATEWAY` retourne `null` ;
- aucun succès ne doit être exposé si la conversion finale retourne `null` ;
- le DTO retourné doit représenter l’état réellement créé dans le stockage ;
- le message utilisateur doit être lisible, stable et testable.