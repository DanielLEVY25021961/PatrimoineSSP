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

## 8) AUTONOMIE-CONVERTISSEURS-METIER-OUTPUTDTO-01 — Fiche de recodage autonome

### 8.1 Structure commune exacte

Les trois convertisseurs sont des classes utilitaires `final` :

- constructeur privé ;
- aucune injection ;
- aucun état mutable métier ;
- méthodes publiques statiques `convert(...)` et `convertList(...)` lorsque présentes ;
- extracteurs privés statiques pour les relations métier ;
- `LinkedHashSet` pour dédoublonner en conservant l'ordre de première apparition.

### 8.2 Ordre exact — TypeProduit

`ConvertisseurMetierToOutputDTOTypeProduit` doit conserver l'ordre suivant :

1. constructeur privé ;
2. `public static TypeProduitDTO.OutputDTO convert(TypeProduit)` ;
3. `public static List<TypeProduitDTO.OutputDTO> convertList(List<? extends TypeProduit>)` ;
4. `private static List<String> extractSousTypeProduits(TypeProduit)`.

`convert(TypeProduit)` retourne `null` si la source est `null`, copie `idTypeProduit`, copie `typeProduit`, puis fournit une liste mutable non `null` de libellés enfants exploitables.

`extractSousTypeProduits(...)` :

- instancie une nouvelle liste ;
- lit les enfants via `getSousTypeProduits()` ;
- retourne la liste vide si la collection est `null` ;
- ignore les enfants `null` ;
- ignore les libellés enfants `null` ;
- conserve l'ordre reçu.

### 8.3 Ordre exact — SousTypeProduit

`ConvertisseurMetierToOutputDTOSousTypeProduit` doit conserver l'ordre suivant :

1. constructeur privé ;
2. `public static SousTypeProduitDTO.OutputDTO convert(SousTypeProduit)` ;
3. `public static List<SousTypeProduitDTO.OutputDTO> convertList(List<? extends SousTypeProduit>)` ;
4. `private static String extractTypeProduit(SousTypeProduit)` ;
5. `private static List<String> extractProduits(SousTypeProduit)`.

`extractTypeProduit(...)` retourne `null` si le parent est absent, sinon le libellé du parent.

`extractProduits(...)` :

- retourne une liste mutable non `null` ;
- retourne la liste vide si la collection de produits est `null` ;
- ignore les produits `null` ;
- ignore les libellés produits `null` ;
- conserve l'ordre reçu.

### 8.4 Ordre exact — Produit

`ConvertisseurMetierToOutputDTOProduit` doit conserver l'ordre suivant :

1. constructeur privé ;
2. `public static ProduitDTO.OutputDTO convert(Produit)` ;
3. `public static List<ProduitDTO.OutputDTO> convertList(List<? extends Produit>)` ;
4. `private static String extractTypeProduit(Produit)` ;
5. `private static String extractSousTypeProduit(Produit)`.

`extractTypeProduit(...)` :

- retourne `null` si le produit est `null` ;
- retourne `null` si le parent `SousTypeProduit` est absent ;
- retourne `null` si le grand-parent `TypeProduit` est absent ;
- sinon retourne le libellé du `TypeProduit`.

`extractSousTypeProduit(...)` :

- retourne `null` si le produit est `null` ;
- retourne `null` si le parent direct est absent ;
- sinon retourne le libellé du parent direct.

### 8.5 `convertList(...)` commun

Pour les convertisseurs qui possèdent `convertList(...)` :

- `null` en entrée retourne `null` ;
- liste vide retourne liste vide ;
- les éléments métier `null` sont ignorés ;
- chaque DTO est produit par `convert(...)` ;
- les DTO `null` sont ignorés si le code validé les filtre ;
- le dédoublonnage s'appuie sur `new LinkedHashSet<>()` ;
- le retour final est une nouvelle `ArrayList<>(set)`.

### 8.6 Tests validés — TypeProduit

1. `testStructureClasseUtilitaire()` ;
2. `testConvertNull()` ;
3. `testConvertSansSousTypes()` ;
4. `testConvertSansSousTypesRetourneListeVideMutable()` ;
5. `testConvertFiltreNulls()` ;
6. `testConvertConserveOrdre()` ;
7. `testConvertIgnoreSousTypeProduitNull()` ;
8. `testConvertListNull()` ;
9. `testConvertListVide()` ;
10. `testConvertListIgnoreNullEtConserveOrdre()` ;
11. `testConvertListDedoublonneParId()` ;
12. `testConvertListDedoublonneParTypeProduitQuandIdsNuls()` ;
13. `testOutputDTOEqualsIdFirst()` ;
14. `testOutputDTOEqualsFallbackTypeProduit()` ;
15. `testOutputDTOIdNonNullVsIdNullDoitEtreInegal()`.

### 8.7 Tests validés — SousTypeProduit

1. `testConvertNull()` ;
2. `testConvertNominal()` ;
3. `testConvertProduitsNull()` ;
4. `testConvertIgnoreElementsNull()`.

### 8.8 Tests validés — Produit

1. `testConvertNull()` ;
2. `testConvertNominal()` ;
3. `testConvertSousTypeProduitNull()` ;
4. `testConvertTypeProduitNullDansSousTypeProduit()` ;
5. `testConvertListNull()` ;
6. `testConvertListOrdreConserve()` ;
7. `testConvertListIgnoreElementsNull()` ;
8. `testConvertListDedoublonneQuandIdsNuls()` ;
9. `testConvertListConserveIdNonNullVsIdNull()`.
