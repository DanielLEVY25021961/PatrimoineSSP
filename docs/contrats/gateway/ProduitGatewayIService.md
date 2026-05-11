# Contrat local — ProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`

## 2) Rôle du port

`ProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `Produit`.

Ce port :
- manipule exclusivement des objets métier ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur ;
- expose un contrat technique utilisé par la couche SERVICE UC.

## 3) Dépendances de contrat

### 3.1) Objets métier principaux

- `levy.daniel.application.model.metier.produittype.Produit`
- `levy.daniel.application.model.metier.produittype.SousTypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<Produit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionAppliParentNull`
- `ExceptionTechniqueGateway`
- `ExceptionTechniqueGatewayNonPersistent`

## 4) Signatures exactes du port

```java
Produit creer(Produit pObject) throws Exception;

List<Produit> rechercherTous() throws Exception;

ResultatPage<Produit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

Produit findByObjetMetier(Produit pObject) throws Exception;

List<Produit> findByLibelle(String pLibelle) throws Exception;

List<Produit> findByLibelleRapide(String pContenu) throws Exception;

List<Produit> findAllByParent(SousTypeProduit pParent) throws Exception;

Produit findById(Long pId) throws Exception;

Produit update(Produit pObject) throws Exception;

void delete(Produit pObject) throws Exception;

long count() throws Exception;
```

## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie du PORT `ProduitGatewayIService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java` — 44922 octets, 1349 lignes LF, SHA-256 `7fed211146aad71d6955c33137597c3c366e83d1dec228b3e0d17060bd52e22c`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java` — 58013 octets, 1839 lignes LF, SHA-256 `98e06ad6ea52e8a993b4871ea9978f8f1805ca39d20a745c05dc63ce8afa151f`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java` — 341525 octets, 8893 lignes LF, SHA-256 `0f3b9907a677058054fa26c1cc45186f33fbcce510debef5730b7d988ad07a44`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java` — 337226 octets, 9317 lignes LF, SHA-256 `8b1b61a637a42b865caba13831608be0b0742fdfa6ac5578664c5fd5be494374`

Ce PORT ne doit jamais être recodé de mémoire. Pour toute analyse ou génération, l'IA doit relire dans cet ordre : `CONTRAT_IA.md`, `CoucheServicesGateway.md`, le présent contrat local, le PORT Java, l'ADAPTER JPA, puis les tests Mock et intégration.

### 99.2) Rôle exact à conserver

`ProduitGatewayIService` porte le contrat technique Gateway de `Produit`, objet métier enfant de `SousTypeProduit`.

Le PORT porte les messages techniques Gateway utilisés par les ADAPTERS et les tests. Ces constantes ne sont pas décoratives : elles verrouillent le texte des exceptions et doivent être conservées dans leur logique, leur nommage et leur ordre.

### 99.3) Constantes contractuelles exactes du PORT

