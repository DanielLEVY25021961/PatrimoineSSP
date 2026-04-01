# Contrat local — SousTypeProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`

## 2) Rôle du port

`SousTypeProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `SousTypeProduit`.

Ce port :
- manipule exclusivement des objets métier ;
- reste strictement technique ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur.

## 3) Dépendances de contrat

### 3.1) Objets métier principaux

- `levy.daniel.application.model.metier.produittype.SousTypeProduit`
- `levy.daniel.application.model.metier.produittype.TypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<SousTypeProduit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionAppliParentNull`
- `ExceptionTechniqueGateway`

## 4) Signatures exactes du port

```java
SousTypeProduit creer(SousTypeProduit pObject) throws Exception;

List<SousTypeProduit> rechercherTous() throws Exception;

ResultatPage<SousTypeProduit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

SousTypeProduit findByObjetMetier(SousTypeProduit pObject) throws Exception;

List<SousTypeProduit> findByLibelle(String pLibelle) throws Exception;

List<SousTypeProduit> findByLibelleRapide(String pContenu) throws Exception;

List<SousTypeProduit> findAllByParent(TypeProduit pParent) throws Exception;

SousTypeProduit findById(Long pId) throws Exception;

SousTypeProduit update(SousTypeProduit pObject) throws Exception;

void delete(SousTypeProduit pObject) throws Exception;

long count() throws Exception;
```

## 5) Contrat technique à retenir

### 5.1) Création / update / delete

Le port expose :
- `creer(SousTypeProduit)` ;
- `update(SousTypeProduit)` ;
- `delete(SousTypeProduit)`.

Ces opérations restent techniques et peuvent signaler des préconditions invalides au niveau Gateway :
- objet `null` ;
- libellé blank ;
- objet non persistant ;
- parent `null` ou parent non persistant selon le scénario.

### 5.2) Recherche

Le port expose :
- la recherche exhaustive ;
- la recherche paginée ;
- la recherche par objet métier ;
- la recherche par libellé exact ;
- la recherche par libellé rapide ;
- la recherche par parent `TypeProduit` ;
- la recherche par identifiant.

### 5.3) Comptage

Le port expose `count()` comme opération technique de comptage du stockage.

## 6) Règles d'architecture à respecter

- aucune dépendance DTO ;
- aucune logique UC ;
- aucune exception de SERVICE UC ;
- aucun Raw Type dans les signatures ;
- la pagination est contractuelle via `ResultatPage<SousTypeProduit>`.

## 7) Fichiers à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`