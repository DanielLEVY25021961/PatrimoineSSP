<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — SousTypeProduitJPA

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java
```

Source validée : `33711c220f009c2973cae4a0a991d4c2a237b83ffbb0e5ede4cbcf9185b72003` — 1455 lignes LF.

## 2) Rôle exact

Entity JPA intermédiaire. Elle porte un parent `TypeProduitI`, une collection enfant `produits` et le libellé objet métier `sousTypeProduit`.

Le fichier doit rester une entity JPA de persistance, pas un DTO, pas un objet UC et pas un Gateway.

## 3) Annotations JPA obligatoires

Annotation de classe : `@Table(name = "SOUS_TYPES_PRODUIT", schema = "PUBLIC", uniqueConstraints = @UniqueConstraint(name = "UNICITE_TYPE_PRODUIT-SOUS_TYPE_PRODUIT", columnNames = {"SOUS_TYPE_PRODUIT", "TYPE_PRODUIT"}), indexes = @Index(name = "INDEX_TYPE_PRODUIT-SOUS_TYPE_PRODUIT", columnList = "SOUS_TYPE_PRODUIT ASC, TYPE_PRODUIT ASC", unique = true))`

Annotations et mappings de tête à conserver :

- ligne 114 : @Entity(name = "SousTypeProduitJPA")
- ligne 115 : @Access(AccessType.FIELD)
- ligne 116 : @Table(name = "SOUS_TYPES_PRODUIT", schema = "PUBLIC" , uniqueConstraints = @UniqueConstraint( 		name = "UNICITE_TYPE_PRODUIT-SOUS_TYPE_PRODUIT" 		, columnNames = {"SOUS_TYPE_PRODUIT", "TYPE_PRODUIT"}) , indexes = @Index(name = "INDEX_TYPE_PRODUIT-SOUS_TYPE_PRODUIT" , columnList = "SOUS_TYPE_PRODUIT ASC, TYPE_PRODUIT ASC", unique = true))
- ligne 178 : 	@Id
- ligne 179 : 	@GeneratedValue(strategy=GenerationType.IDENTITY)
- ligne 180 : 	@Column(name="ID_SOUS_TYPE_PRODUIT")
- ligne 193 : 	@Column(name = "SOUS_TYPE_PRODUIT" 			, unique = false, updatable = true 			, insertable = true, nullable = false)
- ligne 208 : 	@ManyToOne(fetch = FetchType.LAZY 			, optional = false 			, targetEntity = TypeProduitJPA.class)
- ligne 211 : 	@JoinColumn(name = "TYPE_PRODUIT" 	, nullable = false 	, referencedColumnName = "ID_TYPE_PRODUIT" 	, foreignKey = @ForeignKey(name="FK_TYPE_PRODUIT"))
- ligne 231 : 	@OneToMany(targetEntity = ProduitJPA.class 			, cascade = CascadeType.ALL 			, orphanRemoval = true 			, fetch = FetchType.LAZY 			, mappedBy = "sousTypeProduit")

Toute modification d'annotation doit être justifiée par le code validé et les tests. L'IA ne doit pas simplifier les mappings ni remplacer les interfaces d'attribut par des classes concrètes.

## 4) Constantes et attributs structurants

Constantes / éléments statiques à conserver :

- ligne 128 : `private static final long serialVersionUID = 1L;`
- ligne 133 : `public static final String NULL = "null";`
- ligne 139 : `public static final String VIRGULE_ESPACE = ", ";`
- ligne 145 : `public static final char CROCHET_FERMANT = ']';`
- ligne 151 : `public static final char POINT_VIRGULE = ';';`
- ligne 157 : `public static final String MAUVAISE_INSTANCE_PARENT_JPA = "Le pTypeProduit passé en paramètre " + "n'est pas de type Entity JPA : ";`
- ligne 165 : `public static final String MAUVAISE_INSTANCE_PETIT_ENFANT_JPA = "Le pProduit passé en paramètre " + "n'est pas de type Entity JPA : ";`
- ligne 268 : `private static final Logger LOG = LogManager.getLogger(SousTypeProduitJPA.class);`

Attributs métier/JPA à conserver :

- `idSousTypeProduit` : `Long`, ID JPA, colonne `ID_SOUS_TYPE_PRODUIT`.
- `sousTypeProduit` : `String`, colonne `SOUS_TYPE_PRODUIT`, non nullable.
- `typeProduit` : `TypeProduitI`, parent LAZY obligatoire, `targetEntity = TypeProduitJPA.class`, FK `FK_TYPE_PRODUIT`.
- `produits` : `List<ProduitI>`, enfants LAZY, cascade all, orphan removal, mappedBy `sousTypeProduit`.
- `valide` : booléen transient recalculé.
- `LOG` : logger Log4j.

Les constantes de message de mauvaise instance doivent rester centralisées et être réutilisées par les helpers dédiés.

## 5) Ordre exact des méthodes

L'ordre suivant est sacralisé. L'IA doit le reprendre quand elle recode le fichier complet.

1. ligne 280 : `public SousTypeProduitJPA()`
2. ligne 293 : `public SousTypeProduitJPA(final String pSousTypeProduit)`
3. ligne 308 : `public SousTypeProduitJPA(final String pSousTypeProduit , final TypeProduitI pTypeProduit)`
4. ligne 324 : `public SousTypeProduitJPA(final Long pIdSousTypeProduit , final String pSousTypeProduit , final TypeProduitI pTypeProduit)`
5. ligne 343 : `public SousTypeProduitJPA( final String pSousTypeProduit , final TypeProduitI pTypeProduit , final List<ProduitI> pProduits)`
6. ligne 373 : `public SousTypeProduitJPA(final Long pIdSousTypeProduit , final String pSousTypeProduit , final TypeProduitI pTypeProduit , final List<ProduitI> pProduits)`
7. ligne 421 : `public final int hashCode()`
8. ligne 474 : `public final boolean equals(final Object pObject)`
9. ligne 542 : `public final String toString()`
10. ligne 588 : `public final String afficherSousTypeProduit()`
11. ligne 611 : `public final int compareTo(final SousTypeProduitI pObject)`
12. ligne 648 : `private int compareFields(final SousTypeProduitI pObject)`
13. ligne 693 : `public final SousTypeProduitJPA clone() throws CloneNotSupportedException`
14. ligne 726 : `private SousTypeProduitJPA cloneDeep()`
15. ligne 736 : `public final SousTypeProduitJPA deepClone(final CloneContext ctx)`
16. ligne 811 : `public final SousTypeProduitJPA cloneWithoutParentAndChildren()`
17. ligne 839 : `private void recalculerValide()`
18. ligne 867 : `private static String normalize(final String pString)`
19. ligne 897 : `public final String getEnTeteCsv()`
20. ligne 922 : `public final String toStringCsv()`
21. ligne 985 : `public final String getEnTeteColonne( final int pI)`
22. ligne 1025 : `public final Object getValeurColonne( final int pI)`
23. ligne 1073 : `public final void ajouterSTPauProduit(final ProduitI pProduit)`
24. ligne 1095 : `public final void retirerSTPauProduit(final ProduitI pProduit)`
25. ligne 1127 : `protected final void internalAddProduit(final ProduitI pProduit)`
26. ligne 1156 : `protected final void internalRemoveProduit(final ProduitI pProduit)`
27. ligne 1175 : `public final boolean isValide()`
28. ligne 1185 : `public Long getIdSousTypeProduit()`
29. ligne 1195 : `public void setIdSousTypeProduit( final Long pIdSousTypeProduit)`
30. ligne 1206 : `public String getSousTypeProduit()`
31. ligne 1219 : `public void setSousTypeProduit(final String pSousTypeProduit)`
32. ligne 1233 : `public TypeProduitI getTypeProduit()`
33. ligne 1243 : `public void setTypeProduit(final TypeProduitI pTypeProduit)`
34. ligne 1279 : `public List<? extends ProduitI> getProduits()`
35. ligne 1289 : `public void setProduits(final List<? extends ProduitI> pProduits)`
36. ligne 1334 : `private void traiterMauvaiseInstanceTypeProduit( final TypeProduitI pTypeProduit)`
37. ligne 1378 : `private void traiterMauvaiseInstanceProduit( final ProduitI pProduit)`
38. ligne 1423 : `private void traiterMauvaiseInstanceDansListeProduits( final List<? extends ProduitI> pProduits)`

## 6) Règles de comportement

### 6.1 Égalité, hashCode et comparaison

L'égalité privilégie `idSousTypeProduit` quand les deux IDs sont non null. Sans deux IDs exploitables, elle repose sur le parent `TypeProduit` et le libellé `sousTypeProduit` normalisé.

`hashCode()` doit rester compatible avec les tests de stabilité autour de l'ID persistant. `compareTo(...)` compare dans l'ordre métier validé et doit rester insensible à la casse via la normalisation validée.

### 6.2 Normalisation

`normalize(...)` est privé et statique. Il applique `StringUtils.trimToEmpty(pString).toLowerCase(Locale.ROOT)`. Les setters de libellé utilisent cette normalisation. Ne pas remplacer par `trim()` seul ni par une locale implicite.

### 6.3 Relations bidirectionnelles

- `setTypeProduit(...)` vérifie l'implémentation `TypeProduitJPA`, détache l'ancien parent, rattache le nouveau parent par primitive interne et recalcule `valide`.
- `setProduits(...)` nettoie l'ancienne relation, vérifie chaque `ProduitI`, rattache via setter canonique enfant et recalcule `valide`.
- `ajouterSTPauProduit(...)` / `retirerSTPauProduit(...)` sont les opérations publiques sur enfants produits et doivent rester idempotentes.
- `internalAddProduit(...)` et `internalRemoveProduit(...)` sont les primitives internes protégées sans cascade relationnelle supplémentaire.

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

- `SousTypeProduitJPA.testGeneral`
- `SousTypeProduitJPA.testSousTypeProduit`
- `SousTypeProduitJPA.testSousTypeProduitString`
- `SousTypeProduitJPA.testConstructeurs`
- `SousTypeProduitJPA.testEquals`
- `SousTypeProduitJPA.testEqualsCaseInsensitiveSurLibelle`
- `SousTypeProduitJPA.testEqualsFallbackFinalCaseInsensitive`
- `SousTypeProduitJPA.testEqualsMemeLibelleParentsDifferentsSansIds`
- `SousTypeProduitJPA.testEqualsIdFirstIdsDifferentsMemeMetier`
- `SousTypeProduitJPA.testCompareToCaseInsensitive`
- `SousTypeProduitJPA.testValideDevientInvalideApresSetters`
- `SousTypeProduitJPA.testToString`
- `SousTypeProduitJPA.testCompareTo`
- `SousTypeProduitJPA.testClone`
- `SousTypeProduitJPA.testDeepCloneAvecCloneContext`
- `SousTypeProduitJPA.testGetEnTeteCsv`
- `SousTypeProduitJPA.testToStringCsv`
- `SousTypeProduitJPA.testGetEnTeteColonne`
- `SousTypeProduitJPA.testGetValeurColonne`
- `SousTypeProduitJPA.testAjouterRetirerProduit`
- `SousTypeProduitJPA.testCsvAvecProduits`
- `SousTypeProduitJPA.testSetProduits`
- `SousTypeProduitJPA.testGetProduitsImmuable`
- `SousTypeProduitJPA.testSetSousTypeProduitNormalize`
- `SousTypeProduitJPA.testSetTypeProduitBidirectionnel`
- `SousTypeProduitJPA.testCloneAvecProduits`

Avant toute modification, l'IA doit relire ces tests. Chaque nom de test est une spécification du comportement attendu.

## 8) Points anti-improvisation

- Ne pas supprimer les helpers privés `compareFields`, `cloneDeep`, `normalize` et les helpers de mauvaise instance.
- Ne pas remplacer les collections typées interface par des collections typées classe concrète.
- Ne pas remplacer les setters relationnels par une affectation directe.
- Ne pas supprimer `@Transient` sur les méthodes calculées.
- Ne pas supprimer les commentaires didactiques de verrouillage, de clonage, de relation ou de CSV/JTable.

## 9) Critère d'autonomie

Ce contrat est suffisant pour guider l'IA à condition qu'elle relise aussi le fichier Java validé et les tests ci-dessus avant de coder. L'objectif est un recodage quasi identique, pas une simplification fonctionnelle.
