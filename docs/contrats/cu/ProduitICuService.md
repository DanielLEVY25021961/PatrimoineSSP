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
- `List<ProduitDTO.OutputDTO> rechercherTous() throws Exception;`

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

5. preuve dans le stockage en intégration :
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
  avec un niveau de preuve dans le stockage inférieur à celui des classes de référence.

## 7 quater) Loi de structuration des constantes du PORT UC

Dans `ProduitICuService`, les constantes de messages propres à une méthode doivent être regroupées dans un bloc dédié à cette méthode. Les blocs suivent l'ordre des méthodes du PORT.

Chaque groupe est précédé d'un séparateur canonique de largeur fixe de 76 caractères selon la convention de présentation du projet. Le séparateur doit être recopié depuis une référence validée et seul le nombre de tirets nécessaire au centrage du nom de la méthode peut être adapté.

Exemple canonique pour la méthode cible :

```java
	/* -------------------- findByLibelleRapide ------------------------ */
```

Avant d'ajouter ou de déplacer une constante, l'IA doit relire toute la zone des constantes, distinguer les constantes communes des constantes dédiées, puis vérifier l'ordre et la largeur de tous les séparateurs. Il est interdit d'ajouter une constante dédiée en fin de zone ou dans le bloc d'une autre méthode.

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
2. valider que le DTO, le libellé du `Produit` et le libellé du `SousTypeProduit` parent direct sont exploitables ;
3. rechercher les `SousTypeProduit` persistants portant le libellé parent demandé ;
4. résoudre un unique `SousTypeProduit` parent direct compatible :
   - lorsque `pInputDTO.getTypeProduit()` est renseigné, l’utiliser uniquement pour départager les `SousTypeProduit` homonymes selon l’identité propre du parent `[TypeProduit, SousTypeProduit]` ;
   - lorsque `pInputDTO.getTypeProduit()` est blank, accepter la résolution uniquement si un seul parent persistant compatible subsiste ;
5. vérifier l’absence de doublon fonctionnel sur le couple `[SousTypeProduit, Produit]` ;
6. convertir l’`InputDTO` en objet métier `Produit` ;
7. rattacher explicitement le `SousTypeProduit` parent persistant exact à l’objet métier ;
8. déléguer la création à `gateway.creer(...)` ;
9. récupérer l’objet métier réellement créé dans le stockage ;
10. convertir l’objet métier créé en `ProduitDTO.OutputDTO` ;
11. positionner `getMessage()` à `MESSAGE_CREER_OK` uniquement après la conversion réussie ;
12. retourner la réponse finale exploitable par la couche appelante.

### 10.2 Cas observables attendus

- si `pInputDTO == null` :
  - retourne `null`,
  - positionne `getMessage()` à `MESSAGE_CREER_NULL_KO`,
  - n’émet ni LOG ni exception,
  - ne sollicite aucun `GATEWAY` ;

- si `pInputDTO.getProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_CREER_LIBELLE_BLANK_KO`,
  - émet un LOG,
  - lève une `ExceptionParametreBlank`,
  - ne sollicite aucun `GATEWAY` ;

- si `pInputDTO.getSousTypeProduit()` est blank :
  - positionne `getMessage()` à `MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO`,
  - émet un LOG,
  - lève une `IllegalStateException`,
  - ne sollicite aucun `GATEWAY` ;

- si `rechercherParentPersistant(...)` lève une exception :
  - sécurise le message technique avec `MSG_ERREUR_NON_SPECIFIEE` lorsque nécessaire,
  - positionne `getMessage()` à `PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO + message sécurisé`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si aucun `SousTypeProduit` parent direct persistant ne correspond aux critères fournis :
  - positionne `getMessage()` à `MESSAGE_CREER_PARENT_NON_PERSISTANT_KO`,
  - émet un LOG,
  - lève une `IllegalStateException`,
  - ne sollicite pas le `GATEWAY` Produit ;

- si `pInputDTO.getTypeProduit()` est blank et qu’un seul `SousTypeProduit` parent direct persistant est compatible :
  - accepte ce parent unique,
  - poursuit le contrôle d’unicité et la création,
  - déduit le `TypeProduit` du `SousTypeProduit` parent dans la réponse finale ;

- si `pInputDTO.getTypeProduit()` est blank et que plusieurs `SousTypeProduit` persistants distincts restent compatibles :
  - ne sélectionne jamais arbitrairement le premier parent retourné,
  - positionne `getMessage()` à `MESSAGE_CREER_PARENT_NON_PERSISTANT_KO`,
  - émet un LOG,
  - lève une `IllegalStateException`,
  - ne sollicite pas le `GATEWAY` Produit ;

- si `pInputDTO.getTypeProduit()` est renseigné :
  - l’utilise uniquement pour résoudre l’identité propre du `SousTypeProduit` parent `[TypeProduit, SousTypeProduit]`,
  - ne l’ajoute jamais comme troisième composante à l’identité du `Produit` ;

- si le contrôle d’unicité lève une exception :
  - sécurise le message technique avec `MSG_ERREUR_NON_SPECIFIEE` lorsque nécessaire,
  - positionne `getMessage()` à `PREFIX_MESSAGE_CREER_DOUBLON_KO + message sécurisé`,
  - émet un LOG,
  - propage l’exception d’origine,
  - ne sollicite pas `gateway.creer(...)` ;

- si un doublon est détecté sur le couple `[SousTypeProduit, Produit]` :
  - positionne `getMessage()` à `MESSAGE_CREER_DOUBLON_KO + libellé`,
  - émet un LOG,
  - lève une `ExceptionDoublon`,
  - ne sollicite pas `gateway.creer(...)` ;

- si un même libellé `Produit` existe sous un autre `SousTypeProduit` parent direct :
  - ne le considère pas comme un doublon,
  - poursuit la création sous le parent direct résolu ;

- si `gateway.creer(...)` lève une exception :
  - sécurise le message technique avec `MSG_ERREUR_NON_SPECIFIEE` lorsque nécessaire,
  - positionne `getMessage()` à `PREFIX_MESSAGE_CREER_GATEWAY_KO + message sécurisé`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `gateway.creer(...)` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_CREER_GATEWAY_KO`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si `ConvertisseurMetierToOutputDTOProduit.convert(...)` lève une exception :
  - sécurise le message technique avec `MSG_ERREUR_NON_SPECIFIEE` lorsque nécessaire,
  - positionne `getMessage()` à `PREFIX_MESSAGE_CREER_CONVERSION_KO + message sécurisé`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `ConvertisseurMetierToOutputDTOProduit.convert(...)` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_CREER_CONVERSION_KO`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- en cas de succès :
  - retourne un `ProduitDTO.OutputDTO` non `null`,
  - restitue le couple métier exact `[SousTypeProduit, Produit]` et l’identifiant réellement créés dans le stockage,
  - restitue le `TypeProduit` déduit du `SousTypeProduit` parent,
  - positionne `getMessage()` à `MESSAGE_CREER_OK` uniquement après préparation complète de la réponse.

