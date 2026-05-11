# docs/contrats/metier/CloneContext.md

# Contrat local métier — CloneContext

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.CloneContext`
- test associé : `CloneContextTest.java`

## 2) Rôle

`CloneContext` est le cache de clonage profond des objets métier.

Il garantit qu'une source métier donnée correspond à un seul clone pendant une opération de clonage profond. Il empêche les cycles infinis et les duplications lorsque `TypeProduit`, `SousTypeProduit` et `Produit` se référencent mutuellement.

## 3) Structure validée

La classe est `final` et encapsule un cache d'identité.

Méthodes validées :

| Méthode | Contrat observable |
| --- | --- |
| `get(Object key)` | retourne le clone associé à la clé ou `null` si absent ; `get(null)` retourne `null`. |
| `put(Object key, Object value)` | associe une source et son clone dans le contexte. |
| `contains(Object key)` | indique si une clé est présente ; `contains(null)` retourne `false`. |
| `size()` | retourne le nombre d'associations présentes. |
| `clear()` | vide le cache. |
| `computeIfAbsent(Object, CloneComputation<T>)` | garantit une seule computation concurrente pour une même source. |
| `getOrCreate(Object, Supplier<T>)` | retourne le clone existant ou crée, stocke et retourne un nouveau clone. |

## 4) Règles de clonage profond

- le contexte est transmis pendant toute l'opération de clonage profond ;
- le clone doit être enregistré avant de cloner les relations susceptibles de revenir vers la source ;
- si une source est déjà présente, le clone existant est réutilisé ;
- le contexte ne doit pas être remplacé en cours de clonage ;
- `clone()` crée un nouveau contexte de départ, mais `deepClone(CloneContext)` réutilise celui reçu.

## 5) Thread-safety

Les opérations composées de création doivent être synchronisées sur le cache interne.

`computeIfAbsent(...)` est spécifié par `CloneContextTest` comme garantissant l'unicité inter-threads : plusieurs threads demandant la même source ne doivent exécuter qu'une seule computation utile.

## 6) Tests de référence

`CloneContextTest.java` contient 10 tests validés :

1. création du contexte ;
2. `get(null)` ;
3. `put` puis `get` ;
4. deux clés distinctes donnent deux clones distincts ;
5. isolation entre deux contextes ;
6. `contains(key)` après `put` ;
7. `size()` ;
8. `clear()` ;
9. `contains(null)` ;
10. `computeIfAbsent` inter-threads.

## 7) Interdictions

- ne jamais remplacer la sémantique d'identité par une sémantique `equals(...)` ;
- ne jamais rendre le cache statique ;
- ne jamais partager implicitement un contexte entre deux clonages indépendants ;
- ne jamais supprimer la synchronisation des opérations de création ;
- ne jamais créer un nouveau `CloneContext` dans une méthode `deepClone(CloneContext)` déjà appelée avec un contexte non `null`.
