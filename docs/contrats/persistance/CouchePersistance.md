# Contrat local de couche - Couche persistance

## 1) Intention de la couche

La couche `couche_persistance` porte les composants de persistance du domaine `produittype`.

Elle expose :
- les interfaces transverses propres à la persistance ;
- les convertisseurs JPA ↔ métier ;
- les entités JPA ;
- les DAO JPA ;
- les tests de persistance ;
- les contrats locaux de couche.

Cette couche est distincte de `couche_metier`, de `couche_dto` et de `couche_services`.

## 2) Frontière architecturale

La couche `couche_persistance` :
- manipule les entités JPA et les DAO ;
- assure la conversion entre objets métier et objets JPA ;
- ne porte pas la logique UC ;
- ne porte pas la logique Gateway ;
- ne porte pas la logique Controller ;
- ne porte pas la logique View ;
- ne manipule pas les DTO comme contrat applicatif.

La duplication de certaines interfaces transverses entre `couche_metier` et `couche_persistance` est volontaire lorsqu’elle sert l’autonomie stricte des couches.

## 3) Fichiers inclus dans le périmètre sacralisé

### 3.1) Interfaces transverses

- `src/main/java/levy/daniel/application/persistence/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/persistence/metier/IExportateurJTable.java`

### 3.2) Entités JPA / convertisseurs

- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java`

### 3.3) DAO JPA

- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPA.java`

### 3.4) Tests persistance

- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetierTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPARattachementDetachementTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPATest.java`

### 3.5) Contrats locaux de la couche

- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/persistance/ConvertisseurJPAToMetier.md`
- `docs/contrats/persistance/ConvertisseurMetierToJPA.md`
- `docs/contrats/persistance/ProduitJPA.md`
- `docs/contrats/persistance/SousTypeProduitJPA.md`
- `docs/contrats/persistance/TypeProduitJPA.md`
- `docs/contrats/persistance/ProduitDaoJPA.md`
- `docs/contrats/persistance/SousTypeProduitDaoJPA.md`
- `docs/contrats/persistance/TypeProduitDaoJPA.md`

## 4) Règles de cohérence obligatoires

### 4.1) Règle d'autonomie de couche

Les interfaces transverses de persistance appartiennent à `couche_persistance` même lorsqu’un équivalent existe côté métier.
Cette duplication volontaire sert l’autonomie stricte des couches.

### 4.2) Règle convertisseurs

Les convertisseurs de la couche assurent le passage :
- JPA → métier ;
- métier → JPA.

Ils n’ont pas à dériver vers les DTO ni vers la logique UC.

### 4.3) Règle DAO

Les DAO JPA appartiennent exclusivement à la persistance.
Ils ne doivent pas porter de message utilisateur ni de logique applicative de cas d’usage.

### 4.4) Règle de preuve

Les tests de persistance verrouillent :
- la cohérence des convertisseurs ;
- les règles JPA des entités ;
- les rattachements / détachements ;
- les invariants techniques propres à la persistance.

### 4.5) Règle de preuve DAO

La couche `couche_persistance` ne porte pas de batterie de tests DAO dédiée.

La preuve des appels DAO spécifiques et du comportement observable associé est réalisée par :
- les tests Mock de la couche Gateway ;
- les tests d’intégration de la couche Gateway.

En conséquence, la couche `couche_persistance` ne doit pas dupliquer massivement cette preuve au niveau DAO, sauf besoin nouveau explicite.

### 4.6) Règle de frontière

La couche `couche_persistance` ne doit pas absorber :
- les DTO ;
- les services UC ;
- les Gateways ;
- les controllers ;
- les vues.

## 5) Définition de la sacralisation

La couche `couche_persistance` est considérée sacralisée lorsque :
- le présent contrat local est présent ;
- les contrats locaux détaillés des convertisseurs, entities JPA et DAO JPA sont présents ;
- le périmètre IA référence exactement les fichiers de cette couche ;
- les interfaces transverses de persistance sont explicitement incluses ;
- les convertisseurs, entités JPA, DAO et tests sont dans le périmètre validé ;
- la séparation avec `couche_metier`, `couche_dto` et `couche_services` est explicite.

## 6) Exclusions explicites

Ne font pas partie de `couche_persistance` :
- les DTO ;
- les services UC ;
- les Gateways ;
- les controllers ;
- les vues.