```java
	String ANOMALIE_APPLICATIVE = "Anomalie applicative ";
	String OBJET_METIER_PARAM_NULL
		= "- l'objet métier passé en paramètre est null.";
	String OBJET_METIER_A
		= "- l'objet métier passé en paramètre a un ";
	String LIBELLE_BLANK = "libellé blank (null ou que des espaces).";
	String PAS_DANS_STOCKAGE = "pas déjà dans le stockage : ";
	String MESSAGE_CREER_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_CREER_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;
	String MESSAGE_CREER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à créer a un "
			+ LIBELLE_BLANK;
	String MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet que vous voulez créer n'existe "
			+ PAS_DANS_STOCKAGE;
	String MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL =
			ANOMALIE_APPLICATIVE
			+ "- le paramètre pObject ne doit pas être null.";
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
			ANOMALIE_APPLICATIVE
			+ "- le libellé de pObject passé en paramètre "
			+ "ne doit pas être blank.";
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à créer a un "
			+ LIBELLE_BLANK;
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
	= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet que vous voulez rechercher n'existe "
		+ PAS_DANS_STOCKAGE;
	String MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le libellé passé en paramètre est un "
			+ LIBELLE_BLANK;
	String MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ "- le contenu passé en paramètre est null.";
	String MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet passé en paramètre a un "
			+ LIBELLE_BLANK;
	String MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet n'existait "
			+ PAS_DANS_STOCKAGE;
	String ID_PARAM_NULL = "- l'identifiant passé en paramètre est null.";
	String MESSAGE_FINDBYID_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ ID_PARAM_NULL;
	String MESSAGE_UPDATE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_UPDATE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;
	String MESSAGE_UPDATE_KO_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- l'objet que vous voulez modifier n'est pas persistant "
			+ "(ID null) : ";
	String MESSAGE_UPDATE_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à modifier a un "
			+ LIBELLE_BLANK;
	String MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet que vous voulez modifier n'existe "
			+ PAS_DANS_STOCKAGE;
	String MESSAGE_DELETE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String ID_NULL = "ID null.";
	String MESSAGE_DELETE_KO_ID_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ ID_NULL;
	String MESSAGE_DELETE_KO_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- l'objet que vous voulez détruire n'existait "
			+ "pas dans le stockage : ";
	String ERREUR_TECHNIQUE_STOCKAGE
		= "Erreur Technique lors du stockage : ";
	String ERREUR_TECHNIQUE_KO_STOCKAGE
		= "Erreur Technique - Le stockage a retourné null.";
	String PREFIX_MESSAGE_CREER = "MESSAGE_CREER";
	String PREFIX_MESSAGE_FINDBYOBJETMETIER = "MESSAGE_FINDBYOBJETMETIER";
	String PREFIX_MESSAGE_FINDALLBYPARENT = "MESSAGE_FINDALLBYPARENT";
	String PREFIX_MESSAGE_UPDATE = "MESSAGE_UPDATE";
	String SUFFIXE_KO_PARENT_NULL
		= "_KO_PARENT_NULL";
	String SUFFIXE_KO_LIBELLE_PARENT_BLANK
		= "_KO_LIBELLE_PARENT_BLANK";
	String SUFFIXE_KO_PARENT_NON_PERSISTENT
		= "_KO_PARENT_NON_PERSISTENT";
```

Règle stricte : ne pas remplacer ces constantes par des messages inline, ne pas changer leur préfixe `MESSAGE_`, ne pas supprimer les constantes de fragments, ne pas déplacer les messages vers la couche UC ou Controller.

### 99.4) Signatures et ordre exacts des méthodes du PORT

```java
Produit creer(Produit pObject) throws Exception
List<Produit> rechercherTous() throws Exception
ResultatPage<Produit> rechercherTousParPage(RequetePage pRequetePage) throws Exception
Produit findByObjetMetier(Produit pObject) throws Exception
List<Produit> findByLibelle(String pLibelle) throws Exception
List<Produit> findByLibelleRapide(String pContenu) throws Exception
List<Produit> findAllByParent(SousTypeProduit pParent) throws Exception
Produit findById(Long pId) throws Exception
Produit update(Produit pObject) throws Exception
void delete(Produit pObject) throws Exception
long count() throws Exception
```

L'ordre des blocs est contractuel : `creer`, `rechercherTous`, `rechercherTousParPage`, `findByObjetMetier`, `findByLibelle`, `findByLibelleRapide`, éventuel `findAllByParent`, `findById`, `update`, `delete`, `count`.

### 99.5) Tests de référence à préserver