### 10.3 Garanties spécifiques de `creer(...)`

- le parent direct d’un `Produit` est un `SousTypeProduit` ;
- l’identité fonctionnelle et la contrainte d’unicité de `Produit` portent exclusivement sur le couple `[SousTypeProduit, Produit]` ;
- le `TypeProduit` est le grand-parent du `Produit` et se déduit du `SousTypeProduit` parent ; il ne constitue jamais une troisième composante de l’identité du `Produit` ;
- le `TypeProduit` porté par l’`InputDTO` est facultatif pour `creer(...)` et sert uniquement, lorsqu’il est renseigné, à départager les `SousTypeProduit` homonymes selon l’identité propre du parent ;
- lorsque le `TypeProduit` est absent, un parent persistant unique peut être résolu et utilisé ;
- lorsque plusieurs parents persistants distincts restent compatibles avec les critères fournis, aucun parent ne doit être choisi arbitrairement et la création doit être refusée ;
- le parent direct persistant exact est recherché et validé avant le contrôle d’unicité et avant toute création ;
- un même libellé `Produit` sous un autre `SousTypeProduit` parent direct ne constitue pas un doublon ;
- aucun appel à `gateway.creer(...)` n’est effectué si une précondition, la résolution du parent direct ou l’unicité sont invalides ;
- aucun succès ne doit être exposé si le parent direct n’est pas persistant ou si sa résolution reste ambiguë ;
- aucun succès ne doit être exposé si le `GATEWAY` retourne `null` ;
- aucun succès ne doit être exposé si la conversion finale retourne `null` ;
- le DTO retourné doit représenter l’état réellement créé dans le stockage et son rattachement au `SousTypeProduit` parent persistant exact ;
- le message utilisateur doit être déterministe, stable et vérifiable par les tests Mock et d’intégration ;
- les tests Mock du bloc `creer(...)` doivent être dérivés de toutes les branches observables, notamment le parent ambigu sans `TypeProduit`, le parent unique sans `TypeProduit`, le doublon sur le même parent et l’absence de doublon sous un autre parent ;
- les tests d’intégration doivent prouver l’écriture réelle dans le stockage, l’unicité du couple `[SousTypeProduit, Produit]` et le round-trip par les méthodes de recherche.

## 11) Contrat spécifique de `rechercherTous()`

Signature cible :

- `List<ProduitDTO.OutputDTO> rechercherTous() throws Exception;`

### 11.1) Scénario nominal attendu

Le scénario nominal de `rechercherTous()` est :

1. demander au `GATEWAY` la liste complète des `Produit` accessibles dans le stockage ;
2. sécuriser le retour technique du stockage ;
3. retirer les éventuels objets métier `null` ;
4. trier les objets métier selon l’ordre naturel de `Produit` :
   - d’abord le `SousTypeProduit` parent direct ;
   - puis le libellé du `Produit` ;
5. respecter l’identité propre du parent direct `[TypeProduit, SousTypeProduit]` pendant la comparaison des parents ;
6. convertir chaque objet métier en `ProduitDTO.OutputDTO` ;
7. dédoublonner les DTO en conservant l’ordre de la liste triée ;
8. positionner le message observable après préparation complète de la réponse ;
9. retourner une liste non `null`, éventuellement vide.

### 11.2) Cas observables attendus

- si `gateway.rechercherTous()` lève une exception avec message :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `gateway.rechercherTous()` lève une exception sans message :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `gateway.rechercherTous()` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` portant ce même message ;

- si `convertirEtDedoublonner(...)` lève une exception avec message :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_CONVERSION_KO + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `convertirEtDedoublonner(...)` lève une exception sans message :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_CONVERSION_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l’exception d’origine ;

- si `convertirEtDedoublonner(...)` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_CONVERSION_NULL_KO`,
  - émet un LOG,
  - lève une `IllegalStateException` portant ce même message ;

- si la liste d’`OutputDTO` est vide après filtrage, tri, conversion et dédoublonnage :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_VIDE` ;

- si la liste d’`OutputDTO` contient des résultats :
  - retourne une liste non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHER_TOUS_OK`.

### 11.3) Garanties spécifiques de `rechercherTous()`

- la méthode ne retourne jamais `null` lorsque le stockage est exploitable ;
- le tri respecte l’ordre naturel métier `[SousTypeProduit, Produit]` ;
- le `SousTypeProduit` parent conserve sa propre identité `[TypeProduit, SousTypeProduit]` ;
- le `TypeProduit` reste le grand-parent déduit du parent direct et ne devient jamais une troisième composante de l’identité du `Produit` ;
- deux Produits portant le même libellé sous deux `SousTypeProduit` parents directs distincts restent deux résultats distincts ;
- deux représentations du même Produit sous le même parent direct sont dédoublonnées selon l’égalité des `OutputDTO` ;
- les éléments métier `null` et les conversions individuelles `null` ne sont jamais exposés à l’appelant ;
- le message de succès n’est positionné qu’après conversion et dédoublonnage complets ;
- la lecture ne modifie jamais le stockage ;
- la branche défensive `dtos == null` reste contractuelle, mais aucun test distinct ne doit être inventé tant que le helper privé réel `convertirEtDedoublonner(...)` garantit toujours une liste non `null` et ne peut pas être substitué sans modifier le code de production.

### 11.4) Dérivation des preuves attendues

Les tests Mock doivent être dérivés des différences observables suivantes :

- exception Gateway avec message ;
- exception Gateway sans message ;
- retour Gateway `null` ;
- exception de conversion avec message ;
- exception de conversion sans message ;
- résultat vide après filtrage et conversion ;
- scénario nominal combinant filtrage, ordre naturel complet, conversion, dédoublonnage, conservation des homonymes sous des parents distincts, message final et interactions Gateway.

Les tests d’intégration doivent prouver selon le scénario :

