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

## 7 bis) Formalisme obligatoire des commentaires de bloc dans l’ADAPTER UC

Avant toute rédaction, analyse, correction ou génération de code concernant une méthode UC Produit dans l’ADAPTER `ProduitCuService`, l’IA doit relire, en plus de la séquence du §7 :

1. la méthode cible dans `ProduitCuService` ;
2. les méthodes déjà validées de `ProduitCuService` portant un scénario comparable ;
3. si `ProduitCuService` ne fournit pas assez d’exemples stables, une ou plusieurs méthodes déjà validées de `TypeProduitCuService` ou `SousTypeProduitCuService` portant un scénario comparable.

### 7 bis.1) Règle absolue

Les commentaires de bloc dans l’ADAPTER UC ne doivent jamais être inventés.
Ils doivent être déduits du code validé réellement relu.

### 7 bis.2) Règles obligatoires

Dans `ProduitCuService`, un commentaire de bloc doit :

- annoncer exactement ce que fait le bloc situé juste dessous ;
- rester factuel, concret et opérationnel ;
- décrire le comportement observable côté UC ;
- reprendre les constantes, messages et exceptions réellement utilisés quand ils structurent le comportement ;
- distinguer explicitement :
  - l’erreur utilisateur bénigne,
  - la précondition bloquante,
  - la délégation,
  - la sécurisation technique,
  - la préparation de la réponse,
  - le positionnement du message observable,
  - le retour final.

### 7 bis.3) Formes attendues

Lorsque le scénario s’y prête, les commentaires doivent reprendre des formes du type :

- `Erreur utilisateur bénigne : ...`
- `Si ... : émet MESSAGE_X + LOG + ExceptionY.`
- `Délègue au GATEWAY ...`
- `Une réponse technique null du GATEWAY est une anomalie ...`
- `Retire les null, trie ...`
- `Positionne le message observable ...`
- `retourne ...`

### 7 bis.4) Interdictions absolues

Dans `ProduitCuService`, il est interdit :

- d’inventer un nouveau style de commentaires ;
- d’écrire des commentaires philosophiques, vagues ou décoratifs ;
- d’écrire un commentaire plus faible que ceux des méthodes déjà validées ;
- d’utiliser une formule qui n’est pas confirmée par les méthodes de référence relues ;
- de commenter l’intention générale de la méthode au lieu du bloc concret réellement exécuté.

### 7 bis.5) Conséquence opérationnelle

Avant toute livraison de code pour `ProduitCuService`, l’IA doit vérifier que les commentaires générés :

1. correspondent exactement au bloc situé juste dessous ;
2. reprennent le vocabulaire déjà validé dans `ProduitCuService` ;
3. n’introduisent aucune formule nouvelle non confirmée par les méthodes relues ;
4. restent homogènes avec les méthodes déjà validées de `TypeProduitCuService` et `SousTypeProduitCuService` lorsque celles-ci servent de référence.

## 7 ter) Règles anti-régression de génération

Avant toute génération de code Produit, l’IA doit relire les règles sacrées du `CONTRAT_IA.md`
sur les 5 points suivants :

1. comparaisons de chaînes du projet :
   ne jamais utiliser `StringUtils.equalsIgnoreCase(...)`,
   utiliser `Strings.CI.equals(...)` / `Strings.CI.compare(...)` ;

2. commentaires de bloc ADAPTER UC :
   reproduire le style validé,
   sans commentaire vague ni inventé ;

3. Mockito strict :
   aucun stub inutile n’est toléré ;

4. constantes de tests :
   réutiliser ou poser les constantes dans la zone des constantes,
   jamais de littéraux métier dispersés ;

5. preuve BD en intégration :
   les tests d’intégration Produit doivent remonter
   au niveau de preuve SQL directe via `JdbcTemplate`
   déjà validé sur `TypeProduit` et `SousTypeProduit`.

### 7 ter.1) Conséquence opérationnelle

Avant toute livraison de code Produit, l’IA doit vérifier explicitement que :

- aucune comparaison générée n’utilise `StringUtils.equalsIgnoreCase(...)` ;
- aucun commentaire de bloc n’est plus faible
  que ceux déjà validés dans `ProduitCuService` ;
