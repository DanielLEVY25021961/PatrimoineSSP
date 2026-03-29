# docs/contrats/cu/SousTypeProduitICuService.md

# Contrat comportemental — SousTypeProduitICuService (SERVICE METIER UC)

## 1) Port concerné

- `levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService`

## 2) Objet du contrat

Décrire le **comportement réel attendu et observable** du SERVICE METIER UC
rattaché au port `SousTypeProduitICuService`.

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

Ce contrat est rattaché explicitement au port `SousTypeProduitICuService`.

Ordre d’interprétation :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/SousTypeProduitICuService.md`
3. le code du PORT `SousTypeProduitICuService`
4. le code de l’ADAPTER `SousTypeProduitCuService`
5. les tests JUnit Mock et Intégration

En cas d’ambiguïté :

- priorité au **comportement réellement verrouillé par les tests** ;
- puis au **présent contrat** ;
- puis au code.

## 5) Méthodes PORT déjà normalisées servant de référence

Les méthodes suivantes sont considérées comme **références de formalisme UC**
dans le PORT `SousTypeProduitICuService` :

- `SousTypeProduitDTO.OutputDTO creer(SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `List<SousTypeProduitDTO.OutputDTO> rechercherTous() throws Exception;`
- `List<String> rechercherTousString() throws Exception;`
- `ResultatPage<SousTypeProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`
- `List<SousTypeProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;`
- `List<SousTypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`
- `List<SousTypeProduitDTO.OutputDTO> findAllByParent(TypeProduitDTO.InputDTO pTypeProduit) throws Exception;`
- `SousTypeProduitDTO.OutputDTO findByDTO(SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `SousTypeProduitDTO.OutputDTO findById(Long pId) throws Exception;`
- `SousTypeProduitDTO.OutputDTO update(SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `void delete(SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `long count() throws Exception;`
- `String getMessage();`

### Règle absolue

Toute nouvelle remise au carré du PORT UC doit s’aligner
sur le formalisme de ces méthodes déjà normalisées.

## 6) Statut des méthodes du PORT

Aucune méthode du PORT `SousTypeProduitICuService`
ne doit désormais être considérée comme **legacy**.

Conséquence :

- toutes les méthodes du PORT sont désormais des **références de formalisme UC** ;
- aucune régression documentaire ou comportementale n’est acceptable ;
- toute correction future doit maintenir l’homogénéité entre méthodes.

## 7) Séquence de lecture obligatoire avant toute correction

Avant toute rédaction, analyse, correction ou génération de code
concernant une méthode UC, l’IA doit relire, dans cet ordre :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/SousTypeProduitICuService.md`
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

## 9) Spécificité structurante de SousTypeProduit

Le `SousTypeProduit` porte une contrainte supplémentaire
par rapport à `TypeProduit` :

- un `SousTypeProduit` n’est pas autonome ;
- il doit être rattaché à un `TypeProduit` parent ;
- ce parent doit exister dans le stockage ;
- ce parent doit être **persistant** ;
- le SERVICE UC doit donc vérifier ce point
  avant toute création, modification ou recherche par parent.

Conséquence observable côté UC :

- l’absence de parent exploitable n’est pas un succès partiel ;
- le SERVICE UC doit positionner un message utilisateur déterministe ;
- le SERVICE UC doit refuser d’exposer une réponse incohérente
  reposant sur un parent absent ou non persistant.

## 10) Contrat spécifique de `creer(...)`

Signature cible :

- `SousTypeProduitDTO.OutputDTO creer(SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;`

### 10.1 Scénario nominal attendu

Le scénario nominal de `creer(...)` est :

1. recevoir un `SousTypeProduitDTO.InputDTO` ;
2. valider les préconditions visibles côté appelant ;
3. vérifier l’absence de doublon ;
4. retrouver le `TypeProduit` parent persistant ;
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

- si `pInputDTO.getSousTypeProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_CREER_NOM_BLANK`,
  - émet un LOG,
  - lève une exception de validation ;

- si `pInputDTO.getTypeProduit()` est blank :
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
  - retourne un `SousTypeProduitDTO.OutputDTO` non null,
  - positionne `getMessage()` à `MESSAGE_CREER_OK`
    uniquement après préparation complète de la réponse.

### 10.3 Garanties spécifiques de `creer(...)`

- aucun succès ne doit être exposé si le parent n’est pas persistant ;
- aucun succès ne doit être exposé si le `GATEWAY` retourne `null` ;
- aucun succès ne doit être exposé si la conversion finale retourne `null` ;
- le DTO retourné doit représenter l’état réellement créé dans le stockage ;
- le message utilisateur doit être lisible, stable et testable.

## 11) Contrat spécifique de `rechercherTous()`

Signature cible :

- `List<SousTypeProduitDTO.OutputDTO> rechercherTous() throws Exception;`

### 11.1 Scénario nominal attendu

Le scénario nominal de `rechercherTous()` est :

1. demander au `GATEWAY` la liste complète des `SousTypeProduit` ;
2. sécuriser le retour technique du stockage ;
3. retirer les éventuels éléments `null` ;
4. trier les objets métier ;
5. convertir les objets métier en `OutputDTO` ;
6. dédoublonner les `OutputDTO` si nécessaire ;
7. positionner le message observable ;
8. retourner une liste exploitable par la couche appelante.

### 11.2 Cas observables attendus

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception ;

- si la liste retournée devient vide après filtrage / conversion :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si la liste retournée contient des résultats :
  - retourne une liste non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 11.3 Garanties spécifiques de `rechercherTous()`

- la méthode ne doit jamais retourner `null`
  quand le stockage est exploitable ;
- le message observable doit être positionné
  après préparation complète de la réponse ;
- les `null` techniques issus du stockage
  ne doivent jamais fuiter jusqu’à l’appelant ;
- les `OutputDTO` retournés doivent correspondre
  à des objets métier réellement accessibles via le `GATEWAY` ;
- le dédoublonnage éventuel doit rester cohérent
  avec `equals/hashCode` des `OutputDTO`.

## 12) Contrat spécifique de `rechercherTousString()`

Signature cible :

- `List<String> rechercherTousString() throws Exception;`

### 12.1 Scénario nominal attendu

Le scénario nominal de `rechercherTousString()` est :

1. demander au `GATEWAY` la liste complète des `SousTypeProduit` ;
2. sécuriser le retour technique du stockage ;
3. retirer les éventuels éléments `null` ;
4. trier les objets métier ;
5. extraire les libellés exploitables ;
6. retirer les libellés blank ;
7. dédoublonner les libellés en conservant l’ordre utile ;
8. positionner le message observable ;
9. retourner une liste exploitable par la couche appelante.

### 12.2 Cas observables attendus

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception ;

- si la liste retournée devient vide après filtrage / extraction :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si la liste retournée contient des résultats :
  - retourne une liste non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK` ;

- la liste retournée ne doit contenir
  ni élément `null`,
  ni libellé blank,
  ni doublon observable côté appelant.

### 12.3 Garanties spécifiques de `rechercherTousString()`

- la méthode ne doit jamais retourner `null`
  quand le stockage est exploitable ;
- le message observable doit être positionné
  après préparation complète de la réponse ;
- les `null` techniques issus du stockage
  ne doivent jamais fuiter jusqu’à l’appelant ;
- les libellés retournés doivent correspondre
  à des objets métier réellement accessibles via le `GATEWAY` ;
- le dédoublonnage doit conserver un ordre stable
  pour la couche appelante ;
- aucun libellé blank ne doit être exposé.

## 13) Contrat spécifique de `rechercherTousParPage(...)`

Signature cible :

- `ResultatPage<SousTypeProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`

### 13.1 Scénario nominal attendu

Le scénario nominal de `rechercherTousParPage(...)` est :

1. recevoir une `RequetePage` transmise par la couche appelante ;
2. valider que la requête de pagination est exploitable ;
3. demander au `GATEWAY` la page d’objets métier correspondante ;
4. sécuriser le résultat paginé technique retourné ;
5. retirer les éventuels éléments `null` ;
6. trier les objets métier ;
7. convertir les objets métier en `OutputDTO` ;
8. dédoublonner les `OutputDTO` si nécessaire ;
9. reconstruire un `ResultatPage` cohérent pour la couche appelante ;
10. positionner le message observable ;
11. retourner la réponse paginée finale.

### 13.2 Cas observables attendus

- si `pRequetePage == null` :
  - positionne `getMessage()` à `MESSAGE_PAGEABLE_NULL`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception ;

- si le résultat paginé retourné par le `GATEWAY` est `null` :
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_KO`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si la réponse paginée est correctement préparée :
  - retourne un `ResultatPage` non `null`,
  - reprend une pagination cohérente,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_OK`.

### 13.3 Garanties spécifiques de `rechercherTousParPage(...)`

- la méthode ne doit jamais retourner `null`
  quand le scénario se termine avec succès ;
- le message observable doit être positionné
  après préparation complète de la réponse paginée ;
- les `null` techniques issus du stockage
  ne doivent jamais fuiter jusqu’à l’appelant ;
- les `OutputDTO` retournés doivent correspondre
  à des objets métier réellement accessibles via le `GATEWAY` ;
- la pagination reconstruite doit rester cohérente
  avec `pageNumber`, `pageSize` et `totalElements` du résultat technique sécurisé ;
- aucun résultat paginé partiel incohérent ne doit être exposé.

## 14) Contrat spécifique de `findByLibelle(...)`

Signature cible :

- `List<SousTypeProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;`

### 14.1 Scénario nominal attendu

Le scénario nominal de `findByLibelle(...)` est :

1. recevoir un libellé exact transmis par la couche appelante ;
2. valider que ce libellé est exploitable ;
3. demander au `GATEWAY` tous les `SousTypeProduit`
   correspondant exactement à ce libellé ;
4. sécuriser le retour technique du stockage ;
5. retirer les éventuels éléments `null` ;
6. trier les objets métier ;
7. convertir les objets métier en `OutputDTO` ;
8. dédoublonner les `OutputDTO` si nécessaire ;
9. positionner le message observable ;
10. retourner la liste finale.

### 14.2 Cas observables attendus

- si `pLibelle` est blank :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_PARAM_BLANK`,
  - n’émet ni LOG ni exception ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception ;

- si aucun résultat exploitable n’est trouvé :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_OBJ_INTROUVABLE + pLibelle` ;

- si un ou plusieurs résultats exploitables sont trouvés :
  - retourne une liste non vide de DTO,
  - positionne `getMessage()` à `MESSAGE_SUCCES_RECHERCHE`.

### 14.3 Garanties spécifiques de `findByLibelle(...)`

- le libellé exact d’un `SousTypeProduit` n’étant pas unique,
  la méthode doit retourner une collection
  et jamais un DTO unitaire ;
- la méthode ne doit jamais retourner `null`
  quand le stockage est exploitable ;
- le message observable doit être positionné
  après préparation complète de la réponse ;
- les `null` techniques issus du stockage
  ne doivent jamais fuiter jusqu’à l’appelant ;
- les DTO retournés doivent correspondre
  à des objets métier réellement accessibles via le `GATEWAY` ;
- aucun résultat partiel incohérent ne doit être exposé.

## 15) Contrat spécifique de `findByLibelleRapide(...)`

Signature cible :

- `List<SousTypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`

### 15.1 Scénario nominal attendu

Le scénario nominal de `findByLibelleRapide(...)` est :

1. recevoir un contenu partiel transmis par la couche appelante ;
2. valider que ce contenu est exploitable ;
3. si le contenu est blank, déléguer au scénario complet de `rechercherTous()` ;
4. sinon, demander au `GATEWAY` tous les `SousTypeProduit`
   dont le libellé contient ce contenu ;
5. sécuriser le retour technique du stockage ;
6. retirer les éventuels éléments `null` ;
7. trier les objets métier ;
8. convertir les objets métier en `OutputDTO` ;
9. dédoublonner les `OutputDTO` si nécessaire ;
10. positionner le message observable ;
11. retourner la liste finale.

### 15.2 Cas observables attendus

- si `pContenu == null` :
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si `pContenu` est blank :
  - délègue à `rechercherTous()`,
  - retourne alors le comportement observable de `rechercherTous()` ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception ;

- si aucun résultat exploitable n’est trouvé :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si un ou plusieurs résultats exploitables sont trouvés :
  - retourne une liste non vide de DTO,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 15.3 Garanties spécifiques de `findByLibelleRapide(...)`

- la méthode ne doit jamais retourner `null`
  quand le stockage est exploitable ;
- le message observable doit être positionné
  après préparation complète de la réponse ;
- les `null` techniques issus du stockage
  ne doivent jamais fuiter jusqu’à l’appelant ;
- les DTO retournés doivent correspondre
  à des objets métier réellement accessibles via le `GATEWAY` ;
- le dédoublonnage éventuel doit rester cohérent
  avec `equals/hashCode` des `OutputDTO` ;
- aucun résultat partiel incohérent ne doit être exposé.

## 16) Règle de synchronisation PORT / ADAPTER / tests

Toute correction de `creer(...)`, `rechercherTous()`,
`rechercherTousString()`, `rechercherTousParPage(...)`,
`findByLibelle(...)` ou `findByLibelleRapide(...)`
doit rester synchronisée entre :

1. le PORT `SousTypeProduitICuService` ;
2. l’ADAPTER `SousTypeProduitCuService` ;
3. les tests Mock ;
4. les tests d’Intégration ;
5. le présent contrat local.

Aucune de ces cinq pièces ne doit diverger durablement des autres.

### Point de vigilance pour les tests Mock

Pour `creer(...)`, l’absence de doublon côté GATEWAY
doit être simulée par :

- une `List<SousTypeProduit>` vide,
- ou, à défaut, une liste ne contenant aucun élément exploitable.

Le contrat réel du GATEWAY ne doit pas être simulé
par un `get(0)` non sécurisé.

Pour `rechercherTous()`, les tests Mock doivent verrouiller au minimum :

- le cas `gateway.rechercherTous() == null` ;
- le cas exception technique avec message ;
- le cas exception technique sans message ;
- le cas résultats vides après filtrage ;
- le cas nominal avec filtrage, tri et dédoublonnage.

Pour `rechercherTousString()`, les tests Mock doivent verrouiller au minimum :

- le cas `gateway.rechercherTous() == null` ;
- le cas exception technique avec message ;
- le cas exception technique sans message ;
- le cas résultats vides après filtrage ;
- le cas nominal avec filtrage, tri, suppression des blank
  et dédoublonnage.

For `rechercherTousParPage(...)`, les tests Mock doivent verrouiller au minimum :

- le cas `pRequetePage == null` ;
- le cas exception technique avec message ;
- le cas exception technique sans message ;
- le cas `gateway.rechercherTousParPage(...) == null` ;
- le cas nominal avec reprise de la pagination,
  filtrage des `null`, tri et dédoublonnage.

Pour `findByLibelle(...)`, les tests Mock doivent verrouiller au minimum :

- le cas `pLibelle` blank ;
- le cas `gateway.findByLibelle(...) == null` ;
- le cas exception technique avec message ;
- le cas exception technique sans message ;
- le cas introuvable ;
- le cas nominal avec plusieurs résultats exacts possibles,
  tri et dédoublonnage.

Pour `findByLibelleRapide(...)`, les tests Mock doivent verrouiller au minimum :

- le cas `pContenu == null` ;
- le cas `pContenu` blank ;
- le cas `gateway.findByLibelleRapide(...) == null` ;
- le cas exception technique avec message ;
- le cas exception technique sans message ;
- le cas vide après filtrage ;
- le cas nominal avec filtrage, tri et dédoublonnage.

### Point de vigilance pour les tests d’Intégration

Pour `creer(...)`, le test d’intégration cible doit, à terme, prouver :

- la création effective en base ;
- le rattachement effectif au parent ;
- l’absence de doublon ;
- la cohérence du message observable.

Pour `rechercherTous()`, le test d’intégration cible doit, à terme, prouver :

- la cohérence entre la liste retournée et `count()` ;
- la présence réelle en base des lignes retournées ;
- la cohérence du parent pour chaque sous-type vérifié ;
- le cas base vide avec `MESSAGE_RECHERCHE_VIDE`.

Pour `rechercherTousString()`, le test d’intégration cible doit, à terme, prouver :

- la présence des libellés créés dans la réponse ;
- l’absence de doublon dans la réponse ;
- l’absence de libellé blank dans la réponse ;
- la présence physique en base des lignes correspondant aux libellés vérifiés ;
- le cas base vide avec `MESSAGE_RECHERCHE_VIDE`.

Pour `rechercherTousParPage(...)`, le test d’intégration cible doit, à terme, prouver :

- la cohérence entre la pagination retournée et `count()` ;
- la cohérence de `pageNumber`, `pageSize` et `totalElements` ;
- la présence réelle en base des lignes correspondant aux DTO paginés vérifiés ;
- la cohérence du parent pour les sous-types vérifiés ;
- le message exact `MESSAGE_RECHERCHE_PAGINEE_OK` en cas de succès.

Pour `findByLibelle(...)`, le test d’intégration cible doit, à terme, prouver :

- qu’un même libellé exact peut remonter plusieurs DTO ;
- que ces DTO peuvent appartenir à des parents distincts ;
- que les couples parent / sous-type existent physiquement en base ;
- que le message exact `MESSAGE_SUCCES_RECHERCHE`
  est positionné en cas de succès ;
- qu’un libellé introuvable retourne une liste vide
  avec `MESSAGE_OBJ_INTROUVABLE + libellé`.

Pour `findByLibelleRapide(...)`, le test d’intégration cible doit, à terme, prouver :

- que la recherche blank délègue à `rechercherTous()` ;
- que la recherche introuvable retourne une liste vide
  avec `MESSAGE_RECHERCHE_VIDE` ;
- que les DTO correspondant au fragment recherché
  existent physiquement en base ;
- que les objets hors cible ne sont pas attendus dans le résultat ;
- que le message exact `MESSAGE_RECHERCHE_OK`
  est positionné en cas de succès.