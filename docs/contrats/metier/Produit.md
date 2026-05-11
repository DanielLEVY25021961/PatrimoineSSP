# docs/contrats/metier/Produit.md

# Contrat local métier — Produit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.Produit`
- interface liée : `ProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`Produit` est l'objet métier final rattaché à un parent `SousTypeProduit`, lui-même rattaché à un `TypeProduit`.

Il porte :

- `idProduit` ;
- `produit` ;
- un parent `SousTypeProduitI` ;
- un booléen `valide` recalculé.

## 3) Ordre de lecture obligatoire

1. `CONTRAT_IA.md` ;
2. `CoucheMetier.md` ;
3. `Produit.md` ;
4. `ProduitI.java` ;
5. `Produit.java` ;
6. `SousTypeProduit.java` si le parent est concerné ;
7. `TypeProduit.java` si l'accès indirect au grand-parent est concerné ;
8. `CloneContext.java` si le clonage est concerné ;
9. `ProduitTest.java` ;
10. `MetierGlobalConformiteTest.java` ;
11. `TypeProduitSousTypeProduitIntegrationTest.java` si la hiérarchie complète est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherProduit()` ;
6. `compareTo(ProduitI)` ;
7. `compareFields(ProduitI)` ;
8. `clone()` ;
9. `cloneDeep()` ;
10. `deepClone(CloneContext)` ;
11. `cloneWithoutParent()` ;
12. `recalculerValide()` ;
13. `normalize(String)` ;
14. CSV / JTable ;
15. `getTypeProduit()` ;
16. `isValide()` ;
17. getters / setters.

## 5) Égalité et hashCode

`Produit.equals(...)` compare :

1. le parent `SousTypeProduit` ;
2. le libellé `produit`.

Règles :

- comparaison insensible à la casse sur le libellé produit ;
- le grand-parent `TypeProduit` est pris en compte uniquement via le parent `SousTypeProduit` ;
- l'identifiant persistant ne participe pas à l'égalité métier ;
- le verrouillage suit les règles concurrentes validées ;
- ne pas remplacer cette règle par une égalité sur `TypeProduit + SousTypeProduit + Produit` calculée séparément.

## 6) Comparaison

`compareTo(ProduitI)` compare d'abord le parent `SousTypeProduit`, puis le libellé `produit`.

Règles :

- comparaison insensible à la casse ;
- valeurs `null` déterministes ;
- ordre de verrouillage par `System.identityHashCode(...)` avec verrou de classe en cas de collision ;
- les tests de symétrie, transitivité et absence de deadlock sont spécification.

## 7) Parent SousTypeProduit

Règles :

- `setSousTypeProduit(...)` est le setter intelligent ;
- changer de parent retire le produit de l'ancien parent et l'ajoute au nouveau ;
- `getTypeProduit()` lit le `TypeProduit` via le parent ;
- un parent absent est autorisé dans certains états mais rend l'objet non valide ;
- le re-parenting doit éviter les deadlocks `Produit <-> SousTypeProduit`.

## 8) Validité

`valide` dépend du libellé `produit` et du parent `SousTypeProduit`.

Règles :

- `recalculerValide()` est la source du recalcul ;
- les setters doivent recalculer ;
- `cloneWithoutParent()` doit recalculer ;
- ne jamais forcer `valide` sans recalcul.

## 9) Clonage

- `clone()` produit un clone profond via `CloneContext` ;
- `cloneWithoutParent()` copie les champs propres sans parent ;
- `deepClone(CloneContext)` réutilise le contexte fourni ;
- le parent `SousTypeProduit` est cloné profondément si présent ;
- les relations bidirectionnelles doivent rester cohérentes.

## 10) CSV / JTable

