# docs/contrats/metier/NormalizerUtils.md

# Contrat local métier — NormalizerUtils

## 1) Classe concernée

- `levy.daniel.application.model.utilitaires.metier.produittype.NormalizerUtils`
- test associé : `NormalizerUtilsTest.java`

## 2) Rôle

`NormalizerUtils` est une classe utilitaire `final` de normalisation de chaînes utilisée par la couche métier.

La classe est stateless côté appelant : aucun état métier mutable ne doit être exposé.

## 3) Structure validée

- classe `final` ;
- constructeur privé ;
- `Pattern` privé pour les diacritiques combinés ;
- map privée des caractères accentués explicitement remplacés avant normalisation ;
- méthodes statiques uniquement.

## 4) Méthodes validées

| Méthode | Contrat observable |
| --- | --- |
| `normalize(String)` | délègue à `normalize(pInput, false, Locale.getDefault())`. |
| `normalize(String, boolean, Locale)` | retourne une chaîne normalisée selon les options demandées. |

## 5) Comportements obligatoires

- entrée `null` : retourne `""` ;
- chaîne vide : retourne la chaîne vide inchangée ;
- par défaut, les diacritiques sont conservés et la chaîne est convertie en minuscules selon la locale par défaut ;
- si `removeDiacritics == true`, les caractères accentués explicitement mappés sont remplacés, la forme NFD est utilisée, les diacritiques combinés sont supprimés, puis la chaîne est recomposée en NFC ;
- les caractères spéciaux non accentués ne doivent pas être modifiés ;
- les espaces et ponctuations sont conservés ;
- la conversion en minuscules utilise la locale fournie.

## 6) Tests de référence

`NormalizerUtilsTest.java` verrouille :

- le comportement nominal de normalisation ;
- le comportement sur `null`.

## 7) Interdictions

- ne jamais transformer `null` en `null` : le contrat validé retourne `""` ;
- ne jamais supprimer les espaces ou ponctuations par défaut ;
- ne jamais imposer une locale fixe en remplacement de la locale fournie ;
- ne jamais supprimer les diacritiques lorsque `removeDiacritics == false` ;
- ne jamais rendre la classe instanciable.
