# Contrat local — ProduitJPA

## 1) Entity concernée

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java`

## 2) Rôle de l'entity

`ProduitJPA` est l'entity JPA feuille du domaine de persistance.

Elle :
- implémente `ProduitI` ;
- implémente `Cloneable` et `Serializable` ;
- est annotée `@Entity(name = "ProduitJPA")` ;
- est mappée en accès champ (`@Access(AccessType.FIELD)`).

## 3) Structure persistée à retenir

### 3.1) Identifiant et libellé

- `idProduit` : clé primaire JPA (`@Id`, `@GeneratedValue`, `@Column(name="ID_PRODUIT")`)
- `produit` : libellé persistant non nul (`@Column(..., nullable = false)`)

### 3.2) Relation parent

L'entity porte un parent :
- `SousTypeProduitI sousTypeProduit`

Cette relation est mappée en :
- `@ManyToOne`
- `fetch = FetchType.LAZY`
- `optional = false`
- `@JoinColumn(name = "SOUS_TYPE_PRODUIT", referencedColumnName = "ID_SOUS_TYPE_PRODUIT")`

## 4) Comportements importants à relire

### 4.1) Méthodes d'identité et d'ordre

L'entity redéfinit :
- `hashCode()`
- `equals(Object)`
- `compareTo(ProduitI)`
- `toString()`

### 4.2) Clonage

L'entity expose notamment :
- `clone()`
- `deepClone(CloneContext)`
- `cloneWithoutParent()`

### 4.3) Export

L'entity expose aussi :
- `getEnTeteCsv()`
- `toStringCsv()`

Ces méthodes sont prévues comme non persistées (`@Transient` quand nécessaire côté JPA).

## 5) Invariants techniques

- un `ProduitJPA` porte un parent `SousTypeProduitJPA` obligatoire ;
- le booléen technique `valide` est calculé et non persisté ;
- cette entity ne doit pas dériver vers les DTO ni vers la logique UC.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/ConvertisseurJPAToMetier.md`
- `docs/contrats/persistance/ConvertisseurMetierToJPA.md`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPATest.java`