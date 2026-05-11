# docs/contrats/metier/ConvertisseursMetierOutputDTO.md

# Contrat relais métier / DTO — Convertisseurs Metier -> OutputDTO

## 1) Statut

Les convertisseurs `Metier -> OutputDTO` appartiennent formellement à `couche_dto`.

Ce contrat est un relais de couche métier : il doit être relu dès qu'une analyse métier ou UC dépend de la préparation d'une réponse utilisateur à partir d'objets métier.

## 2) Classes concernées

- `ConvertisseurMetierToOutputDTOTypeProduit.java`
- `ConvertisseurMetierToOutputDTOSousTypeProduit.java`
- `ConvertisseurMetierToOutputDTOProduit.java`

Tests associés :

- `ConvertisseurMetierToOutputDTOTypeProduitTest.java` : 15 tests ;
- `ConvertisseurMetierToOutputDTOSousTypeProduitTest.java` : 4 tests ;
- `ConvertisseurMetierToOutputDTOProduitTest.java` : 9 tests.

## 3) Règles communes

- classes utilitaires `final` ;
- constructeur privé ;
- méthodes statiques ;
- `convert(null)` retourne `null` ;
- `convertList(null)` retourne `null` lorsque la méthode existe ;
- les listes sources vides retournent une liste vide ;
- les éléments métier `null` sont ignorés dans `convertList(...)` ;
- l'ordre de première apparition est conservé ;
- le dédoublonnage se fait via `LinkedHashSet` et donc via l'égalité des DTO ;
- les listes embarquées dans les DTO doivent rester non `null` et mutables lorsque le convertisseur validé le garantit ;
- les extracteurs privés n'accèdent qu'aux getters publics des objets métier.

## 4) TypeProduit -> OutputDTO

`ConvertisseurMetierToOutputDTOTypeProduit.convert(TypeProduit)` :

- retourne `null` si la source est `null` ;
- copie `idTypeProduit` ;
- copie `typeProduit` ;
- extrait les libellés des `SousTypeProduit` enfants ;
- ignore les enfants `null` ;
- ignore les libellés enfants `null` ;
- retourne une liste de sous-types non `null` et mutable, même si aucun enfant n'est exploitable.

`convertList(...)` :

- retourne `null` si la liste source est `null` ;
- ignore les éléments `null` ;
- conserve l'ordre de première apparition ;
- dédoublonne selon l'égalité de `TypeProduitDTO.OutputDTO` ;
- le DTO privilégie l'égalité par identifiant lorsque les deux identifiants sont non `null`, puis le fallback libellé lorsque les deux identifiants sont `null`.

## 5) SousTypeProduit -> OutputDTO

`ConvertisseurMetierToOutputDTOSousTypeProduit.convert(SousTypeProduit)` :

- retourne `null` si la source est `null` ;
- copie `idSousTypeProduit` ;
- extrait le libellé du parent `TypeProduit`, ou `null` si le parent est absent ;
- copie `sousTypeProduit` ;
- extrait les libellés des `Produit` enfants ;
- ignore les produits `null` ;
- ignore les libellés produits `null` ;
- retourne une liste de produits non `null` et mutable.

`convertList(...)` :

- retourne `null` si la liste source est `null` ;
- ignore les éléments `null` ;
- conserve l'ordre de première apparition ;
- dédoublonne selon l'égalité de `SousTypeProduitDTO.OutputDTO`.

## 6) Produit -> OutputDTO

`ConvertisseurMetierToOutputDTOProduit.convert(Produit)` :

- retourne `null` si la source est `null` ;
- copie `idProduit` ;
- extrait le libellé du `TypeProduit` via le parent `SousTypeProduit`, ou `null` si le parent ou le grand-parent est absent ;
- extrait le libellé du `SousTypeProduit`, ou `null` si le parent est absent ;
- copie `produit`.

`convertList(...)` :

- retourne `null` si la liste source est `null` ;
- ignore les éléments `null` ;
- conserve l'ordre de première apparition ;
- dédoublonne selon l'égalité de `ProduitDTO.OutputDTO` ;
- conserve le cas critique `id non null` versus `id null` comme deux DTO distincts.

## 7) Interdictions

- ne jamais transformer `convert(null)` en DTO vide ;
- ne jamais transformer `convertList(null)` en liste vide lorsque le test validé attend `null` ;
- ne jamais trier dans les convertisseurs : l'ordre reçu doit être conservé ;
- ne jamais supprimer le `LinkedHashSet` lorsqu'il sert à dédoublonner en conservant l'ordre ;
- ne jamais accéder directement aux champs métier ; utiliser les getters publics ;
- ne jamais rendre les listes embarquées `null` lorsque le convertisseur validé garantit une liste vide mutable.
