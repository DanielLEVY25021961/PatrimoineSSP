# Contrat local — SousTypeProduitDaoJPA

## 1) DAO concerné

- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPA.java`

## 2) Rôle du DAO

`SousTypeProduitDaoJPA` est un repository Spring Data JPA de la couche persistance.

Il :
- est annoté `@Repository("SousTypeProduitDaoJPA")` ;
- étend `JpaRepository<SousTypeProduitJPA, Long>` ;
- sert de support technique au Gateway `SousTypeProduitGatewayJPAService`.

## 3) API spécifique à relire

```java
List<SousTypeProduitJPA> findBySousTypeProduitIgnoreCase(String pLibelle)

List<SousTypeProduitJPA> findBySousTypeProduitContainingIgnoreCase(
        String pContenu)

@Override
List<SousTypeProduitJPA> findAll()

@Override
Page<SousTypeProduitJPA> findAll(Pageable pPageable)

List<SousTypeProduitJPA> findAllByTypeProduit(
        TypeProduitJPA pTypeProduit)
```

Les méthodes héritées `save(...)`, `findById(...)`, `count()` et `delete(...)` sont également utilisées via `JpaRepository`.

## 4) Garanties techniques à retenir

- la recherche par libellé exact est insensible à la casse ;
- la recherche rapide par contenu est insensible à la casse ;
- la recherche par parent `TypeProduitJPA` est explicite ;
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
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`