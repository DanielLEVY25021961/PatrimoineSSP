<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local — ProduitDaoJPA

## 1) Composant concerné

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java
```

Source validée : `9c9f5631dbf2809d3772adc37a5f68fbe5914d80693554ef2eea53ad687fdb1a` — 176 lignes LF.

## 2) Rôle exact

`ProduitDaoJPA` est le repository Spring Data JPA de `ProduitJPA`. Il appartient exclusivement à la couche persistance et est utilisé par `ProduitGatewayJPAService`.

Il ne contient aucune logique UC, aucun message applicatif et aucune conversion.

## 3) Forme Java obligatoire

- bannière de tête `REPOSITORY DAO JPA` ;
- package `levy.daniel.application.persistence.metier.produittype.dao.daosJPA` ;
- imports `List`, `Page`, `Pageable`, `JpaRepository`, `Repository` ;
- imports de l'objet métier utile pour la Javadoc ;
- imports des entities JPA utiles ;
- annotation `@Repository("ProduitDaoJPA")` ;
- interface publique qui étend `JpaRepository<ProduitJPA, Long>`.

## 4) Méthodes dans l'ordre exact

1. `List<ProduitJPA> findByProduitIgnoreCase(String pLibelle);`
2. `List<ProduitJPA> findByProduitContainingIgnoreCase(String pContenu);`
3. `@Override List<ProduitJPA> findAll();`
4. `@Override Page<ProduitJPA> findAll(Pageable pPageable);`
5. `List<ProduitJPA> findAllBySousTypeProduit(SousTypeProduitJPA pSousTypeProduit);`

Les méthodes `findAll()` et `findAll(Pageable)` doivent rester redéclarées avec `@Override`, même si Spring Data les fournit déjà, car leur Javadoc verrouille le contrat observable exploité par les Gateways.

## 5) Sémantique Spring Data

- les méthodes `IgnoreCase` font une recherche exacte insensible à la casse ;
- les méthodes `ContainingIgnoreCase` font une recherche partielle insensible à la casse ;
- les recherches liste retournent une liste vide si rien n'est trouvé ;
- `findBy...IgnoreCase` de `TypeProduitDaoJPA` retourne une seule entity parce que le libellé parent est unique ;
- `findBy...IgnoreCase` des objets enfants retourne une liste parce que le libellé seul n'est pas unique sans le parent ;
- `findAllByTypeProduit(...)` et `findAllBySousTypeProduit(...)` filtrent par parent entity JPA ;
- les comportements techniques Spring Data restent prouvés dans les tests DAO et réexploités par les Gateways.

## 6) Tests qui verrouillent ce DAO

- `ProduitDaoJPA.testFindByProduitIgnoreCaseTrouve`
- `ProduitDaoJPA.testFindByProduitIgnoreCaseNonTrouve`
- `ProduitDaoJPA.testFindByProduitContainingIgnoreCaseTrouve`
- `ProduitDaoJPA.testFindByProduitContainingIgnoreCaseNonTrouve`
- `ProduitDaoJPA.testFindAllBySousTypeProduitTrouve`
- `ProduitDaoJPA.testFindAllBySousTypeProduitParentAbsent`

## 7) `DaoJPATestConfig` à relire avec les tests DAO

`src/test/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/DaoJPATestConfig.java` est obligatoire pour les tests DAO directs. Il centralise :

- `@TestConfiguration(proxyBeanMethods = false)` ;
- `@EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)` ;
- `@EntityScan(basePackageClasses = TypeProduitJPA.class)` ;
- constructeur public d'arité nulle.

L'IA ne doit pas réintroduire des configurations de test concurrentes par DAO.

## 8) Anti-improvisation

- Ne pas renommer les méthodes Spring Data.
- Ne pas transformer une méthode liste en méthode optionnelle.
- Ne pas supprimer les Javadocs métier des recherches.
- Ne pas intégrer de logique de tri, pagination ou conversion dans le DAO.
- Ne pas supprimer les tests DAO du périmètre de preuve.

## 9) Critère d'autonomie

Ce contrat, lu avec la classe validée et les tests DAO, permet de recoder quasiment à l'identique le DAO sans confondre persistance, Gateway et logique applicative.
