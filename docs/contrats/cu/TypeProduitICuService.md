# docs/contrats/cu/TypeProduitICuService.md

# Contrat comportemental — TypeProduitICuService (SERVICE METIER UC)

## 1) Port concerné
- `levy.daniel.application.model.services.produittype.cu.TypeProduitICuService`

## 2) Objectif
Décrire le **comportement réel attendu et observable** du SERVICE METIER UC
rattaché au port `TypeProduitICuService`.

Ce document ne décrit pas la technique de persistance.
Il décrit le comportement **côté UC** :
- orchestration applicative ;
- messages utilisateur ;
- exceptions observables ;
- résultats retournés ;
- traçabilité ;
- cohérence avec les tests Mock et Intégration.

## 3) Rôle du SERVICE UC
Le SERVICE METIER UC :
- reçoit des `InputDTO` depuis la couche appelante ;
- valide les préconditions applicatives observables ;
- convertit les `InputDTO` en objets métier ;
- délègue les opérations techniques au `GATEWAY` ;
- convertit les objets métier retournés en `OutputDTO` ;
- produit des messages utilisateur via `getMessage()` ;
- émet les LOGs nécessaires à la traçabilité ;
- retourne un résultat exploitable par le caller.

**Important :**
- le `GATEWAY` ne produit pas de message utilisateur ;
- le `SERVICE UC` est responsable du **message observable** côté appelant ;
- les messages récurrents doivent être **factorisés dans le PORT**.

## 4) Source de vérité de ce contrat
Ce contrat est rattaché explicitement au port `TypeProduitICuService`.

Ordre d’interprétation :
1. `docs/ai/CONTRAT_IA.md`
2. présent contrat `TypeProduitICuService.md`
3. code du PORT `TypeProduitICuService`
4. code de l’ADAPTER `TypeProduitCuService`
5. tests JUnit Mock et Intégration

En cas d’ambiguïté :
- priorité au **comportement réellement verrouillé par les tests** ;
- puis au **présent contrat** ;
- puis au code.

## 5) Périmètre stabilisé dans cette version
La présente version sacralise en priorité la méthode :

- `TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`

Cette méthode sert désormais de **référence de travail** pour toutes les autres
méthodes de `TypeProduitICuService`.

## 6) Principes directeurs du formalisme UC
Pour chaque méthode UC, le formalisme cible est le suivant :

### 6.1 Dans le PORT UC
La javadoc doit comporter les 3 rubriques suivantes :
- **INTENTION DE SERVICE UC (scénario nominal)**
- **CONTRAT DE SERVICE UC**
- **GARANTIES METIER, UTILISATEUR et TRAÇABILITE**

### 6.2 Dans l’ADAPTER UC
L’implémentation doit :
- consommer les constantes de messages définies dans le PORT ;
- rationaliser les messages utilisateur techniques ;
- distinguer clairement :
  - erreur bénigne,
  - erreur applicative,
  - erreur métier,
  - erreur technique ;
- ne positionner le message de succès **qu’en fin de scénario complet** ;
- comporter des **commentaires de bloc homogènes**.

### 6.3 Dans les tests Mock
Les tests Mock doivent couvrir :
- scénario nominal ;
- violation de contrat ;
- erreur métier ;
- erreur technique ;
- messages utilisateur ;
- non-régression de délégation vers le `GATEWAY`.

### 6.4 Dans les tests d’Intégration
Les tests d’Intégration doivent couvrir :
- comportement observable ;
- message final ;
- exceptions ;
- **preuve BD physique** lorsque la méthode modifie le stockage.

## 7) Contrat important — creer(TypeProduitDTO.InputDTO)

### 7.1 Signature
```java
TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO)
        throws Exception;