# docs/contrats/metier/TypeProduit.md

# Contrat local métier — TypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.TypeProduit`
- interface liée : `TypeProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`TypeProduit` est la racine métier de la hiérarchie `TypeProduit -> SousTypeProduit -> Produit`.

Il porte :

- `idTypeProduit` ;
- `typeProduit` ;
- une collection de `SousTypeProduitI` enfants.

## 3) Ordre de lecture obligatoire

Avant toute action :

1. `docs/ai/CONTRAT_IA.md` ;
2. `docs/contrats/metier/CoucheMetier.md` ;
3. `docs/contrats/metier/TypeProduit.md` ;
4. `TypeProduitI.java` ;
5. `TypeProduit.java` ;
6. `SousTypeProduitI.java` et `SousTypeProduit.java` si les enfants sont concernés ;
7. `CloneContext.java` si le clonage est concerné ;
8. `TypeProduitTest.java` ;
9. `TypeProduitSousTypeProduitIntegrationTest.java` si la bidirectionnalité est concernée ;
10. `MetierGlobalConformiteTest.java` si la cohérence globale est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé dans la classe validée :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherTypeProduit()` ;
6. `afficherSousTypeProduits()` ;
7. `compareTo(TypeProduitI)` ;
8. `compareFields(TypeProduitI)` ;
9. `clone()` ;
10. `cloneDeep()` ;
11. `deepClone(CloneContext)` ;
12. `cloneWithoutChildren()` ;
13. `rattacherEnfantSTP(SousTypeProduitI)` ;
14. `rattacherSiNecessaire(SousTypeProduitI)` ;
15. `detacherEnfantSTP(SousTypeProduitI)` ;
16. `detacherSiNecessaire(SousTypeProduitI)` ;
17. méthodes internes d'ajout/retrait de sous-types ;
18. `normalize(String)` ;
19. CSV / JTable ;
20. getters / setters ;
21. méthodes de verrouillage multi-objets.

Ne jamais réordonner sans demande explicite.

## 5) Égalité et hashCode

`equals(...)` est une égalité métier sur `typeProduit` uniquement.

Règles :

- comparaison insensible à la casse ;
- deux libellés `null` sont égaux sur ce critère ;
- un libellé `null` et un libellé non `null` ne sont pas égaux ;
- la collection d'enfants ne participe pas à l'égalité ;
- l'identifiant persistant ne participe pas à l'égalité métier ;
- le verrouillage multi-objets est ordonné par `System.identityHashCode(...)` avec verrou de classe en cas de collision.

`hashCode()` doit rester cohérent avec l'égalité et utiliser la même normalisation métier.

## 6) Comparaison

`compareTo(TypeProduitI)` compare le libellé `typeProduit` de manière insensible à la casse.

Règles :

- `compareTo(null)` retourne `-1` selon le comportement validé ;
- les valeurs `null` sont ordonnées de manière déterministe ;
- le verrouillage suit l'ordre `System.identityHashCode(...)` pour éviter les deadlocks.

## 7) Relations parent/enfants

`TypeProduit` est responsable de la cohérence bidirectionnelle avec les `SousTypeProduit` enfants.

Règles :

- `rattacherEnfantSTP(null)` ne doit pas créer d'effet parasite ;
- un enfant blank ou non exploitable est ignoré selon le comportement validé ;
- une mauvaise instance doit être traitée conformément au code validé ;
- le rattachement utilise le setter intelligent `SousTypeProduit.setTypeProduit(this)` ;
- le détachement utilise le setter intelligent `SousTypeProduit.setTypeProduit(null)` ;
- le re-parenting retire l'enfant de l'ancien parent ;
- les doublons par identité ne doivent pas être ajoutés ;
- les méthodes internes ne doivent pas appeler `contains()` ou `remove(Object)` lorsqu'elles doivent éviter `equals(...)` sous verrou.

## 8) Collections

`getSousTypeProduits()` expose un snapshot et non la liste interne mutable partagée.

