# docs/contrats/metier/SousTypeProduit.md

# Contrat local métier — SousTypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.SousTypeProduit`
- interface liée : `SousTypeProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`SousTypeProduit` est l'objet métier intermédiaire entre `TypeProduit` et `Produit`.

Il porte :

- `idSousTypeProduit` ;
- `sousTypeProduit` ;
- un parent `TypeProduitI` ;
- une collection de `ProduitI` enfants ;
- un booléen `valide` recalculé.

## 3) Ordre de lecture obligatoire

1. `CONTRAT_IA.md` ;
2. `CoucheMetier.md` ;
3. `SousTypeProduit.md` ;
4. `SousTypeProduitI.java` ;
5. `SousTypeProduit.java` ;
6. `TypeProduit.java` si le parent est concerné ;
7. `Produit.java` si les enfants sont concernés ;
8. `CloneContext.java` si le clonage est concerné ;
9. `SousTypeProduitTest.java` ;
10. `TypeProduitSousTypeProduitIntegrationTest.java` si la relation parent est concernée ;
11. `MetierGlobalConformiteTest.java` si la cohérence globale est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherSousTypeProduit()` ;
6. `compareTo(SousTypeProduitI)` ;
7. `compareFields(SousTypeProduitI)` ;
8. `clone()` ;
9. `cloneDeep()` ;
10. `deepClone(CloneContext)` ;
11. `cloneWithoutParentAndChildren()` ;
12. `recalculerValide()` ;
13. `normalize(String)` ;
14. CSV / JTable ;
15. `ajouterSTPauProduit(...)` ;
16. `retirerSTPauProduit(...)` ;
17. méthodes internes d'ajout/retrait par identité ;
18. `isValide()` ;
19. getters / setters ;
20. remplacement de collections.

## 5) Égalité et hashCode

`SousTypeProduit.equals(...)` compare :

1. le parent `TypeProduit` ;
2. le libellé `sousTypeProduit`.

Règles :

- comparaison insensible à la casse sur les libellés ;
- l'identifiant persistant ne participe pas à l'égalité ;
- la collection de produits ne participe pas à l'égalité ;
- le parent est comparé comme objet métier, pas seulement comme chaîne isolée ;
- verrouillage déterministe par `System.identityHashCode(...)` avec verrou de classe en cas de collision.

## 6) Comparaison

`compareTo(SousTypeProduitI)` compare d'abord le parent `TypeProduit`, puis le libellé `sousTypeProduit`.

Les valeurs `null` sont ordonnées de manière déterministe selon le code validé. La comparaison des libellés est insensible à la casse.

## 7) Parent TypeProduit

Règles :

- `setTypeProduit(...)` est le setter intelligent de rattachement au parent ;
- changer de parent doit retirer l'objet de l'ancien parent et l'ajouter au nouveau ;
- le re-parenting doit éviter les deadlocks ;
- les opérations doivent recalculer `valide` ;
- un parent `null` est autorisé dans certains états, mais rend l'objet non valide.

## 8) Enfants Produit

Règles :

- `ajouterSTPauProduit(...)` rattache un `Produit` au présent objet métier ;
- `retirerSTPauProduit(...)` détache un `Produit` ;
- les méthodes internes utilisent la comparaison par identité lorsque nécessaire ;
- ne pas utiliser `contains()` ou `remove(Object)` si cela déclenche `equals(...)` sous verrou ;
- les doublons par identité ne doivent pas être ajoutés ;
- `setProduits(...)` doit reconstruire la cohérence avec les produits fournis.

## 9) Validité

`valide` est vrai uniquement lorsque l'objet dispose des éléments métier requis par le code validé, notamment parent et libellé.

Règles :

- `recalculerValide()` est la source du recalcul ;
- les setters et clones partiels doivent recalculer ;
- `cloneWithoutParentAndChildren()` doit produire un clone sans parent ni enfants, avec validité recalculée ;
- ne jamais forcer `valide` sans recalcul.

## 10) Clonage