- `getEnTeteCsv()` retourne exactement `idproduit;type de produit;sous-type de produit;produit;` ;
- `toStringCsv()` respecte l'ordre `idproduit`, `type de produit`, `sous-type de produit`, `produit` ;
- les valeurs absentes sont rendues par le texte `null` en CSV ;
- `getEnTeteColonne(0)` : `idproduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- `getEnTeteColonne(3)` : `produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(...)` retourne `null` pour une colonne valide sans valeur, et `invalide` pour une colonne non supportée.

## 11) Tests de référence

`ProduitTest.java` contient 39 tests validés couvrant :

- constructeurs ;
- comportement général ;
- égalité / hashCode ;
- thread-safety ;
- `toString` ;
- `compareTo` ;
- symétrie et transitivité ;
- absence de deadlock ;
- clonage ;
- validité ;
- CSV / JTable ;
- getters / setters ;
- relation au parent.

## 12) Interdictions

- ne jamais faire participer directement le grand-parent `TypeProduit` à l'égalité autrement que via le parent `SousTypeProduit` ;
- ne jamais casser `SousTypeProduit <-> Produit` ;
- ne jamais supprimer le recalcul de validité ;
- ne jamais remplacer le clonage profond par une copie superficielle ;
- ne jamais modifier les en-têtes CSV/JTable historiques.

## 13) AUTONOMIE-PRODUIT-01 — Fiche de recodage autonome

Cette section complète les sections précédentes. Elle a priorité lorsqu'il faut recoder `Produit` en autonomie quasiment à l'identique.

### 13.1 Structure exacte à reproduire

`Produit` est une classe métier `public` qui implémente `ProduitI` et `Cloneable`.

Ordre structurel à conserver : constantes, attributs, logger, constructeurs, égalité, affichage, comparaison, clonage, validité, normalisation, CSV/JTable, getters/setters, traitement de mauvaise instance parent.

### 13.2 Constantes et attributs contractuels

| Élément | Visibilité | Valeur / rôle obligatoire |
| --- | --- | --- |
| `NULL` | `public static final String` | valeur exacte `null` |
| `VIRGULE_ESPACE` | `public static final String` | valeur exacte `, ` |
| `CROCHET_FERMANT` | `public static final char` | valeur exacte `]` |
| `POINT_VIRGULE` | `public static final char` | valeur exacte `;` |
| `MAUVAISE_INSTANCE_ENFANT_METIER` | `public static final String` | `le SousTypeProduit passé en paramètre n'est pas de type Objet métier : `, construit par concaténation historique |
| `ENTETECSV` | `public static final String` | valeur exacte `idproduit;type de produit;sous-type de produit;produit;` |
| `idProduit` | `private Long` | identifiant du Produit |
| `produit` | `private String` | libellé métier du Produit |
| `sousTypeProduit` | `private SousTypeProduitI` | parent métier direct |
| `valide` | `private transient boolean` | état calculé, vrai uniquement si parent direct et libellé existent |
| `LOG` | `private static final Logger` | `LogManager.getLogger(Produit.class)` |

### 13.3 Ordre exact des méthodes validées

1. `public Produit()` ;
2. `public Produit(String)` ;
3. `public Produit(String, SousTypeProduitI)` ;
4. `public Produit(Long, String, SousTypeProduitI)` ;
5. `public final int hashCode()` ;
6. `public final boolean equals(Object)` ;
7. `public final String toString()` ;
8. `public String afficherProduit()` ;
9. `public final int compareTo(ProduitI)` ;
10. `private int compareFields(ProduitI)` ;
11. `public final Produit clone() throws CloneNotSupportedException` ;
12. `private Produit cloneDeep()` ;
13. `public final Produit deepClone(CloneContext)` ;
14. `public final Produit cloneWithoutParent()` ;
15. `private void recalculerValide()` ;
16. `private static String normalize(String)` ;
17. `public final String getEnTeteCsv()` ;
18. `public final String toStringCsv()` ;
19. `public final String getEnTeteColonne(int)` ;
20. `public final Object getValeurColonne(int)` ;
21. `public final TypeProduitI getTypeProduit()` ;
22. `public final boolean isValide()` ;
23. `public final Long getIdProduit()` ;
24. `public final void setIdProduit(Long)` ;
25. `public final String getProduit()` ;
26. `public final void setProduit(String)` ;
27. `public final SousTypeProduitI getSousTypeProduit()` ;
28. `public void setSousTypeProduit(SousTypeProduitI)` ;
29. `private void traiterMauvaiseInstanceSousTypeProduit(SousTypeProduitI)`.

### 13.4 Algorithmes sensibles à reproduire

#### Égalité et hashCode

`Produit.equals(...)` est défini par le parent direct `SousTypeProduit` et par le libellé `produit` insensible à la casse.

Règles impératives :

- ne pas faire participer directement le grand-parent `TypeProduit` ;
- ne pas faire participer l'identifiant ;
- accepter uniquement une instance `Produit` validée ;
- verrouiller `this` et `other` dans l'ordre d'`identityHashCode` ;
- comparer le parent direct par `Objects.equals(...)` ;
- comparer le libellé par `equalsIgnoreCase(...)` ;
- `hashCode()` doit rester cohérent avec cette égalité.