- aucun stub Mockito inutile ne subsiste ;
- aucune constante métier n’est laissée sous forme de littéral dispersé ;
- aucun test d’intégration important n’est livré
  avec un niveau de preuve BD inférieur à celui des classes de référence.

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

## 11) Contrat spécifique de `rechercherTousString()`

Signature cible :
- `List<String> rechercherTousString() throws Exception;`

### 11.1) Scénario nominal attendu

Le scénario nominal de `rechercherTousString()` est :

1. déléguer la recherche exhaustive à `rechercherTous()` ;
2. récupérer la liste de `ProduitDTO.OutputDTO` préparée par cette méthode ;
3. extraire les libellés Produit exploitables ;
4. retirer les éventuels libellés `null` ou blank ;
5. positionner le message observable ;
6. retourner la liste finale de `String`.

### 11.2) Cas observables attendus

- si `rechercherTous()` échoue :
  - propage l'exception ;
  - conserve le message déjà positionné par `rechercherTous()` ;

- si aucun libellé exploitable n'est disponible :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un libellé exploitable est disponible :
  - retourne une liste non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 11.3) Garanties spécifiques de `rechercherTousString()`

- la méthode ne doit jamais retourner `null`
  si la recherche exhaustive a abouti ;
- aucun libellé `null` ou blank ne doit être exposé à l'appelant ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la liste finale ;
- les libellés retournés doivent correspondre
  aux `ProduitDTO.OutputDTO` réellement préparés par `rechercherTous()`.
  
  ## 12) Contrat spécifique de `rechercherTousParPage(...)`

Signature cible :
- `ResultatPage<ProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`

### 12.1) Scénario nominal attendu

Le scénario nominal de `rechercherTousParPage(...)` est :

1. recevoir une `RequetePage` ;
2. valider que la requête de pagination n'est pas `null` ;
3. déléguer la recherche paginée au `GATEWAY` Produit ;
4. récupérer la page métier correspondante ;
5. retirer les éventuels éléments métier `null` ;
6. trier les objets métier ;
7. convertir le contenu métier en `ProduitDTO.OutputDTO` ;
8. reconstruire un `ResultatPage<ProduitDTO.OutputDTO>` cohérent ;
9. positionner le message observable ;
10. retourner la page finale.

### 12.2) Cas observables attendus

- si `pRequetePage == null` :
  - positionne `getMessage()` à `MESSAGE_PAGEABLE_NULL` ;
  - lève une `IllegalStateException` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + <message technique>` ;
  - propage l'exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - propage l'exception ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_KO` ;
  - lève une `IllegalStateException` ;

- si la recherche paginée aboutit :
  - retourne un `ResultatPage<ProduitDTO.OutputDTO>` non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_OK`
    uniquement après préparation complète de la réponse paginée.

### 12.3) Garanties spécifiques de `rechercherTousParPage(...)`

- la méthode ne doit jamais exposer une réponse paginée partielle incohérente ;
- le contenu paginé retourné doit correspondre à l'état métier effectivement accessible via le `GATEWAY` ;
- le message de succès ne doit être positionné qu'après conversion complète de la page résultat ;
- `pageNumber`, `pageSize` et `totalElements` doivent rester cohérents avec la réponse technique paginée préparée par le service UC.

## 13) Contrat spécifique de `findByLibelle(...)`

Signature cible :
- `List<ProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;`

### 13.1) Scénario nominal attendu

Le scénario nominal de `findByLibelle(...)` est :

1. recevoir un libellé exact ;
2. valider le libellé demandé ;
3. déléguer la recherche exacte au `GATEWAY` Produit ;
4. retirer les éventuels objets métier `null` ;
5. trier les objets métier ;
6. convertir les résultats métier en `ProduitDTO.OutputDTO` ;
7. positionner le message observable ;
8. retourner la liste finale.

### 13.2) Cas observables attendus

- si `pLibelle` est blank :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_PARAM_BLANK` ;
  - ne lève aucune exception ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `KO_TECHNIQUE_RECHERCHE` ;
  - propage une exception technique ;

- si aucun objet n'est trouvé :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un objet est trouvé :
  - retourne une liste non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 13.3) Garanties spécifiques de `findByLibelle(...)`

- la méthode ne doit jamais exposer d'objet métier `null` à l'appelant ;
- la liste retournée, si elle n'est pas vide,
  doit correspondre à l'état métier effectivement accessible via le `GATEWAY` ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- la recherche exacte peut retourner plusieurs `OutputDTO`
  si plusieurs `Produit` distincts partagent le même libellé exact.

