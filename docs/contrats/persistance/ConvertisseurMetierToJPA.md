<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — ConvertisseurMetierToJPA

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java
```

Source validée : `9d6a81a65fbbf19f13342bd09e49afe78e3981abd5b240ada073ae50bcbd82f6` — 1661 lignes LF.

## 2) Rôle exact

`ConvertisseurMetierToJPA` est une classe utilitaire `final` de la couche persistance. Elle convertit les objets `métier` vers les objets `JPA` pour le domaine `produittype`.

Elle ne doit jamais dépendre des DTO, des UC, des Gateways ou des Controllers.

## 3) Constantes et caches obligatoires

- ligne 58 : `public static final String SAUT_DE_LIGNE = System.getProperty("line.separator");`
- ligne 64 : `public static final String UNCHECKED = "unchecked";`
- ligne 69 : `public static final String NULL = "null";`
- ligne 74 : `public static final String IMPLEMENTATION_NON_METIER = "Implémentation non-METIER ";`
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
- ligne 163 : `private static final Logger LOG = LogManager .getLogger(ConvertisseurMetierToJPA.class);`
- ligne 194 : `private static final class ConversionContext {  // ************************ATTRIBUTS*****************************/  /** * <div> * <p>IdentityHashMap servant de cache</p> * </div> */ private final Map<Object, Object> cache;`

Règles :

- `SAUT_DE_LIGNE`, `NULL`, formats d'affichage et séparateurs sont conservés pour les diagnostics historiques ;
- `IMPLEMENTATION_NON_METIER` est utilisé par `requireMetier(...)` ;
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

1. ligne 174 : `private ConvertisseurMetierToJPA()`
2. ligne 214 : `private ConversionContext()`
3. ligne 242 : `private <T> T get(final Object pKey)`
4. ligne 276 : `private void put(final Object pKey, final Object pValue)`
5. ligne 306 : `public static TypeProduitJPA typeProduitMETIERToJPA( final TypeProduit pTypeProduit)`
6. ligne 327 : `public static SousTypeProduitJPA sousTypeProduitMETIERToJPA( final SousTypeProduit pSousTypeProduit)`
7. ligne 348 : `public static ProduitJPA produitMETIERToJPA( final Produit pProduit)`
8. ligne 391 : `private static TypeProduitJPA typeProduitMETIERToJPA( final TypeProduit pTypeProduit, final ConversionContext ctx)`
9. ligne 509 : `private static SousTypeProduitJPA sousTypeProduitMETIERToJPA( final SousTypeProduit pSousTypeProduit, final ConversionContext ctx)`
10. ligne 652 : `private static ProduitJPA produitMETIERToJPA( final Produit pProduit, final ConversionContext ctx)`
11. ligne 742 : `public static boolean equalsMetier( final TypeProduitI pTypeProduit, final TypeProduitJPA pTypeProduitJPA)`
12. ligne 821 : `public static boolean equalsMetier( final SousTypeProduitI pSousTypeProduit, final SousTypeProduitJPA pSousTypeProduitJPA)`
13. ligne 912 : `public static boolean equalsMetier( final ProduitI pProduit, final ProduitJPA pProduitJPA)`
14. ligne 962 : `private static <T> T requireMetier( final Object o, final Class<T> expected, final String contexte)`
15. ligne 1024 : `public static String afficherTypeProduitFormate( final TypeProduitI pTypeProduit)`
16. ligne 1216 : `public static String afficherTypeProduitFormate( final TypeProduitJPA pTypeProduitJPA)`
17. ligne 1369 : `private String afficherProduitsJPA(final List<ProduitJPA> pList)`
18. ligne 1469 : `private String afficherProduits(final List<Produit> pList)`
19. ligne 1572 : `private String afficherSousTypeProduitsJPA( final List<? extends SousTypeProduitI> pList)`
20. ligne 1623 : `private String afficherSousTypeProduits( final List<SousTypeProduit> pList)`

## 6) API publique à conserver

- `typeProduitMETIERToJPA(TypeProduit)`
- `sousTypeProduitMETIERToJPA(SousTypeProduit)`
- `produitMETIERToJPA(Produit)`

Chaque méthode publique crée un nouveau `ConversionContext` et délègue à l'overload privé correspondant. Ne pas déplacer la logique complète dans l'API publique.

## 7) Algorithme de conversion TypeProduit

- retourner `null` si l'entrée est `null` ;
- retourner `null` si le libellé principal est blank ;
- consulter le cache via `ctx.get(...)` ;
- instancier la cible vide ;
- mettre la cible en cache avant de convertir les relations ;
- copier les scalaires ID + libellé ;
- convertir les enfants non `null` ;
- vérifier l'implémentation concrète avec `requireMetier(...)` ;
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

- `ConvertisseurMetierToJPA.testTypeProduitMETIERToJPANull`
- `ConvertisseurMetierToJPA.testTypeProduitMETIERToJPA`
- `ConvertisseurMetierToJPA.testSousTypeProduitMETIERToJPA`
- `ConvertisseurMetierToJPA.testEqualsMetierTypeProduit`
- `ConvertisseurMetierToJPA.testEqualsMetierSousTypeProduit`
- `ConvertisseurMetierToJPA.testEqualsMetierProduit`
- `ConvertisseurMetierToJPA.testConversionDepuisTypeProduit`
- `ConvertisseurMetierToJPA.testConversionDepuisSousTypeProduit`
- `ConvertisseurMetierToJPA.testConversionDepuisProduit`
- `ConvertisseurMetierToJPA.testConversionAvecCollectionsVides`
- `ConvertisseurMetierToJPA.testConversionAvecNomsDupliques`
- `ConvertisseurMetierToJPA.testPerformanceGrandeCollection`
- `ConvertisseurMetierToJPA.testConcurrencesDansLesMaps`
- `ConvertisseurMetierToJPA.testRequireMetier`
- `ConvertisseurMetierToJPA.testCachePartageEntreAppels`
- `ConvertisseurMetierToJPA.testTypeProduitMETIERToJPADepuisLeafGrapheStable`

## 13) Anti-improvisation

- Ne pas remplacer `IdentityHashMap` par `HashMap`.
- Ne pas supprimer `SHARED_CACHE`.
- Ne pas supprimer les overloads privés avec `ConversionContext`.
- Ne pas transformer les erreurs d'implémentation en retours `null`.
- Ne pas vider les collections cibles quand une collection source est `null` ou non chargée.
- Ne pas contourner les setters canoniques relationnels.

## 14) Critère d'autonomie

Le contrat décrit le squelette, l'ordre, les caches, les helpers et les tests. Pour coder, l'IA doit relire le fichier validé et reproduire le formalisme historique au lieu d'écrire un convertisseur minimal.
