# Contrat local — ProduitDaoJPA

## 1) DAO concerné

- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java`

## 2) Rôle du DAO

`ProduitDaoJPA` est un repository Spring Data JPA de la couche persistance.

Il :
- est annoté `@Repository("ProduitDaoJPA")` ;
- étend `JpaRepository<ProduitJPA, Long>` ;
- sert de support technique au Gateway `ProduitGatewayJPAService`.

## 3) API spécifique à relire

```java
List<ProduitJPA> findByProduitIgnoreCase(String pLibelle)

List<ProduitJPA> findByProduitContainingIgnoreCase(
        String pContenu)

@Override
List<ProduitJPA> findAll()

@Override
Page<ProduitJPA> findAll(Pageable pPageable)

List<ProduitJPA> findAllBySousTypeProduit(
        SousTypeProduitJPA pSousTypeProduit)
```

Les méthodes héritées `save(...)`, `findById(...)`, `count()` et `delete(...)` sont également utilisées via `JpaRepository`.

## 4) Garanties techniques à retenir

- la recherche par libellé exact est insensible à la casse ;
- la recherche rapide par contenu est insensible à la casse ;
- la recherche par parent `SousTypeProduitJPA` est explicite ;
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
- `docs/contrats/persistance/ProduitJPA.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`