- le cas réellement vide dans le stockage ;
- le message dédié `MESSAGE_RECHERCHER_TOUS_VIDE` ;
- le cas nominal avec des identifiants persistants ;
- la présence physique des couples `[SousTypeProduit, Produit]` retournés ;
- la conservation du même libellé Produit sous plusieurs parents directs ;
- l’ordre naturel de la réponse ;
- le message dédié `MESSAGE_RECHERCHER_TOUS_OK` ;
- l’absence de modification du stockage pendant la lecture.

## 12) Contrat spécifique de `rechercherTousString()`

Signature cible :

- `List<String> rechercherTousString() throws Exception;`

### 12.1) Scénario nominal attendu

Le scénario nominal de `rechercherTousString()` est :

1. déléguer une seule fois la recherche exhaustive à `rechercherTous()` ;
2. récupérer la liste non `null`, ordonnée et dédoublonnée par identité
   de `ProduitDTO.OutputDTO` préparée par `rechercherTous()` ;
3. parcourir cette liste dans l'ordre reçu ;
4. extraire les libellés avec `ProduitDTO.OutputDTO.getProduit()` ;
5. retirer les éventuels DTO `null` et les libellés `null` ou blank ;
6. conserver l'ordre et la multiplicité des libellés issus des DTO distincts ;
7. positionner le message observable après préparation complète de la liste ;
8. retourner une liste finale de `String` non `null`.

### 12.2) Cas observables attendus

- si le `GATEWAY` Produit retourne `null` pendant `rechercherTous()` :
  - propage l'`ExceptionStockageVide` levée par `rechercherTous()` ;
  - conserve exactement le message
    `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO` ;

- si le `GATEWAY` Produit lève une exception avec message
  pendant `rechercherTous()` :
  - propage la même exception ;
  - conserve exactement
    `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO`
    `+ TIRET_ESPACE + <message technique>` ;

- si le `GATEWAY` Produit lève une exception sans message
  pendant `rechercherTous()` :
  - propage la même exception ;
  - conserve exactement
    `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO`
    `+ TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;

- si la conversion en `ProduitDTO.OutputDTO` lève une exception avec message
  pendant `rechercherTous()` :
  - propage la même exception ;
  - conserve exactement
    `MESSAGE_RECHERCHER_TOUS_CONVERSION_KO`
    `+ TIRET_ESPACE + <message technique>` ;

- si la conversion en `ProduitDTO.OutputDTO` lève une exception sans message
  pendant `rechercherTous()` :
  - propage la même exception ;
  - conserve exactement
    `MESSAGE_RECHERCHER_TOUS_CONVERSION_KO`
    `+ TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;

- si la liste préparée par `rechercherTous()` est vide
  ou ne contient aucun libellé exploitable :
  - retourne une liste vide mais non `null` ;
  - remplace le message de la recherche DTO par
    `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un libellé exploitable est disponible :
  - retourne une liste non `null` ;
  - conserve l'ordre de la liste d'`OutputDTO` ;
  - conserve une occurrence par `OutputDTO` distinct ;
  - remplace le message de la recherche DTO par
    `MESSAGE_RECHERCHE_OK`.

### 12.3) Garanties spécifiques de `rechercherTousString()`

- la méthode ne retourne jamais `null` lorsque `rechercherTous()` aboutit ;
- elle ne retourne aucun élément `null` ;
- elle ne retourne aucun libellé blank ;
- elle ne contacte directement aucun `GATEWAY` ;
- elle n'écrit rien dans le stockage ;
- elle conserve l'ordre métier déjà préparé par `rechercherTous()` ;
- elle ne réalise aucun dédoublonnage supplémentaire au niveau `String` ;
- deux `Produit` homonymes rattachés à deux `SousTypeProduit`
  parents directs distincts restent deux DTO distincts et produisent donc
  deux occurrences du même libellé dans la réponse `String` ;
- deux représentations du même Produit déjà dédoublonnées par
  `rechercherTous()` ne réapparaissent pas artificiellement ;
- le message de succès ou d'absence de résultat n'est positionné
  qu'après préparation complète de la liste finale ;
- si `rechercherTous()` échoue, aucune liste partielle n'est retournée
  et le message posé par cette méthode est conservé.

### 12.4) Tests de référence du bloc

Les tests Mock du bloc sont exactement :

- `testRechercherTousStringGatewayRetourNull` ;
- `testRechercherTousStringGatewayKOAvecMessage` ;
- `testRechercherTousStringGatewayKOSansMessage` ;
- `testRechercherTousStringConversionStringKOAvecMessage` ;
- `testRechercherTousStringConversionStringKOSansMessage` ;
- `testRechercherTousStringVideApresFiltrage` ;
- `testRechercherTousStringVideApresLibellesBlank` ;
- `testRechercherTousStringNominal`.

Les tests d'intégration du bloc sont exactement :

- `testRechercherTousStringOk` ;
- `testRechercherTousStringVide`.

Le test nominal d'intégration doit comparer la liste UC à un résultat lu
directement dans le stockage, conserver les occurrences homonymes issues
de parents distincts et prouver que la lecture ne modifie aucune ligne.

## 13) Contrat spécifique de `rechercherTousParPage(...)`

Signature cible :
- `ResultatPage<ProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`

### 13.1) Scénario nominal attendu

Le scénario nominal de `rechercherTousParPage(...)` est :

1. recevoir une `RequetePage` ;
2. valider que la requête de pagination n'est pas `null` ;
3. déléguer la recherche paginée au `GATEWAY` Produit ;
4. sécuriser la page métier retournée ;
5. retirer les éventuels éléments métier `null` ;
6. trier les objets métier ;
7. convertir le contenu métier en `ProduitDTO.OutputDTO` ;
8. dédoublonner les DTO en conservant l'ordre métier ;
9. reconstruire un `ResultatPage<ProduitDTO.OutputDTO>` cohérent ;
10. positionner le message observable après reconstruction complète ;
11. retourner la page finale.

### 13.2) Cas observables attendus

- si `pRequetePage == null` :
  - positionne `getMessage()` à `MESSAGE_PAGEABLE_NULL` ;
  - émet un LOG de service ;
  - lève une `IllegalStateException` ;
  - ne sollicite aucun `GATEWAY` ;

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO + TIRET_ESPACE + <message technique>` ;
  - émet un LOG de service ;
  - propage l'exception d'origine ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - émet un LOG de service ;
  - propage l'exception d'origine ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_KO` ;
  - émet un LOG de service ;
  - lève une `IllegalStateException` ;

- si le filtrage, le tri, la conversion
  ou la reconstruction de la page DTO lève une exception avec message :
  - positionne `getMessage()` à
    `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_PREPARATION_KO + TIRET_ESPACE + <message technique>` ;
  - émet un LOG de service ;
  - propage l'exception d'origine ;

- si cette préparation lève une exception sans message :
  - positionne `getMessage()` à
    `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_PREPARATION_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - émet un LOG de service ;
  - propage l'exception d'origine ;

