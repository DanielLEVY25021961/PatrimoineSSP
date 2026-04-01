# Contrat local — ConvertisseurJPAToMetier

## 1) Composant concerné

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java`

## 2) Rôle du composant

`ConvertisseurJPAToMetier` est une classe utilitaire `final` de la couche persistance.

Elle convertit des entities JPA du domaine `produittype` en objets métier :
- `TypeProduitJPA` → `TypeProduit`
- `SousTypeProduitJPA` → `SousTypeProduit`
- `ProduitJPA` → `Produit`

Le composant ne dépend pas des DTO et n'exécute aucune logique UC.

## 3) API publique à relire

### 3.1) Conversions principales

```java
public static TypeProduit typeProduitJPAToMetier(
        final TypeProduitJPA pTypeProduitJPA)

public static SousTypeProduit sousTypeProduitJPAToMetier(
        final SousTypeProduitJPA pSousTypeProduitJPA)

public static Produit produitJPAToMetier(
        final ProduitJPA pProduitJPA)
```

### 3.2) Comparaisons de cohérence JPA ↔ métier

```java
public static boolean equalsMetier(
        final TypeProduitJPA pTypeProduitJPA,
        final TypeProduit pTypeProduit)

public static boolean equalsMetier(
        final SousTypeProduitJPA pSousTypeProduitJPA,
        final SousTypeProduit pSousTypeProduit)

public static boolean equalsMetier(
        final ProduitJPA pProduitJPA,
        final Produit pProduit)
```

## 4) Garanties techniques à retenir

### 4.1) Conversion cycle-safe

Le composant embarque un `ConversionContext` fondé sur une `IdentityHashMap` afin de stabiliser les graphes et d'éviter les boucles de conversion.

### 4.2) Gestion des cas nuls / blank

Les méthodes privées de conversion :
- retournent `null` si l'entity source est `null` ;
- retournent `null` si le libellé métier porté par l'entity source est blank.

### 4.3) Conservation du graphe parent / enfants

Les conversions :
- passent les identifiants et libellés scalaires ;
- reconstruisent les rattachements parent / enfant ;
- évitent les wipes de collections lorsque des collections JPA sont `null` ou non chargées ;
- utilisent les setters canoniques pour préserver la cohérence des relations.

### 4.4) Contrôle de type

Les relations relues via interfaces sont sécurisées par des contrôles de type JPA attendus avant conversion profonde.

## 5) Frontière de couche

Le composant appartient à `couche_persistance`.

Il ne doit pas :
- manipuler les DTO ;
- produire de message utilisateur ;
- porter de logique Gateway ou UC.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/ConvertisseurMetierToJPA.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/ProduitJPA.md`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetierTest.java`