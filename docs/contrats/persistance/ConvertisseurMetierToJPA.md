# Contrat local — ConvertisseurMetierToJPA

## 1) Composant concerné

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java`

## 2) Rôle du composant

`ConvertisseurMetierToJPA` est une classe utilitaire `final` de la couche persistance.

Elle convertit des objets métier du domaine `produittype` en entities JPA :
- `TypeProduit` → `TypeProduitJPA`
- `SousTypeProduit` → `SousTypeProduitJPA`
- `Produit` → `ProduitJPA`

Le composant ne dépend pas des DTO et n'exécute aucune logique UC.

## 3) API publique à relire

### 3.1) Conversions principales

```java
public static TypeProduitJPA typeProduitMETIERToJPA(
        final TypeProduit pTypeProduit)

public static SousTypeProduitJPA sousTypeProduitMETIERToJPA(
        final SousTypeProduit pSousTypeProduit)

public static ProduitJPA produitMETIERToJPA(
        final Produit pProduit)
```

### 3.2) Comparaisons de cohérence métier ↔ JPA

```java
public static boolean equalsMetier(
        final TypeProduit pTypeProduit,
        final TypeProduitJPA pTypeProduitJPA)

public static boolean equalsMetier(
        final SousTypeProduit pSousTypeProduit,
        final SousTypeProduitJPA pSousTypeProduitJPA)

public static boolean equalsMetier(
        final Produit pProduit,
        final ProduitJPA pProduitJPA)
```

## 4) Garanties techniques à retenir

### 4.1) Conversion cycle-safe

Le composant embarque un `ConversionContext` fondé sur une `IdentityHashMap` afin de stabiliser les graphes et d'éviter les boucles de conversion.

### 4.2) Gestion des cas nuls / blank

Les méthodes privées de conversion :
- retournent `null` si l'objet métier source est `null` ;
- retournent `null` si le libellé métier source est blank.

### 4.3) Conservation du graphe parent / enfants

Les conversions :
- passent les identifiants et libellés scalaires ;
- reconstruisent les rattachements parent / enfant ;
- utilisent les setters canoniques pour préserver la cohérence des relations ;
- évitent les wipes de collections lorsqu'une collection métier est `null`.

### 4.4) Contrôle de type

Les relations relues via interfaces sont sécurisées par des contrôles de type métier attendus avant conversion profonde.

## 5) Frontière de couche

Le composant appartient à `couche_persistance`.

Il ne doit pas :
- manipuler les DTO ;
- produire de message utilisateur ;
- porter de logique Gateway ou UC.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/ConvertisseurJPAToMetier.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/ProduitJPA.md`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPATest.java`