- si la réponse paginée est correctement reconstruite :
  - retourne un `ResultatPage<ProduitDTO.OutputDTO>` non `null` ;
  - reprend `pageNumber`, `pageSize` et `totalElements` sécurisés ;
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_PAGINEE_OK`.

### 13.3) Garanties spécifiques de `rechercherTousParPage(...)`

- la méthode ne doit jamais retourner `null` lorsque le scénario aboutit ;
- le message observable doit refléter la branche réellement exécutée ;
- une panne de préparation côté UC ne doit jamais être attribuée au `GATEWAY` ;
- le message de succès doit être positionné après reconstruction complète de la page DTO ;
- les `null` techniques issus du stockage ne doivent jamais fuiter jusqu'à l'appelant ;
- le contenu DTO doit être non `null`, filtré, trié et dédoublonné ;
- `pageNumber`, `pageSize` et `totalElements` doivent provenir de la réponse technique sécurisée ;
- les DTO vérifiés doivent correspondre aux identités métier `[SousTypeProduit, Produit]` réellement présentes dans le stockage ;
- le `TypeProduit` grand-parent peut être lu uniquement pour désambiguïser le `SousTypeProduit` parent direct, sans devenir une composante supplémentaire de l'identité Produit ;
- la lecture paginée ne doit pas modifier le stockage ;
- aucun résultat paginé partiel incohérent ne doit être exposé.

## 14) Contrat spécifique de `findByLibelle(...)`

Signature cible :

- `List<ProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;`

### 14.1) Scénario nominal attendu

Le scénario nominal de `findByLibelle(...)` est :

1. recevoir un libellé exact transmis par la couche appelante ;
2. refuser localement un paramètre `null` ou blank sans appeler le `GATEWAY` ;
3. appeler une seule fois `gateway.findByLibelle(pLibelle)` lorsque le paramètre est non blank ;
4. sécuriser la liste technique retournée par le `GATEWAY` ;
5. retirer les éventuels objets métier `null` ;
6. trier les objets métier selon l'ordre métier `[SousTypeProduit, Produit]` ;
7. convertir les objets métier en `ProduitDTO.OutputDTO` ;
8. dédoublonner les DTO en conservant l'ordre issu du tri ;
9. positionner le message observable après préparation complète de la liste DTO ;
10. retourner une liste non `null`, éventuellement vide.

### 14.2) Cas observables attendus

- si `pLibelle` est `null` ou blank :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_PARAM_BLANK` ;
  - n'émet aucun LOG ;
  - ne lève aucune exception ;
  - n'appelle jamais le `GATEWAY` ;

- si `gateway.findByLibelle(pLibelle)` lève une exception avec message :
  - positionne `getMessage()` à
    `MESSAGE_FINDBYLIBELLE_GATEWAY_KO`
    `+ TIRET_ESPACE + <message technique>` ;
  - émet un LOG ;
  - propage la même exception ;

- si `gateway.findByLibelle(pLibelle)` lève une exception sans message :
  - positionne `getMessage()` à
    `MESSAGE_FINDBYLIBELLE_GATEWAY_KO`
    `+ TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - émet un LOG ;
  - propage la même exception ;

- si `gateway.findByLibelle(pLibelle)` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL` ;
  - émet un LOG ;
  - lève une `ExceptionStockageVide`
    portant exactement `MESSAGE_STOCKAGE_NULL` ;

- si le filtrage, le tri ou la conversion de la liste DTO
  lève une exception avec message :
  - positionne `getMessage()` à
    `MESSAGE_FINDBYLIBELLE_PREPARATION_KO`
    `+ TIRET_ESPACE + <message technique>` ;
  - émet un LOG ;
  - propage la même exception ;

- si cette préparation lève une exception sans message :
  - positionne `getMessage()` à
    `MESSAGE_FINDBYLIBELLE_PREPARATION_KO`
    `+ TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
  - émet un LOG ;
  - propage la même exception ;

- si aucun résultat n'est trouvé après filtrage, tri, conversion et dédoublonnage :
  - retourne une liste vide mais non `null` ;
  - positionne `getMessage()` à `MESSAGE_OBJ_INTROUVABLE + pLibelle` ;

- si un ou plusieurs résultats sont trouvés :
  - retourne une liste non vide de DTO triés et dédoublonnés ;
  - positionne `getMessage()` à
    `MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE`.

### 14.3) Garanties spécifiques de `findByLibelle(...)`

- le libellé exact d'un `Produit` n'étant pas unique entre plusieurs
  `SousTypeProduit` parents, la méthode retourne une collection
  et non un DTO unitaire ;
- l'identité fonctionnelle d'un `Produit` reste
  `[SousTypeProduit, Produit]` ;
- le `TypeProduit` n'est pas une troisième composante de cette identité :
  il appartient à l'identité interne du `SousTypeProduit` parent ;
- la méthode ne retourne jamais `null` lorsque le scénario aboutit ;
- elle ne retourne aucun élément `null` ;
- le tri respecte l'ordre naturel métier `[SousTypeProduit, Produit]` ;
- le message de succès n'est positionné
  qu'après préparation complète de la liste DTO ;
- les DTO retournés correspondent aux objets métier
  effectivement fournis par le `GATEWAY` ;
- l'appel à `findByLibelle(...)` n'écrit rien dans le stockage ;
- une erreur de préparation de la liste DTO côté UC
  ne doit pas être attribuée au `GATEWAY` ;
- aucun résultat partiel incohérent n'est exposé à l'appelant.

## 15) Contrat spécifique de `findByLibelleRapide(...)`

Signature cible :
- `List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`

### 15.1) Scénario nominal attendu

Le scénario nominal de `findByLibelleRapide(...)` est :

1. recevoir un contenu de recherche rapide sur le libellé `Produit` ;
2. refuser un contenu `null` ;
3. si le contenu est blank, déléguer entièrement à `rechercherTous()` ;
4. sinon, déléguer au `GATEWAY` Produit la recherche des objets métier dont le libellé contient le contenu demandé ;
5. sécuriser la réponse technique du `GATEWAY` ;
6. retirer les éventuels objets métier `null` ;
7. trier les objets métier selon l'ordre naturel `[SousTypeProduit, Produit]`, le parent direct `SousTypeProduit` conservant sa propre identité `[TypeProduit, SousTypeProduit]` ;
8. convertir les résultats métier en `ProduitDTO.OutputDTO` ;
9. dédoublonner les DTO en conservant l'ordre métier ;
10. positionner le message observable après préparation complète de la réponse ;
11. retourner une liste non `null`, éventuellement vide.

### 15.2) Cas observables attendus

- si `pContenu == null` :
  - positionne `getMessage()` à `MESSAGE_PARAM_NULL`,
  - émet un LOG,
  - lève une `IllegalStateException` portant ce même message,
  - ne sollicite aucun `GATEWAY` ;

- si `pContenu` est blank :
  - délègue entièrement à `rechercherTous()`,
  - retourne exactement sa réponse exhaustive,
  - conserve ses messages et ses exceptions,
  - n'appelle jamais `gateway.findByLibelleRapide(...)` ;

- si `gateway.findByLibelleRapide(...)` lève une exception avec message :
  - positionne `getMessage()` à `MESSAGE_FINDBYLIBELLERAPIDE_GATEWAY_KO + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage la même exception ;