- `clone()` produit un clone profond via `CloneContext` ;
- `cloneWithoutParentAndChildren()` copie les champs propres sans parent ni enfants ;
- `deepClone(CloneContext)` réutilise le contexte, clone le parent si nécessaire, clone les produits enfants et reconstruit les relations ;
- le clonage ne doit pas dupliquer une source déjà présente dans le contexte.

## 11) CSV / JTable

- `getEnTeteCsv()` retourne l'en-tête validé `idSousTypeProduit;type de produit;sous-type de produit;` ;
- `toStringCsv()` respecte l'ordre `idSousTypeProduit`, `type de produit`, `sous-type de produit` ;
- les valeurs absentes sont rendues par le texte `null` en CSV ;
- `getEnTeteColonne(0)` : `idSousTypeProduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(1)` délègue au parent via `getValeurColonne(1)`.

## 12) Tests de référence

`SousTypeProduitTest.java` contient 36 tests validés couvrant :

- constructeurs ;
- égalité et comparaison ;
- insensibilité à la casse ;
- thread-safety ;
- clonage ;
- CSV / JTable ;
- gestion des produits ;
- validité ;
- clone sans parent ni enfants.

## 13) Interdictions

- ne jamais parler de `SousTypeProduit` comme d'un simple couple ; c'est l'objet métier testé ;
- ne jamais casser `TypeProduit <-> SousTypeProduit` ;
- ne jamais casser `SousTypeProduit <-> Produit` ;
- ne jamais faire participer l'identifiant ou la collection de produits à l'égalité ;
- ne jamais exposer une collection interne mutable partagée ;
- ne jamais supprimer le recalcul de validité.

## 14) AUTONOMIE-SOUSTYPEPRODUIT-01 — Fiche de recodage autonome

Cette section complète les sections précédentes. Elle a priorité lorsqu'il faut recoder `SousTypeProduit` en autonomie quasiment à l'identique.

### 14.1 Structure exacte à reproduire

`SousTypeProduit` est une classe métier `public` qui implémente `SousTypeProduitI` et `Cloneable`.

Ordre structurel à conserver : constantes, attributs, logger, constructeurs, égalité, affichage, comparaison, clonage, validité, normalisation, CSV/JTable, relation avec les produits, getters/setters, traitements des mauvaises instances.

### 14.2 Constantes et attributs contractuels

| Élément | Visibilité | Valeur / rôle obligatoire |
| --- | --- | --- |
| `NULL` | `public static final String` | valeur exacte `null` |
| `VIRGULE_ESPACE` | `public static final String` | valeur exacte `, ` |
| `CROCHET_FERMANT` | `public static final char` | valeur exacte `]` |
| `POINT_VIRGULE` | `public static final char` | valeur exacte `;` |
| `MAUVAISE_INSTANCE_PARENT_METIER` | `public static final String` | `Le pTypeProduit passé en paramètre n'est pas de type Objet Métier : `, construit par concaténation historique |
| `MAUVAISE_INSTANCE_PETIT_ENFANT_METIER` | `public static final String` | `Le pProduit passé en paramètre n'est pas de type Objet Métier : `, construit par concaténation historique |
| `EN_TETE_CSV_SOUS_TYPE_PRODUIT` | `public static final String` | valeur exacte `idSousTypeProduit;type de produit;sous-type de produit;` |
| `idSousTypeProduit` | `private Long` | identifiant du SousTypeProduit |
| `sousTypeProduit` | `private String` | libellé métier de l'objet |
| `typeProduit` | `private TypeProduitI` | parent métier |
| `produits` | `private List<ProduitI>` | collection interne initialisée en `new ArrayList<ProduitI>()` |
| `valide` | `private transient boolean` | état calculé, jamais forcé durablement hors recalcul |
| `LOG` | `private static final Logger` | `LogManager.getLogger(SousTypeProduit.class)` |

### 14.3 Ordre exact des méthodes validées

