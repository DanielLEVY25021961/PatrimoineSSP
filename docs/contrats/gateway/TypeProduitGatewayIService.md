# Contrat local — TypeProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

## 2) Rôle du port

`TypeProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `TypeProduit`.

Ce port :
- manipule exclusivement des objets métier ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur ;
- expose un contrat technique destiné à être consommé par la couche SERVICE UC.

## 3) Dépendances de contrat

### 3.1) Objet métier principal

- `levy.daniel.application.model.metier.produittype.TypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<TypeProduit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionTechniqueGateway`

## 4) Signatures exactes du port

```java
TypeProduit creer(TypeProduit pObject) throws Exception;

List<TypeProduit> rechercherTous() throws Exception;

ResultatPage<TypeProduit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

TypeProduit findByObjetMetier(TypeProduit pObject) throws Exception;

TypeProduit findByLibelle(String pLibelle) throws Exception;

List<TypeProduit> findByLibelleRapide(String pContenu) throws Exception;

TypeProduit findById(Long pId) throws Exception;

TypeProduit update(TypeProduit pObject) throws Exception;

void delete(TypeProduit pObject) throws Exception;

long count() throws Exception;
```

## 5) Contrat technique à retenir

### 5.1) Création / update / delete

Le port expose les opérations de persistance technique suivantes :
- création technique via `creer(TypeProduit)` ;
- modification technique via `update(TypeProduit)` ;
- suppression technique via `delete(TypeProduit)`.

Le port documente explicitement des cas d'erreur applicative côté Gateway :
- paramètre `null` ;
- libellé blank ;
- objet non persistant lorsque l'ID est obligatoire.

### 5.2) Recherche

Le port expose :
- la recherche exhaustive via `rechercherTous()` ;
- la recherche paginée via `rechercherTousParPage(RequetePage)` ;
- la recherche par objet métier via `findByObjetMetier(TypeProduit)` ;
- la recherche par libellé exact via `findByLibelle(String)` ;
- la recherche rapide via `findByLibelleRapide(String)` ;
- la recherche par identifiant via `findById(Long)`.

### 5.3) Comptage

Le port expose `count()` comme opération technique de comptage du stockage.

## 6) Règles d'architecture à respecter

- aucune dépendance DTO ;
- aucune logique UC ;
- aucune journalisation utilisateur ;
- aucune responsabilité de présentation ;
- aucun Raw Type dans les signatures ;
- la pagination est contractuelle via `ResultatPage<TypeProduit>`.

## 7) Fichiers à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`