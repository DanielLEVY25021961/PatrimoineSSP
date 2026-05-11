# docs/contrats/cu/TypeProduitICuService.md

# Contrat comportemental — TypeProduitICuService (SERVICE METIER UC)

## 1) Port concerné

- `levy.daniel.application.model.services.produittype.cu.TypeProduitICuService`

## 2) Objet du contrat

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
- convertit les `InputDTO` en objets métier lorsque nécessaire ;
- délègue les opérations techniques au `GATEWAY` ;
- convertit les objets métier retournés en `OutputDTO` lorsque nécessaire ;
- produit des messages utilisateur via `getMessage()` ;
- émet les LOGs nécessaires à la traçabilité ;
- retourne un résultat exploitable par la couche appelante.

### Règles structurantes

- le `GATEWAY` ne produit pas de message utilisateur ;
- le `SERVICE UC` est responsable du **message observable** côté appelant ;
- les messages récurrents doivent être **factorisés dans le PORT** ;
- le comportement observable décrit dans le PORT doit rester cohérent avec l’ADAPTER et avec les tests.

## 4) Source de vérité de ce contrat

Ce contrat est rattaché explicitement au port `TypeProduitICuService`.

Ordre d’interprétation :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/TypeProduitICuService.md`
3. le code du PORT `TypeProduitICuService`
4. le code de l’ADAPTER `TypeProduitCuService`
5. les tests JUnit Mock et Intégration

En cas d’ambiguïté :

- priorité au **comportement réellement verrouillé par les tests** ;
- puis au **présent contrat** ;
- puis au code.

## 5) Méthodes PORT déjà normalisées servant de référence

Les méthodes suivantes sont considérées comme **références de formalisme UC**
dans le PORT `TypeProduitICuService` :

- `TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;`
- `List<String> rechercherTousString() throws Exception;`
- `ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage(RequetePage pRequetePage) throws Exception;`
- `TypeProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`
- `TypeProduitDTO.OutputDTO findByDTO(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `TypeProduitDTO.OutputDTO findById(Long pId) throws Exception;`
- `TypeProduitDTO.OutputDTO update(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `void delete(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `long count() throws Exception;`
- `String getMessage();`

### Règle absolue

Toute nouvelle remise au carré du PORT UC doit s’aligner
sur le formalisme de ces méthodes déjà normalisées.

## 6) Statut des méthodes du PORT

Aucune méthode du PORT `TypeProduitICuService`
ne doit désormais être considérée comme **legacy**.

Conséquence :

- toutes les méthodes du PORT sont désormais des **références de formalisme UC** ;
- aucune régression documentaire ou comportementale n’est acceptable ;
- toute correction future doit maintenir l’homogénéité entre méthodes.

## 7) Séquence de lecture obligatoire avant toute correction

Avant toute rédaction, analyse, correction ou génération de code
concernant une méthode UC, l’IA doit relire, dans cet ordre :

1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/cu/TypeProduitICuService.md`
3. les méthodes déjà normalisées du PORT listées au §5
4. la méthode UC cible dans le PORT
5. la méthode correspondante dans l’ADAPTER
6. les tests Mock
7. les tests d’Intégration

### Interdictions absolues

- ne jamais inventer un nouveau style ;
- ne jamais dégrader une méthode déjà normalisée ;
- ne jamais se baser sur une ancienne réponse de chat plutôt que sur les fichiers relus ;
- ne jamais contourner le contrat local au motif qu’une méthode “semble simple”.

## 7 bis) Formalisme obligatoire des commentaires de bloc dans l’ADAPTER UC

Avant toute rédaction, analyse, correction ou génération de code concernant une méthode UC TypeProduit dans l’ADAPTER `TypeProduitCuService`, l’IA doit relire, en plus de la séquence du §7 :

1. la méthode cible dans `TypeProduitCuService` ;
2. les méthodes déjà validées de `TypeProduitCuService` portant un scénario comparable ;
3. si `TypeProduitCuService` ne fournit pas assez d’exemples stables, une ou plusieurs méthodes déjà validées de `SousTypeProduitCuService` ou `ProduitCuService` portant un scénario comparable.

### 7 bis.1) Règle absolue

Les commentaires de bloc dans l’ADAPTER UC ne doivent jamais être inventés.
Ils doivent être déduits du code validé réellement relu.

### 7 bis.2) Règles obligatoires

Dans `TypeProduitCuService`, un commentaire de bloc doit :

- annoncer exactement ce que fait le bloc situé juste dessous ;
- rester factuel, concret et opérationnel ;
- décrire le comportement observable côté UC ;
- reprendre les constantes, messages et exceptions réellement utilisés quand ils structurent le comportement ;
- distinguer explicitement :
  - l’erreur utilisateur bénigne,
  - la précondition bloquante,
  - la délégation,
  - la sécurisation technique,
  - la préparation de la réponse,
  - le positionnement du message observable,
  - le retour final.

### 7 bis.3) Formes attendues

Lorsque le scénario s’y prête, les commentaires doivent reprendre des formes du type :

- `Erreur utilisateur bénigne : ...`
- `Si ... : émet MESSAGE_X + LOG + ExceptionY.`
- `Délègue au GATEWAY ...`
- `Une réponse technique null du GATEWAY est une anomalie ...`
- `Retire les null, trie ...`
- `Positionne le message observable ...`
- `retourne ...`

### 7 bis.4) Interdictions absolues

Dans `TypeProduitCuService`, il est interdit :

- d’inventer un nouveau style de commentaires ;
- d’écrire des commentaires philosophiques, vagues ou décoratifs ;
- d’écrire un commentaire plus faible que ceux des méthodes déjà validées ;
- d’utiliser une formule qui n’est pas confirmée par les méthodes de référence relues ;
- de commenter l’intention générale de la méthode au lieu du bloc concret réellement exécuté.

## 7 ter) Règles anti-régression de génération

Avant toute génération de code, l’IA doit aussi relire les règles sacrées du `CONTRAT_IA.md`
sur les 5 points suivants :

1. comparaisons de chaînes du projet :
   ne jamais utiliser `StringUtils.equalsIgnoreCase(...)`,
   utiliser `Strings.CI.equals(...)` / `Strings.CI.compare(...)` ;

2. commentaires de bloc ADAPTER UC :
   reproduire le style validé,
   sans commentaire vague ni inventé ;

3. Mockito strict :
   aucun stub inutile n’est toléré ;

4. constantes de tests :
   réutiliser ou poser les constantes dans la zone des constantes,
   jamais de littéraux métier dispersés ;

5. preuve dans le stockage en intégration :
   conserver le niveau de preuve SQL directe via `JdbcTemplate`
   lorsque ce niveau de preuve est déjà validé dans le projet.

## 8) Formalisme javadoc obligatoire dans le PORT UC

### 8.1 Structure obligatoire

Toute méthode UC du PORT doit comporter, dans sa javadoc,
**dans cet ordre exact** :

1. une **phrase d’ouverture** ;
2. une rubrique **INTENTION DE SERVICE UC (scénario nominal)** ;
3. une rubrique **CONTRAT DE SERVICE UC** ;
4. une rubrique **GARANTIES METIER, UTILISATEUR et TRAÇABILITE** ;
5. un bloc détaillé `@param / @return / @throws` lorsque applicable.

Aucune de ces rubriques ne doit manquer.

### 8.2 Habillage HTML obligatoire pour la lisibilité

Afin d’éviter les rubriques invisibles
et d’obtenir un rendu homogène dans la javadoc,
la partie descriptive doit être encapsulée
dans une structure HTML explicite :

- un bloc racine `<div> ... </div>` ;
- des paragraphes `<p> ... </p>` pour la phrase d’ouverture ;
- un paragraphe `<p><strong>...</strong></p>`
  pour **chaque titre de rubrique** ;
- des listes `<ul><li>...</li></ul>`
  pour les points de scénario, de contrat et de garanties.

### 8.3 Phrase d’ouverture

La phrase d’ouverture doit :

- commencer par un verbe d’action clair ;
- nommer le type principal manipulé lorsque c’est pertinent ;
- résumer la finalité observable de la méthode ;
- rester courte et descriptive.

### 8.4 Rubrique INTENTION DE SERVICE UC (scénario nominal)

Cette rubrique décrit le **déroulé nominal**
du point de vue orchestration UC.

Elle doit :

- partir de ce que reçoit ou fait la couche UC ;
- expliciter les validations utiles ;
- expliciter la délégation au `GATEWAY` ou à une autre méthode UC ;
- expliciter la préparation de la réponse finale ;
- se terminer par la restitution d’une réponse exploitable.

Elle ne doit pas :

- détailler les exceptions une première fois ;
- mélanger nominal et cas d’erreur ;
- parler de bas niveau de persistance.

### 8.5 Rubrique CONTRAT DE SERVICE UC

Cette rubrique décrit les **cas observables**.

Elle doit :

- énumérer les cas d’entrée problématique lorsque la méthode a des paramètres ;
- préciser, pour chaque cas, le triplet :
  - retour observable,
  - message utilisateur,
  - LOG / exception / absence de LOG / absence d’exception ;
- expliciter la délégation lorsqu’elle existe ;
- expliciter les cas métier ;
- expliciter le cas nominal de succès ;
- expliciter le comportement en cas de panne technique remontée.

Le contrat doit être **falsifiable par les tests** :
chaque point écrit ici doit pouvoir être vérifié
dans les tests Mock et/ou Intégration.

### 8.6 Rubrique GARANTIES METIER, UTILISATEUR et TRAÇABILITE

Cette rubrique décrit ce que la méthode garantit
sur la qualité de la réponse.

Elle doit comporter, selon le cas,
des garanties du type :

- le message retourné par `getMessage()` reflète l’issue observable ;
- le message de succès n’est positionné qu’après traitement complet ;
- le DTO ou la liste retournée correspond à l’état métier effectivement accessible ;
- aucun résultat partiel incohérent n’est exposé à l’appelant ;
- la réponse déléguée reste alignée sur la méthode appelée ;
- le getter `getMessage()` reste un pur reflet d’état local.

### 8.7 Bloc `@param / @return / @throws`

Le bloc de tags doit être complet et détaillé.

#### `@param`

Chaque paramètre doit préciser :

- son type ;
- son rôle métier/technique ;
- ce qu’il représente dans le scénario UC.

#### `@return`

Le retour doit préciser :

- le type réellement retourné ;
- le cas nominal ;
- les cas où `null` est autorisé ;
- le caractère éventuellement vide / jamais null
  pour les listes et résultats paginés.

#### `@throws`

Les exceptions doivent être détaillées par catégorie :

- exception de validation observable ;
- exception métier ;
- exception technique ;
- `IllegalStateException` en cas d’incohérence finale de réponse ;
- `Exception` de garde si l’implémentation le prévoit encore.

### 8.8 Règles rédactionnelles obligatoires

La rédaction javadoc du PORT UC doit respecter les règles suivantes :

- français clair et homogène ;
- vocabulaire stable d’une méthode à l’autre ;
- même intitulé exact de rubrique d’une méthode à l’autre ;
- mêmes conventions d’écriture pour `null`, `blank`, `GATEWAY`, `DTO` ;
- références `{@link ...}` sur les types, constantes et méthodes citées ;
- formulations observables et testables ;
- pas de formule vague du type
  « fait le nécessaire », « traite la demande », « gère le cas ».

### 8.9 Interdictions absolues dans la javadoc du PORT

Il est interdit :

- d’enlever le bloc `<div>` ;
- d’enlever les `<p>` portant les titres de rubriques ;
- de fusionner les rubriques en un texte libre ;
- de remplacer `CONTRAT DE SERVICE UC`
  par `CONTRAT (métier / observable)` sur une méthode remise au carré ;
- de supprimer la rubrique `GARANTIES ...` ;
- de livrer une javadoc réduite à quelques puces techniques ;
- de produire une javadoc plus pauvre qu’une méthode déjà normalisée ;
- de dégrader les signatures documentées en types bruts.

### 8.10 Gabarit canonique à reproduire

```java
/**
 * <div>
 * <p>Phrase d’ouverture décrivant la finalité observable de la méthode.</p>
 *
 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
 * <ul>
 * <li>décrire la réception ou le déclenchement côté UC ;</li>
 * <li>décrire les validations utiles ;</li>
 * <li>décrire la délégation au GATEWAY ou à une autre méthode UC ;</li>
 * <li>décrire la préparation de la réponse ;</li>
 * <li>décrire la restitution d’une réponse exploitable.</li>
 * </ul>
 *
 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
 * <ul>
 * <li>cas d’entrée problématique : retour, message, LOG, exception ;</li>
 * <li>cas métier ;</li>
 * <li>cas nominal de succès ;</li>
 * <li>cas d’échec technique remonté par le GATEWAY
 * ou par la préparation de la réponse.</li>
 * </ul>
 *
 * <p><strong>GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</strong></p>
 * <ul>
 * <li>le message reflète l’issue observable ;</li>
 * <li>le succès n’est positionné qu’après traitement complet ;</li>
 * <li>la réponse retournée correspond à l’état métier réellement accessible ;</li>
 * <li>aucun résultat partiel incohérent n’est exposé à l’appelant.</li>
 * </ul>
 * </div>
 *
 * @param pXxx : Type :
 * signification métier/technique du paramètre.
 * @return TypeRetour :
 * description du retour nominal et des cas `null` / vide autorisés.
 * @throws ExceptionX
 * si ...
 * @throws ExceptionY
 * si ...
 * @throws Exception
 * toute autre exception levée par l’implémentation.
 */
```

## 9) Règles obligatoires dans l’ADAPTER UC

L’implémentation de l’ADAPTER UC doit :

- consommer les constantes de messages définies dans le PORT ;
- rationaliser les messages utilisateur techniques ;
- distinguer clairement :
  - erreur bénigne,
  - erreur métier,
  - erreur technique ;
- ne positionner le message de succès **qu’en fin de scénario complet** ;
- aligner strictement le comportement observable
  sur le contrat décrit dans la javadoc du PORT ;
- comporter des **commentaires de bloc factuels et opérationnels**.

### Règles de style additionnelles

- les commentaires de bloc doivent être quasi-contractuels ;
- éviter les formulations prétentieuses ou floues ;
- ne jamais appeler `IllegalStateException`
  une « exception applicative » ;
- préférer des formulations du type :
  `Si pXxx == null : positionne MESSAGE_X + LOG + exception`
  quand c’est effectivement le comportement.

## 10) Règles obligatoires dans les tests Mock

Les tests Mock doivent couvrir, selon le contrat de la méthode :

- scénario nominal ;
- violation de contrat ;
- erreur bénigne ;
- erreur métier ;
- erreur technique ;
- message utilisateur final ;
- délégation correcte vers le `GATEWAY`
  ou vers une autre méthode UC ;
- absence d’interaction technique
  lorsqu’aucune interaction ne doit avoir lieu.

Le nom des tests doit rendre visible
le cas contractuel couvert.

## 11) Règles obligatoires dans les tests d’Intégration

Les tests d’Intégration doivent couvrir, selon le contrat de la méthode :

- comportement observable ;
- message final exact ;
- exceptions exactes lorsque c’est intégrablement testable ;
- cohérence du DTO ou de la liste retournée ;
- **preuve physique dans le stockage** lorsque la méthode écrit ou détruit en stockage ;
- absence d’effet de bord lorsqu’aucune écriture ne doit se produire.

Pour une méthode de lecture ou d’agrégation pure,
la preuve d’intégration doit au minimum démontrer :

- la réalité du résultat ;
- ou l’absence réelle de donnée correspondante ;
- et la cohérence du message final.

## 12) Règle spécifique à `getMessage()`

La méthode `getMessage()` est un **getter du message courant local**
du SERVICE METIER UC.

### Contrat spécifique

- `getMessage()` peut retourner `null`
  avant toute opération ayant positionné un message ;
- `getMessage()` retourne ensuite
  le **dernier message observable**
  posé par l’opération UC la plus récente ;
- ce message peut correspondre :
  - à un succès,
  - à une absence de résultat,
  - à une erreur bénigne,
  - à une erreur métier,
  - à une erreur technique ;
- `getMessage()` ne délègue jamais au `GATEWAY` ;
- `getMessage()` ne modifie aucun état métier ;
- `getMessage()` n’émet aucun LOG ;
- `getMessage()` ne doit lever aucune exception.

### Conséquence de test

Les tests Mock et Intégration doivent verrouiller :

- l’état initial potentiellement `null` ;
- la restitution d’un message d’erreur ;
- la restitution d’un message de succès ;
- la règle **« le dernier message gagne »**.

## 13) Ordre de traitement contractuel d’une méthode UC

Pour toute méthode UC,
l’ordre de travail obligatoire est :

1. PORT UC
2. ADAPTER UC
3. tests Mock
4. tests d’Intégration
5. mise à jour du contrat local si nécessaire

Aucune étape ultérieure ne doit être engagée
si l’étape précédente n’est pas stabilisée.

## 14) Règle de non-régression documentaire

Dès qu’une méthode UC a été remise au carré :

- sa javadoc du PORT devient une **référence stable** ;
- toute méthode ultérieure de même nature
  doit s’y aligner ;
- aucune nouvelle correction ne doit réintroduire
  un formalisme plus pauvre ;
- aucune signature documentée ne doit être redégradée
  en type brut ou en forme abrégée.

## 15) Objectif de cette sacralisation

L’objectif n’est pas seulement de documenter les méthodes,
mais de rendre impossible :

- la régression de style ;
- la disparition visuelle des rubriques ;
- la perte de traçabilité des cas observables ;
- la dérive entre PORT, ADAPTER et tests ;
- les réponses d’IA “à peu près correctes” mais non conformes.

Ce document est donc le **référentiel opérationnel**
à relire avant toute correction
sur `TypeProduitICuService`.

## Annexe IA — autonomie progressive `TypeProduit`

Cette annexe complète le contrat local pendant la phase de correction de la couche UC. Elle ne remplace jamais la relecture obligatoire du PORT Java, de l'ADAPTER UC et des tests concernés.

### A.1) Constantes du PORT UC à conserver

| Constante | Valeur littérale Java validée |
|---|---|
| `TIRET_ESPACE` | `" - "` |
| `PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER` | `"Impossible de vérifier l'unicité " + "du Type de Produit dans le stockage : "` |
| `PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER` | `"Impossible de créer le Type de Produit dans le stockage : "` |
| `MESSAGE_CREATION_TECHNIQUE_KO_CREER` | `"Impossible de créer le Type de Produit - " + "le stockage n'a retourné aucun objet créé."` |
| `PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER` | `"Impossible de préparer la réponse utilisateur " + "après la création du Type de Produit : "` |
| `MESSAGE_CONVERSION_TECHNIQUE_KO_CREER` | `"Impossible de préparer la réponse utilisateur " + "après la création du Type de Produit."` |
| `MESSAGE_PARAM_BLANK` | `"Vous avez passé une chaine " + "de caractères blank (null ou que des espaces) en paramètre."` |
| `MSG_ERREUR_NON_SPECIFIEE` | `"Erreur non spécifiée"` |
| `MESSAGE_MODIF_KO` | `"KO - la modification a retourné null : "` |
| `MESSAGE_MODIF_OK` | `"OK - modification réussie de : "` |
| `MESSAGE_DELETE_OK` | `"OK - destruction réussie de : "` |
| `MESSAGE_DELETE_KO` | `"KO - échec de la destruction de : "` |
| `METHODE_CREER` | `"méthode Creer(...)"` |
| `METHODE_RECHERCHER_TOUS` | `"méthode rechercherTous()"` |
| `METHODE_FIND_BY_LIBELLE` | `"méthode findByLibelle(...)"` |
| `METHODE_FIND_BY_LIBELLE_RAPIDE` | `"méthode findByLibelleRapide()"` |
| `METHODE_FIND_BY_ID` | `"méthode findById(...)"` |
| `METHODE_UPDATE` | `"méthode update(...)"` |
| `METHODE_DELETE` | `"méthode delete(...)"` |
| `METHODE_COUNT` | `"méthode count()"` |

Ces constantes sont observables via les tests UC et les messages `getMessage()`. L'IA ne doit pas renommer, reformuler ou remplacer ces messages par une formulation équivalente.

### A.2) Signatures du PORT UC à conserver

- `TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;`
- `List<String> rechercherTousString() throws Exception;`
- `ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage( RequetePage pRequetePage) throws Exception;`
- `TypeProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;`
- `List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;`
- `TypeProduitDTO.OutputDTO findByDTO( TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `TypeProduitDTO.OutputDTO findById(Long pId) throws Exception;`
- `TypeProduitDTO.OutputDTO update( TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `void delete(TypeProduitDTO.InputDTO pInputDTO) throws Exception;`
- `long count() throws Exception;`

### A.3) ADAPTER UC associé

### `TypeProduitCuService` — rappel local

#### Constructeur validé

- `public TypeProduitCuService( @Qualifier("TypeProduitGatewayJPAService") final TypeProduitGatewayIService pGateway)`

Règle : conserver cette injection telle que validée. Ne pas remplacer par une injection de champ, un constructeur vide ou une dépendance inventée.

#### Attributs structurants

- `private final TypeProduitGatewayIService gateway;`

#### Ordre exact des méthodes publiques validées

1. `public TypeProduitDTO.OutputDTO creer( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
2. `public List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception`
3. `public List<String> rechercherTousString() throws Exception`
4. `public ResultatPage<OutputDTO> rechercherTousParPage( final RequetePage pRequetePage) throws Exception`
5. `public OutputDTO findByLibelle(final String pLibelle) throws Exception`
6. `public List<OutputDTO> findByLibelleRapide( final String pContenu) throws Exception`
7. `public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception`
8. `public OutputDTO findById(final Long pId) throws Exception`
9. `public TypeProduitDTO.OutputDTO update( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
10. `public void delete(final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
11. `public long count() throws Exception`
12. `public String getMessage()`

#### Helpers privés / internes à ne pas oublier

- `private boolean isDoublon( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
- `private TypeProduit convertirInputDTOEnMetier( final TypeProduitDTO.InputDTO pInputDTO)`
- `private List<TypeProduit> filtrerEtTrier( final List<TypeProduit> pSource)`
- `private List<TypeProduitDTO.OutputDTO> convertirEtDedoublonner( final List<TypeProduit> pMetiers)`
- `private void alimenterMessageDepuisGateway()`
- `private long safeTotalElements( final ResultatPage<TypeProduit> pResultatPage)`
- `private <T> T traiterErreur( final String pMessage, final String pMethode, final Exception pE) throws Exception`
- `private boolean isErreurMetierAttendue( final String pMessage, final Exception pException)`

Ces helpers sont contractuels pour l'autonomie IA : ils ne doivent pas être supprimés, fusionnés ou remplacés par une version approximative sans relire le code validé et les tests concernés.

### A.4) Matrice Mock UC actuelle

| Bloc | Nombre de tests | Méthodes de test |
|---|---:|---|
| `creer` | 11 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerDoublon`<br>`testCreerControleDoublonKOAvecMessage`<br>`testCreerControleDoublonKOSansMessage`<br>`testCreerGatewayCreerKOAvecMessage`<br>`testCreerGatewayCreerKOSansMessage`<br>`testCreerGatewayCreerKORetourNull`<br>`testCreerConversionOutputDTOKOAvecMessage`<br>`testCreerConversionOutputDTOKOSansMessage`<br>`testCreerNominal` |
| `rechercherTous` | 7 | `testRechercherTousGatewayRetourNull`<br>`testRechercherTousGatewayKOAvecMessage`<br>`testRechercherTousGatewayKOSansMessage`<br>`testRechercherTousConversionOutputDTOKOAvecMessage`<br>`testRechercherTousConversionOutputDTOKOSansMessage`<br>`testRechercherTousVideApresFiltrage`<br>`testRechercherTousNominal` |
| `rechercherTousString` | 8 | `testRechercherTousStringGatewayRetourNull`<br>`testRechercherTousStringGatewayKOAvecMessage`<br>`testRechercherTousStringGatewayKOSansMessage`<br>`testRechercherTousStringConversionStringKOAvecMessage`<br>`testRechercherTousStringConversionStringKOSansMessage`<br>`testRechercherTousStringVideApresFiltrage`<br>`testRechercherTousStringVideApresLibellesBlank`<br>`testRechercherTousStringNominal` |
| `rechercherTousParPage` | 8 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageGatewayKOAvecMessage`<br>`testRechercherTousParPageGatewayKOSansMessage`<br>`testRechercherTousParPageGatewayRetourNull`<br>`testRechercherTousParPageConversionOutputDTOKOAvecMessage`<br>`testRechercherTousParPageConversionOutputDTOKOSansMessage`<br>`testRechercherTousParPageVideApresFiltrage`<br>`testRechercherTousParPageNominal` |
| `findByLibelle` | 8 | `testFindByLibelleNull`<br>`testFindByLibelleBlank`<br>`testFindByLibelleGatewayRetourNull`<br>`testFindByLibelleGatewayKOAvecMessage`<br>`testFindByLibelleGatewayKOSansMessage`<br>`testFindByLibelleConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleConversionOutputDTOKOSansMessage`<br>`testFindByLibelleNominal` |
| `findByLibelleRapide` | 9 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideGatewayKOAvecMessage`<br>`testFindByLibelleRapideGatewayKOSansMessage`<br>`testFindByLibelleRapideGatewayRetourNull`<br>`testFindByLibelleRapideConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleRapideConversionOutputDTOKOSansMessage`<br>`testFindByLibelleRapideVideApresFiltrage`<br>`testFindByLibelleRapideNominal` |
| `findByDTO` | 9 | `testFindByDTONull`<br>`testFindByDTOLibelleNull`<br>`testFindByDTOBlank`<br>`testFindByDTOGatewayRetourNull`<br>`testFindByDTOGatewayKOAvecMessage`<br>`testFindByDTOGatewayKOSansMessage`<br>`testFindByDTOConversionOutputDTOKOAvecMessage`<br>`testFindByDTOConversionOutputDTOKOSansMessage`<br>`testFindByDTONominal` |
| `findById` | 7 | `testFindByIdNull`<br>`testFindByIdGatewayRetourNull`<br>`testFindByIdGatewayKOAvecMessage`<br>`testFindByIdGatewayKOSansMessage`<br>`testFindByIdConversionOutputDTOKOAvecMessage`<br>`testFindByIdConversionOutputDTOKOSansMessage`<br>`testFindByIdNominal` |
| `update` | 14 | `testUpdateNull`<br>`testUpdateLibelleNull`<br>`testUpdateBlank`<br>`testUpdateRechercheKOAvecMessage`<br>`testUpdateRechercheKOSansMessage`<br>`testUpdateIntrouvable`<br>`testUpdateNonPersistant`<br>`testUpdateModificationKOAvecMessage`<br>`testUpdateModificationKOSansMessage`<br>`testUpdateModificationRetourNull`<br>`testUpdateModificationRetourNonPersistant`<br>`testUpdateConversionOutputDTOKOAvecMessage`<br>`testUpdateConversionOutputDTOKOSansMessage`<br>`testUpdateNominal` |
| `delete` | 10 | `testDeleteNull`<br>`testDeleteLibelleNull`<br>`testDeleteBlank`<br>`testDeleteRechercheKOAvecMessage`<br>`testDeleteRechercheKOSansMessage`<br>`testDeleteIntrouvable`<br>`testDeleteNonPersistant`<br>`testDeleteDestructionKOAvecMessage`<br>`testDeleteDestructionKOSansMessage`<br>`testDeleteNominal` |
| `count` | 5 | `testCountGatewayKOAvecMessage`<br>`testCountGatewayKOSansMessage`<br>`testCountRetourNegatif`<br>`testCountZero`<br>`testCountNominal` |
| `getMessage` | 5 | `testGetMessageInitialNull`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageApresCountZero`<br>`testGetMessageApresCountNominal`<br>`testGetMessageDernierMessageGagne` |

### A.5) Matrice Intégration UC actuelle

| Bloc | Nombre de tests | Méthodes de test |
|---|---:|---|
| `creer` | 6 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerOkAvecPreuveBdEtRoundTrip`<br>`testCreerDoublonAvecPreuveBd`<br>`testCreerOkFindByLibelleOkFindByIdOk`<br>`testCreerDoublon` |
| `rechercherTous` | 3 | `testRechercherTous`<br>`testRechercherTousOkAvecPreuveBd`<br>`testRechercherTousVide` |
| `rechercherTousString` | 3 | `testRechercherTousString`<br>`testRechercherTousStringOkAvecPreuveBd`<br>`testRechercherTousStringVide` |
| `rechercherTousParPage` | 3 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageOk`<br>`testRechercherTousParPageOkAvecPreuveBd` |
| `findByLibelle` | 3 | `testFindByLibelleBlank`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleOkAvecPreuveBd` |
| `findByLibelleRapide` | 4 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideIntrouvable`<br>`testFindByLibelleRapideOkAvecPreuveBd` |
| `findByDTO` | 4 | `testFindByDTONull`<br>`testFindByDTOBlank`<br>`testFindByDTOIntrouvable`<br>`testFindByDTOOkAvecPreuveBd` |
| `findById` | 3 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdOkAvecPreuveBd` |
| `update` | 4 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateIntrouvable`<br>`testUpdateOkAvecPreuveBdEtIdConserve` |
| `delete` | 4 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteIntrouvable`<br>`testDeleteOkAvecPreuveBd` |
| `count` | 2 | `testCountRetourneLeNombrePhysiqueEtLeMessageObservable`<br>`testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage` |
| `getMessage` | 4 | `testGetMessageInitialNull`<br>`testGetMessageApresSuccesReel`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageDernierMessageGagne` |

### A.6) Règle de correction locale

Pour corriger ou coder un bloc `TypeProduit`, l'IA doit :

1. relire le présent contrat ;
2. relire le PORT UC Java ;
3. relire l'ADAPTER UC associé ;
4. relire le Gateway utilisé par le bloc ;
5. relire les DTO/convertisseurs et exceptionsservices utiles ;
6. relire les tests déjà validés du même bloc ;
7. reprendre le nommage, l'ordre, la Javadoc et les commentaires du bloc de référence le plus proche ;
8. livrer uniquement un bloc complet dans le chat pour les méthodes Java ou un fichier complet individuel pour les contrats fragiles.
