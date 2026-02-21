/* ********************************************************************* */
/* ************************* PORT GATEWAY ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway;

import java.util.List;

import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE TypeProduitGatewayIService.java :
 * </p>
 *
 * <p>
 * Cette INTERFACE modélise :
 * un <span style="font-weight:bold;">PORT GATEWAY</span>
 * technique appelée par
 * le SERVICE METIER UC et est chargée de communiquer avec le stockage.
 * </p>
 * <p style="font-weight:bold;">PORT SERVICE GATEWAY</p>
 *
 * <p>
 * Ce SERVICE GATEWAY n'est chargé que de traiter la
 * <span style="font-weight:bold;">partie technique</span>
 * (communication avec le stockage).
 * Les règles métier doivent être implémentées
 * au niveau du SERVICE METIER CU.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * Ce port définit les opérations techniques d'accès au stockage
 * pour l'objet métier parent de {@link SousTypeProduit} :
 * {@link TypeProduit}.
 * </p>
 * <p>
 * Il ne contient aucune logique métier et ne produit aucun message
 * à destination de l'utilisateur final.
 * </p>
 * <p>
 * La stratégie du présent Gateway est de jeter systématiquement
 * une Exception avec un message circonstancié
 * en cas de problème dans la couche PERSISTANCE.
 * L'Exception est considérée :
 * <ul>
 * <li>Technique si elle remonte de la couche stockage
 * (stockage indisponible, erreur JPA, rollback, problème réseau, ...).</li>
 * <li>Applicative si elle provient de la
 * couche SERVICE METIER UC (paramètre null, blank, ...).</li>
 * </ul>
 * Cette Exception devra être traitée
 * au niveau du SERVICE UC métier afin d'informer l'Utilisateur.
 * </p>
 * <p>
 * Le présent SERVICE GATEWAY ne produit pas de journalisation (LOG).
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * Utilisée par l'implémentation SERVICE METIER CU
 * {@link TypeProduitCUService} pour :
 * </p>
 * <ul>
 * <li>créer / modifier une Entity via {@link #creer(TypeProduit)}</li>
 * <li>lister toutes les Entities via {@link #rechercherTous()}</li>
 * <li>lister toutes les Entities par pages via {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par objet métier via {@link #findByObjetMetier(TypeProduit)}</li>
 * <li>rechercher par libellé exact via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant via {@link #findByLibelleRapide(String)}</li>
 * <li>rechercher par ID via {@link #findById(Long)}</li>
 * <li>modifier via {@link #update(TypeProduit)}</li>
 * <li>supprimer via {@link #delete(TypeProduit)}</li>
 * <li>compter via {@link #count()}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.2
 * @since 25 janvier 2026
 */
public interface TypeProduitGatewayIService {

	// *************************** CONSTANTES ******************************/

	/**
	 * "Anomalie applicative "
	 */
	String ANOMALIE_APPLICATIVE = "Anomalie applicative ";

	/**
	 * "- l'objet métier passé en paramètre est null."
	 */
	String OBJET_METIER_PARAM_NULL
		= "- l'objet métier passé en paramètre est null.";

	/**
	 * "- le contenu passé en paramètre est null."
	 */
	String CONTENU_PARAM_NULL
		= "- le contenu passé en paramètre est null.";

	/**
	 * "- l'ID passé en paramètre est null."
	 */
	String ID_PARAM_NULL
		= "- l'ID passé en paramètre est null.";

	/**
	 * "- l'objet métier passé en paramètre a un "
	 */
	String OBJET_METIER_A
		= "- l'objet métier passé en paramètre a un ";

	/**
	 * "libellé blank (null ou que des espaces)."
	 */
	String LIBELLE_BLANK = "libellé blank (null ou que des espaces).";


	// ===================== CREER =====================

	/**
	 * "Anomalie applicative - l'objet métier passé en paramètre est null."
	 */
	String MESSAGE_CREER_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;