Règles :

- ne jamais retourner directement la collection interne ;
- conserver une collection interne exploitable dès la construction ;
- conserver la visibilité par interface.

## 9) Clonage

`clone()` produit un clonage profond via `CloneContext`.

Règles :

- `cloneWithoutChildren()` copie `idTypeProduit` et `typeProduit`, sans enfants ;
- `deepClone(CloneContext)` réutilise un clone déjà présent dans le contexte ;
- le clone profond clone chaque enfant et le rattache au clone parent ;
- le clonage profond validé utilise une implémentation interne de type callback avec méthode `compute()` dans le contexte de clonage ;
- le clonage ne doit pas créer de cycle infini ;
- le clonage doit rester thread-safe.

## 10) CSV / JTable

- `getEnTeteCsv()` retourne exactement `idTypeProduit;type de produit;` ;
- `toStringCsv()` respecte cet ordre et utilise `null` textuel quand une valeur est absente ;
- `getEnTeteColonne(0)` retourne `idTypeProduit` ;
- `getEnTeteColonne(1)` retourne `type de produit` ;
- tout autre indice retourne `invalide` ;
- `getValeurColonne(0)` retourne l'identifiant en `String` ou `null` ;
- `getValeurColonne(1)` retourne le libellé ou `null` ;
- tout autre indice retourne `invalide`.

## 11) Tests de référence

`TypeProduitTest.java` contient 38 tests validés couvrant notamment :

- constructeurs ;
- `equals` / `hashCode` ;
- insensibilité à la casse ;
- thread-safety ;
- `toString` ;
- `compareTo` ;
- clonage ;
- rattachement / détachement ;
- méthodes internes d'ajout/retrait ;
- CSV ;
- JTable.

## 12) Interdictions

- ne jamais faire participer les enfants ou l'identifiant à l'égalité métier ;
- ne jamais casser la bidirectionnalité `TypeProduit <-> SousTypeProduit` ;
- ne jamais exposer la liste interne ;
- ne jamais supprimer les verrous déterministes ;
- ne jamais transformer le clonage profond en copie superficielle.

## 13) AUTONOMIE-TYPEPRODUIT-01 — Fiche de recodage autonome

Cette section complète les sections précédentes. Elle a priorité lorsqu'il faut recoder `TypeProduit` en autonomie quasiment à l'identique.

### 13.1 Structure exacte à reproduire

`TypeProduit` est une classe métier `public` qui implémente `TypeProduitI` et `Cloneable`.

Ordre structurel à conserver :

1. commentaire historique de fichier ;
2. `package levy.daniel.application.model.metier.produittype;` ;
3. imports ;
4. Javadoc de classe ;
5. déclaration `public class TypeProduit implements TypeProduitI, Cloneable` ;
6. constantes publiques historiques ;
7. verrou privé de collision d'`identityHashCode` ;
8. attributs métier ;
9. logger ;
10. constructeurs ;
11. méthodes métier générales ;
12. méthodes de relation parent/enfants ;
13. méthodes internes par identité ;
14. normalisation locale ;
15. CSV/JTable ;
16. getters/setters ;
17. verrouillage multi-objets ;
18. traitements des mauvaises instances.

### 13.2 Constantes et attributs contractuels