## 14) Contrat spécifique de `findByLibelleRapide(...)`

Signature cible :
- `List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`

### 14.1) Scénario nominal attendu

Le scénario nominal de `findByLibelleRapide(...)` est :

1. recevoir un contenu de recherche rapide ;
2. valider le contenu demandé ;
3. si le contenu est blank, déléguer à `rechercherTous()` ;
4. sinon, déléguer la recherche rapide au `GATEWAY` Produit ;
5. retirer les éventuels objets métier `null` ;
6. trier les objets métier ;
7. convertir les résultats métier en `ProduitDTO.OutputDTO` ;
8. positionner le message observable ;
9. retourner la liste finale.

### 14.2) Cas observables attendus

- si `pContenu == null` :
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL` ;
  - lève une exception ;

- si `pContenu` est blank :
  - délègue à `rechercherTous()` ;
  - conserve les mêmes messages et les mêmes erreurs que `rechercherTous()` ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `KO_TECHNIQUE_RECHERCHE` ;
  - propage une exception technique ;

- si aucun objet n'est trouvé :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un objet est trouvé :
  - retourne une liste non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 14.3) Garanties spécifiques de `findByLibelleRapide(...)`

- la méthode ne doit jamais exposer d'objet métier `null` à l'appelant ;
- la liste retournée, si elle n'est pas vide,
  doit correspondre à l'état métier effectivement accessible via le `GATEWAY` ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- un contenu blank ne doit pas être traité comme une recherche technique,
  mais comme une délégation explicite à `rechercherTous()`.
  
  ## 15) Contrat spécifique de `findAllByParent(...)`

Signature cible :
- `List<ProduitDTO.OutputDTO> findAllByParent(SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;`

### 15.1) Scénario nominal attendu

Le scénario nominal de `findAllByParent(...)` est :

1. recevoir un parent `SousTypeProduitDTO.InputDTO` ;
2. valider que le parent demandé n'est pas `null` ;
3. valider que le libellé du parent n'est pas blank ;
4. retrouver le parent persistant correspondant ;
5. déléguer au `GATEWAY` Produit la recherche de tous les Produits rattachés à ce parent ;
6. retirer les éventuels objets métier `null` ;
7. trier les objets métier ;
8. convertir les résultats métier en `ProduitDTO.OutputDTO` ;
9. positionner le message observable ;
10. retourner la liste finale.

### 15.2) Cas observables attendus

- si `pSousTypeProduit == null` :
  - positionne `getMessage()` à `RECHERCHE_SOUSTYPEPRODUIT_NULL` ;
  - lève une exception ;

- si le libellé du parent est blank :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT` ;
  - lève une exception ;

- si le parent n'est pas trouvé ou n'est pas persistant :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT` ;
  - lève une exception ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `KO_TECHNIQUE_RECHERCHE` ;
  - propage une exception technique ;

- si aucun objet n'est trouvé :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un objet est trouvé :
  - retourne une liste non `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 15.3) Garanties spécifiques de `findAllByParent(...)`

- la méthode ne doit jamais exposer d'objet métier `null` à l'appelant ;
- la liste retournée, si elle n'est pas vide,
  doit correspondre aux Produits effectivement accessibles
  pour le parent persistant demandé ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- aucun résultat partiel incohérent ne doit être exposé à l'appelant.

## 16) Contrat spécifique de `findByDTO(...)`

Signature cible :
- `ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 16.1) Scénario nominal attendu

Le scénario nominal de `findByDTO(...)` est :

1. recevoir un `ProduitDTO.InputDTO` ;
2. valider le DTO de recherche ;
3. valider les informations de parent nécessaires à la recherche ;
4. retrouver le parent persistant correspondant ;
5. demander au `GATEWAY` Produit tous les Produits rattachés à ce parent ;
6. rechercher dans cette liste l'objet correspondant exactement au libellé demandé ;
7. convertir l'objet métier trouvé en `ProduitDTO.OutputDTO` ;
8. positionner le message observable ;
9. retourner la réponse finale.

### 16.2) Cas observables attendus

- si `pInputDTO == null` :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OBJ_NULL` ;
  - ne lève aucune exception ;

