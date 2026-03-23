# docs/contrats/gateway/TypeProduitGatewayJPAService.md

# Contrat comportemental — TypeProduitGatewayJPAService (Gateway JPA)

## 1) Port concerné
- `TypeProduitGatewayIService`

## 2) Objectif
Décrire le **comportement réel** observé (tests d’intégration) du gateway JPA.

## 3) Contrats importants

### 3.1 creer(TypeProduit)
- Paramètre null -> Exception applicative (contrat du port)
- Libellé blank -> Exception applicative (contrat du port)
- Stockage KO / DAO null / exception technique -> ExceptionTechniqueGateway

### 3.2 rechercherTous()
- Retourne toujours une liste non null (éventuellement vide)
- Tri + dédoublonnage selon la stratégie du gateway

### 3.3 rechercherTousParPage(RequetePage)
- Si requête null -> pagination par défaut
- Le résultat doit être cohérent : content non null, pageNumber/pageSize/totalElements cohérents

**Cas limite à documenter :**
- `pageSize == 0` : comportement attendu (à figer par test)
  - Option A : page vide (content vide), totalElements intact
  - Option B : délégation Spring Data (à préciser si différent)

### 3.4 update(TypeProduit)

#### Contrat : libellé existant (collision)
**Comportement réel souhaité (contrat explicite) :**
- **Pas d’exception**
- **Pas de modification**
- Retourne l’objet persistant inchangé

Exemple (spéc) :
```java
final TypeProduit t1 = service.findByLibelle("vêtement");
final TypeProduit t2 = service.findByLibelle("bazar");

final Long id2 = t2.getIdTypeProduit();

/* Tentative de collision. */
final TypeProduit aModifier = new TypeProduit(id2, "vêtement");

/* Comportement réel : pas d'exception et pas de modification. */
final TypeProduit retour = service.update(aModifier);