| Élément | Visibilité | Valeur / rôle obligatoire |
| --- | --- | --- |
| `NULL` | `public static final String` | valeur exacte `null` |
| `VIRGULE_ESPACE` | `public static final String` | valeur exacte `, ` |
| `CROCHET_FERMANT` | `public static final char` | valeur exacte `]` |
| `POINT_VIRGULE` | `public static final char` | valeur exacte `;` |
| `SAUT_DE_LIGNE` | `public static final String` | `System.getProperty("line.separator")` |
| `MAUVAISE_INSTANCE_METIER` | `public static final String` | `le SousTypeProduit passé en paramètre n'est pas de type Objet métier : `, construit par concaténation historique |
| `MAUVAISE_INSTANCE_ENFANT_METIER` | `public static final String` | même message historique que `MAUVAISE_INSTANCE_METIER` |
| `EN_TETE_TYPE_PRODUIT` | `public static final String` | valeur exacte `******* TypeProduit : ` |
| `EN_TETE_SOUS_TYPES_PRODUIT` | `public static final String` | valeur exacte `******* sousTypeProduits du TypeProduit : ` |
| `EN_TETE_LISTE_PRODUITS` | `public static final String` | valeur exacte `***** liste des produits dans le sousProduit : ` |
| `FORMAT_ID_TYPE_PRODUIT` | `public static final String` | format exact `[idTypeProduit : %-2s - typeProduit : %-10s]` |
| `FORMAT_SOUS_TYPE_PRODUIT` | `public static final String` | format historique multi-concaténé avec `idSousTypeProduit`, `sousTypeProduit`, `idTypeProduit du TypeProduit dans le SousTypeProduit` et `typeProduitString du TypeProduit dans le SousTypeProduit` |
| `FORMAT_PRODUIT` | `public static final String` | format historique multi-concaténé avec `idProduit dans produits du SousTypeProduit`, `produit dans produits du SousTypeProduit`, `sousTypeProduit dans le produit` |
| `VERROU_COLLISION_IDENTITY_HASHCODE` | `private static final Object` | verrou de départ unique lors des collisions rarissimes d'`identityHashCode` |
| `idTypeProduit` | `private Long` | identifiant du TypeProduit |
| `typeProduit` | `private String` | libellé métier du TypeProduit |
| `sousTypeProduits` | `private List<SousTypeProduitI>` | collection interne initialisée en `new ArrayList<SousTypeProduitI>()` |
| `LOG` | `private static final Logger` | `LogManager.getLogger(TypeProduit.class)` |

### 13.3 Ordre exact des méthodes validées

Ordre d'apparition à conserver :

1. `public TypeProduit()` ;
2. `public TypeProduit(String)` ;
3. `public TypeProduit(Long, String)` ;
4. `public TypeProduit(String, List<SousTypeProduitI>)` ;
5. `public TypeProduit(Long, String, List<SousTypeProduitI>)` ;
6. `public final int hashCode()` ;
7. `public final boolean equals(Object)` ;
8. `public final String toString()` ;
9. `public final String afficherTypeProduit()` ;
10. `public final String afficherTypeProduitFormate(TypeProduitI)` ;
11. `public final String afficherSousTypeProduits()` ;
12. `public final int compareTo(TypeProduitI)` ;
13. `private int compareFields(TypeProduitI)` ;
14. `public final TypeProduit clone() throws CloneNotSupportedException` ;
15. `private TypeProduit cloneDeep()` ;
16. `public final TypeProduit deepClone(CloneContext)` ;
17. `public final TypeProduit cloneWithoutChildren()` ;
18. `public final void rattacherEnfantSTP(SousTypeProduitI)` ;
19. `private void rattacherSiNecessaire(SousTypeProduitI)` ;
20. `public final void detacherEnfantSTP(SousTypeProduitI)` ;
21. `private void detacherSiNecessaire(SousTypeProduitI)` ;
22. `protected final void internalAddSousTypeProduit(SousTypeProduitI)` ;
23. `protected final void internalRemoveSousTypeProduit(SousTypeProduitI)` ;
24. `private boolean containsSousTypeProduitByReference(SousTypeProduitI)` ;
25. `private void removeSousTypeProduitByReference(SousTypeProduitI)` ;
26. `private static String normalize(String)` ;
27. `public final String getEnTeteCsv()` ;
28. `public final String toStringCsv()` ;
29. `public final String getEnTeteColonne(int)` ;
30. `public final Object getValeurColonne(int)` ;
31. `public final Long getIdTypeProduit()` ;
32. `public final void setIdTypeProduit(Long)` ;
33. `public final String getTypeProduit()` ;
34. `public final void setTypeProduit(String)` ;
35. `public final List<? extends SousTypeProduitI> getSousTypeProduits()` ;
36. `public final void setSousTypeProduits(List<? extends SousTypeProduitI>)` ;
37. `private static void executerSousVerrousDeterministes(Object, Object, Object, Runnable)` ;
38. `private void traiterMauvaiseInstanceDansListe(List<? extends SousTypeProduitI>)` ;
39. `private void traiterMauvaiseInstanceSousTypeProduit(SousTypeProduitI)`.

