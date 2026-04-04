# Contrat local — TypeProduitDaoJPA

## 1) DAO concerné

- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPA.java`

## 2) Rôle du DAO

`TypeProduitDaoJPA` est un repository Spring Data JPA de la couche persistance.

Il :
- est annoté `@Repository("TypeProduitDaoJPA")` ;
- étend `JpaRepository<TypeProduitJPA, Long>` ;
- sert de support technique au Gateway `TypeProduitGatewayJPAService`.

## 3) API spécifique à relire

```java
TypeProduitJPA findByTypeProduitIgnoreCase(String pLibelle)

List<TypeProduitJPA> findByTypeProduitContainingIgnoreCase(
        String pContenu)

@Override
List<TypeProduitJPA> findAll()

@Override
Page<TypeProduitJPA> findAll(Pageable pPageable)
```

Les méthodes héritées `save(...)`, `findById(...)`, `count()` et `delete(...)` sont également utilisées via `JpaRepository`.

## 4) Garanties techniques à retenir

- la recherche par libellé exact est insensible à la casse ;
- la recherche rapide par contenu est insensible à la casse ;
- `findAll()` et `findAll(Pageable)` exposent la lecture exhaustive et paginée ;
- le DAO reste purement technique et ne produit aucun message utilisateur.

## 5) Frontière de couche

Le DAO appartient à `couche_persistance`.

Il ne doit pas :
- manipuler les DTO ;
- porter de logique UC ;
- porter de logique Gateway ;
- produire des exceptions applicatives de service.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`