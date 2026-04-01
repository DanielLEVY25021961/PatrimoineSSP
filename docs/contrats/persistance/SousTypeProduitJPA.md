# Contrat local — SousTypeProduitJPA

## 1) Entity concernée

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java`

## 2) Rôle de l'entity

`SousTypeProduitJPA` est l'entity JPA intermédiaire du domaine de persistance.

Elle :
- implémente `SousTypeProduitI` ;
- implémente `Cloneable` et `Serializable` ;
- est annotée `@Entity(name = "SousTypeProduitJPA")` ;
- est mappée en accès champ (`@Access(AccessType.FIELD)`).

## 3) Structure persistée à retenir

### 3.1) Identifiant et libellé

- `idSousTypeProduit` : clé primaire JPA (`@Id`, `@GeneratedValue`, `@Column(name="ID_SOUS_TYPE_PRODUIT")`)
- `sousTypeProduit` : libellé persistant non nul (`@Column(..., nullable = false)`)

### 3.2) Relation parent

L'entity porte un parent :
- `TypeProduitI typeProduit`

Cette relation est mappée en :
- `@ManyToOne`
- `fetch = FetchType.LAZY`
- `optional = false`
- `@JoinColumn(name = "TYPE_PRODUIT", referencedColumnName = "ID_TYPE_PRODUIT")`

### 3.3) Relation enfant

L'entity porte une collection :
- `List<ProduitI> produits`

Cette relation est mappée en :
- `@OneToMany`
- `cascade = CascadeType.ALL`
- `orphanRemoval = true`
- `fetch = FetchType.LAZY`
- `mappedBy = "sousTypeProduit"`

## 4) Comportements importants à relire

### 4.1) Méthodes d'identité et d'ordre

L'entity redéfinit :
- `hashCode()`
- `equals(Object)`
- `compareTo(SousTypeProduitI)`
- `toString()`

### 4.2) Clonage et stabilisation des relations

L'entity expose notamment :
- `clone()`
- `deepClone(CloneContext)`
- `cloneWithoutParentAndChildren()`
- `ajouterSTPauProduit(ProduitI)`
- `retirerSTPauProduit(ProduitI)`
- `internalAddProduit(ProduitI)`
- `internalRemoveProduit(ProduitI)`

### 4.3) Export

L'entity expose aussi :
- `getEnTeteCsv()`
- `toStringCsv()`

Ces méthodes sont prévues comme non persistées (`@Transient` quand nécessaire côté JPA).

## 5) Invariants techniques

- un `SousTypeProduitJPA` porte un parent `TypeProduitJPA` obligatoire ;
- la collection `produits` est la relation enfant canonique ;
- la couche persistance verrouille la cohérence parent / enfants ;
- cette entity ne doit pas dériver vers les DTO ni vers la logique UC.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/persistance/ProduitJPA.md`
- `docs/contrats/persistance/ConvertisseurJPAToMetier.md`
- `docs/contrats/persistance/ConvertisseurMetierToJPA.md`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPATest.java`