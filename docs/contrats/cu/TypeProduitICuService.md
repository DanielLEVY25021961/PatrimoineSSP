# docs/contrats/cu/TypeProduitICuService.md

# Contrat comportemental — TypeProduitICuService (SERVICE METIER UC)

## 1) Port concerné

- `levy.daniel.application.model.services.produittype.cu.TypeProduitICuService`

## 2) Objet du contrat

Décrire le **comportement réel attendu et observable** du SERVICE METIER UC
rattaché au port `TypeProduitICuService`.

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

Ce contrat est rattaché explicitement au port `TypeProduitICuService`.

Ordre d’interprétation :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/TypeProduitICuService.md`
3. le code du PORT `TypeProduitICuService`
4. le code de l’ADAPTER `TypeProduitCuService`
5. les tests JUnit Mock et Intégration

En cas d’ambiguïté :

- priorité au **comportement réellement verrouillé par les tests** ;
- puis au **présent contrat** ;
- puis au code.

## 5) Méthodes PORT déjà normalisées servant de référence

Les méthodes suivantes sont considérées comme **références de formalisme UC**
dans le PORT `TypeProduitICuService` :

- `TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;`
- `List<String> rechercherTousString() throws Exception;`
- `ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`
- `TypeProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`
- `TypeProduitDTO.OutputDTO findByDTO(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `TypeProduitDTO.OutputDTO findById(Long pId) throws Exception;`
- `TypeProduitDTO.OutputDTO update(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `void delete(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `long count() throws Exception;`
- `String getMessage();`

### Règle absolue

Toute nouvelle remise au carré du PORT UC doit s’aligner
sur le formalisme de ces méthodes déjà normalisées.

## 6) Statut des méthodes du PORT

Aucune méthode du PORT `TypeProduitICuService`
ne doit désormais être considérée comme **legacy**.

Conséquence :

- toutes les méthodes du PORT sont désormais des **références de formalisme UC** ;
- aucune régression documentaire ou comportementale n’est acceptable ;
- toute correction future doit maintenir l’homogénéité entre méthodes.

## 7) Séquence de lecture obligatoire avant toute correction

Avant toute rédaction, analyse, correction ou génération de code
concernant une méthode UC, l’IA doit relire, dans cet ordre :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/TypeProduitICuService.md`
3. les méthodes déjà normalisées du PORT listées au §5
4. la méthode UC cible dans le PORT
5. la méthode correspondante dans l’ADAPTER
6. les tests Mock
7. les tests d’Intégration

### Interdictions absolues

- ne jamais inventer un nouveau style ;
- ne jamais dégrader une méthode déjà normalisée ;
- ne jamais se baser sur une ancienne réponse de chat plutôt que sur les fichiers relus ;
- ne jamais contourner le contrat local au motif qu’une méthode “semble simple”.

## 8) Formalisme javadoc obligatoire dans le PORT UC

### 8.1 Structure obligatoire

Toute méthode UC du PORT doit comporter, dans sa javadoc,
**dans cet ordre exact** :

1. une **phrase d’ouverture** ;
2. une rubrique **INTENTION DE SERVICE UC (scénario nominal)** ;
3. une rubrique **CONTRAT DE SERVICE UC** ;
4. une rubrique **GARANTIES METIER, UTILISATEUR et TRAÇABILITE** ;
5. un bloc détaillé `@param / @return / @throws` lorsque applicable.

Aucune de ces rubriques ne doit manquer.

### 8.2 Habillage HTML obligatoire pour la lisibilité

Afin d’éviter les rubriques invisibles
et d’obtenir un rendu homogène dans la javadoc,
la partie descriptive doit être encapsulée
dans une structure HTML explicite :

- un bloc racine `<div> ... </div>` ;
- des paragraphes `<p> ... </p>` pour la phrase d’ouverture ;
- un paragraphe `<p><strong>...</strong></p>`
  pour **chaque titre de rubrique** ;
- des listes `<ul><li>...</li></ul>`
  pour les points de scénario, de contrat et de garanties.

### 8.3 Phrase d’ouverture

La phrase d’ouverture doit :

- commencer par un verbe d’action clair ;
- nommer le type principal manipulé lorsque c’est pertinent ;
- résumer la finalité observable de la méthode ;
- rester courte et descriptive.

### 8.4 Rubrique INTENTION DE SERVICE UC (scénario nominal)

Cette rubrique décrit le **déroulé nominal**
du point de vue orchestration UC.

Elle doit :

- partir de ce que reçoit ou fait la couche UC ;
- expliciter les validations utiles ;
- expliciter la délégation au `GATEWAY` ou à une autre méthode UC ;
- expliciter la préparation de la réponse finale ;
- se terminer par la restitution d’une réponse exploitable.

Elle ne doit pas :

- détailler les exceptions une première fois ;
- mélanger nominal et cas d’erreur ;
- parler de bas niveau de persistance.

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

Le contrat doit être **falsifiable par les tests** :
chaque point écrit ici doit pouvoir être vérifié
dans les tests Mock et/ou Intégration.

### 8.6 Rubrique GARANTIES METIER, UTILISATEUR et TRAÇABILITE

Cette rubrique décrit ce que la méthode garantit
sur la qualité de la réponse.

Elle doit comporter, selon le cas,
des garanties du type :

- le message retourné par `getMessage()` reflète l’issue observable ;
- le message de succès n’est positionné qu’après traitement complet ;
- le DTO ou la liste retournée correspond à l’état métier effectivement accessible ;
- aucun résultat partiel incohérent n’est exposé à l’appelant ;
- la réponse déléguée reste alignée sur la méthode appelée ;
- le getter `getMessage()` reste un pur reflet d’état local.

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
- les cas où `null` est autorisé ;
- le caractère éventuellement vide / jamais null
  pour les listes et résultats paginés.

#### `@throws`

Les exceptions doivent être détaillées par catégorie :

- exception de validation observable ;
- exception métier ;
- exception technique ;
- `IllegalStateException` en cas d’incohérence finale de réponse ;
- `Exception` de garde si l’implémentation le prévoit encore.

### 8.8 Règles rédactionnelles obligatoires

La rédaction javadoc du PORT UC doit respecter les règles suivantes :

- français clair et homogène ;
- vocabulaire stable d’une méthode à l’autre ;
- même intitulé exact de rubrique d’une méthode à l’autre ;
- mêmes conventions d’écriture pour `null`, `blank`, `GATEWAY`, `DTO` ;
- références `{@link ...}` sur les types, constantes et méthodes citées ;
- formulations observables et testables ;
- pas de formule vague du type
  « fait le nécessaire », « traite la demande », « gère le cas ».

### 8.9 Interdictions absolues dans la javadoc du PORT

Il est interdit :

- d’enlever le bloc `<div>` ;
- d’enlever les `<p>` portant les titres de rubriques ;
- de fusionner les rubriques en un texte libre ;
- de remplacer `CONTRAT DE SERVICE UC`
  par `CONTRAT (métier / observable)` sur une méthode remise au carré ;
- de supprimer la rubrique `GARANTIES ...` ;
- de livrer une javadoc réduite à quelques puces techniques ;
- de produire une javadoc plus pauvre qu’une méthode déjà normalisée ;
- de dégrader les signatures documentées en types bruts.

### 8.10 Gabarit canonique à reproduire

```java
/**
 * <div>
 * <p>Phrase d’ouverture décrivant la finalité observable de la méthode.</p>
 *
 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
 * <ul>
 * <li>décrire la réception ou le déclenchement côté UC ;</li>
 * <li>décrire les validations utiles ;</li>
 * <li>décrire la délégation au GATEWAY ou à une autre méthode UC ;</li>
 * <li>décrire la préparation de la réponse ;</li>
 * <li>décrire la restitution d’une réponse exploitable.</li>
 * </ul>
 *
 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
 * <ul>
 * <li>cas d’entrée problématique : retour, message, LOG, exception ;</li>
 * <li>cas métier ;</li>
 * <li>cas nominal de succès ;</li>
 * <li>cas d’échec technique remonté par le GATEWAY
 * ou par la préparation de la réponse.</li>
 * </ul>
 *
 * <p><strong>GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</strong></p>
 * <ul>
 * <li>le message reflète l’issue observable ;</li>
 * <li>le succès n’est positionné qu’après traitement complet ;</li>
 * <li>la réponse retournée correspond à l’état métier réellement accessible ;</li>
 * <li>aucun résultat partiel incohérent n’est exposé à l’appelant.</li>
 * </ul>
 * </div>
 *
 * @param pXxx : Type :
 * signification métier/technique du paramètre.
 * @return TypeRetour :
 * description du retour nominal et des cas `null` / vide autorisés.
 * @throws ExceptionX
 * si ...
 * @throws ExceptionY
 * si ...
 * @throws Exception
 * toute autre exception levée par l’implémentation.
 */