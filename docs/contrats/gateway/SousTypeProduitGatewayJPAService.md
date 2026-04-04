# Contrat local — SousTypeProduitGatewayJPAService

## 1) Adapter concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`

## 2) Port concerné

- `SousTypeProduitGatewayIService`

## 3) Objectif

Décrire le pivot comportemental local du gateway JPA `SousTypeProduitGatewayJPAService` pour la sacralisation de `couche_services.gateway`.

Le présent contrat local doit être relu conjointement avec :
- le PORT `SousTypeProduitGatewayIService` ;
- l'implémentation `SousTypeProduitGatewayJPAService` ;
- les tests Mock ;
- les tests d'intégration.

## 4) Contrats importants

### 4.1) creer(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Libellé du parent blank -> `ExceptionAppliLibelleBlank`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Sauvegarde DAO `null` -> `ExceptionTechniqueGateway`
- En nominal : retourne l'objet métier persisté

### 4.2) rechercherTous()

- Retourne toujours une liste non `null`
- Une absence de contenu retourne une liste vide
- Le résultat observable est filtré, trié, dédoublonné et converti en objets métier

### 4.3) rechercherTousParPage(RequetePage)

- Si `pRequetePage == null` -> utilise une `RequetePage` par défaut
- `pageJPA == null` -> `ExceptionTechniqueGateway`
- `pageJPA.getContent() == null` -> `ExceptionTechniqueGateway`
- Le `ResultatPage<SousTypeProduit>` retourné reste cohérent :
  - `content` non `null`
  - `pageNumber` cohérent
  - `pageSize` cohérent
  - `totalElements` cohérent

### 4.4) findByObjetMetier(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- En nominal : retourne l'objet métier correspondant ou `null` si introuvable

### 4.5) findByLibelle(String)

- Libellé blank -> `ExceptionAppliLibelleBlank`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.6) findByLibelleRapide(String)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Contenu blank -> délègue à `rechercherTous()`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.7) findAllByParent(TypeProduit)

- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Aucun enfant trouvé -> liste vide
- Résultat nominal -> liste non `null`

### 4.8) findById(Long)

- Paramètre `null` -> `ExceptionAppliParamNull`
- DAO vide -> retourne `null`
- DAO `null` -> `ExceptionTechniqueGateway`
- Résultat nominal -> retourne l'objet métier persisté

### 4.9) update(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Objet absent en stockage -> retourne `null`
- Si aucune modification n'est détectée -> retourne l'objet persistant converti sans sauvegarde inutile
- En nominal : sauvegarde l'entité modifiée puis retourne l'objet métier mis à jour

### 4.10) delete(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- DAO `null` -> `ExceptionTechniqueGateway`
- Objet absent en stockage -> ne fait rien
- En nominal : supprime techniquement l'objet ciblé

### 4.11) count()

- Retourne un compteur technique `>= 0`
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

## 5) Règles de frontière

- le gateway ne manipule aucun DTO ;
- le gateway ne produit aucun message utilisateur ;
- les contrôles portent uniquement sur la sécurité technique du contrat Gateway ;
- les exceptions de `exceptionsservices` n'appartiennent pas à ce périmètre.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`