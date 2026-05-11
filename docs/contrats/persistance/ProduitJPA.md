<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — ProduitJPA

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java
```

Source validée : `d0d50edb7bc71e1d35e7145686f88cf4e7b645546caa2ef998bfa2bee50a2073` — 1161 lignes LF.

## 2) Rôle exact

Entity JPA feuille. Elle porte un parent `SousTypeProduitI`, expose indirectement le grand-parent `TypeProduitI` et le libellé `produit`.

Le fichier doit rester une entity JPA de persistance, pas un DTO, pas un objet UC et pas un Gateway.

## 3) Annotations JPA obligatoires

Annotation de classe : `@Table(name = "PRODUITS", schema = "PUBLIC", indexes = {@Index(name = "INDEX_PRODUIT_SOUS_TYPE_PRODUIT", columnList = "PRODUIT ASC, SOUS_TYPE_PRODUIT ASC", unique = true)})`

Annotations et mappings de tête à conserver :

- ligne 134 : @Entity(name = "ProduitJPA")
- ligne 135 : @Access(AccessType.FIELD)
- ligne 136 : @Table(name = "PRODUITS", schema = "PUBLIC" , indexes = {@Index(name = "INDEX_PRODUIT_SOUS_TYPE_PRODUIT" , columnList = "PRODUIT ASC, SOUS_TYPE_PRODUIT ASC", unique = true)})
- ligne 195 : 	@Id
- ligne 196 : 	@GeneratedValue(strategy=GenerationType.IDENTITY)
- ligne 197 : 	@Column(name="ID_PRODUIT")
- ligne 215 : 	@Column(name = "PRODUIT" 			, unique = false, updatable = true 			, insertable = true, nullable = false)
- ligne 230 : 	@ManyToOne(fetch = FetchType.LAZY 	, optional = false 	, targetEntity = SousTypeProduitJPA.class)
- ligne 233 : 	@JoinColumn(name = "SOUS_TYPE_PRODUIT" 	, nullable = false 	, referencedColumnName = "ID_SOUS_TYPE_PRODUIT" 	, foreignKey = @ForeignKey(name="FK_SOUS_TYPE_PRODUIT"))

Toute modification d'annotation doit être justifiée par le code validé et les tests. L'IA ne doit pas simplifier les mappings ni remplacer les interfaces d'attribut par des classes concrètes.

## 4) Constantes et attributs structurants

Constantes / éléments statiques à conserver :

- ligne 146 : `private static final long serialVersionUID = 1L;`
- ligne 152 : `public static final String NULL = "null";`
- ligne 158 : `public static final String VIRGULE_ESPACE = ", ";`
- ligne 164 : `public static final char CROCHET_FERMANT = ']';`
- ligne 170 : `public static final char POINT_VIRGULE = ';';`
- ligne 176 : `public static final String MAUVAISE_INSTANCE_ENFANT_JPA = "le SousTypeProduit passé en paramètre " + "n'est pas de type Entity JPA : ";`
- ligne 183 : `public static final String ENTETECSV = "idproduit;type de produit;sous-type de produit;produit;";`
- ligne 269 : `private static final Logger LOG = LogManager.getLogger(ProduitJPA.class);`

Attributs métier/JPA à conserver :

- `idProduit` : `Long`, ID JPA, colonne `ID_PRODUIT`.
- `produit` : `String`, colonne `PRODUIT`, non nullable.
- `sousTypeProduit` : `SousTypeProduitI`, parent LAZY obligatoire, `targetEntity = SousTypeProduitJPA.class`, FK `FK_SOUS_TYPE_PRODUIT`.
- `valide` : booléen transient recalculé.
- `LOG` : logger Log4j.

Les constantes de message de mauvaise instance doivent rester centralisées et être réutilisées par les helpers dédiés.

## 5) Ordre exact des méthodes

L'ordre suivant est sacralisé. L'IA doit le reprendre quand elle recode le fichier complet.

1. ligne 280 : `public ProduitJPA()`
2. ligne 293 : `public ProduitJPA(final String pProduit)`
3. ligne 308 : `public ProduitJPA(final String pProduit , final SousTypeProduitI pSousTypeProduit)`
4. ligne 346 : `public ProduitJPA(final Long pIdProduit , final String pProduit , final SousTypeProduitI pSousTypeProduit)`
5. ligne 374 : `public final int hashCode()`
6. ligne 426 : `public final boolean equals(final Object pObject)`
7. ligne 494 : `public final String toString()`
8. ligne 540 : `public String afficherProduit()`
9. ligne 564 : `public final int compareTo(final ProduitI pObject)`
10. ligne 600 : `private int compareFields(final ProduitI pObject)`
11. ligne 651 : `public final ProduitJPA clone() throws CloneNotSupportedException`
12. ligne 683 : `private ProduitJPA cloneDeep()`
13. ligne 693 : `public final ProduitJPA deepClone(final CloneContext ctx)`
14. ligne 732 : `public final ProduitJPA cloneWithoutParent()`
15. ligne 761 : `private void recalculerValide()`
16. ligne 787 : `private static String normalize(final String pString)`
17. ligne 817 : `public final String getEnTeteCsv()`
18. ligne 842 : `public final String toStringCsv()`
19. ligne 902 : `public final String getEnTeteColonne( final int pI)`
20. ligne 947 : `public final Object getValeurColonne( final int pI)`
21. ligne 1003 : `public final TypeProduitI getTypeProduit()`
22. ligne 1015 : `public final boolean isValide()`
23. ligne 1025 : `public Long getIdProduit()`
24. ligne 1035 : `public void setIdProduit(final Long pIdProduit)`
25. ligne 1045 : `public String getProduit()`
26. ligne 1055 : `public void setProduit(final String pProduit)`
27. ligne 1070 : `public SousTypeProduitI getSousTypeProduit()`
28. ligne 1080 : `public void setSousTypeProduit(final SousTypeProduitI pSousTypeProduit)`
29. ligne 1135 : `private void traiterMauvaiseInstanceSousTypeProduit( final SousTypeProduitI pSousTypeProduit)`

## 6) Règles de comportement

### 6.1 Égalité, hashCode et comparaison

L'égalité privilégie `idProduit` quand les deux IDs sont non null. Sans deux IDs exploitables, elle repose sur le parent `SousTypeProduit` et le libellé `produit` normalisé. Le grand-parent n'est jamais comparé directement.

`hashCode()` doit rester compatible avec les tests de stabilité autour de l'ID persistant. `compareTo(...)` compare dans l'ordre métier validé et doit rester insensible à la casse via la normalisation validée.

### 6.2 Normalisation

`normalize(...)` est privé et statique. Il applique `StringUtils.trimToEmpty(pString).toLowerCase(Locale.ROOT)`. Les setters de libellé utilisent cette normalisation. Ne pas remplacer par `trim()` seul ni par une locale implicite.

### 6.3 Relations bidirectionnelles

- `setSousTypeProduit(...)` vérifie `SousTypeProduitJPA`, détache l'ancien parent si nécessaire, rattache au nouveau parent via primitive interne et recalcule `valide`.
- `getTypeProduit()` retourne le parent du parent si le parent existe, sinon `null`.
- `cloneWithoutParent()` conserve les scalaires et supprime la relation parent.
- `traiterMauvaiseInstanceSousTypeProduit(...)` centralise le rejet des parents non JPA.

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

- `ProduitJPA.testConstructeurNull`
- `ProduitJPA.testEquals`
- `ProduitJPA.testEqualsMemeLibelleParentsDifferentsSansIds`
- `ProduitJPA.testEqualsIdFirstIdsDifferentsMemeMetier`
- `ProduitJPA.testDeepCloneAvecCloneContext`
- `ProduitJPA.testCompareToParentNullVsNonNull`
- `ProduitJPA.testRecalculerValideViaSetters`
- `ProduitJPA.testToString`
- `ProduitJPA.testToStringAvecNull`
- `ProduitJPA.testCompareTo`
- `ProduitJPA.testClone`
- `ProduitJPA.testCloneWithoutParent`
- `ProduitJPA.testGetEnTeteCsv`
- `ProduitJPA.testToStringCsv`
- `ProduitJPA.testGetEnTeteColonne`
- `ProduitJPA.testGetValeurColonne`
- `ProduitJPA.testGeneral`
- `ProduitJPA.testHashSetTransientThenPersistIdChangesHashCode`
- `ProduitJPA.testToStringDoesNotExplodeGraph`
- `ProduitJPA.testSetProduitNormalize`
- `ProduitJPA.testSetSousTypeProduitBidirectionnel`
- `ProduitJPA.testCloneAvecSousTypeEtTypeProduit`
- `ProduitJPA.testGetTypeProduitViaSousTypeProduit`

Avant toute modification, l'IA doit relire ces tests. Chaque nom de test est une spécification du comportement attendu.

## 8) Points anti-improvisation

- Ne pas supprimer les helpers privés `compareFields`, `cloneDeep`, `normalize` et les helpers de mauvaise instance.
- Ne pas remplacer les collections typées interface par des collections typées classe concrète.
- Ne pas remplacer les setters relationnels par une affectation directe.
- Ne pas supprimer `@Transient` sur les méthodes calculées.
- Ne pas supprimer les commentaires didactiques de verrouillage, de clonage, de relation ou de CSV/JTable.

## 9) Critère d'autonomie

Ce contrat est suffisant pour guider l'IA à condition qu'elle relise aussi le fichier Java validé et les tests ci-dessus avant de coder. L'objectif est un recodage quasi identique, pas une simplification fonctionnelle.