#### Test Mock
- ligne 477 : `testCreerNull`
- ligne 522 : `testCreerLibelleBlank`
- ligne 574 : `testCreerParentNull`
- ligne 625 : `testCreerParentLibelleBlank`
- ligne 680 : `testCreerParentIdNull`
- ligne 738 : `testCreerParentAbsent`
- ligne 801 : `testCreerParentDAOExceptionMessageNonNull`
- ligne 877 : `testCreerParentDAOExceptionMessageNull`
- ligne 950 : `testCreerDAOSaveRetourneNull`
- ligne 1020 : `testCreerDAOSaveExceptionMessageNonNull`
- ligne 1104 : `testCreerDAOSaveExceptionMessageNull`
- ligne 1187 : `testCreerDoublon`
- ligne 1283 : `testCreerParentCaracteresSpeciaux`
- ligne 1370 : `testCreerNominal`
- ligne 1453 : `testRechercherTousDAORetourneNull`
- ligne 1503 : `testRechercherTousDAORetourneVide`
- ligne 1554 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 1618 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1689 : `testRechercherTousTriDedoublonnage`
- ligne 1809 : `testRechercherTousNominal`
- ligne 1962 : `testRechercherTousParPageNull`
- ligne 2093 : `testRechercherTousParPageDAORetourneNull`
- ligne 2161 : `testRechercherTousParPageContenuNull`
- ligne 2237 : `testRechercherTousParPageDAOExceptionMessageNonNull`
- ligne 2309 : `testRechercherTousParPageDAOExceptionMessageNull`
- ligne 2383 : `testRechercherTousParPagePageSizeZero`
- ligne 2499 : `testRechercherTousParPageAvecTri`
- ligne 2651 : `testRechercherTousParPageContenuAvecNulls`
- ligne 2771 : `testRechercherTousParPagePageVide`
- ligne 2860 : `testRechercherTousParPageNominalRequeteNeutre`
- ligne 2975 : `testRechercherTousParPageNominal`
- ligne 3094 : `testFindByObjetMetierNull`
- ligne 3135 : `testFindByObjetMetierLibelleBlank`
- ligne 3188 : `testFindByObjetMetierParentNull`
- ligne 3239 : `testFindByObjetMetierParentLibelleBlank`
- ligne 3293 : `testFindByObjetMetierParentIdNull`
- ligne 3353 : `testFindByObjetMetierParentAbsent`
- ligne 3416 : `testFindByObjetMetierParentDAOExceptionMessageNonNull`
- ligne 3491 : `testFindByObjetMetierParentDAOExceptionMessageNull`
- ligne 3567 : `testFindByObjetMetierDAORetourneNull`
- ligne 3634 : `testFindByObjetMetierDAOExceptionMessageNonNull`
- ligne 3717 : `testFindByObjetMetierDAOExceptionMessageNull`
- ligne 3802 : `testFindByObjetMetierNonTrouve`
- ligne 3889 : `testFindByObjetMetierNominal`
- ligne 3987 : `testFindByLibelleNull`
- ligne 4025 : `testFindByLibelleBlank`
- ligne 4070 : `testFindByLibelleDAORetourneNull`
- ligne 4144 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 4229 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 4315 : `testFindByLibelleNonTrouve`
- ligne 4403 : `testFindByLibelleNominal`
- ligne 4540 : `testFindByLibelleRapideNull`
- ligne 4583 : `testFindByLibelleRapideBlank`
- ligne 4687 : `testFindByLibelleRapideDAORetourneNull`
- ligne 4759 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 4846 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 4934 : `testFindByLibelleRapideNonTrouve`
- ligne 5017 : `testFindByLibelleRapideNominal`
- ligne 5154 : `testFindAllByParentNull`
- ligne 5192 : `testFindAllByParentParentLibelleBlank`
- ligne 5241 : `testFindAllByParentParentIdNull`
- ligne 5294 : `testFindAllByParentParentAbsent`
- ligne 5353 : `testFindAllByParentParentDAOExceptionMessageNonNull`
- ligne 5417 : `testFindAllByParentParentDAOExceptionMessageNull`
- ligne 5482 : `testFindAllByParentDAORetourneNull`
- ligne 5545 : `testFindAllByParentDAOExceptionMessageNonNull`
- ligne 5620 : `testFindAllByParentDAOExceptionMessageNull`
- ligne 5696 : `testFindAllByParentNonTrouve`
- ligne 5769 : `testFindAllByParentAvecDoublons`
- ligne 5888 : `testFindAllByParentNominal`
- ligne 6025 : `testFindByIdNull`
- ligne 6068 : `testFindByIdDAORetourneNull`
- ligne 6123 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 6193 : `testFindByIdDAOExceptionMessageNull`
- ligne 6264 : `testFindByIdNonTrouve`
- ligne 6331 : `testFindByIdNominal`
- ligne 6416 : `testUpdateNull`
- ligne 6448 : `testUpdateLibelleNull`
- ligne 6486 : `testUpdateLibelleBlank`
- ligne 6525 : `testUpdateIdNull`
- ligne 6565 : `testUpdateParentNull`
- ligne 6601 : `testUpdateParentLibelleBlank`
- ligne 6641 : `testUpdateParentIdNull`
- ligne 6685 : `testUpdateParentAbsent`
- ligne 6737 : `testUpdateParentDAOExceptionMessageNonNull`
- ligne 6795 : `testUpdateParentDAOExceptionMessageNull`
- ligne 6852 : `testUpdateDAOFindByIdRetourneNull`
- ligne 6907 : `testUpdateAbsent`
- ligne 6965 : `testUpdateDAOFindByIdExceptionMessageNonNull`
- ligne 7027 : `testUpdateDAOFindByIdExceptionMessageNull`
- ligne 7087 : `testUpdateDAOSaveRetourneNull`
- ligne 7147 : `testUpdateDAOSaveExceptionMessageNonNull`
- ligne 7215 : `testUpdateDAOSaveExceptionMessageNull`
- ligne 7282 : `testUpdateSansModification`
- ligne 7347 : `testUpdateParentLibelleCaseSensitive`
- ligne 7409 : `testUpdateModificationCasse`
- ligne 7486 : `testUpdateParentModifie`
- ligne 7568 : `testUpdateNominal`
- ligne 7646 : `testDeleteNull`
- ligne 7679 : `testDeleteIdNull`
- ligne 7721 : `testDeleteDAOFindByIdRetourneNull`
- ligne 7790 : `testDeleteAbsent`
- ligne 7858 : `testDeleteDAOFindByIdExceptionMessageNonNull`
- ligne 7944 : `testDeleteDAOFindByIdExceptionMessageNull`
- ligne 8038 : `testDeleteDAODeleteExceptionMessageNonNull`
- ligne 8130 : `testDeleteDAODeleteExceptionMessageNull`
- ligne 8224 : `testDeleteDAOFlushExceptionMessageNonNull`
- ligne 8312 : `testDeleteDAOFlushExceptionMessageNull`
- ligne 8399 : `testDeleteNominal`
- ligne 8466 : `testCountStockageVide`
- ligne 8524 : `testCountDAOExceptionMessageNonNull`
- ligne 8597 : `testCountDAOExceptionMessageNull`
- ligne 8676 : `testCountNominal`
- ligne 8730 : `testSanitySafeMessage`

