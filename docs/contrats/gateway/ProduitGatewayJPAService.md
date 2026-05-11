# Contrat local — ProduitGatewayJPAService

## 1) Adapter concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`

## 2) Port concerné

- `ProduitGatewayIService`

## 3) Objectif

Décrire le pivot comportemental local du gateway JPA `ProduitGatewayJPAService` pour la sacralisation de `couche_services.gateway`.

Le présent contrat local doit être relu conjointement avec :
- le PORT `ProduitGatewayIService` ;
- l'implémentation `ProduitGatewayJPAService` ;
- les tests Mock ;
- les tests d'intégration.

## 4) Contrats importants

### 4.1) creer(Produit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Libellé du parent blank -> `ExceptionAppliLibelleBlank`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Sauvegarde DAO `null` -> `ExceptionTechniqueGateway`
- En nominal : retourne l'objet métier persisté

### 4.2) rechercherTous()

- Retourne toujours une liste non `null`
- Une absence de contenu retourne une liste vide
- Le résultat observable est filtré, trié, dédoublonné et converti en objets métier

### 4.3) rechercherTousParPage(RequetePage)

- Si `pRequetePage == null` -> utilise une `RequetePage` par défaut
- `pageJPA == null` -> `ExceptionTechniqueGateway`
- `pageJPA.getContent() == null` -> `ExceptionTechniqueGateway`
- Le `ResultatPage<Produit>` retourné reste cohérent :
  - `content` non `null`
  - `pageNumber` cohérent
  - `pageSize` cohérent
  - `totalElements` cohérent

### 4.4) findByObjetMetier(Produit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- En nominal : retourne l'objet métier correspondant ou `null` si introuvable

### 4.5) findByLibelle(String)

- Libellé blank -> `ExceptionAppliLibelleBlank`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.6) findByLibelleRapide(String)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Contenu blank -> délègue à `rechercherTous()`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.7) findAllByParent(SousTypeProduit)

- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Aucun enfant trouvé -> liste vide
- Résultat nominal -> liste non `null`

### 4.8) findById(Long)

- Paramètre `null` -> `ExceptionAppliParamNull`
- DAO vide -> retourne `null`
- DAO `null` -> `ExceptionTechniqueGateway`
- Résultat nominal -> retourne l'objet métier persisté

### 4.9) update(Produit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Objet absent en stockage -> retourne `null`
- Si aucune modification n'est détectée -> retourne l'objet persistant converti sans sauvegarde inutile
- En nominal : sauvegarde l'entité modifiée puis retourne l'objet métier mis à jour

### 4.10) delete(Produit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- DAO `null` -> `ExceptionTechniqueGateway`
- Objet absent en stockage -> ne fait rien
- En nominal : supprime techniquement l'objet ciblé

### 4.11) count()

- Retourne un compteur technique `>= 0`
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

## 5) Règles de frontière

- le gateway ne manipule aucun DTO ;
- le gateway ne produit aucun message utilisateur ;
- les contrôles portent uniquement sur la sécurité technique du contrat Gateway ;
- les exceptions de `exceptionsservices` n'appartiennent pas à ce périmètre.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`
## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie de l'ADAPTER `ProduitGatewayJPAService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java` — 58013 octets, 1839 lignes LF, SHA-256 `98e06ad6ea52e8a993b4871ea9978f8f1805ca39d20a745c05dc63ce8afa151f`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java` — 44922 octets, 1349 lignes LF, SHA-256 `7fed211146aad71d6955c33137597c3c366e83d1dec228b3e0d17060bd52e22c`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java` — 341525 octets, 8893 lignes LF, SHA-256 `0f3b9907a677058054fa26c1cc45186f33fbcce510debef5730b7d988ad07a44`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java` — 337226 octets, 9317 lignes LF, SHA-256 `8b1b61a637a42b865caba13831608be0b0742fdfa6ac5578664c5fd5be494374`

