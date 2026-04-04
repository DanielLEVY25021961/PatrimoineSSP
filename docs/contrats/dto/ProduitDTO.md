# docs/contrats/dto/ProduitDTO.md

# Contrat local DTO — ProduitDTO

## 1) Classe concernée

- `levy.daniel.application.model.dto.produittype.ProduitDTO`

## 2) Objet du contrat

Décrire le comportement attendu et observable du DTO `ProduitDTO`, en particulier :
- sa structure de transfert ;
- le rôle de `InputDTO` et `OutputDTO` ;
- les invariants de structure ;
- les règles d’égalité / hashCode ;
- l’alignement avec le convertisseur associé ;
- les tests JUnit qui verrouillent ces comportements.

## 3) Rôle applicatif

`ProduitDTO` est un objet de transfert entre couches applicatives.
Il ne descend pas à la persistance.

Il sert à exposer ou transporter :
- le produit ;
- son sous-type parent ;
- son type parent ;
- son identité exposée selon les scénarios `InputDTO` / `OutputDTO`.

## 4) Invariants principaux

- le DTO peut être partiel côté entrée ;
- la sortie doit refléter l’état observable retourné par le service UC ;
- l’identité exposée doit rester cohérente avec les tests ;
- aucune logique de persistance ne doit migrer dans le DTO.

## 5) Égalité / hashCode

Le point central de `ProduitDTO` est l’alignement entre :
- `equals(...)`
- `hashCode()`
- les cas avec `id`
- les cas sans `id`

Le comportement exact attendu est celui verrouillé par :
- `ProduitDTOTest.java`

Toute correction doit préserver ces cas critiques.

## 6) Convertisseur associé

Le DTO `ProduitDTO` est lié au convertisseur :
- `ConvertisseurMetierToOutputDTOProduit.java`

Toute évolution du DTO doit rester alignée avec :
- le convertisseur ;
- les tests du convertisseur ;
- les cas critiques déjà verrouillés côté `OutputDTO`.

## 7) Tests de référence

Tests à relire avant correction :
- `ProduitDTOTest.java`
- `ConvertisseurMetierToOutputDTOProduitTest.java`

## 8) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `ProduitDTO`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/dto/ProduitDTO.md`
3. `ProduitDTO.java`
4. `ConvertisseurMetierToOutputDTOProduit.java`
5. `ProduitDTOTest.java`
6. `ConvertisseurMetierToOutputDTOProduitTest.java`

## 9) Interdictions absolues

- ne jamais reclasser `ProduitDTO` dans la couche persistance ;
- ne jamais casser le contrat `equals(...)` / `hashCode()` déjà verrouillé ;
- ne jamais désaligner le DTO et son convertisseur ;
- ne jamais déplacer dans le DTO une logique métier profonde ou une logique DAO/JPA.