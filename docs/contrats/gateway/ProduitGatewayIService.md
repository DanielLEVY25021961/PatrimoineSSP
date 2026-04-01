# Contrat local — ProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`

## 2) Rôle du port

`ProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `Produit`.

Ce port :
- manipule exclusivement des objets métier ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur ;
- expose un contrat technique utilisé par la couche SERVICE UC.

## 3) Dépendances de contrat

### 3.1) Objets métier principaux

- `levy.daniel.application.model.metier.produittype.Produit`
- `levy.daniel.application.model.metier.produittype.SousTypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<Produit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionAppliParentNull`
- `ExceptionTechniqueGateway`
- `ExceptionTechniqueGatewayNonPersistent`

## 4) Signatures exactes du port

```java
Produit creer(Produit pObject) throws Exception;

List<Produit> rechercherTous() throws Exception;

ResultatPage<Produit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

Produit findByObjetMetier(Produit pObject) throws Exception;

List<Produit> findByLibelle(String pLibelle) throws Exception;

List<Produit> findByLibelleRapide(String pContenu) throws Exception;

List<Produit> findAllByParent(SousTypeProduit pParent) throws Exception;

Produit findById(Long pId) throws Exception;

Produit update(Produit pObject) throws Exception;

void delete(Produit pObject) throws Exception;

long count() throws Exception;
```

## 5) Contrat technique à retenir

### 5.1) Création / update / delete

Le port expose :
- `creer(Produit)` ;
- `update(Produit)` ;
- `delete(Produit)`.

Ces opérations restent techniques et documentent des préconditions invalides au niveau Gateway :
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
- la recherche rapide ;
- la recherche par parent `SousTypeProduit` ;
- la recherche par identifiant.

### 5.3) Comptage

Le port expose `count()` comme opération technique de comptage du stockage.

## 6) Règles d'architecture à respecter

- aucune dépendance DTO ;
- aucune logique UC ;
- aucune exception de SERVICE UC ;
- aucun Raw Type dans les signatures ;
- la pagination est contractuelle via `ResultatPage<Produit>`.

## 7) Fichiers à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`