	// ===================== FIND BY OBJET METIER =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 *  - le paramètre pObject ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL =
			ANOMALIE_APPLICATIVE
			+ "- le paramètre pObject ne doit pas être null.";

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 *  - le libellé de pObject passé en paramètre
	 *  ne doit pas être blank."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
			ANOMALIE_APPLICATIVE
			+ "- le libellé de pObject passé en paramètre "
			+ "ne doit pas être blank.";

	// ===================== FIND BY LIBELLE =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le libellé passé en paramètre est blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le libellé passé en paramètre est blank "
			+ "(null ou que des espaces).";


	// ===================== FIND BY LIBELLE RAPIDE =====================

	/**
	 * "Anomalie applicative - le contenu passé en paramètre est null."
	 */
	String MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ CONTENU_PARAM_NULL;


	// ===================== FIND BY ID =====================

	/**
	 * "Anomalie applicative - l'ID passé en paramètre est null."
	 */
	String MESSAGE_FINDBYID_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ ID_PARAM_NULL;


	// ===================== UPDATE =====================

	/**
	 * "Anomalie applicative - l'objet métier passé en paramètre est null."
	 */
	String MESSAGE_UPDATE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;

	/**
	 * <div>
	 * <p>"l'objet que vous voulez modifier n'est pas persistant
	 * (ID null) : "</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_NON_PERSISTENT
		= "l'objet que vous voulez modifier n'est pas persistant "
			+ "(ID null) : ";


	// ===================== DELETE =====================

	/**
	 * "Anomalie applicative - l'objet métier passé en paramètre est null."
	 */
	String MESSAGE_DELETE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;

	/**
	 * "ID null."
	 */
	String ID_NULL = "ID null.";

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un ID null."</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO_ID_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ ID_NULL;

	/**
	 * <div>
	 * <p>"l'objet que vous voulez détruire n'existait
	 * pas dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO_NON_PERSISTENT
		= "l'objet que vous voulez détruire n'existait "
			+ "pas dans le stockage : ";


	// ===================== TECHNIQUE =====================

	/**
	 * <div>
	 * <p>"Erreur Technique lors du stockage : "</p>
	 * </div>
	 */
	String ERREUR_TECHNIQUE_STOCKAGE
		= "Erreur Technique lors du stockage : ";

	/**
	 * "Erreur Technique - Le stockage a retourné null."
	 */
	String ERREUR_TECHNIQUE_KO_STOCKAGE
		= "Erreur Technique - Le stockage a retourné null.";


	// ***************************** METHODES ****************************/