### 13.4 Algorithmes sensibles à reproduire

#### Constructeur complet

Le constructeur complet doit :

- appeler `super()` ;
- copier `pIdTypeProduit` et `pTypeProduit` ;
- initialiser `this.sousTypeProduits` avec une nouvelle `ArrayList<>()` ;
- ne jamais conserver directement la liste reçue ;
- si la liste reçue est non `null`, rattacher chaque enfant via `rattacherEnfantSTP(...)`.

#### `hashCode()` et `equals(...)`

`hashCode()` lit `typeProduit` par snapshot court sous verrou et retourne `0` si le libellé est `null`, sinon `typeProduit.toLowerCase(Locale.ROOT).hashCode()`.

`equals(...)` :

- retourne `true` sur référence identique ;
- retourne `false` sur `null` ;
- retourne `false` si l'objet comparé n'est pas `TypeProduit` ;
- compare uniquement `typeProduit`, insensible à la casse ;
- ne tient compte ni de l'identifiant ni des enfants ;
- verrouille `this` et `other` dans l'ordre `System.identityHashCode(...)` ;
- utilise `TypeProduit.class` comme verrou de départ en cas de collision d'`identityHashCode`.

#### `compareTo(...)` et `compareFields(...)`

`compareTo(null)` retourne `-1` selon le comportement validé de `TypeProduit`. La comparaison porte uniquement sur `typeProduit`, avec gestion déterministe des `null` et comparaison insensible à la casse via `Strings.CI.compare(...)` lorsque les deux libellés existent.

#### Relations parent/enfants

`rattacherEnfantSTP(...)` doit :

- traiter la mauvaise instance ;
- ignorer `null` ;
- déléguer au setter canonique de l'enfant ;
- éviter tout ajout direct qui casserait la bidirectionnalité ;
- gérer le re-parenting par l'enfant.

`detacherEnfantSTP(...)` doit :

- traiter la mauvaise instance ;
- ignorer `null` ;
- détacher uniquement si le parent actuel de l'enfant est `this` ;
- déléguer à `SousTypeProduit.setTypeProduit(null)`.

`internalAddSousTypeProduit(...)` et `internalRemoveSousTypeProduit(...)` sont des méthodes internes protégées utilisées par `SousTypeProduit`. Elles doivent :

- conserver le traitement des mauvaises instances ;
- ignorer `null` ;
- travailler par identité de référence ;
- ne jamais appeler `contains(...)` ni `remove(Object)` ;
- ne jamais appeler le setter canonique de l'enfant.

`containsSousTypeProduitByReference(...)` et `removeSousTypeProduitByReference(...)` parcourent la liste interne et comparent avec `==`.

#### `setSousTypeProduits(...)`

`setSousTypeProduits(...)` doit :

- faire un snapshot défensif du paramètre si non `null` ;
- traiter les mauvaises instances avant modification ;
- prendre un snapshot des enfants actuels sous verrou ;
- détacher les enfants actuels via `stp.setTypeProduit(null)` sous verrouillage déterministe ;
- retourner après détachement si la nouvelle liste est `null` ;
- ignorer les éléments `null` ;
- rattacher chaque nouvel enfant via `stp.setTypeProduit(TypeProduit.this)` ;
- verrouiller ancien parent éventuel, nouveau parent et enfant par `executerSousVerrousDeterministes(...)`.

