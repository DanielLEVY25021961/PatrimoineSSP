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

## 8) AUTONOMIE-NORMALIZERUTILS-01 — Fiche de recodage autonome

### 8.1 Structure exacte

`NormalizerUtils` est une classe utilitaire `final`.

Structure à conserver :

- `private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");` ;
- `private static final Map<Character, Character> ACCENTED_CHARACTERS_MAP = new HashMap<>();` ;
- bloc `static` d'alimentation de la map ;
- logger `private static final Logger LOG = LogManager.getLogger(NormalizerUtils.class);` ;
- constructeur privé ;
- `public static String normalize(String)` ;
- `public static String normalize(String, boolean, Locale)`.

### 8.2 Table exacte des caractères accentués

La map explicite doit être conservée avant la normalisation Unicode :

| Caractère | Remplacement |
| --- | --- |
| `À` | `a` |
| `Â` | `a` |
| `Ä` | `a` |
| `Á` | `a` |
| `È` | `e` |
| `É` | `e` |
| `Ê` | `e` |
| `Ë` | `e` |
| `Î` | `i` |
| `Ï` | `i` |
| `Í` | `i` |
| `Ì` | `i` |
| `Ô` | `o` |
| `Ö` | `o` |
| `Ò` | `o` |
| `Û` | `u` |
| `Ü` | `u` |
| `Ù` | `u` |
| `Ç` | `c` |
| `Ñ` | `n` |

Cette table fait partie du contrat. L'IA ne doit pas la remplacer par une normalisation Unicode seule.

### 8.3 Algorithme exact

`normalize(String)` délègue à `normalize(pInput, false, Locale.getDefault())`.

`normalize(String, boolean, Locale)` doit :

1. retourner `""` si l'entrée est `null` ;
2. retourner l'entrée inchangée si elle est vide ;
3. initialiser `result` avec l'entrée ;
4. si `removeDiacritics == true`, remplacer d'abord les caractères présents dans `ACCENTED_CHARACTERS_MAP` ;
5. appliquer `Normalizer.normalize(result, Normalizer.Form.NFD)` ;
6. si `removeDiacritics == true`, supprimer les diacritiques combinés avec `DIACRITICS_PATTERN` ;
7. recomposer en `Normalizer.Form.NFC` ;
8. convertir le résultat final en minuscules avec la locale fournie ;
9. conserver les espaces, ponctuations et caractères spéciaux non concernés.

### 8.4 Tests validés

- `testNormalizeDefault()` verrouille la conservation des diacritiques en mode par défaut ;
- `testNormalizeWithOptions()` verrouille les options de suppression/conservation des diacritiques et la tolérance `null`.
