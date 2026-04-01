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