L'ADAPTER JPA est le point technique qui dialogue avec les DAO et convertisseurs. Il ne porte aucune logique UC, aucun DTO et aucun message utilisateur. Il transforme les erreurs techniques en exceptions Gateway et respecte strictement les messages exposés par le PORT.

### 99.2) Attributs, constantes et collaborateurs à conserver

#### Champs détectés
- `private static final String CHAMP_ID_PRODUIT = "idProduit";`
- `private static final String CHAMP_LIBELLE_PRODUIT = "produit";`
- `private final ProduitDaoJPA produitDaoJPA;`
- `private final SousTypeProduitDaoJPA sousTypeProduitDaoJPA;`
- `private static final Logger LOG = LogManager.getLogger(ProduitGatewayJPAService.class);`

#### Constantes explicites
```java
	private static final String CHAMP_ID_PRODUIT = "idProduit";
	private static final String CHAMP_LIBELLE_PRODUIT = "produit";
	private static final Logger LOG
		= LogManager.getLogger(ProduitGatewayJPAService.class);
```

#### Constructeur(s)

Constructeur unique d'injection Spring à conserver strictement :

```java
	public ProduitGatewayJPAService(
			@Qualifier("ProduitDaoJPA")
			final ProduitDaoJPA pProduitDaoJPA,
			@Qualifier("SousTypeProduitDaoJPA")
			final SousTypeProduitDaoJPA pSousTypeProduitDaoJPA) {
		super();
		this.produitDaoJPA = pProduitDaoJPA;
		this.sousTypeProduitDaoJPA = pSousTypeProduitDaoJPA;
	}
```

Javadoc constructeur à conserver dans son intention et son formalisme :
- `CONSTRUCTEUR appelé automatiquement par SPRING` ;
- injection d'un `ProduitDaoJPA` et d'un `SousTypeProduitDaoJPA` ;
- avertissement explicite : ne surtout pas créer de constructeur d'arité nulle, faute de quoi Spring ne pourra plus injecter.

Règle stricte : conserver l'injection par constructeur validée avec `@Qualifier("ProduitDaoJPA")` puis `@Qualifier("SousTypeProduitDaoJPA")`, conserver l'ordre des paramètres DAO validé, ne jamais la remplacer par une injection de champ, un constructeur vide, un constructeur d'arité nulle ou une autre forme d'injection, ne pas créer de dépendance DTO, UC ou Controller, ne pas supprimer `LOG` même s'il est peu utilisé.

### 99.3) Ordre exact des méthodes de l'ADAPTER

- ligne 189 : `public Produit creer(final Produit pObject)`
- ligne 280 : `public List<Produit> rechercherTous()`
- ligne 343 : `public ResultatPage<Produit> rechercherTousParPage(final RequetePage pRequetePage)`
- ligne 431 : `public Produit findByObjetMetier(final Produit pObject)`
- ligne 537 : `public List<Produit> findByLibelle(final String pLibelle)`
- ligne 614 : `public List<Produit> findByLibelleRapide(final String pContenu)`
- ligne 695 : `public List<Produit> findAllByParent(final SousTypeProduit pParent)`
- ligne 766 : `public Produit findById(final Long pId)`
- ligne 835 : `public Produit update(final Produit pObject)`
- ligne 1001 : `public void delete(final Produit pObject)`
- ligne 1083 : `public long count()`
- ligne 1166 : `private SousTypeProduitJPA verifierPersistanceParent(final SousTypeProduitI pParent, final String pMethode)`
- ligne 1266 : `private List<Produit> filtrerTrierDedoublonner(final List<ProduitJPA> pEntities)`
- ligne 1341 : `private static Comparator<ProduitJPA> construireComparateurProduit()`
- ligne 1428 : `private static boolean appliquerModifications(final ProduitJPA pPersistant, final Produit pObject, final SousTypeProduitJPA pParentPersistant)`
- ligne 1496 : `private static Pageable convertirRequetePageEnPageable(final RequetePage pRequetePage)`
- ligne 1553 : `private static String resoudreMessageParentNull(final String pMethode)`
- ligne 1588 : `private static String resoudreMessageLibelleParentBlank(final String pMethode)`
- ligne 1629 : `private static String resoudreMessageParentNonPersistent(final String pMethode)`
- ligne 1667 : `private static String construireMessageParentNonPersistent(final String pMethode, final String pLibelleParent)`
- ligne 1713 : `private static boolean isBlank(final String pString)`
- ligne 1765 : `private static String safeMessage(final Object p)`
- ligne 1826 : `private static boolean safeEquals(final Object p1, final Object p2)`