	/**
	 * <div>
	 * <p style="font-weight:bold;">Sauvegarde un {@link TypeProduit}
	 * dans le stockage en déléguant à un composant technique (DAO).</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Convertir l'objet métier en Entity stockable.</li>
	 * <li>Déléguer la sauvegarde de l'Entity dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Convertir l'Entity stockable en objet métier.</li>
	 * <li>Retourner l'objet métier effectivement persisté.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pObject == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message {@link #MESSAGE_CREER_KO_PARAM_NULL}.</li>
	 * <li>Si {@code libelle de l'objet métier} est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_CREER_KO_LIBELLE_BLANK}.</li>
	 * <li>Si {@code DAO.save(entity)} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, contrainte d'intégrité,
	 * etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * <li>L'objet retourné correspond à l'état réellement persisté
	 * (si non {@code null}).</li>
	 * <li>Aucune écriture partielle n'est réalisée.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : TypeProduit :
	 * le Type de Produit à stocker.
	 * @return TypeProduit :
	 * le Type de Produit sauvegardé dans le stockage.
	 * @throws ExceptionAppliParamNull si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank
	 * si le libellé de {@code pObject} est blank.
	 * @throws ExceptionTechniqueGateway
	 * si le stockage retourne {@code null} ou
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	TypeProduit creer(TypeProduit pObject) throws Exception;

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Retourne tous les {@link TypeProduit}
	 * présents dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche des Entities dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Fabriquer une liste réponse d'Entities sans null.</li>
	 * <li>Trier la liste réponse.</li>
	 * <li>Garantir l'unicité dans la liste réponse
	 * (au sens métier : unicité sur le libellé, sans tenir compte
	 * de la casse).</li>
	 * <li>Convertir chaque Entity de la liste réponse en objet métier.</li>
	 * <li>Retourner la liste d'objets métier.</li>
	 * <li>Retourner toujours une {@link List} non {@code null}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Retourne toujours une {@link List} non {@code null}
	 * (éventuellement vide).</li>
	 * <li>Si {@code DAO.findAll()} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si {@code DAO.findAll()} retourne une liste vide :
	 * retourne une liste vide. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * <li>La collection retournée
	 * correspond à l'état réellement persisté.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;TypeProduit&gt; :
	 * Liste des TypeProduit dans le stockage. jamais null.
	 * @throws ExceptionTechniqueGateway
	 * si le stockage retourne {@code null}
	 * ou si une erreur technique survient.
	 * @throws Exception toute autre exception
	 * levée par l'implémentation.
	 */
	List<TypeProduit> rechercherTous() throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link TypeProduit}
	 * présents dans le stockage sous forme de {@link ResultatPage}.</p>
	 * <ul>
	 * <li>Pagination : implémentation JPA (Spring Data) -&gt;
	 * résultat neutre.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche des Entities dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Retourner une page définie par défaut
	 * si {@code pRequetePage == null}.</li>
	 * <li>Récupérer la page des Entities auprès
	 * du composant de persistance (DAO).</li>
	 * <li>Convertir la page d'Entities en page d'objets métier.</li>
	 * <li>Retourner un {@link ResultatPage} cohérent avec la requête
	 * (page, taille, total).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pRequetePage == null} :
	 * utilise une requête par défaut.
	 * Pas d'Exception.</li>
	 * <li>Si {@code DAO.findAll(pageable)} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si {@code DAO.findAll(pageable).getContent()}
	 * retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * <li>Retourne un {@link ResultatPage} cohérent avec la requête
	 * (page, taille, total).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>La collection d'objets retournée correspond
	 * à l'état réellement persisté.</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : {@link RequetePage} :
	 * requête pagination.
	 * @return {@link ResultatPage}&lt;TypeProduit&gt; :
	 * page d'objets métier. Jamais {@code null}.
	 * @throws ExceptionTechniqueGateway  si {@code DAO.findAll(pageable)}
	 * ou son contenu retourne {@code null},
	 * ou si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	ResultatPage<TypeProduit> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduit} persistant à partir
	 * d'un objet métier.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche d'un persistant via
	 * {@link #findByLibelle(String)} (recherche par libellé).</li>
	 * <li>Retourner {@code null} si aucun objet n'est trouvé.</li>
	 * <li>Retourner l'objet métier trouvé.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si pObject est null : jette une {@link ExceptionAppliParamNull}
	 * avec {@link #MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL}.</li>
	 * <li>Si le libellé de pObject est blank : jette une
	 * {@link ExceptionAppliLibelleBlank}
	 * avec {@link #MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK}.</li>
	 * <li>Sinon, recherche un persistant correspondant
	 * via {@link #findByLibelle(String)}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * <li>L'objet retourné correspond à l'état réellement persisté
	 * (si non {@code null}).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : TypeProduit :
	 * objet métier à retrouver dans le stockage.
	 * @return TypeProduit :
	 * objet persistant correspondant, ou null si non trouvé.
	 * @throws ExceptionAppliParamNull
	 * si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank
	 * si le libellé de {@code pObject} est blank.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	TypeProduit findByObjetMetier(TypeProduit pObject)
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un
	 * {@link TypeProduit}
	 * via son libellé exact (sans tenir compte de la casse).</p>
	 * <ul>
	 * <li>ATTENTION : le Modèle Conceptuel de Données MCD
	 * et les Entities indiquent que
	 * le libellé est
	 * <span style="font-weight:bold;">forcément unique</span>
	 * pour un {@link TypeProduit}.</li>
	 * <li>[libelle]
	 * doit être unique (Cf equals/HashCode
	 * d'un {@link TypeProduit}).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche de l'Entity par libellé exact
	 * (sans tenir compte de la casse) au DAO.</li>
	 * <li>Retourner {@code null} si aucun objet n'est trouvé.</li>
	 * <li>Convertir l'Entity trouvée en objet métier.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est Blank (y compris {@code null}) :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK}.</li>
	 * <li>Si aucun objet n'est trouvé :
	 * retourne {@code null}. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * <li>L'objet retourné correspond à l'état réellement persisté
	 * (si non {@code null}).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String : libellé exact à rechercher.
	 * @return TypeProduit :
	 * l'objet métier correspondant au libellé, ou {@code null}.
	 * @throws ExceptionAppliLibelleBlank
	 * si {@code pLibelle} est blank (y compris {@code null}).
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	TypeProduit findByLibelle(String pLibelle) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la List&lt;TypeProduit&gt; des {@link TypeProduit}
	 * dont le libellé contient pContenu (sans tenir compte de la casse).
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche des Entities dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Retourner tous les enregistrements si pContenu est blank
	 * (mais pas {@code null}).</li>
	 * <li>Retourner une liste vide si le contenu n'est pas trouvé.</li>
	 * <li>Fabriquer une liste réponse d'Entities sans null.</li>
	 * <li>Convertir chaque Entity de la liste réponse 
	 * en objet métier.</li>
	 * <li>Garantir l'unicité dans la liste réponse 
	 * au sens métier (unicité sur le libellé sans tenir compte 
	 * de la casse), au moyen d'un ensemble de libellés 
	 * normalisés (trim + lowerCase) puis tri final 
	 * de la liste résultat.</li>
	 * <li>Trier la liste finale par libellé 
	 * (sans tenir compte de la casse).</li>
	 * <li>Retourner la liste d'objets métier.</li>
	 * <li>Retourner toujours une {@link List} non {@code null}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Retourne toujours une {@link List} non {@code null}.</li>
	 * <li>Si {@code pContenu == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL}.</li>
	 * <li>Si {@code pContenu} est Blank mais pas {@code null} :
	 * retourne tous les enregistrements.</li>
	 * <li>Si {@code DAO.find...()} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si aucun résultat : retourne une liste vide.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>La collection d'objets retournée correspond
	 * à l'état réellement persisté.</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String :
	 * contenu partiel du libellé (recherche rapide).
	 * @return List&lt;TypeProduit&gt; :
	 * Liste des objets métier dont le libellé contient pContenu.
	 * Jamais {@code null}.
	 * @throws ExceptionAppliParamNull
	 * si {@code pContenu == null}.
	 * @throws ExceptionTechniqueGateway
	 * si le stockage retourne {@code null}
	 * ou si une erreur technique survient.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	List<TypeProduit> findByLibelleRapide(String pContenu) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">Retourne un {@link TypeProduit}
	 * déterminé par son ID dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche de l'Entity par ID au DAO.</li>
	 * <li>Retourner {@code null} si aucun objet n'est trouvé.</li>
	 * <li>Convertir l'Entity trouvée en objet métier.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pId == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message {@link #MESSAGE_FINDBYID_KO_PARAM_NULL}.</li>
	 * <li>Si {@code DAO} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si le {@link TypeProduit} n'est pas trouvé :
	 * retourne {@code null}. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * <li>L'objet retourné correspond à l'état réellement persisté
	 * (si non {@code null}).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long : ID dans le stockage.
	 * @return TypeProduit :
	 * l'objet métier qui possède pId, ou {@code null}.
	 *
	 * @throws ExceptionAppliParamNull
	 * si {@code pId == null}.
	 * @throws ExceptionTechniqueGateway
	 * si le DAO retourne null ou
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	TypeProduit findById(Long pId) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Modifie un {@link TypeProduit}
	 * déjà existant dans le stockage.</p>
	 * <p>Cette méthode ne s'applique qu'à un objet déjà persistant.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Vérifier la persistance de l'objet (ID non {@code null}).</li>
	 * <li>Rechercher l'Entity à modifier par ID via le DAO.</li>
	 * <li>Retourner {@code null} si l'objet n'existe pas en stockage.</li>
	 * <li>Détecter les modifications (libellé).</li>
	 * <li>Si aucune modification :
	 * retourner l'objet persistant inchangé.</li>
	 * <li>Appliquer les modifications sur l'Entity persistée.</li>
	 * <li>Si modification : persister la mise à jour via le DAO.</li>
	 * <li>Retourner l'objet métier persisté modifié ou inchangé.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pObject == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message {@link #MESSAGE_UPDATE_KO_PARAM_NULL}.</li>
	 * <li>Si {@code libelle de l'objet métier} est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_UPDATE_KO_LIBELLE_BLANK}.</li>
	 * <li>Si {@code libelle de l'objet métier} correspond 
	 * à celui d'un autre {@link TypeProduit} déjà présent 
	 * dans le stockage : aucune exception n'est levée, 
	 * aucune modification n'est appliquée, 
	 * et l'objet persistant inchangé est retourné.</li>
	 * <li>Si l'ID est {@code null} :
	 * jette une {@link ExceptionAppliParamNonPersistent}
	 * avec un message {@link #MESSAGE_UPDATE_KO_NON_PERSISTENT}
	 * + {@code pObject.getTypeProduit()}.</li>
	 * <li>Si {@code DAO} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si l'objet n'existe pas en stockage :
	 * retourne {@code null}.</li>
	 * <li>Si une modification est effectuée :
	 * retourne l'objet persistant modifié.</li>
	 * <li>Si aucune modification n'est effectuée :
	 * retourne l'objet persistant inchangé.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Aucune écriture partielle n'est réalisée.</li>
	 * <li>L'objet retourné correspond à l'état réellement persisté
	 * (si non {@code null}).</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : TypeProduit :
	 * Objet métier portant les modifications (hors ID).
	 * @return TypeProduit :
	 * objet de même ID que pObject, modifié ou inchangé,
	 * ou {@code null} si absent du stockage.
	 *
	 * @throws ExceptionAppliParamNull
	 * si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank
	 * si le libellé de {@code pObject} est blank.
	 * @throws ExceptionAppliParamNonPersistent
	 * si l'ID de {@code pObject} est {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si le DAO retourne null ou
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	TypeProduit update(TypeProduit pObject) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">Détruit un {@link TypeProduit}
	 * déjà existant dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Rechercher l'Entity persistée par ID via le DAO.</li>
	 * <li>Déléguer la destruction de l'Entity persistée au DAO.</li>
	 * <li>Ne rien faire si l'objet n'existe pas en stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pObject == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message {@link #MESSAGE_DELETE_KO_PARAM_NULL}.</li>
	 * <li>Si l'ID est {@code null} :
	 * jette une {@link ExceptionAppliParamNonPersistent}
	 * avec un message {@link #MESSAGE_DELETE_KO_ID_NULL}.</li>
	 * <li>L'inexistence en stockage est constatée
	 * exclusivement par la recherche préalable via
	 * {@code DAO.findById(id)}.</li>
	 * <li>Si {@code DAO.findById(id)} retourne vide :
	 * ne fait rien. Pas d'Exception.</li>
	 * <li>Si {@code DAO} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>L'objet est retiré du stockage (si présent).</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : TypeProduit :
	 * Objet métier à détruire dans le stockage.
	 *
	 * @throws ExceptionAppliParamNull si {@code pObject == null}.
	 * @throws ExceptionAppliParamNonPersistent
	 * si l'ID de {@code pObject} est {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si le DAO retourne null ou
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	void delete(TypeProduit pObject) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compte le nombre de {@link TypeProduit}
	 * dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer le comptage des Entities dans le stockage
	 * au composant de persistance (DAO).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Fonctionnement nominal : retourne un compteur {@code >= 0}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 * + un message sûr dérivé de l'Exception (jamais {@code null}),
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Le compteur retourné est cohérent avec l'état du stockage
	 * au moment de l'appel.</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @return long : nombre d'enregistrements.
	 *
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	long count() throws Exception;

}
