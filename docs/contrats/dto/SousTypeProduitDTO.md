# docs/contrats/dto/SousTypeProduitDTO.md

# Contrat local DTO — SousTypeProduitDTO

## 1) Classe concernée

- `levy.daniel.application.model.dto.produittype.SousTypeProduitDTO`

## 2) Objet du contrat

Décrire le comportement attendu et observable du DTO `SousTypeProduitDTO`, en particulier :
- sa structure ;
- les rôles de `InputDTO` et `OutputDTO` ;
- les contraintes sur les collections de produits embarquées ;
- les règles d’égalité / hashCode ;
- les tests qui verrouillent ces comportements.

## 3) Rôle applicatif

`SousTypeProduitDTO` est un objet de transfert entre couches applicatives.
Il ne doit pas être classé en persistance.

Il sert notamment à exposer :
- le sous-type ;
- son type parent ;
- éventuellement une collection de produits exposée côté sortie.

## 4) Invariants principaux

- le DTO peut être partiel côté entrée ;
- la sortie doit rester cohérente avec la représentation exposée par le service UC ;
- la collection de produits transportée doit respecter le contrat de mutabilité réellement verrouillé par les tests ;
- la nullité des champs doit rester cohérente avec les convertisseurs et avec les tests.

## 5) Égalité / hashCode

Le comportement attendu est celui verrouillé par :
- `SousTypeProduitDTOTest.java`

Toute correction doit préserver :
- la cohérence Java ;
- l’alignement entre identité exposée, `id` éventuel et fallback métier ;
- la non-régression sur les cas critiques déjà verrouillés par les tests.

## 6) Collections embarquées

Le point clé de ce DTO est la collection de produits exposée en sortie.

Le contrat attendu doit rester aligné avec :
- `SousTypeProduitDTO.java`
- `ConvertisseurMetierToOutputDTOSousTypeProduit.java`
- `SousTypeProduitDTOTest.java`
- `ConvertisseurMetierToOutputDTOSousTypeProduitTest.java`

Toute correction doit préserver :
- la cohérence de nullité ;
- l’homogénéité de mutabilité ;
- la stabilité de l’ordre et du contenu réellement verrouillés par les tests.

## 7) Convertisseur associé

Le DTO `SousTypeProduitDTO` est lié au convertisseur :
- `ConvertisseurMetierToOutputDTOSousTypeProduit.java`

Toute évolution doit conserver l’alignement avec son convertisseur et son test associé.

## 8) Tests de référence

Tests à relire avant correction :
- `SousTypeProduitDTOTest.java`
- `ConvertisseurMetierToOutputDTOSousTypeProduitTest.java`

## 9) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `SousTypeProduitDTO`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/dto/SousTypeProduitDTO.md`
3. `SousTypeProduitDTO.java`
4. `ConvertisseurMetierToOutputDTOSousTypeProduit.java`
5. `SousTypeProduitDTOTest.java`
6. `ConvertisseurMetierToOutputDTOSousTypeProduitTest.java`

## 10) Interdictions absolues

- ne jamais reclasser `SousTypeProduitDTO` dans la couche persistance ;
- ne jamais casser le contrat de collection de produits ;
- ne jamais modifier `equals(...)` / `hashCode()` sans vérification des tests ;
- ne jamais désaligner le DTO de son convertisseur.