Cet ordre doit être conservé au recodage : méthodes du PORT d'abord, puis méthodes techniques internes dans l'ordre validé par la classe.

### 99.4) Algorithmes et comportements à préserver


- `creer` : refuse objet `null`, libellé blank, parent `null`, libellé parent blank, parent ID `null`, parent absent ; vérifie le parent `SousTypeProduit` ; convertit métier→JPA avec parent persistant ; sauvegarde ; convertit retour.
- `rechercherTous` : DAO `findAll()`, retour `null` interdit, conversion, tri par parent puis libellé produit, dédoublonnage.
- `rechercherTousParPage` : requête `null` -> `RequetePage`; conversion `Pageable`; page JPA `null` ou contenu `null` -> exception technique ; conversion du contenu.
- `findByObjetMetier` : validations objet/libellé/parent ; vérifie parent ; cherche tous les produits du parent puis compare `produit` en ignorant la casse ; non trouvé -> `null`.
- `findByLibelle` : refuse blank ; DAO `findByProduitIgnoreCase`; liste `null` -> exception ; tri/dédoublonnage.
- `findByLibelleRapide` : `null` interdit ; blank -> `rechercherTous`; DAO `findByProduitContainingIgnoreCase`; liste `null` interdite.
- `findAllByParent` : parent `null`, libellé parent blank, ID parent `null`, parent absent ou exception DAO sont distingués ; DAO `findAllBySousTypeProduit` ; liste `null` interdite.
- `findById` : ID `null` interdit ; Optional `null` interdit ; absent -> `null` ; présent -> conversion.
- `update` : validations objet/libellé/ID/parent ; absent -> `null`; sans modification -> retour sans sauvegarde ; modification parent/libellé -> `save`.
- `delete` : validations ; absent -> ne fait rien ; nominal DAO `delete` puis `flush` ; erreurs delete/flush converties en `ExceptionTechniqueGateway`.
- Helpers parent : mêmes règles que `SousTypeProduitGatewayJPAService`, adaptées au parent `SousTypeProduit` et aux constantes `ProduitGatewayIService`.


### 99.5) Tests Mock associés, par bloc de responsabilité

#### Bloc `Creer`
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

#### Bloc `RechercherTousParPage`
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

#### Bloc `RechercherTous`
- ligne 1453 : `testRechercherTousDAORetourneNull`
- ligne 1503 : `testRechercherTousDAORetourneVide`
- ligne 1554 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 1618 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1689 : `testRechercherTousTriDedoublonnage`
- ligne 1809 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
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

#### Bloc `FindByLibelleRapide`
- ligne 4540 : `testFindByLibelleRapideNull`
- ligne 4583 : `testFindByLibelleRapideBlank`
- ligne 4687 : `testFindByLibelleRapideDAORetourneNull`
- ligne 4759 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 4846 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 4934 : `testFindByLibelleRapideNonTrouve`
- ligne 5017 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 3987 : `testFindByLibelleNull`
- ligne 4025 : `testFindByLibelleBlank`
- ligne 4070 : `testFindByLibelleDAORetourneNull`
- ligne 4144 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 4229 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 4315 : `testFindByLibelleNonTrouve`
- ligne 4403 : `testFindByLibelleNominal`

#### Bloc `FindAllByParent`
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