- si `gateway.findByLibelleRapide(...)` lève une exception dont le message est `null` ou blank :
  - positionne `getMessage()` à `MESSAGE_FINDBYLIBELLERAPIDE_GATEWAY_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage la même exception ;

- si le `GATEWAY` retourne `null` :
  - positionne `getMessage()` à `MESSAGE_STOCKAGE_NULL`,
  - émet un LOG,
  - lève une `ExceptionStockageVide` portant ce même message ;

- si le filtrage, le tri ou la conversion en `ProduitDTO.OutputDTO` lève une exception avec message :
  - positionne `getMessage()` à `MESSAGE_FINDBYLIBELLERAPIDE_PREPARATION_KO + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage la même exception ;

- si cette préparation lève une exception dont le message est `null` ou blank :
  - positionne `getMessage()` à `MESSAGE_FINDBYLIBELLERAPIDE_PREPARATION_KO + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage la même exception ;

- si aucun objet n'est trouvé après filtrage, tri, conversion et dédoublonnage :
  - retourne une liste vide mais non `null`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si au moins un objet est trouvé :
  - retourne une liste non `null` de `ProduitDTO.OutputDTO` triés et dédoublonnés,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 15.3) Garanties spécifiques de `findByLibelleRapide(...)`

- la méthode ne retourne jamais `null` lorsque le traitement aboutit ;
- aucun objet métier `null` ne fuit jusqu'au controller appelant ;
- le tri respecte l'ordre naturel `[SousTypeProduit, Produit]` ;
- le `TypeProduit` restitué dans le DTO est déduit du `SousTypeProduit` parent direct et ne devient jamais une troisième composante de l'identité du `Produit` ;
- l'identité fonctionnelle reste exclusivement `[SousTypeProduit, Produit]` ;
- la réponse est dédoublonnée sans perdre l'ordre métier ;
- le message de succès n'est positionné qu'après filtrage, tri, conversion et dédoublonnage complets ;
- un contenu blank restitue réellement la réponse exhaustive de `rechercherTous()` ;
- la méthode n'écrit rien dans le stockage ;
- les tests Mock doivent distinguer la panne GATEWAY de la panne de préparation ;
- les tests d'intégration doivent comparer la réponse aux identités réellement présentes dans le stockage et prouver l'absence d'effet de bord.

## 16) Contrat spécifique de `findAllByParent(...)`

Signature cible :
- `List<ProduitDTO.OutputDTO> findAllByParent(SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;`

### 16.1) Scénario nominal attendu

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

### 16.2) Cas observables attendus

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

### 16.3) Garanties spécifiques de `findAllByParent(...)`

- la méthode ne doit jamais exposer d'objet métier `null` à l'appelant ;
- la liste retournée, si elle n'est pas vide,
  doit correspondre aux Produits effectivement accessibles
  pour le parent persistant demandé ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- aucun résultat partiel incohérent ne doit être exposé à l'appelant.

## 17) Contrat spécifique de `findByDTO(...)`

Signature cible :
- `ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 17.1) Scénario nominal attendu

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

### 17.2) Cas observables attendus

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

### 17.3) Garanties spécifiques de `findByDTO(...)`

- la méthode ne doit jamais exposer de résultat incohérent à l'appelant ;
- l'objet retourné, s'il n'est pas `null`,
  doit correspondre à un `Produit` effectivement retrouvé dans le stockage ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- en cas d'absence de correspondance exacte,
  la méthode doit retourner `null`
  avec `MESSAGE_RECHERCHE_VIDE`.
  
## 18) Contrat spécifique de `findById(...)`

Signature cible :
- `ProduitDTO.OutputDTO findById(Long pId) throws Exception;`

### 18.1) Scénario nominal attendu

Le scénario nominal de `findById(...)` est :

1. recevoir un identifiant technique de `Produit` ;
2. déléguer la recherche au `GATEWAY` Produit ;
3. récupérer l'objet métier correspondant ;
4. convertir l'objet métier retrouvé en `ProduitDTO.OutputDTO` ;
5. positionner le message observable ;
6. retourner la réponse finale.

### 18.2) Cas observables attendus

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

### 18.3) Garanties spécifiques de `findById(...)`

- la méthode ne doit jamais exposer de résultat incohérent à l'appelant ;
- l'objet retourné, s'il n'est pas `null`,
  doit correspondre à un `Produit` effectivement retrouvé dans le stockage ;
- le message de succès ne doit être positionné
  qu'après préparation complète de la réponse utilisateur ;
- en cas d'absence de résultat,
  la méthode doit retourner `null`
  avec un message observable explicite.
  
## 19) Contrat spécifique de `update(...)`

Signature cible :
- `ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 19.1) Scénario nominal attendu

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

### 19.3) Garanties spécifiques de `update(...)`

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
  
## 20) Contrat spécifique de `delete(...)`

Signature cible :
- `void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;`

### 20.1) Scénario nominal attendu

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

### 20.2) Cas observables attendus

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

### 20.3) Garanties spécifiques de `delete(...)`

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
  
## 21) Contrat spécifique de `count()`

Signature cible :
- `long count() throws Exception;`

### 21.1) Scénario nominal attendu

Le scénario nominal de `count()` est :

1. demander au `GATEWAY` le nombre total de `Produit` présents dans le stockage ;
2. sécuriser la valeur numérique retournée par le `GATEWAY` ;
3. retourner un résultat de comptage exploitable par la couche appelante ;
4. positionner un message utilisateur cohérent avec l'issue observable du comptage.

### 21.2) Cas observables attendus

- si le `GATEWAY` lève une exception technique avec message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message`,
  - émet un LOG,
  - propage l'exception ;