1. `public SousTypeProduit()` ;
2. `public SousTypeProduit(String)` ;
3. `public SousTypeProduit(String, TypeProduitI)` ;
4. `public SousTypeProduit(Long, String, TypeProduitI)` ;
5. `public SousTypeProduit(String, TypeProduitI, List<ProduitI>)` ;
6. `public SousTypeProduit(Long, String, TypeProduitI, List<ProduitI>)` ;
7. `public final int hashCode()` ;
8. `public final boolean equals(Object)` ;
9. `public final String toString()` ;
10. `public final String afficherSousTypeProduit()` ;
11. `public final int compareTo(SousTypeProduitI)` ;
12. `private int compareFields(SousTypeProduitI)` ;
13. `public final SousTypeProduit clone() throws CloneNotSupportedException` ;
14. `private SousTypeProduit cloneDeep()` ;
15. `public final SousTypeProduit deepClone(CloneContext)` ;
16. `public final SousTypeProduit cloneWithoutParentAndChildren()` ;
17. `private void recalculerValide()` ;
18. `private static String normalize(String)` ;
19. `public final String getEnTeteCsv()` ;
20. `public final String toStringCsv()` ;
21. `public final String getEnTeteColonne(int)` ;
22. `public final Object getValeurColonne(int)` ;
23. `public final void ajouterSTPauProduit(ProduitI)` ;
24. `public final void retirerSTPauProduit(ProduitI)` ;
25. `protected final void internalAddProduit(ProduitI)` ;
26. `protected final void internalRemoveProduit(ProduitI)` ;
27. `private boolean containsProduitByReference(ProduitI)` ;
28. `private void removeProduitByReference(ProduitI)` ;
29. `public final boolean isValide()` ;
30. `public final Long getIdSousTypeProduit()` ;
31. `public final void setIdSousTypeProduit(Long)` ;
32. `public final String getSousTypeProduit()` ;
33. `public final void setSousTypeProduit(String)` ;
34. `public final TypeProduitI getTypeProduit()` ;
35. `public final void setTypeProduit(TypeProduitI)` ;
36. `public final List<ProduitI> getProduits()` ;
37. `public final void setProduits(List<? extends ProduitI>)` ;
38. `private void traiterMauvaiseInstanceTypeProduit(TypeProduitI)` ;
39. `private void traiterMauvaiseInstanceProduit(ProduitI)` ;
40. `private void traiterMauvaiseInstanceDansListeProduits(List<? extends ProduitI>)`.

### 14.4 Algorithmes sensibles à reproduire

#### Égalité et hashCode

`hashCode()` utilise un snapshot court sous verrou de `typeProduit` et `sousTypeProduit`. Le parent participe par son `hashCode()`. Le libellé propre participe en minuscules `Locale.ROOT`, ou `0` s'il est `null`.

`equals(...)` :

- accepte uniquement une autre instance `SousTypeProduit` ;
- compare `typeProduit` par `Objects.equals(...)` ;
- compare `sousTypeProduit` de manière insensible à la casse ;
- ne fait pas participer l'identifiant ni la collection `produits` ;
- verrouille `this` et `other` dans l'ordre d'`identityHashCode` ;
- utilise `SousTypeProduit.class` comme verrou de départ en cas de collision.

#### Comparaison

`compareTo(...)` :

- retourne `0` si la référence est identique ;
- retourne `-1` si l'objet comparé est `null` ;
- compare d'abord le parent `TypeProduit`, puis le libellé `sousTypeProduit` ;
- gère les `null` de façon déterministe ;
- utilise une comparaison insensible à la casse pour le libellé.

#### Validité

`recalculerValide()` doit fixer `valide` à `true` uniquement si `typeProduit != null` et `sousTypeProduit != null`. Les setters et clones partiels doivent l'appeler lorsque l'état peut changer.

#### Relation au parent `TypeProduit`

`setTypeProduit(...)` est le setter canonique de relation. Il doit :

- traiter les mauvaises instances ;
- ne rien faire si le parent demandé est déjà le parent courant par identité ;
- retirer l'objet de l'ancien parent via `internalRemoveSousTypeProduit(...)` si l'ancien parent est un `TypeProduit` validé ;
- affecter le nouveau parent ;
- ajouter l'objet au nouveau parent via `internalAddSousTypeProduit(...)` si le nouveau parent est un `TypeProduit` validé ;
- recalculer `valide` ;
- gérer le re-parenting sans deadlock par ordre déterministe lorsque deux parents validés sont concernés.