#### `executerSousVerrousDeterministes(...)`

Cette méthode doit :

- accepter jusqu'à trois verrous et une action ;
- éliminer les verrous `null` ;
- exécuter directement l'action si aucun verrou n'est fourni ;
- trier les verrous par `System.identityHashCode(...)` croissant ;
- détecter une collision d'`identityHashCode` entre objets distincts ;
- prendre `VERROU_COLLISION_IDENTITY_HASHCODE` avant les autres verrous en cas de collision ;
- exécuter l'action sous un, deux ou trois verrous selon le nombre réel de verrous ;
- conserver l'appel effectif à `Runnable.run()` uniquement une fois que les verrous nécessaires sont pris.

### 13.5 CSV/JTable exacts

- `getEnTeteCsv()` : `idTypeProduit;type de produit;` ;
- `toStringCsv()` : identifiant puis libellé, séparés par `;`, avec `null` textuel si valeur absente ;
- `getEnTeteColonne(0)` : `idTypeProduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(0)` : identifiant converti en `String` ou `null` ;
- `getValeurColonne(1)` : libellé ou `null` ;
- autre indice : `invalide`.

### 13.6 Tests validés à conserver comme spécification

Les tests `TypeProduitTest.java` validés sont :

1. `testConstructeurNull()` ;
2. `testEquals()` ;
3. `testEqualsIgnoreCase()` ;
4. `testEqualsHashCodeThreadSafe()` ;
5. `testToString()` ;
6. `testToStringThreadSafe()` ;
7. `testAfficherSousTypeProduits()` ;
8. `testCompareTo()` ;
9. `testCompareToThreadSafe()` ;
10. `testClone()` ;
11. `testCloneWithoutChildren()` ;
12. `testCloneThreadSafe()` ;
13. `testAjouterEtRetirerSousTypeProduit()` ;
14. `testRattacherEnfantSTPCasLimitesMonoThread()` ;
15. `testRattacherEnfantSTPThreadSafe()` ;
16. `testDetacherEnfantSTPCasLimitesMonoThread()` ;
17. `testDetacherEnfantSTPThreadSafe()` ;
18. `testInternalAddSousTypeProduitMonoThread()` ;
19. `testInternalAddEtRemoveSousTypeProduit()` ;
20. `testInternalAddSousTypeProduitThreadSafe()` ;
21. `testInternalRemoveSousTypeProduitNull()` ;
22. `testInternalRemoveSousTypeProduitAbsent()` ;
23. `testInternalRemoveSousTypeProduitMauvaiseInstance()` ;
24. `testInternalRemoveSousTypeProduitThreadSafe()` ;
25. `testGetEnTeteCsv()` ;
26. `testToStringCsv()` ;
27. `testToStringCsvCasMixtes()` ;
28. `testToStringCsvThreadSafe()` ;
29. `testGetEnTeteColonne()` ;
30. `testGetEnTeteColonneIndexNegatif()` ;
31. `testGetValeurColonne()` ;
32. `testGetValeurColonneCasMixtes()` ;
33. `testGetValeurColonneThreadSafe()` ;
34. `testGetSousTypeProduits()` ;
35. `testGetSousTypeProduitsThreadSafe()` ;
36. `testSetSousTypeProduits()` ;
37. `testSetSousTypeProduitsIgnoreNullElements()` ;
38. `testSetSousTypeProduitsThreadSafe()`.

Tests d'intégration à relire en complément dès qu'une relation `TypeProduit <-> SousTypeProduit` est touchée :

- `testIntegrationBidirectionnelleNominale()` ;
- `testIntegrationReParentingNominal()` ;
- `testSetSousTypeProduitsThreadSafe()`.

Tests globaux à relire dès qu'un clonage profond ou une relation complète est touché :

- `testInvariantMetierGlobalCoherenceLiens()` ;
- `testCloneProfondCompletProduit()` ;
- `testAntiDeadlockSousTypeProduitProduit()`.