- si le parent porté par `pInputDTO` est blank :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT` ;
  - lève une exception ;

- si aucun parent persistant n'est trouvé :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si aucun `Produit` ne correspond :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si un `Produit` exact est trouvé :
  - retourne un `ProduitDTO.OutputDTO` non `null` ;
  - positionne `getMessage()` à `MESSAGE_SUCCES_RECHERCHE`.

### 16.3) Garanties spécifiques de `findByDTO(...)`

- la méthode ne doit jamais exposer de résultat incohérent à l'appelant ;
- l'objet retourné, s'il n'est pas `null`,
  doit correspondre à un `Produit` effectivement retrouvé dans le stockage ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- en cas d'absence de correspondance exacte,
  la méthode doit retourner `null`
  avec `MESSAGE_RECHERCHE_VIDE`.
  
  ## 17) Contrat spécifique de `findById(...)`

Signature cible :
- `ProduitDTO.OutputDTO findById(Long pId) throws Exception;`

### 17.1) Scénario nominal attendu

Le scénario nominal de `findById(...)` est :

1. recevoir un identifiant technique de `Produit` ;
2. déléguer la recherche au `GATEWAY` Produit ;
3. récupérer l'objet métier correspondant ;
4. convertir l'objet métier retrouvé en `ProduitDTO.OutputDTO` ;
5. positionner le message observable ;
6. retourner la réponse finale.

### 17.2) Cas observables attendus

- si `pId == null` :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL` ;
  - ne lève aucune exception ;

- si aucun objet n'est trouvé pour l'identifiant demandé :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_OBJ_INTROUVABLE + pId` ;

- si un objet est trouvé :
  - retourne un `ProduitDTO.OutputDTO` non `null` ;
  - positionne `getMessage()` à `MESSAGE_SUCCES_RECHERCHE` ;

- en cas d'erreur technique remontée par le `GATEWAY`
  ou par la conversion finale :
  - propage une exception conforme à l'implémentation.

### 17.3) Garanties spécifiques de `findById(...)`

- la méthode ne doit jamais exposer de résultat incohérent à l'appelant ;
- l'objet retourné, s'il n'est pas `null`,
  doit correspondre à un `Produit` effectivement retrouvé dans le stockage ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- en cas d'absence de résultat,
  la méthode doit retourner `null`
  avec un message observable explicite.
  
  ## 18) Contrat spécifique de `update(...)`

Signature cible :
- `ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 18.1) Scénario nominal attendu

Le scénario nominal de `update(...)` est :

1. recevoir un `ProduitDTO.InputDTO` transmis par la couche appelante ;
2. valider les préconditions observables sur le DTO,
   sur le libellé Produit et sur le parent ;
3. retrouver le `SousTypeProduit` parent persistant correspondant au DTO ;
4. demander au `GATEWAY` tous les `Produit` rattachés à ce parent ;
5. identifier, dans cette collection,
   le `Produit` effectivement persistant
   correspondant au couple `[parent, libellé]` ;
6. reconstruire l'objet métier à partir du DTO de modification ;
7. réinjecter l'identifiant persistant retrouvé
   et rattacher explicitement le parent persistant ;
8. déléguer la modification technique au composant `GATEWAY` ;
9. convertir l'objet métier modifié en `ProduitDTO.OutputDTO` ;
10. positionner le message observable ;
11. retourner la réponse finale.

### 18.2) Cas observables attendus

- si `pInputDTO == null` :
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL` ;
  - lève une `ExceptionParametreNull` ;

- si `pInputDTO.getProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_PARAM_BLANK` ;
  - lève une `ExceptionParametreBlank` ;

- si le parent porté par le DTO est blank,
  absent ou non persistant :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT` ;
  - lève une `IllegalStateException` ;

- si aucun `Produit` persistant
  ne correspond au couple `[parent, libellé]` :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_OBJ_INTROUVABLE + libellé` ;

- si l'objet retrouvé n'est pas persistant :
  - positionne `getMessage()` à `MESSAGE_OBJ_NON_PERSISTE + libellé` ;
  - lève une `ExceptionNonPersistant` ;

- si la modification échoue techniquement :
  - positionne un message utilisateur technique cohérent
    construit à partir de `MESSAGE_MODIF_KO + libellé` ;
  - propage une exception circonstanciée conforme à l'implémentation ;

- si le `GATEWAY` retourne `null` :
  - retourne `null` ;
  - positionne `getMessage()` à `MESSAGE_MODIF_KO + libellé` ;

- si la conversion finale retourne `null` :
  - positionne `getMessage()` à
    `MESSAGE_MODIF_KO + libellé + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - lève une `IllegalStateException` ;