#### Comparaison

`compareTo(...)` :

- retourne `0` pour la même référence ;
- gère `null` de façon déterministe selon le code validé ;
- compare d'abord le parent direct `SousTypeProduit` ;
- compare ensuite le libellé `produit` de façon insensible à la casse ;
- conserve les tests de symétrie, transitivité et absence de deadlock.

#### Validité

`recalculerValide()` fixe `valide` à `true` uniquement si `produit != null` et `sousTypeProduit != null`. Les setters `setProduit(...)` et `setSousTypeProduit(...)` doivent recalculer.

#### Parent direct

`getTypeProduit()` ne stocke pas le grand-parent. Il prend un snapshot court de `sousTypeProduit`, sort du verrou, puis appelle `sousTypeProduitSnapshot.getTypeProduit()` si le parent direct existe.

`setSousTypeProduit(...)` doit :

- traiter la mauvaise instance sous verrou ;
- mémoriser l'ancien parent direct ;
- ne rien faire si l'ancien parent est identique au nouveau ;
- retirer le produit de l'ancien parent via `internalRemoveProduit(this)` si l'ancien parent est un `SousTypeProduit` validé ;
- affecter le nouveau parent ;
- ajouter le produit au nouveau parent via `internalAddProduit(this)` si le nouveau parent est un `SousTypeProduit` validé ;
- recalculer `valide` ;
- conserver les commentaires expliquant l'usage de `instanceof ... variable` Java 17.

#### Clonage profond

`deepClone(CloneContext)` doit :

- jeter `IllegalArgumentException` si le contexte est `null` ;
- retourner le clone déjà présent dans le contexte ;
- créer un clone sans parent et l'enregistrer immédiatement ;
- prendre un snapshot du parent direct sous verrou ;
- cloner le parent direct si présent ;
- rattacher le clone au parent cloné via `setSousTypeProduit(...)`.

`cloneWithoutParent()` copie `idProduit` et `produit`, fixe le parent à `null`, puis recalcule `valide`.

### 13.5 CSV/JTable exacts

- `getEnTeteCsv()` : `idproduit;type de produit;sous-type de produit;produit;` ;
- `toStringCsv()` : identifiant, libellé du grand-parent via le parent, libellé du parent, libellé produit ;
- valeurs absentes en CSV : texte `null` ;
- `getEnTeteColonne(0)` : `idproduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- `getEnTeteColonne(3)` : `produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(...)` retourne `null` pour une colonne valide sans valeur, et `invalide` pour une colonne non supportée.

### 13.6 Tests validés à conserver comme spécification

1. `testConstructeurNull()` ;
2. `testGeneral()` ;
3. `testEquals()` ;
4. `testEqualsThreadSafe()` ;
5. `testToString()` ;
6. `testToStringThreadSafe()` ;
7. `testCompareTo()` ;
8. `testCompareToThreadSafe()` ;
9. `testCompareToIgnoreCase()` ;
10. `testCompareToSymetrieEtTransitivite()` ;
11. `testCompareToThreadSafeSansPreVerrouillage()` ;
12. `testClone()` ;
13. `testCloneThreadSafe()` ;
14. `testDeepClone()` ;
15. `testDeepCloneThreadSafe()` ;
16. `testCloneWithoutParent()` ;
17. `testCloneWithoutParentThreadSafe()` ;
18. `testRecalculerValideThreadSafe()` ;
19. `testNormalizeThreadSafe()` ;
20. `testGetEnTeteCsv()` ;
21. `testToStringCsv()` ;
22. `testToStringCsvThreadSafe()` ;
23. `testGetEnTeteColonne()` ;
24. `testGetEnTeteColonneThreadSafe()` ;
25. `testGetValeurColonne()` ;
26. `testGetValeurColonneThreadSafe()` ;
27. `testIsValide()` ;
28. `testIsValideThreadSafe()` ;
29. `testGetIdProduitThreadSafe()` ;
30. `testSetIdProduitThreadSafe()` ;
31. `testGetProduitThreadSafe()` ;
32. `testSetProduit()` ;
33. `testSetProduitThreadSafe()` ;
34. `testSetSousTypeProduit()` ;
35. `testGetSousTypeProduitThreadSafe()` ;
36. `testSetSousTypeProduitThreadSafe()` ;
37. `testGetTypeProduit()` ;
38. `testGetTypeProduitThreadSafe()` ;
39. `testSousTypeProduitProduitAntiDeadlockThreadSafe()`.