- si le `GATEWAY` lève une exception technique sans message :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE`,
  - émet un LOG,
  - propage l'exception ;

- si le `GATEWAY` retourne une valeur strictement négative :
  - positionne `getMessage()` à
    `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + "comptage négatif incohérent : " + resultat`,
  - émet un LOG,
  - lève une `IllegalStateException` ;

- si le comptage retourné vaut `0` :
  - retourne `0`,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_VIDE` ;

- si le comptage retourné est strictement positif :
  - retourne ce résultat,
  - positionne `getMessage()` à `MESSAGE_RECHERCHE_OK`.

### 21.3) Garanties spécifiques de `count()`

- le message retourné par `getMessage()`
  reflète l'issue observable réelle du comptage ;
- le message de succès ou d'absence de résultat
  n'est positionné qu'après récupération effective
  du comptage renvoyé par le `GATEWAY` ;
- le résultat retourné correspond
  au nombre total de `Produit`
  effectivement accessibles dans le stockage ;
- aucune valeur de comptage incohérente
  ne doit être exposée à la couche appelante ;
- les tests Mock et Intégration
  doivent verrouiller explicitement :
  - le KO technique avec message,
  - le KO technique sans message,
  - le retour négatif incohérent,
  - le retour `0`,
  - le retour strictement positif,
  - la cohérence avec le `COUNT(*)` physique.
  
## 22) Contrat spécifique de `getMessage()`

Signature cible :
- `String getMessage();`

### 22.1) Scénario nominal attendu

Le scénario nominal de `getMessage()` est :

1. lire le dernier message observable actuellement mémorisé par le SERVICE UC ;
2. retourner ce message tel quel à la couche appelante ;
3. ne déclencher aucun traitement métier supplémentaire ;
4. ne déléguer à aucun `GATEWAY` ;
5. ne produire aucun effet de bord.

### 22.2) Cas observables attendus

- avant toute opération précédente ayant produit un message :
  - retourne `null` ;

- après une opération ayant produit un message observable :
  - retourne exactement ce message ;

- après une nouvelle opération produisant un autre message :
  - retourne le message le plus récent ;
  - le dernier message gagne ;

- la simple lecture via `getMessage()` :
  - ne modifie pas le message courant ;
  - ne lit pas le stockage ;
  - n'écrit rien ;
  - ne lève aucune exception.

### 22.3) Garanties spécifiques de `getMessage()`

- `getMessage()` est une lecture pure ;
- `getMessage()` ne délègue jamais à un `GATEWAY` ;
- `getMessage()` ne fait aucun recalcul ;
- `getMessage()` n'émet aucun `LOG` ;
- `getMessage()` ne modifie jamais l'état interne du service ;
- `getMessage()` peut retourner `null`
  tant qu'aucune opération précédente
  n'a encore produit de message observable ;
- les tests Mock et Intégration
  doivent verrouiller explicitement :
  - le retour `null` initial,
  - le retour du message après erreur locale,
  - le retour du message après succès observable,
  - la règle : le dernier message gagne.

## Annexe IA — autonomie progressive `Produit`

Cette annexe complète le contrat local pendant la phase de correction de la couche UC. Elle ne remplace jamais la relecture obligatoire du PORT Java, de l'ADAPTER UC et des tests concernés.

### A.1) Constantes du PORT UC à conserver

