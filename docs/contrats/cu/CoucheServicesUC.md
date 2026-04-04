# Contrat local de couche - Couche services UC

## 1) Intention de la couche

La sous-couche `couche_services.uc` porte les services métier de cas d'usage du domaine `produittype`.

Elle expose :
- les PORTS UC métier ;
- les ADAPTERS UC métier ;
- les exceptions applicatives de SERVICE UC ;
- les tests Mock UC ;
- les tests d'intégration UC ;
- les contrats locaux nécessaires à la relecture de la sous-couche.

Cette sous-couche appartient à `couche_services`.

## 2) Frontière architecturale

La sous-couche `couche_services.uc` :
- manipule des `InputDTO` et `OutputDTO` lorsque le scénario l'exige ;
- orchestre le métier et délègue les opérations techniques aux Gateways ;
- produit le message observable côté appelant via `getMessage()` ;
- ne manipule pas directement les entités JPA ;
- ne porte pas la logique Controller ;
- ne porte pas la logique View.

Les DTO relèvent de `couche_dto`.
Les Gateways relèvent de `couche_services.gateway`.
Les entités JPA relèvent de `couche_persistance`.

## 3) Fichiers inclus dans le périmètre sacralisé

### 3.1) PORTS UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/cu/ProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/SousTypeProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/TypeProduitICuService.java`

### 3.2) ADAPTERS UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuService.java`

### 3.3) Exceptions services UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionDoublon.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionNonPersistant.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionStockageVide.java`

### 3.4) Tests UC métier

- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceMockTest.java`

### 3.5) Contrats locaux de la sous-couche

- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`

## 4) Règles de cohérence obligatoires

### 4.1) Règle DTO

Les méthodes UC manipulent les DTO applicatifs quand le contrat de service l'exige.
Le service UC ne doit pas dériver vers des entités JPA ou des structures de persistance.

### 4.2) Règle Gateway

Le service UC ne réalise pas lui-même les opérations techniques de stockage.
Il délègue ces opérations à `couche_services.gateway`.

### 4.3) Règle message observable

Le service UC est responsable du message observable côté appelant.
Le `GATEWAY` ne doit jamais produire ce message utilisateur.

### 4.4) Règle exceptionsservices

Les exceptions de `exceptionsservices` appartiennent à `couche_services.uc`.
Elles ne doivent pas être rattachées à `couche_services.gateway`.

### 4.5) Règle de preuve

Les tests Mock verrouillent le contrat observable et la délégation.
Les tests d'intégration prouvent le comportement UC réel, le message final et, lorsque pertinent, la preuve BD.

### 4.6) Règle de contrats locaux

Les contrats locaux de cette sous-couche servent de pivots de relecture avant toute analyse, tout diagnostic ou toute génération de code portant sur UC.

## 5) Définition de la sacralisation

La sous-couche `couche_services.uc` est considérée sacralisée lorsque :
- le présent contrat local est présent ;
- le périmètre IA référence exactement les fichiers de cette sous-couche ;
- les exceptions `exceptionsservices` sont rattachées à UC et non à Gateway ;
- les tests Mock et d'intégration UC sont dans le périmètre validé ;
- la séparation avec `couche_services.gateway` est explicite ;
- la séparation avec `couche_dto`, `couche_persistance`, `couche_controllers` et `couche_vues` est explicite.

## 6) Exclusions explicites

Ne font pas partie de `couche_services.uc` :
- les Gateways ;
- les exceptions de `exceptionsgateway` ;
- les entités JPA et convertisseurs de persistance ;
- les controllers ;
- les vues.