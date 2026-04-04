# Contrat local — TypeProduitJPA

## 1) Entity concernée

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java`

## 2) Rôle de l'entity

`TypeProduitJPA` est l'entity JPA racine du domaine de persistance pour les types de produit.

Elle :
- implémente `TypeProduitI` ;
- implémente `Cloneable` et `Serializable` ;
- est annotée `@Entity(name = "TypeProduitJPA")` ;
- est mappée en accès champ (`@Access(AccessType.FIELD)`).

## 3) Structure persistée à retenir

### 3.1) Identifiant et libellé

- `idTypeProduit` : clé primaire JPA (`@Id`, `@GeneratedValue`, `@Column(name="ID_TYPE_PRODUIT")`)
- `typeProduit` : libellé persistant non nul (`@Column(..., nullable = false)`)

### 3.2) Relation enfant

L'entity porte une collection :
- `List<SousTypeProduitI> sousTypeProduits`

Cette relation est mappée en :
- `@OneToMany`
- `cascade = CascadeType.ALL`
- `orphanRemoval = true`
- `fetch = FetchType.LAZY`
- `mappedBy = "typeProduit"`

## 4) Comportements importants à relire

### 4.1) Méthodes d'identité et d'ordre

L'entity redéfinit :
- `hashCode()`
- `equals(Object)`
- `compareTo(TypeProduitI)`
- `toString()`

### 4.2) Clonage et rattachement

L'entity expose notamment :
- `clone()`
- `deepClone(CloneContext)`
- `cloneWithoutChildren()`

Le rattachement / détachement des enfants est stabilisé par des méthodes dédiées de la classe.

### 4.3) Export et projection

L'entity expose aussi des méthodes d'export / projection telles que :
- `getEnTeteCsv()`
- `toStringCsv()`
- `getEnTeteColonne(int)`
- `getValeurColonne(int)`

Ces méthodes sont prévues comme non persistées (`@Transient` quand nécessaire côté JPA).

## 5) Invariants techniques

- un `TypeProduitJPA` porte un identifiant technique et un libellé persistant ;
- la collection `sousTypeProduits` est la relation enfant canonique ;
- la couche persistance verrouille la cohérence du rattachement / détachement des sous-types ;
- cette entity ne doit pas dériver vers les DTO ni vers la logique UC.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/ConvertisseurJPAToMetier.md`
- `docs/contrats/persistance/ConvertisseurMetierToJPA.md`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPARattachementDetachementTest.java`