| Constante | Valeur littérale Java validée |
|---|---|
| `TIRET_ESPACE` | `" - "` |
| `MESSAGE_CREER_NULL_KO` | `"KO - vous ne pouvez pas sauvegarder " + "un Produit null."` |
| `MESSAGE_CREER_LIBELLE_BLANK_KO` | `"KO - vous ne pouvez pas sauvegarder un Produit " + "dont le libellé est blank (null ou que des espaces)."` |
| `MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO` | `"KO - Le Produit doit posséder un parent (SousTypeProduit) " + "avec un libellé non blank."` |
| `PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO` | `"KO - Impossible de trouver le parent via " + "sousTypeProduitGateway.findByLibelle(...) " + "avec le libellé parent indiqué : "` |
| `MESSAGE_CREER_PARENT_NON_PERSISTANT_KO` | `"KO - Le Produit doit posséder un parent " + "(SousTypeProduit) persistant"` |
| `PREFIX_MESSAGE_CREER_DOUBLON_KO` | `"KO - Impossible de vérifier l'unicité " + "du Produit dans le stockage : "` |
| `MESSAGE_CREER_DOUBLON_KO` | `"KO - Vous ne pouvez pas sauvegarder un Produit " + "déjà existant dans le stockage : "` |
| `PREFIX_MESSAGE_CREER_GATEWAY_KO` | `"KO - Impossible de créer le Produit dans le stockage : "` |
| `MESSAGE_CREER_GATEWAY_KO` | `"KO - Impossible de créer le Produit - " + "le stockage n'a retourné aucun objet métier créé."` |
| `PREFIX_MESSAGE_CREER_CONVERSION_KO` | `"KO - Impossible de créer l'OutputDTO " + "après la création du Produit : "` |
| `MESSAGE_CREER_CONVERSION_KO` | `"KO - OutputDTO null via la conversion " + "après la création du Produit."` |
| `MESSAGE_CREER_OK` | `"OK - La création de l'objet s'est bien déroulée."` |
| `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO` | `"KO - rechercherTous() - le Gateway a jeté Exception"` |
| `MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO` | `"KO - rechercherTous() - le Gateway a retourné Null"` |
| `MESSAGE_RECHERCHER_TOUS_CONVERSION_KO` | `"KO - rechercherTous() - convertirEtDedoublonner(...) a jeté Exception"` |
| `MESSAGE_RECHERCHER_TOUS_CONVERSION_NULL_KO` | `"KO - rechercherTous() - convertirEtDedoublonner(...) a retourné null"` |
| `MESSAGE_RECHERCHER_TOUS_VIDE` | `"OK - La recherche n'a retourné aucun résutat."` |
| `MESSAGE_RECHERCHER_TOUS_OK` | `"OK - La recherche a retourné des résultats."` |
| `MESSAGE_STOCKAGE_NULL` | `"Le stockage n'a pas retourné d'enregistrements (null)."` |
| `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO` | `"KO - rechercherTousParPage(...) " + "- le Gateway a jeté Exception"` |
| `MESSAGE_RECHERCHER_TOUS_PAR_PAGE_PREPARATION_KO` | `"KO - rechercherTousParPage(...) " + "- la préparation de la page DTO a jeté Exception"` |
| `MESSAGE_PAGEABLE_NULL` | `"l'indication de page demandée ne doit pas être null."` |
| `MESSAGE_PARAM_BLANK` | `"Vous avez passé une chaine " + "de caractères blank (null ou que des espaces) en paramètre."` |
| `MSG_ERREUR_NON_SPECIFIEE` | `"Erreur non spécifiée"` |
| `MESSAGE_FINDBYLIBELLE_GATEWAY_KO` | `"KO - findByLibelle(...) " + "- le Gateway a jeté Exception"` |
| `MESSAGE_FINDBYLIBELLE_PREPARATION_KO` | `"KO - findByLibelle(...) " + "- la préparation de la réponse utilisateur a jeté Exception"` |
| `MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE` | `"OK - findByLibelle(...) a retourné des enregistrements"` |
| `MESSAGE_CREER_KO` | `"Erreur lors de la création de l'objet"` |
| `MESSAGE_RECHERCHE_VIDE` | `"La recherche n'a retourné aucun résutat."` |
| `MESSAGE_RECHERCHE_OK` | `"OK - La recherche a retourné des résultats."` |
| `MESSAGE_RECHERCHE_OBJ_NULL` | `"Le Produit est null"` |
| `MESSAGE_SUCCES_RECHERCHE` | `"La recherche a abouti"` |
| `MESSAGE_OBJ_INTROUVABLE` | `"Objet Introuvable : "` |
| `MESSAGE_OBJ_NON_PERSISTE` | `"Objet non persisté en base : "` |
| `MESSAGE_RECHERCHE_PAGINEE_OK` | `"OK - la recherche paginée a retourné des résultats."` |
| `MESSAGE_RECHERCHE_PAGINEE_KO` | `"KO - la recherche paginée a retourné null."` |
| `MESSAGE_MODIF_OK` | `"La modification a été effectuée"` |
| `MESSAGE_MODIF_KO` | `"La modification a échouée"` |
| `MESSAGE_DELETE_OK` | `"La suppression a été effectuée"` |
| `MESSAGE_DELETE_KO` | `"La suppression a échouée"` |
| `RECHERCHE_SOUSTYPEPRODUIT_NULL` | `"Le SousTypeProduit parent ne doit pas être null"` |
| `MESSAGE_SOUSTYPEPRODUIT_NULL` | `"Le SousTypeProduit ne doit pas être null"` |
| `MESSAGE_PAS_PARENT` | `"Le Produit doit posséder un parent (SousTypeProduit)"` |
| `KO_TECHNIQUE_RECHERCHE` | `"Une recherche technique a échouée"` |
| `METHODE_CREER` | `"méthode Creer(...)"` |
| `METHODE_RECHERCHER_TOUS` | `"méthode rechercherTous()"` |
| `METHODE_RECHERCHER_TOUS_STRING` | `"méthode rechercherTousString()"` |
| `METHODE_RECHERCHER_TOUS_PAGE` | `"méthode rechercherTousParPage(...)"` |
| `METHODE_FIND_BY_LIBELLE` | `"méthode findByLibelle(...)"` |
| `METHODE_FIND_BY_LIBELLE_RAPIDE` | `"méthode findByLibelleRapide()"` |
| `METHODE_FIND_ALL_BY_PARENT` | `"méthode FindAllByParent(...)"` |
| `METHODE_FIND_BY_DTO` | `"méthode findByDTO(...)"` |
| `METHODE_FIND_BY_ID` | `"méthode findById(...)"` |
| `METHODE_UPDATE` | `"méthode update(...)"` |
| `METHODE_DELETE` | `"méthode delete(...)"` |
| `METHODE_COUNT` | `"méthode count()"` |

Ces constantes sont observables via les tests UC et les messages `getMessage()`. L'IA ne doit pas renommer, reformuler ou remplacer ces messages par une formulation équivalente.

### A.2) Signatures du PORT UC à conserver

- `ProduitDTO.OutputDTO creer( ProduitDTO.InputDTO pInputDTO) throws Exception;`
- `List<ProduitDTO.OutputDTO> rechercherTous() throws Exception;`
- `List<String> rechercherTousString() throws Exception;`
- `ResultatPage<ProduitDTO.OutputDTO> rechercherTousParPage( RequetePage pRequetePage) throws Exception;`
- `List<ProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;`
- `List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`
- `List<ProduitDTO.OutputDTO> findAllByParent( SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;`
- `ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) throws Exception;`
- `ProduitDTO.OutputDTO findById(Long pId) throws Exception;`
- `ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;`
- `void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;`
- `long count() throws Exception;`

### A.3) ADAPTER UC associé

### `ProduitCuService` — rappel local

#### Constructeur validé

- `public ProduitCuService( final ProduitGatewayIService pGateway, final SousTypeProduitGatewayIService pSousTypeProduitGateway)`

Règle : conserver cette injection telle que validée. Ne pas remplacer par une injection de champ, un constructeur vide ou une dépendance inventée.

#### Attributs structurants

- `private final ProduitGatewayIService gateway;`
- `private final SousTypeProduitGatewayIService sousTypeProduitGateway;`

#### Ordre exact des méthodes publiques validées

1. `public OutputDTO creer( final InputDTO pInputDTO) throws Exception`
2. `public List<OutputDTO> rechercherTous() throws Exception`
3. `public List<String> rechercherTousString() throws Exception`
4. `public ResultatPage<OutputDTO> rechercherTousParPage( final RequetePage pRequetePage) throws Exception`
5. `public List<ProduitDTO.OutputDTO> findByLibelle( final String pLibelle) throws Exception`
6. `public List<OutputDTO> findByLibelleRapide( final String pContenu) throws Exception`
7. `public List<OutputDTO> findAllByParent( final SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception`
8. `public OutputDTO findByDTO( final InputDTO pInputDTO) throws Exception`
9. `public OutputDTO findById( final Long pId) throws Exception`
10. `public OutputDTO update( final InputDTO pInputDTO) throws Exception`
11. `public void delete( final InputDTO pInputDTO) throws Exception`
12. `public long count() throws Exception`
13. `public String getMessage()`
14. `public int compare(final Produit o1, final Produit o2)`

