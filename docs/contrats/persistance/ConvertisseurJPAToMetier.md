<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — ConvertisseurJPAToMetier

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java
```

Source validée : `b44b1be980846800ce51ed9c6b1b1d5890ea5898521bb901af8c0879d040eb89` — 1669 lignes LF.

## 2) Rôle exact

`ConvertisseurJPAToMetier` est une classe utilitaire `final` de la couche persistance. Elle convertit les objets `JPA` vers les objets `métier` pour le domaine `produittype`.

Elle ne doit jamais dépendre des DTO, des UC, des Gateways ou des Controllers.

## 3) Constantes et caches obligatoires

- ligne 58 : `public static final String SAUT_DE_LIGNE = System.getProperty("line.separator");`
- ligne 64 : `public static final String UNCHECKED = "unchecked";`
- ligne 69 : `public static final String NULL = "null";`
- ligne 74 : `public static final String IMPLEMENTATION_NON_JPA = "Implémentation non-JPA ";`
- ligne 80 : `public static final String IDTYPEPRODUIT = "[idTypeProduit : ";`
- ligne 85 : `public static final String FORMAT_ID = "%-2s";`
- ligne 90 : `public static final String FORMAT_IDPRODUIT = "%-3s";`
- ligne 95 : `public static final String FORMAT_IDSTP = "%-3s";`
- ligne 100 : `public static final String FORMAT_IDTP = "%-3s";`
- ligne 105 : `public static final String FORMAT_STP = "%-20s";`
- ligne 110 : `public static final String SOUS_TYPE_PRODUIT = " - sousTypeProduit : ";`
- ligne 116 : `public static final String FORMAT_TP = "%-13s";`
- ligne 121 : `public static final String FORMAT_P = "%-40s";`
- ligne 126 : `public static final String TIRET_ESPACE = " - ";`
- ligne 131 : `public static final char CROCHET_FERMANT = ']';`
- ligne 136 : `public static final String DEUX_POINTS_ESPACE = " : ";`
- ligne 146 : `private static final Map<Object, Object> SHARED_CACHE = Collections.synchronizedMap(new IdentityHashMap<>());`
- ligne 163 : `private static final Logger LOG = LogManager .getLogger(ConvertisseurJPAToMetier.class);`
- ligne 194 : `private static final class ConversionContext {  // ************************ATTRIBUTS*****************************/  /** * <div> * <p>IdentityHashMap servant de cache</p> * </div> */ private final Map<Object, Object> cache;`

Règles :

- `SAUT_DE_LIGNE`, `NULL`, formats d'affichage et séparateurs sont conservés pour les diagnostics historiques ;
- `IMPLEMENTATION_NON_JPA` est utilisé par `requireJPA(...)` ;
- `SHARED_CACHE` est un `Map<Object, Object>` statique, thread-safe, construit avec `Collections.synchronizedMap(new IdentityHashMap<>())` ;
- `LOG` reste un logger Log4j.

## 4) Structure obligatoire

- classe `public final` ;
- constructeur privé d'arité nulle ;
- classe interne privée statique `ConversionContext` ;
- attribut interne `private final Map<Object, Object> cache` ;
- constructeur interne qui instancie `new IdentityHashMap<>()` ;
- `get(...)` annoté `@SuppressWarnings(UNCHECKED)` ;
- `put(...)` qui alimente le cache partagé puis le cache local.

## 5) Ordre exact des méthodes

1. ligne 174 : `private ConvertisseurJPAToMetier()`
2. ligne 215 : `private ConversionContext()`
3. ligne 243 : `private <T> T get(final Object pKey)`
4. ligne 277 : `private void put(final Object pKey, final Object pValue)`
5. ligne 307 : `public static TypeProduit typeProduitJPAToMetier( final TypeProduitJPA pTypeProduitJPA)`
6. ligne 328 : `public static SousTypeProduit sousTypeProduitJPAToMetier( final SousTypeProduitJPA pSousTypeProduitJPA)`
7. ligne 350 : `public static Produit produitJPAToMetier( final ProduitJPA pProduitJPA)`
8. ligne 393 : `private static TypeProduit typeProduitJPAToMetier( final TypeProduitJPA pTypeProduitJPA, final ConversionContext ctx)`
9. ligne 513 : `private static SousTypeProduit sousTypeProduitJPAToMetier( final SousTypeProduitJPA pSousTypeProduitJPA, final ConversionContext ctx)`
10. ligne 656 : `private static Produit produitJPAToMetier( final ProduitJPA pProduitJPA , final ConversionContext ctx)`
11. ligne 747 : `public static boolean equalsMetier( final TypeProduitJPA pTypeProduitJPA , final TypeProduitI pTypeProduit)`
12. ligne 827 : `public static boolean equalsMetier( final SousTypeProduitJPA pSousTypeProduitJPA , final SousTypeProduitI pSousTypeProduit)`
13. ligne 917 : `public static boolean equalsMetier( final ProduitJPA pProduitJPA , final ProduitI pProduit)`
14. ligne 967 : `private static <T> T requireJPA( final Object o , final Class<T> expected , final String contexte)`
15. ligne 1030 : `public static String afficherTypeProduitFormate( final TypeProduitJPA pTypeProduitJPA)`
16. ligne 1224 : `public static String afficherTypeProduitFormate( final TypeProduit pTypeProduit)`
17. ligne 1379 : `private String afficherProduitsJPA(final List<ProduitJPA> pList)`
18. ligne 1479 : `private String afficherProduits(final List<Produit> pList)`
19. ligne 1580 : `private String afficherSousTypeProduitsJPA( final List<? extends SousTypeProduitI> pList)`
20. ligne 1631 : `private String afficherSousTypeProduits( final List<SousTypeProduit> pList)`

## 6) API publique à conserver

- `typeProduitJPAToMetier(TypeProduitJPA)`
- `sousTypeProduitJPAToMetier(SousTypeProduitJPA)`
- `produitJPAToMetier(ProduitJPA)`

Chaque méthode publique crée un nouveau `ConversionContext` et délègue à l'overload privé correspondant. Ne pas déplacer la logique complète dans l'API publique.

## 7) Algorithme de conversion TypeProduit

- retourner `null` si l'entrée est `null` ;
- retourner `null` si le libellé principal est blank ;
- consulter le cache via `ctx.get(...)` ;
- instancier la cible vide ;
- mettre la cible en cache avant de convertir les relations ;
- copier les scalaires ID + libellé ;
- convertir les enfants non `null` ;
- vérifier l'implémentation concrète avec `requireJPA(...)` ;
- rattacher par setter canonique enfant ;
- ne jamais vider une collection cible quand la collection source est `null` ou non chargée.

## 8) Algorithme de conversion SousTypeProduit

- mêmes règles `null`, blank, cache et mise en cache anticipée ;
- copier ID + libellé ;
- convertir le parent avec l'overload privé ;
- rattacher le parent par setter canonique ;
- convertir les produits non `null` ;
- rattacher les produits par setter canonique ;
- préserver la sécurité LAZY et les graphes partiellement chargés.

## 9) Algorithme de conversion Produit

- mêmes règles `null`, blank, cache et mise en cache anticipée ;
- copier ID + libellé ;
- convertir le parent `SousTypeProduit` avec l'overload privé ;
- rattacher par `setSousTypeProduit(...)` si le parent courant diffère ;
- ne jamais comparer directement le grand-parent pour définir l'égalité du produit.

## 10) Égalité métier entre couches

Les méthodes `equalsMetier(...)` sont conservées pour les trois niveaux :

- deux paramètres `null` → `true` ;
- un seul paramètre `null` → `false` ;
- IDs comparés avec `Objects.equals(...)` ;
- libellés comparés avec `Strings.CI.equals(...)` ;
- `SousTypeProduit` compare aussi le parent `TypeProduit` ;
- `Produit` compare aussi le parent `SousTypeProduit`.

## 11) Helpers d'affichage

Les méthodes `afficherTypeProduitFormate(...)`, `afficherProduitsJPA(...)`, `afficherProduits(...)`, `afficherSousTypeProduitsJPA(...)` et `afficherSousTypeProduits(...)` sont conservées. Elles servent le diagnostic didactique et verrouillent les constantes de formatage.

## 12) Tests qui verrouillent ce fichier

- `ConvertisseurJPAToMetier.testTypeProduitJPAToMetierNull`
- `ConvertisseurJPAToMetier.testTypeProduitJPAToMetier`
- `ConvertisseurJPAToMetier.testSousTypeProduitJPAToMetier`
- `ConvertisseurJPAToMetier.testEqualsMetierTypeProduit`
- `ConvertisseurJPAToMetier.testEqualsMetierSousTypeProduit`
- `ConvertisseurJPAToMetier.testEqualsMetierProduit`
- `ConvertisseurJPAToMetier.testConversionDepuisTypeProduitJPA`
- `ConvertisseurJPAToMetier.testConversionDepuisSousTypeProduitJPA`
- `ConvertisseurJPAToMetier.testConversionDepuisProduitJPA`
- `ConvertisseurJPAToMetier.testLazyParentCollectionNonChargeeDepuisProduit`
- `ConvertisseurJPAToMetier.testConversionAvecCollectionsVides`
- `ConvertisseurJPAToMetier.testConversionAvecNomsDupliques`
- `ConvertisseurJPAToMetier.testPerformanceGrandeCollection`
- `ConvertisseurJPAToMetier.testConcurrencesDansLesMaps`
- `ConvertisseurJPAToMetier.testConcurrenceIdentityCacheTypeProduit`
- `ConvertisseurJPAToMetier.testLazyTypeProduitCollectionVideDepuisProduit`
- `ConvertisseurJPAToMetier.testCachePartageEntreDeuxAppelsTypeProduit`
- `ConvertisseurJPAToMetier.testRequireJPA`

## 13) Anti-improvisation

- Ne pas remplacer `IdentityHashMap` par `HashMap`.
- Ne pas supprimer `SHARED_CACHE`.
- Ne pas supprimer les overloads privés avec `ConversionContext`.
- Ne pas transformer les erreurs d'implémentation en retours `null`.
- Ne pas vider les collections cibles quand une collection source est `null` ou non chargée.
- Ne pas contourner les setters canoniques relationnels.

## 14) Critère d'autonomie

Le contrat décrit le squelette, l'ordre, les caches, les helpers et les tests. Pour coder, l'IA doit relire le fichier validé et reproduire le formalisme historique au lieu d'écrire un convertisseur minimal.