#### Bloc `FindById`
- ligne 6025 : `testFindByIdNull`
- ligne 6068 : `testFindByIdDAORetourneNull`
- ligne 6123 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 6193 : `testFindByIdDAOExceptionMessageNull`
- ligne 6264 : `testFindByIdNonTrouve`
- ligne 6331 : `testFindByIdNominal`

#### Bloc `Update`
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

#### Bloc `Delete`
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

#### Bloc `Count`
- ligne 8466 : `testCountStockageVide`
- ligne 8524 : `testCountDAOExceptionMessageNonNull`
- ligne 8597 : `testCountDAOExceptionMessageNull`
- ligne 8676 : `testCountNominal`

#### Bloc `Sanity`
- ligne 8730 : `testSanitySafeMessage`

### 99.6) Tests d'intégration associés, par bloc de responsabilité

#### Bloc `Creer`
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

#### Bloc `RechercherTousParPage`
- ligne 2705 : `testRechercherTousParPageNull`
- ligne 2808 : `testRechercherTousParPageAvecTri`
- ligne 2937 : `testRechercherTousParPageStockageVide`
- ligne 2998 : `testRechercherTousParPageTailleSuperieureAuTotal`
- ligne 3090 : `testRechercherTousParPagePageHorsBorne`
- ligne 3165 : `testRechercherTousParPageTailleZero`
- ligne 3273 : `testRechercherTousParPageNominal`

#### Bloc `RechercherTous`
- ligne 2448 : `testRechercherTousStockageVide`
- ligne 2523 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
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

#### Bloc `FindByLibelleRapide`
- ligne 5085 : `testFindByLibelleRapideNull`
- ligne 5144 : `testFindByLibelleRapideBlank`
- ligne 5245 : `testFindByLibelleRapideNonTrouve`
- ligne 5330 : `testFindByLibelleRapideCaseInsensitive`
- ligne 5464 : `testFindByLibelleRapideDedoublonnage`
- ligne 5576 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 4625 : `testFindByLibelleNull`
- ligne 4682 : `testFindByLibelleBlank`
- ligne 4738 : `testFindByLibelleNonTrouve`
- ligne 4823 : `testFindByLibelleCaseInsensitive`
- ligne 4959 : `testFindByLibelleNominal`

#### Bloc `FindAllByParent`
- ligne 5703 : `testFindAllByParentNull`
- ligne 5760 : `testFindAllByParentParentLibelleNull`
- ligne 5825 : `testFindAllByParentParentLibelleBlank`
- ligne 5891 : `testFindAllByParentParentIdNull`
- ligne 5971 : `testFindAllByParentParentAbsent`
- ligne 6064 : `testFindAllByParentParentSansEnfant`
- ligne 6206 : `testFindAllByParentNominal`

#### Bloc `FindById`
- ligne 6366 : `testFindByIdNull`
- ligne 6422 : `testFindByIdNonTrouve`
- ligne 6501 : `testFindByIdNominal`
- ligne 6611 : `testFindByIdIdCree`

#### Bloc `Update`
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

#### Bloc `Delete`
- ligne 7981 : `testDeleteNull`
- ligne 8037 : `testDeleteIdNull`
- ligne 8112 : `testDeleteAbsent`
- ligne 8211 : `testDeleteNominal`
- ligne 8374 : `testDeleteDoubleSuppression`

#### Bloc `Count`
- ligne 8567 : `testCountStockageVide`
- ligne 8643 : `testCountNominal`
- ligne 8726 : `testCountApresCreationPuisSuppression`

### 99.7) Règle anti-improvisation locale

L'IA ne doit jamais remplacer un helper privé par une version jugée équivalente sans reconfronter les tests ci-dessus. Les helpers `filtrerTrierDedoublonner`, `convertirRequetePageEnPageable`, `safeMessage`, `safeEquals`, `isBlank`, les résolutions de messages parent et `appliquerModifications` sont contractuels lorsqu'ils existent dans la classe validée.