- en cas de succès :
  - retourne un `ProduitDTO.OutputDTO` non `null` ;
  - positionne `getMessage()` à `MESSAGE_MODIF_OK + libellé`
    uniquement après préparation complète
    de la réponse utilisateur.

### 18.3) Garanties spécifiques de `update(...)`

- la ré-identification de l'objet à modifier
  s'appuie sur le couple `[parent, libellé]`
  et jamais sur le seul libellé Produit ;
- le message retourné par `getMessage()`
  reflète l'issue observable réelle de l'opération ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur finale ;
- l'identifiant persistant retrouvé
  est réinjecté dans l'objet envoyé au `GATEWAY` ;
- l'objet retourné, s'il n'est pas `null`,
  correspond à un `Produit` effectivement modifié
  dans le stockage et exprimé sous forme de DTO.
  
  ## 19) Contrat spécifique de `delete(...)`

Signature cible :
- `void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 19.1) Scénario nominal attendu

Le scénario nominal de `delete(...)` est :

1. recevoir un `ProduitDTO.InputDTO` transmis par la couche appelante ;
2. valider les préconditions observables sur le DTO,
   sur le libellé Produit et sur le parent ;
3. retrouver le `SousTypeProduit` parent persistant correspondant au DTO ;
4. demander au `GATEWAY` tous les `Produit` rattachés à ce parent ;
5. identifier, dans cette collection,
   le `Produit` effectivement persistant
   correspondant au couple `[parent, libellé]` ;
6. déléguer la destruction technique au composant `GATEWAY` ;
7. positionner le message observable ;
8. terminer sans exposer de résultat incohérent à la couche appelante.

### 19.2) Cas observables attendus

- si `pInputDTO == null` :
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL` ;
  - lève une `ExceptionParametreNull` ;

- si `pInputDTO.getProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_PARAM_BLANK` ;
  - lève une `ExceptionParametreBlank` ;

- si le parent porté par le DTO est blank,
  absent ou non persistant :
  - positionne `getMessage()` à `MESSAGE_PAS_PARENT` ;
  - lève une `IllegalStateException` ;

- si la recherche des enfants du parent
  retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL` ;
  - lève une `ExceptionStockageVide` ;

- si aucun `Produit` persistant
  ne correspond au couple `[parent, libellé]` :
  - ne supprime rien ;
  - positionne `getMessage()` à `MESSAGE_OBJ_INTROUVABLE + libellé` ;

- si l'objet retrouvé n'est pas persistant :
  - positionne `getMessage()` à `MESSAGE_OBJ_NON_PERSISTE + libellé` ;
  - lève une `ExceptionNonPersistant` ;

- si une recherche technique échoue :
  - positionne un message utilisateur technique cohérent
    construit à partir de `KO_TECHNIQUE_RECHERCHE`,
    de `TIRET_ESPACE`
    et d'un détail technique sécurisé ;
  - propage une exception circonstanciée conforme à l'implémentation ;

- si la suppression technique échoue :
  - positionne un message utilisateur technique cohérent
    construit à partir de `MESSAGE_DELETE_KO + libellé` ;
  - propage une exception circonstanciée conforme à l'implémentation ;

- en cas de succès :
  - détruit effectivement l'objet persistant ciblé ;
  - positionne `getMessage()` à `MESSAGE_DELETE_OK + libellé`
    uniquement après destruction effective.

### 19.3) Garanties spécifiques de `delete(...)`

- la ré-identification de l'objet à détruire
  s'appuie sur le couple `[parent, libellé]`
  et jamais sur le seul libellé Produit ;
- aucune suppression ne doit viser
  un autre parent portant le même libellé Produit ;
- le message retourné par `getMessage()`
  reflète l'issue observable réelle de l'opération ;
- le message de succès
  n'est positionné qu'après destruction effective
  de l'objet persistant ;
- les tests Mock et Intégration
  doivent verrouiller explicitement
  la preuve du couple `[parent, libellé]`.