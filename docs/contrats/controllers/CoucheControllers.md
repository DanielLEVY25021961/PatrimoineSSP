# Couche Controllers — Contrat local IA

## 1) Objet

Ce fichier est le contrat local pivot de la couche Controllers du projet PatrimoineSSP.

Il relaie les règles transverses déjà sacralisées dans `docs/ai/CONTRAT_IA.md` pour les Controllers Desktop/Web, les tests Controllers Mock, les tests MockMvc et les tests d'intégration Controllers.

Il ne dispense jamais de relire les classes Controllers, les services UC appelés, les DTO, les tests concernés et les références validées avant toute analyse, correction ou génération de code.

## 2) Périmètre

La couche Controllers couvre notamment :

- les controllers Desktop ;
- les controllers Web ;
- les tests Mock Controllers ;
- les tests MockMvc ;
- les tests d'intégration Controllers ;
- les échanges DTO entre Controllers et SERVICE METIER UC.

## 3) Ordre de lecture obligatoire

Avant toute analyse, audit, validation, correction ou génération de code Controller, l'IA doit lire dans cet ordre :

1. `docs/ai/CONTRAT_IA.md` ;
2. le présent contrat local `CoucheControllers.md` ;
3. le PORT ou SERVICE UC appelé par le Controller ;
4. la classe Controller cible ;
5. les DTO et classes métier utiles ;
6. les tests Controllers déjà validés dans la même famille ;
7. les tests concernés par la méthode ou le bloc travaillé.

Aucune conclusion ne doit être tirée de mémoire.

## 4) Tests Mockito Controllers

Un test Mock Controller prouve le comportement observable du Controller côté appelant sans prouver directement le stockage.

Il doit vérifier selon le cas :

- la validation des entrées Controller ;
- les DTO envoyés au SERVICE METIER UC ;
- les DTO ou réponses retournés au client appelant ;
- le message utilisateur observable ;
- la délégation exacte au SERVICE METIER UC ;
- l'absence d'interaction avec le SERVICE METIER UC lorsque le Controller bloque localement l'opération ;
- la propagation ou la traduction contrôlée des exceptions ;
- le comportement attendu lorsque le SERVICE METIER UC retourne `null`, une liste vide, une réponse nominale ou jette une exception.

## 5) RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01 — Application Controllers

Cette règle est l'application locale Controllers de la règle Mockito transverse définie dans `CONTRAT_IA.md`.
Elle s'applique à tout test Controller utilisant Mockito, y compris les tests MockMvc qui configurent un SERVICE METIER UC mocké.

Sont concernés :

- `when(...)` ;
- `doThrow(...)` ;
- `doReturn(...)` ;
- tout comportement configuré sur un mock, un spy ou un collaborateur Mockito.

Règle obligatoire :

1. relire la méthode Controller testée ;
2. relire la méthode du SERVICE METIER UC appelée lorsque le scénario la sollicite ;
3. déterminer l'ordre réel des appels ;
4. identifier le point exact où le scénario s'arrête : validation locale, exception attendue, retour anticipé, réponse MockMvc ou branche nominale ;
5. ne stubber que les appels réellement consommés avant ce point ;
6. supprimer tout stubbing non consommé avant livraison.

Interdictions :

- ne jamais stubber un SERVICE METIER UC mocké pour le rendre « complet » ;
- ne jamais configurer une réponse UC si le Controller rejette l'entrée avant l'appel UC ;
- ne jamais préparer un getter, un DTO ou une méthode mockée si le scénario échoue avant sa lecture ;
- ne jamais conserver un stubbing préventif, décoratif ou supposé utile ;
- ne jamais ignorer un risque de `UnnecessaryStubbingException`.

Conséquence :

- les commentaires `Configuration du Mock` doivent décrire uniquement les comportements réellement consommés ;
- les vérifications Mockito doivent correspondre aux interactions réellement attendues ou interdites ;
- `verifyNoInteractions(...)` doit être utilisé lorsqu'un collaborateur ne doit pas être appelé ;
- le test ne doit pas simuler un scénario métier plus large que le comportement Controller réellement testé.

## 6) MockMvc utilisant Mockito

Lorsqu'un test MockMvc configure des services mockés avec Mockito, les mêmes règles s'appliquent.

Un test MockMvc ne doit pas stubber un SERVICE METIER UC si la requête est rejetée avant l'appel au service.

Un test MockMvc doit distinguer clairement :

- la préparation de la requête HTTP ;
- la configuration strictement nécessaire des mocks ;
- l'exécution MockMvc ;
- les assertions sur le statut, la vue, le modèle, le JSON ou la réponse ;
- les interactions Mockito attendues ou interdites.

## 7) Non-réinvention

L'IA doit reprendre le formalisme des tests Controllers déjà validés dans la même famille avant de générer un nouveau test.

Elle ne doit pas inventer :

- un nouvel ordre de méthodes ;
- une nouvelle javadoc de tête ;
- de nouveaux commentaires de bloc ;
- un nouveau style d'assertion ;
- un nouveau style de vérification Mockito.

Toute règle technique transverse doit rester canonique dans `CONTRAT_IA.md` et être relayée localement ici uniquement pour son application Controllers.
