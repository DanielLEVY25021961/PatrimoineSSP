# Contrat local de couche - Couche services Gateway

## 1) Intention de la couche

La sous-couche `couche_services.gateway` porte les services techniques d'accès aux données du domaine `produittype`.

Elle expose :
- les PORTS Gateway ;
- les ADAPTERS Gateway JPA ;
- les exceptions techniques et applicatives propres au niveau Gateway ;
- le socle transverse de pagination utilisé par les Gateways ;
- les tests Mock Gateway ;
- les tests d'intégration Gateway.

Cette sous-couche appartient à `couche_services`.

## 2) Frontière architecturale

La sous-couche `couche_services.gateway` :
- manipule les objets métier ;
- ne manipule pas les DTO ;
- ne porte pas la logique UC ;
- ne porte pas de logique Controller ;
- ne porte pas de logique View.

Les DTO relèvent de `couche_dto`.
Les services UC relèvent de `couche_services.uc`.

## 3) Fichiers inclus dans le périmètre sacralisé

### 3.1) PORTS Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

### 3.2) ADAPTERS Gateway JPA

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`

### 3.3) Exceptions Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliLibelleBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNonPersistent.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParentNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGateway.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGatewayNonPersistent.java`

### 3.4) Pagination transverse Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/pagination/DirectionTri.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/RequetePage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/ResultatPage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/TriSpec.java`

### 3.5) Tests Gateway

- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`

### 3.6) Tests de pagination

- `src/test/java/levy/daniel/application/model/services/produittype/pagination/DirectionTriTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/RequetePageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/ResultatPageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/TriSpecTest.java`

## 4) Règles de cohérence obligatoires

### 4.1) Règle DTO

Aucun fichier de cette sous-couche ne doit dépendre des DTO de `couche_dto`.

### 4.2) Règle UC

Aucun fichier de cette sous-couche ne doit porter des messages utilisateur ou un comportement propre aux cas d'usage UC.

### 4.3) Règle métier

Les Gateways manipulent les objets métier et assurent la traduction technique vers la persistance.

### 4.4) Règle pagination

Le socle de pagination appartient au périmètre sacralisé de Gateway tant qu'il est utilisé comme support contractuel des recherches paginées des Gateways.

### 4.5) Règle de preuve

Les tests Mock verrouillent le contrat technique.
Les tests d'intégration prouvent le comportement réel JPA/BDD.

## 5) Définition de la sacralisation

La sous-couche `couche_services.gateway` est considérée sacralisée lorsque :
- le présent contrat local est présent ;
- le périmètre IA référence exactement les fichiers de cette sous-couche ;
- les tests Gateway et pagination sont dans le périmètre validé ;
- la séparation avec `couche_services.uc` est explicite ;
- la séparation avec `couche_dto` est explicite.

## 6) Exclusions explicites

Ne font pas partie de `couche_services.gateway` :
- les services UC ;
- les DTO ;
- les converters DTO ;
- les controllers ;
- les vues.