# Contrat local — ProduitICuService

## `ProduitDTO.OutputDTO creer(ProduitDTO.InputDTO pInputDTO)`

### Intention de service UC (scénario nominal)
- recevoir un `ProduitDTO.InputDTO` provenant de la couche de présentation ;
- valider les préconditions applicatives observables par l'utilisateur ;
- vérifier l'absence de doublon fonctionnel ;
- récupérer le `SousTypeProduit` parent persistant nécessaire au rattachement métier ;
- convertir l'InputDTO en objet métier `Produit` ;
- déléguer l'écriture au composant technique GATEWAY ;
- récupérer l'objet métier effectivement stocké ;
- convertir l'objet métier retourné en `ProduitDTO.OutputDTO` ;
- retourner une réponse exploitable par le CONTROLLER appelant.

### Contrat de service UC
- si `pInputDTO == null`, retourne `null`, positionne `getMessage()` à `MESSAGE_CREER_NULL` et n'émet ni LOG ni Exception ;
- si `pInputDTO.getProduit()` est blank, positionne `getMessage()` à `MESSAGE_CREER_NOM_BLANK`, émet un LOG de service et lève `ExceptionParametreBlank` ;
- si le libellé du parent est blank, positionne `getMessage()` à `MESSAGE_PAS_PARENT`, émet un LOG de service et lève `IllegalStateException` ;
- si le DTO correspond à un doublon fonctionnel, positionne `getMessage()` à `MESSAGE_DOUBLON + libellé`, émet un LOG de service et lève `ExceptionDoublon` ;
- si le parent `SousTypeProduit` n'existe pas dans le stockage ou n'est pas persistant, positionne `getMessage()` à `MESSAGE_PAS_PARENT`, émet un LOG de service et lève `IllegalStateException` ;
- sinon, délègue la création au composant GATEWAY, puis retourne le `ProduitDTO.OutputDTO` correspondant à l'objet réellement stocké et rattaché à son parent persistant ;
- en cas d'échec remonté par la vérification d'unicité, par la vérification du parent, par le GATEWAY ou par une étape interne du SERVICE UC, propage une exception circonstanciée conforme à l'implémentation.

### Garanties métier, utilisateur et traçabilité
- le message retourné par `getMessage()` reflète l'issue observable de l'opération pour l'appelant ;
- en cas de succès, `getMessage()` est positionné à `MESSAGE_CREER_OK` uniquement après préparation complète de la réponse utilisateur ;
- en cas d'échec métier, applicatif ou technique, le SERVICE UC produit un message utilisateur déterministe et traçable ;
- le résultat retourné, s'il est non `null`, correspond à l'état métier effectivement créé dans le stockage avec rattachement à un parent persistant ;
- le SERVICE UC conserve son rôle d'orchestration applicative entre couche de présentation, métier, GATEWAY et message utilisateur.

## `List<ProduitDTO.OutputDTO> rechercherTous()`

### Intention de service UC (scénario nominal)
- demander au `GATEWAY` la liste complète des `Produit` présents dans le stockage ;
- sécuriser le retour technique du stockage ;
- retirer les éventuels éléments `null` ;
- trier les objets métier ;
- convertir les objets métier en `OutputDTO` ;
- dédoublonner les `OutputDTO` si nécessaire ;
- positionner le message observable ;
- retourner une liste exploitable par la couche appelante.

### Contrat de service UC
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

### Garanties métier, utilisateur et traçabilité
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