#### Relation aux enfants `Produit`

`ajouterSTPauProduit(...)` et `retirerSTPauProduit(...)` doivent déléguer au setter canonique du produit (`Produit.setSousTypeProduit(...)`) et ne pas se contenter de modifier la liste.

`internalAddProduit(...)` et `internalRemoveProduit(...)` :

- traitent les mauvaises instances ;
- ignorent `null` ;
- travaillent par identité ;
- n'appellent pas le setter canonique du produit ;
- ne doivent pas utiliser `contains(...)` ni `remove(Object)`.

`containsProduitByReference(...)` et `removeProduitByReference(...)` comparent avec `==`.

#### `setProduits(...)`

`setProduits(...)` doit :

- faire un snapshot défensif de la liste entrante si non `null` ;
- traiter les mauvaises instances avant modification ;
- prendre un snapshot des produits actuels ;
- détacher les produits actuels via `produit.setSousTypeProduit(null)` ;
- si la nouvelle liste est `null`, laisser la collection interne vide ;
- ignorer les éléments `null` ;
- rattacher les nouveaux produits via `produit.setSousTypeProduit(this)` ;
- ne jamais conserver la référence mutable reçue.

#### Clonage profond

`deepClone(CloneContext)` doit :

- jeter `IllegalArgumentException` si le contexte est `null` ;
- retourner le clone déjà présent dans le contexte ;
- créer un clone sans parent ni enfants puis l'enregistrer immédiatement ;
- prendre un snapshot thread-safe des produits ;
- prendre un snapshot thread-safe du parent ;
- cloner le parent et rattacher le clone au parent cloné si présent ;
- cloner chaque produit et le rattacher au clone.

### 14.5 CSV/JTable exacts

- `getEnTeteCsv()` : `idSousTypeProduit;type de produit;sous-type de produit;` ;
- `toStringCsv()` : identifiant, libellé du parent, libellé propre ;
- valeurs absentes en CSV : texte `null` ;
- `getEnTeteColonne(0)` : `idSousTypeProduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(1)` récupère le libellé du parent via `getValeurColonne(1)` lorsque le parent existe.

### 14.6 Tests validés à conserver comme spécification

1. `testGeneral()` ;
2. `testSousTypeProduit()` ;
3. `testSousTypeProduitString()` ;
4. `testConstructeursComplets()` ;
5. `testEquals()` ;
6. `testEqualsIgnoreCase()` ;
7. `testEqualsHashCodeThreadSafe()` ;
8. `testToString()` ;
9. `testToStringThreadSafe()` ;
10. `testCompareTo()` ;
11. `testCompareToIgnoreCase()` ;
12. `testCompareToThreadSafe()` ;
13. `testClone()` ;
14. `testDeepCloneThreadSafe()` ;
15. `testDeepClone()` ;
16. `testCsv()` ;
17. `testJTable()` ;
18. `testGetEnTeteCsv()` ;
19. `testToStringCsv()` ;
20. `testToStringCsvThreadSafe()` ;
21. `testGetEnTeteColonne()` ;
22. `testGetEnTeteColonneThreadSafe()` ;
23. `testGetValeurColonne()` ;
24. `testGetValeurColonneThreadSafe()` ;
25. `testAjouterRetirerProduitThreadSafe()` ;
26. `testGestionProduits()` ;
27. `testIsValide()` ;
28. `testIsValideThreadSafe()` ;
29. `testCloneWithoutParentAndChildren()` ;
30. `testRecalculerValide()` ;
31. `testNormalizeViaReflexion()` ;
32. `testInternalGestionProduits()` ;
33. `testGetProduitsIterationThreadSafe()` ;
34. `testGetProduitsThreadSafe()` ;
35. `testSetProduits()` ;
36. `testSetProduitsThreadSafe()`.
