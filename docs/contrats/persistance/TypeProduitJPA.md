<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — TypeProduitJPA

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java
```

Source validée : `b23e27556e450a09e92dd9e443f92eb7ab0eff7d9324ca03605a35da15d1450c` — 1409 lignes LF.

## 2) Rôle exact

Entity JPA racine de la hiérarchie persistance `produittype`. Elle représente le parent `TypeProduit` et porte la collection enfant `sousTypeProduits`.

Le fichier doit rester une entity JPA de persistance, pas un DTO, pas un objet UC et pas un Gateway.

## 3) Annotations JPA obligatoires

Annotation de classe : `@Table(name = "TYPES_PRODUIT", schema = "PUBLIC", uniqueConstraints = @UniqueConstraint(name = "UNICITE_TYPE_PRODUIT", columnNames = {"TYPE_PRODUIT"}), indexes = {@Index(name = "INDEX_TYPE_PRODUIT", columnList = "TYPE_PRODUIT")})`

Annotations et mappings de tête à conserver :

- ligne 134 : @Entity(name = "TypeProduitJPA")
- ligne 135 : @Access(AccessType.FIELD)
- ligne 136 : @Table(name = "TYPES_PRODUIT", schema = "PUBLIC" , uniqueConstraints = @UniqueConstraint( 		name = "UNICITE_TYPE_PRODUIT", columnNames = {"TYPE_PRODUIT"}) , indexes = {@Index(name = "INDEX_TYPE_PRODUIT", columnList = "TYPE_PRODUIT")})
- ligne 204 : 	@Id
- ligne 205 : 	@GeneratedValue(strategy=GenerationType.IDENTITY)
- ligne 206 : 	@Column(name="ID_TYPE_PRODUIT")
- ligne 215 : 	@Column(name = TYPE_PRODUIT 			, unique = false, updatable = true 			, insertable = true, nullable = false)
- ligne 232 : 	@OneToMany(targetEntity = SousTypeProduitJPA.class 			, cascade = CascadeType.ALL 			, orphanRemoval = true 			, fetch=FetchType.LAZY 			, mappedBy="typeProduit")

Toute modification d'annotation doit être justifiée par le code validé et les tests. L'IA ne doit pas simplifier les mappings ni remplacer les interfaces d'attribut par des classes concrètes.

## 4) Constantes et attributs structurants

Constantes / éléments statiques à conserver :

- ligne 147 : `private static final long serialVersionUID = 1L;`
- ligne 152 : `public static final String NULL = "null";`
- ligne 157 : `public static final String VIRGULE_ESPACE = ", ";`
- ligne 162 : `public static final char CROCHET_FERMANT = ']';`
- ligne 167 : `public static final char POINT_VIRGULE = ';';`
- ligne 172 : `public static final String SAUT_DE_LIGNE = System.getProperty("line.separator");`
- ligne 178 : `public static final String TYPE_PRODUIT = "TYPE_PRODUIT";`
- ligne 184 : `public static final String MAUVAISE_INSTANCE_JPA = "le SousTypeProduit dans la liste passée en paramètre " + "n'est pas de type Entity JPA : ";`
- ligne 192 : `public static final String MAUVAISE_INSTANCE_ENFANT_JPA = "le SousTypeProduit passé en paramètre " + "n'est pas de type Entity JPA : ";`
- ligne 253 : `private static final Logger LOG = LogManager.getLogger(TypeProduitJPA.class);`

Attributs métier/JPA à conserver :

- `idTypeProduit` : `Long`, ID JPA, colonne `ID_TYPE_PRODUIT`.
- `typeProduit` : `String`, colonne `TYPE_PRODUIT`, non nullable.
- `sousTypeProduits` : `List<SousTypeProduitI>`, `@OneToMany` LAZY, cascade all, orphan removal, mappedBy `typeProduit`.
- `LOG` : logger Log4j.

Les constantes de message de mauvaise instance doivent rester centralisées et être réutilisées par les helpers dédiés.

## 5) Ordre exact des méthodes

L'ordre suivant est sacralisé. L'IA doit le reprendre quand elle recode le fichier complet.

1. ligne 264 : `public TypeProduitJPA()`
2. ligne 278 : `public TypeProduitJPA(final String pTypeProduit)`
3. ligne 293 : `public TypeProduitJPA(final Long pIdTypeProduit , final String pTypeProduit)`
4. ligne 312 : `public TypeProduitJPA(final String pTypeProduit, final List<SousTypeProduitI> pSousTypeProduits)`
5. ligne 335 : `public TypeProduitJPA(final Long pIdTypeProduit , final String pTypeProduit , final List<SousTypeProduitI> pSousTypeProduits)`
6. ligne 369 : `public final int hashCode()`
7. ligne 405 : `public final boolean equals(final Object obj)`
8. ligne 463 : `public final String toString()`
9. ligne 499 : `public final String afficherTypeProduit()`
10. ligne 514 : `public final String afficherTypeProduitFormate( final TypeProduitI pTypeProduit)`
11. ligne 639 : `public final String afficherSousTypeProduits()`
12. ligne 674 : `public final int compareTo(final TypeProduitI pObject)`
13. ligne 701 : `private int compareFields(final TypeProduitI pObject)`
14. ligne 736 : `public final TypeProduitJPA clone() throws CloneNotSupportedException`
15. ligne 768 : `private TypeProduitJPA cloneDeep()`
16. ligne 778 : `public final TypeProduitJPA deepClone(final CloneContext ctx)`
17. ligne 833 : `public final TypeProduitJPA cloneWithoutChildren()`
18. ligne 854 : `public final void rattacherEnfantSTP( final SousTypeProduitI pEnfant)`
19. ligne 891 : `private void rattacherSiNecessaire(final SousTypeProduitI pEnfant)`
20. ligne 912 : `public final void detacherEnfantSTP( final SousTypeProduitI pEnfant)`
21. ligne 949 : `private void detacherSiNecessaire(final SousTypeProduitI pEnfant)`
22. ligne 984 : `protected final void internalAddSousTypeProduit( final SousTypeProduitI pSousTypeProduit)`
23. ligne 1027 : `protected final void internalRemoveSousTypeProduit( final SousTypeProduitI pSousTypeProduit)`
24. ligne 1060 : `private static String normalize(final String pString)`
25. ligne 1090 : `public final String getEnTeteCsv()`
26. ligne 1114 : `public final String toStringCsv()`
27. ligne 1152 : `public final String getEnTeteColonne(final int pI)`
28. ligne 1188 : `public final Object getValeurColonne(final int pI)`
29. ligne 1224 : `public Long getIdTypeProduit()`
30. ligne 1234 : `public void setIdTypeProduit(final Long pIdTypeProduit)`
31. ligne 1244 : `public String getTypeProduit()`
32. ligne 1257 : `public void setTypeProduit(final String pTypeProduit)`
33. ligne 1278 : `public List<? extends SousTypeProduitI> getSousTypeProduits()`
34. ligne 1288 : `public void setSousTypeProduits( final List<? extends SousTypeProduitI> pSousTypeProduits)`
35. ligne 1335 : `private void traiterMauvaiseInstanceDansListe( final List<? extends SousTypeProduitI> pSousTypeProduits)`
36. ligne 1381 : `private void traiterMauvaiseInstanceSousTypeProduit( final SousTypeProduitI pSousTypeProduit)`

## 6) Règles de comportement

### 6.1 Égalité, hashCode et comparaison

L'égalité privilégie `idTypeProduit` quand les deux IDs sont non null. Sans deux IDs exploitables, elle repose sur le libellé `typeProduit` normalisé et insensible à la casse.

`hashCode()` doit rester compatible avec les tests de stabilité autour de l'ID persistant. `compareTo(...)` compare dans l'ordre métier validé et doit rester insensible à la casse via la normalisation validée.

### 6.2 Normalisation

`normalize(...)` est privé et statique. Il applique `StringUtils.trimToEmpty(pString).toLowerCase(Locale.ROOT)`. Les setters de libellé utilisent cette normalisation. Ne pas remplacer par `trim()` seul ni par une locale implicite.

### 6.3 Relations bidirectionnelles

- `rattacherEnfantSTP(...)` ignore `null`, vérifie que l'enfant est `SousTypeProduitJPA`, ajoute par identité si absent, puis appelle le setter canonique enfant si le parent n'est pas déjà `this`.
- `detacherEnfantSTP(...)` ignore `null`, vérifie le type JPA, retire par identité si présent, puis détache côté enfant seulement si l'enfant pointe encore vers `this`.
- `internalAddSousTypeProduit(...)` et `internalRemoveSousTypeProduit(...)` sont les primitives internes protégées : elles ne doivent pas déclencher de cascade relationnelle supplémentaire.
- `setSousTypeProduits(...)` nettoie l'ancienne relation, vérifie chaque élément, rattache les nouveaux enfants via le setter canonique et interdit les mauvaises implémentations.

Les relations doivent être maintenues par les setters canoniques et les primitives internes prévues. L'IA ne doit pas écrire directement dans les collections pour contourner la logique validée.

### 6.4 Clonage

- `clone()` appelle le clonage profond historique validé.
- `cloneDeep()` privé initialise le `CloneContext` quand il existe.
- `deepClone(CloneContext ctx)` doit respecter le cache du contexte pour éviter les cycles.
- Les variantes `cloneWithout...` suppriment volontairement parent et/ou enfants selon le nom de la méthode.

### 6.5 CSV / JTable

Les méthodes CSV/JTable doivent conserver :

- ordre des colonnes validé ;
- retour `"invalide"` hors index ;
- annotation `@Transient` sur les getters calculés ;
- en-têtes et séparateurs historiques ;
- représentation de `null` conforme au code validé.

## 7) Tests qui verrouillent ce fichier

- `TypeProduitJPA.testConstructeurNull`
- `TypeProduitJPA.testEquals`
- `TypeProduitJPA.testToString`
- `TypeProduitJPA.testCompareTo`
- `TypeProduitJPA.testClone`
- `TypeProduitJPA.testCloneDeepGrapheComplet`
- `TypeProduitJPA.testCloneWithoutChildren`
- `TypeProduitJPA.testDeepCloneAvecCacheCloneContext`
- `TypeProduitJPA.testGetEnTeteCsv`
- `TypeProduitJPA.testToStringCsv`
- `TypeProduitJPA.testGetEnTeteColonne`
- `TypeProduitJPA.testGetValeurColonne`
- `TypeProduitJPA.testGetTypeProduit`
- `TypeProduitJPA.testSetIdTypeProduit`
- `TypeProduitJPA.testBetonTypeProduitJPAAvecSousTypeProduitJPA`
- `TypeProduitJPA.testGetSousTypeProduits`
- `TypeProduitJPA.testSetTypeProduitNormalisation`
- `TypeProduitJPA.testBetonGrapheComplet`
- `TypeProduitJPA.testSetSousTypeProduitsNull`
- `TypeProduitJPA.testSetSousTypeProduitsListe`
- `TypeProduitJPA.testGetSousTypeProduitsVueLive`
- `TypeProduitJPA.testFailFastMauvaiseInstance`
- `TypeProduitJPA.testGetSousTypeProduitsTypage`
- `TypeProduitJPA.testRattacherEnfantSTP`
- `TypeProduitJPA.testDetacherEnfantSTP`
- `TypeProduitJPARattachementDetachement.testRattachementSetterCanoniqueSansDoublon`
- `TypeProduitJPARattachementDetachement.testDetachementIdempotentParentEtEnfant`
- `TypeProduitJPARattachementDetachement.testBlankNullRetourSansEffet`

Avant toute modification, l'IA doit relire ces tests. Chaque nom de test est une spécification du comportement attendu.

## 8) Points anti-improvisation

- Ne pas supprimer les helpers privés `compareFields`, `cloneDeep`, `normalize` et les helpers de mauvaise instance.
- Ne pas remplacer les collections typées interface par des collections typées classe concrète.
- Ne pas remplacer les setters relationnels par une affectation directe.
- Ne pas supprimer `@Transient` sur les méthodes calculées.
- Ne pas supprimer les commentaires didactiques de verrouillage, de clonage, de relation ou de CSV/JTable.

## 9) Critère d'autonomie

Ce contrat est suffisant pour guider l'IA à condition qu'elle relise aussi le fichier Java validé et les tests ci-dessus avant de coder. L'objectif est un recodage quasi identique, pas une simplification fonctionnelle.
