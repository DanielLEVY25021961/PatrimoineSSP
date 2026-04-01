# docs/contrats/dto/TypeProduitDTO.md

# Contrat local DTO — TypeProduitDTO

## 1) Classe concernée

- `levy.daniel.application.model.dto.produittype.TypeProduitDTO`

## 2) Objet du contrat

Décrire le comportement attendu et observable du DTO `TypeProduitDTO`, en particulier :
- sa structure interne ;
- les rôles respectifs de `InputDTO` et `OutputDTO` ;
- les invariants de nullité ;
- les règles d’égalité / hashCode ;
- les collections transportées ;
- les tests JUnit qui verrouillent ces comportements.

## 3) Rôle applicatif

`TypeProduitDTO` est un objet de transfert entre couches applicatives.
Il ne relève pas de la persistance.

Il est destiné à :
- recevoir des données entrantes côté contrôleur / service UC ;
- exposer des données sortantes côté service UC / contrôleur.

## 4) Structure attendue

`TypeProduitDTO` doit permettre d’exposer au minimum :
- un `InputDTO` pour les flux entrants ;
- un `OutputDTO` pour les flux sortants.

Le comportement exact de ces structures est celui verrouillé par :
- `TypeProduitDTO.java`
- `TypeProduitDTOTest.java`
- les convertisseurs DTO associés

## 5) Invariants principaux

- un DTO peut être incomplet selon le scénario ;
- un `InputDTO` peut être utilisé pour porter une saisie partielle ;
- un `OutputDTO` doit refléter l’état observable exposé à la couche appelante ;
- les champs de collections doivent rester cohérents avec le contrat de la classe ;
- aucune règle métier profonde ne doit migrer dans le DTO.

## 6) Égalité / hashCode

Le contrat attendu est celui réellement verrouillé par `TypeProduitDTOTest`.

Règles générales :
- respect du contrat Java ;
- cohérence `equals(...)` / `hashCode()`;
- comportement stable sur `OutputDTO` ;
- aucune divergence entre l’identité exposée et les tests.

## 7) Collections et mutabilité

Le contrat attendu est celui réellement verrouillé par les tests DTO et par les convertisseurs.

Le DTO doit exposer une structure cohérente pour la couche appelante :
- sans incohérence de nullité ;
- sans contradiction avec le convertisseur ;
- avec une mutabilité conforme aux tests de référence.

## 8) Convertisseur associé

Le DTO `TypeProduitDTO` est lié au convertisseur :
- `ConvertisseurMetierToOutputDTOTypeProduit.java`

Toute évolution du DTO doit préserver l’alignement avec ce convertisseur et avec son test :
- `ConvertisseurMetierToOutputDTOTypeProduitTest.java`

## 9) Tests de référence

Tests à relire avant correction :
- `TypeProduitDTOTest.java`
- `ConvertisseurMetierToOutputDTOTypeProduitTest.java`

## 10) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `TypeProduitDTO`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/dto/TypeProduitDTO.md`
3. `TypeProduitDTO.java`
4. `ConvertisseurMetierToOutputDTOTypeProduit.java`
5. `TypeProduitDTOTest.java`
6. `ConvertisseurMetierToOutputDTOTypeProduitTest.java`

## 11) Interdictions absolues

- ne jamais reclasser `TypeProduitDTO` dans la couche persistance ;
- ne jamais déplacer une règle métier profonde dans le DTO ;
- ne jamais modifier `equals(...)` / `hashCode()` sans vérifier les tests ;
- ne jamais casser l’alignement entre DTO et convertisseur.