# docs/contrats/gateway/TypeProduitGatewayJPAService.md

# Contrat comportemental — TypeProduitGatewayJPAService (Gateway JPA)

## 1) Port concerné

- `TypeProduitGatewayIService`

## 2) Objectif

Décrire le pivot comportemental local du gateway JPA `TypeProduitGatewayJPAService` pour la sacralisation de `couche_services.gateway`.

Le présent contrat local doit être relu conjointement avec :
- le PORT `TypeProduitGatewayIService` ;
- l'implémentation `TypeProduitGatewayJPAService` ;
- les tests Mock ;
- les tests d'intégration.

## 3) Contrats importants

### 3.1) creer(TypeProduit)

- Paramètre `null` -> Exception applicative (contrat du port)
- Libellé blank -> Exception applicative (contrat du port)
- Stockage KO / DAO `null` / exception technique -> `ExceptionTechniqueGateway`

### 3.2) rechercherTous()

- Retourne toujours une liste non `null` (éventuellement vide)
- Tri + dédoublonnage selon la stratégie du gateway

### 3.3) rechercherTousParPage(RequetePage)

- Si requête `null` -> pagination par défaut
- Le résultat doit être cohérent :
  - `content` non `null`
  - `pageNumber` cohérent
  - `pageSize` cohérent
  - `totalElements` cohérent

**Cas limite à documenter / verrouiller par test :**
- `pageSize == 0`
  - soit page vide avec `totalElements` conservé,
  - soit comportement délégué de Spring Data si le gateway le conserve explicitement.

### 3.4) findByLibelle(String)

- Le libellé exact est une opération technique du gateway
- Le comportement sur `blank` relève de l'exception applicative du port
- Le comportement sur stockage KO relève de `ExceptionTechniqueGateway`

### 3.5) update(TypeProduit)

#### Contrat : libellé existant (collision)

**Comportement réel recherché et documenté :**
- pas d'exception ;
- pas de modification ;
- retour de l'objet persistant inchangé.

Exemple de scénario de référence :

```java
final TypeProduit t1 = service.findByLibelle("vêtement");
final TypeProduit t2 = service.findByLibelle("bazar");

final Long id2 = t2.getIdTypeProduit();

/* Tentative de collision. */
final TypeProduit aModifier = new TypeProduit(id2, "vêtement");

/* Comportement réel : pas d'exception et pas de modification. */
final TypeProduit retour = service.update(aModifier);
```

### 3.6) delete(TypeProduit)

- L'absence en stockage est constatée techniquement par recherche préalable
- Le gateway ne produit pas de message utilisateur
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

### 3.7) count()

- Retourne un compteur technique `>= 0`
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

## 4) Fichiers de preuve à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`