#### Test intégration
- ligne 1239 : `testCreerNull`
- ligne 1298 : `testCreerLibelleNull`
- ligne 1377 : `testCreerLibelleBlank`
- ligne 1456 : `testCreerParentNull`
- ligne 1524 : `testCreerParentLibelleNull`
- ligne 1607 : `testCreerParentLibelleBlank`
- ligne 1690 : `testCreerParentIdNull`
- ligne 1775 : `testCreerParentAbsent`
- ligne 1880 : `testCreerDoublon`
- ligne 2017 : `testCreerNominal`
- ligne 2220 : `testCreerPlusieurs`
- ligne 2448 : `testRechercherTousStockageVide`
- ligne 2523 : `testRechercherTousNominal`
- ligne 2705 : `testRechercherTousParPageNull`
- ligne 2808 : `testRechercherTousParPageAvecTri`
- ligne 2937 : `testRechercherTousParPageStockageVide`
- ligne 2998 : `testRechercherTousParPageTailleSuperieureAuTotal`
- ligne 3090 : `testRechercherTousParPagePageHorsBorne`
- ligne 3165 : `testRechercherTousParPageTailleZero`
- ligne 3273 : `testRechercherTousParPageNominal`
- ligne 3392 : `testFindByObjetMetierNull`
- ligne 3447 : `testFindByObjetMetierLibelleNull`
- ligne 3558 : `testFindByObjetMetierLibelleBlank`
- ligne 3669 : `testFindByObjetMetierParentNull`
- ligne 3735 : `testFindByObjetMetierParentLibelleNull`
- ligne 3833 : `testFindByObjetMetierParentLibelleBlank`
- ligne 3932 : `testFindByObjetMetierParentIdNull`
- ligne 4028 : `testFindByObjetMetierParentAbsent`
- ligne 4136 : `testFindByObjetMetierNonTrouve`
- ligne 4268 : `testFindByObjetMetierNominal`
- ligne 4414 : `testFindByObjetMetierIdIgnoreCasseIgnoree`
- ligne 4625 : `testFindByLibelleNull`
- ligne 4682 : `testFindByLibelleBlank`
- ligne 4738 : `testFindByLibelleNonTrouve`
- ligne 4823 : `testFindByLibelleCaseInsensitive`
- ligne 4959 : `testFindByLibelleNominal`
- ligne 5085 : `testFindByLibelleRapideNull`
- ligne 5144 : `testFindByLibelleRapideBlank`
- ligne 5245 : `testFindByLibelleRapideNonTrouve`
- ligne 5330 : `testFindByLibelleRapideCaseInsensitive`
- ligne 5464 : `testFindByLibelleRapideDedoublonnage`
- ligne 5576 : `testFindByLibelleRapideNominal`
- ligne 5703 : `testFindAllByParentNull`
- ligne 5760 : `testFindAllByParentParentLibelleNull`
- ligne 5825 : `testFindAllByParentParentLibelleBlank`
- ligne 5891 : `testFindAllByParentParentIdNull`
- ligne 5971 : `testFindAllByParentParentAbsent`
- ligne 6064 : `testFindAllByParentParentSansEnfant`
- ligne 6206 : `testFindAllByParentNominal`
- ligne 6366 : `testFindByIdNull`
- ligne 6422 : `testFindByIdNonTrouve`
- ligne 6501 : `testFindByIdNominal`
- ligne 6611 : `testFindByIdIdCree`
- ligne 6759 : `testUpdateNull`
- ligne 6815 : `testUpdateLibelleNull`
- ligne 6888 : `testUpdateLibelleBlank`
- ligne 6962 : `testUpdateIdNull`
- ligne 7039 : `testUpdateParentNull`
- ligne 7112 : `testUpdateParentLibelleNull`
- ligne 7197 : `testUpdateParentLibelleBlank`
- ligne 7283 : `testUpdateParentIdNull`
- ligne 7369 : `testUpdateParentAbsent`
- ligne 7465 : `testUpdateAbsent`
- ligne 7558 : `testUpdateSansModification`
- ligne 7666 : `testUpdateNominal`
- ligne 7820 : `testUpdateParentModifie`
- ligne 7981 : `testDeleteNull`
- ligne 8037 : `testDeleteIdNull`
- ligne 8112 : `testDeleteAbsent`
- ligne 8211 : `testDeleteNominal`
- ligne 8374 : `testDeleteDoubleSuppression`
- ligne 8567 : `testCountStockageVide`
- ligne 8643 : `testCountNominal`
- ligne 8726 : `testCountApresCreationPuisSuppression`

Règle d'autonomie : pour recoder le PORT, l'IA doit vérifier que chaque constante et chaque signature expliquent au moins un bloc de tests. Si un message est utilisé par un test d'exception, il doit rester dans le PORT et non dans l'ADAPTER.