#### Helpers privés / internes à ne pas oublier

- `private boolean isDoublon( final ProduitDTO.InputDTO pInputDTO) throws Exception`
- `private SousTypeProduit rechercherParentPersistant( final ProduitDTO.InputDTO pInputDTO) throws Exception`
- `private Produit convertirInputDTOEnMetier( final ProduitDTO.InputDTO pInputDTO)`
- `private List<Produit> filtrerEtTrier( final List<Produit> pListe)`
- `private long safeTotalElements( final ResultatPage<?> rp)`
- `private String safeParentLibelle( final Produit pProduit)`
- `private <T> T traiterErreur( final String pMessage, final String pMethode, final Exception pE) throws Exception`
- `private boolean isErreurMetierAttendue( final String pMessage, final Exception pException)`

Ces helpers sont contractuels pour l'autonomie IA : ils ne doivent pas être supprimés, fusionnés ou remplacés par une version approximative sans relire le code validé et les tests concernés.

### A.4) Méthode générative de dérivation des tests Mock UC

Le contrat ne fixe aucun nombre de tests. L’IA détermine les tests nécessaires à partir des branches observables décrites pour chaque méthode.

#### A.4.1) Branche observable distincte

Un test Mock distinct est requis lorsque le scénario modifie au moins un élément observable :

- retour ;
- message `getMessage()` ;
- LOG ;
- exception ;
- interaction Gateway ;
- absence d’interaction ;
- conversion ou garde défensive.

#### A.4.2) Cas techniques avec et sans message

Lorsqu’une exception technique est sécurisée par `MSG_ERREUR_NON_SPECIFIEE`, l’IA doit dériver deux scénarios :

1. exception avec message ;
2. exception sans message.

Ces scénarios sont distincts parce que le message observable diffère.

#### A.4.3) Retours techniques incohérents

L’IA doit dériver un test lorsque l’ADAPTER traite explicitement :

- un retour Gateway `null` ;
- une collection `null` ;
- un résultat paginé `null` ;
- une conversion `null` lorsque le cas est atteignable sans altérer le code de production ;
- un objet non persistant ;
- un comptage négatif ;
- un parent absent, non persistant ou ambigu.

Une garde défensive inatteignable avec les helpers privés réels reste documentée, mais ne justifie pas à elle seule un test artificiel qui modifierait le code de production.

#### A.4.4) Résultats vides après transformation

Une branche vide doit être testée lorsqu’elle apparaît après :

- filtrage des objets métier `null` ;
- suppression des libellés blank ;
- recherche sans résultat ;
- dédoublonnage ;
- sélection d’un couple `[SousTypeProduit, Produit]`.

Le test doit vérifier le retour et le message exact associé.

#### A.4.5) Scénario nominal

Le scénario nominal doit prouver ensemble les garanties compatibles du même chemin d’exécution :

- identité métier `[SousTypeProduit, Produit]` ;
- identité propre du parent `[TypeProduit, SousTypeProduit]` ;
- rattachement au parent persistant exact ;
- filtrage ;
- tri par parent direct puis par libellé Produit ;
- conversion ;
- dédoublonnage ;
- conservation d’un même libellé Produit sous deux parents directs différents ;
- message final ;
- interactions Gateway exactes.

L’IA ne doit pas créer plusieurs tests nominaux redondants lorsque ces garanties peuvent être prouvées clairement dans un seul scénario.

#### A.4.6) Contrôle Mockito

Avant toute livraison, l’IA doit vérifier :

- chaque stubbing est consommé ;
- aucun getter n’est stubé après le point d’arrêt du scénario ;
- les Gateways non concernés n’ont aucune interaction ;
- les appels attendus sont vérifiés avec leurs arguments ;
- les constantes, annotations, Javadocs et commentaires reprennent le formalisme validé.

### A.5) Méthode générative de dérivation des tests d’intégration UC

Les tests d’intégration ne sont pas déduits d’un total à atteindre. Ils sont déduits des garanties métier qui nécessitent des collaborateurs réels et une preuve observable dans le stockage.

#### A.5.1) Cas nécessitant une intégration

Une preuve d’intégration est nécessaire lorsque le contrat exige notamment :

- une création, modification ou suppression réelle ;
- un identifiant persistant ;
- un rattachement au `SousTypeProduit` parent direct ;
- une identité métier fondée sur le couple `[SousTypeProduit, Produit]` ;
- une résolution non ambiguë de l’identité propre du parent `[TypeProduit, SousTypeProduit]` ;
- une absence réelle de donnée ;
- une cohérence entre une liste UC et le contenu du stockage ;
- une absence d’effet de bord pour une lecture ;
- un round-trip par une autre méthode du SERVICE UC.

#### A.5.2) Répartition des preuves

Les tests d’intégration doivent vérifier :

- le retour ou le DTO observable ;
- le message final exact ;
- l’exception métier ou de validation lorsqu’elle est intégrablement testable ;
- la preuve directe dans le stockage avec `JdbcTemplate` lorsque cette preuve sert le contrat ;
- le `flush()` JPA avant une lecture SQL directe lorsque nécessaire ;
- la conservation des autres couples homonymes lors d’une modification ou suppression ciblée.

Ils ne doivent pas recontrôler exhaustivement :

- les fallbacks d’exception technique ;
- tous les appels Mockito ;
- les clauses internes déjà prouvées par les tests Gateway ou Mock UC.

#### A.5.3) Lectures pures

Pour une méthode de lecture, l’intégration doit prouver selon le contrat :

- le cas vide réel ;
- le cas nominal réel ;
- les identifiants et couples métier retournés ;
- la cohérence du message ;
- l’ordre métier lorsque celui-ci est contractuel ;
- l’absence de modification du stockage.

Le nombre de méthodes de test utilisé pour fournir ces preuves reste un choix de conception contrôlé par la lisibilité et l’absence de redondance.

### A.6) Règle de correction locale

Pour corriger ou coder un bloc `Produit`, l'IA doit :

1. relire le présent contrat ;
2. relire le PORT UC Java ;
3. relire l'ADAPTER UC associé ;
4. relire le Gateway utilisé par le bloc ;
5. relire les DTO/convertisseurs et exceptionsservices utiles ;
6. relire les tests déjà validés du même bloc ;
7. reprendre le nommage, l'ordre, la Javadoc et les commentaires du bloc de référence le plus proche ;
8. livrer uniquement un bloc complet dans le chat pour les méthodes Java ou un fichier complet individuel pour les contrats fragiles.