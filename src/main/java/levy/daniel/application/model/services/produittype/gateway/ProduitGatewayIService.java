/* ********************************************************************* */
/* ************************* PORT GATEWAY ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway;

import java.util.List;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE ProduitGatewayIService.java :
 * </p>
 *
 * <p>
 * Cette INTERFACE modélise :
 * un <span style="font-weight:bold;">PORT GATEWAY</span>
 * technique appelée par
 * le SERVICE METIER et chargée de communiquer avec le stockage.
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
 * pour l'objet métier enfant de {@link SousTypeProduit} : 
 * {@link Produit}.
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
 * {@link ProduitCUService} pour :
 * </p>
 * <ul>
 * <li>créer / modifier une Entity via {@link #creer(Produit)}</li>
 * <li>lister toutes les Entities via {@link #rechercherTous()}</li>
 * <li>lister toutes les Entities par pages via 
 * {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par objet métier via {@link #findByObjetMetier(Produit)}</li>
 * <li>rechercher par libellé exact via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant via {@link #findByLibelleRapide(String)}</li>
 * <li>lister toutes les Entities par parent via {@link #findAllByParent(SousTypeProduit)}</li>
 * <li>rechercher par ID via {@link #findById(Long)}</li>
 * <li>modifier via {@link #update(Produit)}</li>
 * <li>supprimer via {@link #delete(Produit)}</li>
 * <li>compter via {@link #count()}</li>
 * </ul>
 * </div>
 * 
 * <div>
 * <p>{@code safeMessage(e)} est une méthode privée 
 * qui fournit un message {@code non null} dérivé du message 
 * généré par une Exception.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.1
 * @since 25 janvier 2026
 */
public interface ProduitGatewayIService {

	// *************************** CONSTANTES ******************************/

	/**
	 * <div>
	 * <p>"Anomalie applicative "</p>
	 * </div>
	 */
	String ANOMALIE_APPLICATIVE = "Anomalie applicative ";

	/**
	 * <div>
	 * <p>"- l'objet métier passé en paramètre est null."</p>
	 * </div>
	 */
	String OBJET_METIER_PARAM_NULL
		= "- l'objet métier passé en paramètre est null.";

	/**
	 * <div>
	 * <p>"- l'objet métier passé en paramètre a un "</p>
	 * </div>
	 */
	String OBJET_METIER_A
		= "- l'objet métier passé en paramètre a un ";

	/**
	 * <div>
	 * <p>"libellé blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String LIBELLE_BLANK = "libellé blank (null ou que des espaces).";
	
	/**
	 * <div>
	 * <p>"pas déjà dans le stockage : "</p>
	 * </div>
	 */
	String PAS_DANS_STOCKAGE = "pas déjà dans le stockage : ";


	// ===================== CREER =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
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

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	
	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à créer a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à créer a un "
			+ LIBELLE_BLANK;
	
	/**
	 * <div>
	 * <p>""Anomalie applicative - 
	 * le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE 
			+ "- le parent de l'objet que vous voulez créer n'existe "
			+ PAS_DANS_STOCKAGE;

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

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à créer a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à créer a un "
			+ LIBELLE_BLANK;

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
	= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet que vous voulez rechercher n'existe "
		+ PAS_DANS_STOCKAGE;

	// ===================== FIND BY LIBELLE =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le libellé passé en paramètre est un 
	 * libellé blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le libellé passé en paramètre est un "
			+ LIBELLE_BLANK;

	// ===================== FIND BY LIBELLE RAPIDE =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - le contenu passé en paramètre est null."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ "- le contenu passé en paramètre est null.";

	// ===================== FIND ALL BY PARENT =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
	 */
	String MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet passé en paramètre a un "
			+ LIBELLE_BLANK;

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - le parent de l'objet n'existait
	 * pas déjà dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE 
		+ "- le parent de l'objet n'existait "
			+ PAS_DANS_STOCKAGE;

	// ===================== FIND BY ID =====================
	
	/**
	 * <div>
	 * <p>"- l'identifiant passé en paramètre est null."</p>
	 * </div>
	 */
	String ID_PARAM_NULL = "- l'identifiant passé en paramètre est null.";
	
	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - l'identifiant passé en paramètre est null."</p>
	 * </div>
	 */
	String MESSAGE_FINDBYID_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ ID_PARAM_NULL;

	// ===================== UPDATE =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
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
	 * <p>"Anomalie applicative 
	 * - l'objet que vous voulez modifier n'est pas persistant
	 * (ID null) : "</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE 
		+ "- l'objet que vous voulez modifier n'est pas persistant "
			+ "(ID null) : ";

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";

	/**
	 * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à modifier a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à modifier a un "
			+ LIBELLE_BLANK;

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - le parent de l'objet que vous voulez modifier n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE 
		+ "- le parent de l'objet que vous voulez modifier n'existe "
			+ PAS_DANS_STOCKAGE;


	// ===================== DELETE =====================

	/**
	 * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;

	/**
	 * <div>
	 * <p>"ID null."</p>
	 * </div>
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
	 * <p>"Anomalie applicative 
	 * - l'objet que vous voulez détruire n'existait
	 * pas dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE 
		+ "- l'objet que vous voulez détruire n'existait "
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
	 * <div>
	 * <p>"Erreur Technique - Le stockage a retourné null."</p>
	 * </div>
	 */
	String ERREUR_TECHNIQUE_KO_STOCKAGE
		= "Erreur Technique - Le stockage a retourné null.";


	// ============= PREFIXES + SUFFIXES =====================

	
	/** Préfixe : "MESSAGE_CREER". */
	String PREFIX_MESSAGE_CREER = "MESSAGE_CREER";

	/** Préfixe : "MESSAGE_FINDBYOBJETMETIER". */
	String PREFIX_MESSAGE_FINDBYOBJETMETIER = "MESSAGE_FINDBYOBJETMETIER";

	/** Préfixe : "MESSAGE_FINDALLBYPARENT". */
	String PREFIX_MESSAGE_FINDALLBYPARENT = "MESSAGE_FINDALLBYPARENT";

	/** Préfixe : "MESSAGE_UPDATE". */
	String PREFIX_MESSAGE_UPDATE = "MESSAGE_UPDATE";
	
	/** Suffixe : "_KO_PARENT_NULL". */
	String SUFFIXE_KO_PARENT_NULL
		= "_KO_PARENT_NULL";

	/** Suffixe : "_KO_LIBELLE_PARENT_BLANK". */
	String SUFFIXE_KO_LIBELLE_PARENT_BLANK
		= "_KO_LIBELLE_PARENT_BLANK";

	/** Suffixe : "_KO_PARENT_NON_PERSISTENT". */
	String SUFFIXE_KO_PARENT_NON_PERSISTENT 
		= "_KO_PARENT_NON_PERSISTENT";


	// ***************************** METHODES ****************************/


	/**
	 * <div>
	 * <p style="font-weight:bold;">Sauvegarde un {@link Produit}
	 * dans le stockage en déléguant à un composant technique (DAO).</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Vérifier la persistance du parent {@link SousTypeProduit}.</li>
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
	 * <li>Si {@code pObject.getSousTypeProduit() == null} :
	 * jette une {@link ExceptionAppliParentNull}
	 * avec un message {@link #MESSAGE_CREER_KO_PARENT_NULL}.</li>
	 * <li>Si {@code parent} n'est pas persistant (ID {@code null}) :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message {@link #MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si le parent n'existe pas dans le stockage :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message {@link #MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage
	 * (base indisponible, erreur JPA, rollback, réseau, etc.) :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @param pObject : Produit :
	 * le Produit à stocker.
	 * @return Produit :
	 * le Produit sauvegardé dans le stockage.
	 * 
	 * @throws ExceptionAppliParamNull si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank 
	 * si le libellé de {@code pObject} est blank.
	 * @throws ExceptionAppliParentNull 
	 * si l'objet métier n'a pas de parent.
	 * @throws ExceptionTechniqueGatewayNonPersistent 
	 * si l'objet a un parent sans ID (non persisté) 
	 * ou introuvable dans le stockage.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	Produit creer(Produit pObject) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit}
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
	 * au moyen d'un {@code LinkedHashSet}.</li>
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
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @return List&lt;Produit&gt; :
	 * Liste des Produits dans le stockage. Jamais {@code null}.
	 * 
	 * @throws ExceptionTechniqueGateway 
	 * si le stockage retourne {@code null} 
	 * ou si une erreur technique survient.
	 * @throws Exception toute autre exception 
	 * levée par l'implémentation.
	 */
	List<Produit> rechercherTous() throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit}
	 * présents dans le stockage et les retourne par page.</p>
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
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @return {@link ResultatPage}&lt;Produit&gt; : 
	 * page d'objets métier. Jamais {@code null}.
	 * 
	 * @throws ExceptionTechniqueGateway si {@code DAO.findAll(pageable)} 
	 * ou son contenu retourne {@code null},
	 * ou si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	ResultatPage<Produit> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} persistant à partir
	 * d'un objet métier.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche de l'Entity par objet métier au DAO.</li>
	 * <li>Retourner {@code null} si aucun objet n'est trouvé.</li>
	 * <li>Convertir l'Entity trouvée en objet métier.</li>
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
	 * <li>Si {@code pObject.getSousTypeProduit() == null} :
	 * jette une {@link ExceptionAppliParentNull}
	 * avec un message 
	 * {@link #MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL}.</li>
	 * <li>Si {@code libelle du parent} est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message 
	 * {@link #MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK}.</li>
	 * <li>Si {@code parent} n'est pas persistant (ID {@code null}) :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message 
	 * {@link #MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si le parent n'existe pas dans le stockage :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message 
	 * {@link #MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si le stockage retourne {@code null} lors de la recherche :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si aucun objet métier n'est attaché au parent persistant : 
	 * retourne {@code null}. Pas d'Exception.</li>
	 * <li>Recherche par le libellé l'objet métier 
	 * dans la liste des enfants du parent.</li>
	 * <li>Si l'Entity n'est pas trouvée : 
	 * Retourne {@code null}. Pas d'Exception.</li>
	 * <li>Si l'Entity est trouvée : 
	 * la convertit en objet métier et la retourne.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)}
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
	 * @param pObject : Produit :
	 * objet métier à retrouver dans le stockage.
	 * @return Produit :
	 * objet persistant correspondant, ou {@code null} si non trouvé.
	 * 
	 * @throws ExceptionAppliParamNull 
	 * si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank 
	 * si le libellé de {@code pObject} est blank
	 * ou si le libellé du parent est blank.
	 * @throws ExceptionAppliParentNull
	 * si {@code pObject.getSousTypeProduit() == null}.
	 * @throws ExceptionTechniqueGatewayNonPersistent
	 * si le parent n'a pas d'ID ou est introuvable dans le stockage.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	Produit findByObjetMetier(Produit pObject) 
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche une collection de
	 * {@link Produit}
	 * via un libellé exact.</p>
	 * <ul>
	 * <li>ATTENTION : le Modèle Conceptuel de Données MCD
	 * et les Entities indiquent que
	 * le libellé n'est
	 * <span style="font-weight:bold;">pas forcément unique</span>
	 * pour un {@link Produit}.</li>
	 * <li>C'est le couple [parent, libelle]
	 * qui doit être unique (Cf equals/HashCode
	 * d'un {@link Produit}).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche de la collection
	 * d'Entities dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Retourner une liste vide si aucun objet métier
	 * n'existe avec ce libellé.</li>
	 * <li>Filtrer, trier, dédoublonner, convertir
	 * les Entities JPA trouvées en Objet métier.</li>
	 * <li>Retourner la collection d'objets métier.</li>
	 * <li>Peut retourner une liste vide.</li>
	 * <li>Retourner toujours une {@link List} non {@code null}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est Blank (y compris {@code null}) :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK}.</li>
	 * <li>Si {@code DAO.findByProduitIgnoreCase(pLibelle)} 
	 * retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si aucun objet n'est trouvé : 
	 * retourne une liste vide. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
	 * et propage l'Exception technique cause.</li>
	 * <li>Retourne toujours une {@link List} non {@code null}
	 * (éventuellement vide).</li>
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
	 * @param pLibelle : String : libellé exact à rechercher.
	 * @return List&lt;Produit&gt; :
	 * résultats (éventuellement vides), jamais {@code null}.
	 * 
	 * @throws ExceptionAppliLibelleBlank 
	 * si {@code pLibelle} est Blank (y compris {@code null})
	 * @throws ExceptionTechniqueGateway 
	 * si {@code DAO.findByProduitIgnoreCase(pLibelle)} 
	 * retourne {@code null} 
	 * ou si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation. 
	 */
	List<Produit> findByLibelle(String pLibelle) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la List&lt;Produit&gt; des {@link Produit}
	 * dont le libellé contient pContenu.
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
	 * <li>Trier la liste réponse.</li>
	 * <li>Garantir l'unicité dans la liste réponse
	 * au moyen d'un {@code LinkedHashSet}.</li>
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
	 * <li>Si {@code pContenu == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message 
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL}.</li>
	 * <li>Si {@code pContenu} est Blank mais pas {@code null} :
	 * retourne tous les enregistrements. Pas d'Exception.</li>
	 * <li>Si {@code DAO.findBySousTypeProduitContainingIgnoreCase(String)} 
	 * retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si aucun résultat : 
	 * retourne une liste vide. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @return List&lt;Produit&gt; :
	 * Liste des objets métier dont le libellé contient pContenu.
	 * Jamais {@code null}.
	 * 
	 * @throws ExceptionAppliParamNull 
	 * si {@code pContenu == null}.
	 * @throws ExceptionTechniqueGateway 
	 * si le stockage retourne {@code null} 
	 * ou si une erreur technique survient.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	List<Produit> findByLibelleRapide(String pContenu) 
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit}
	 * rattachés au {@link SousTypeProduit} pParent parent.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Vérifier la persistance du parent {@link SousTypeProduit}.</li>
	 * <li>Convertir le parent métier en Entity stockable 
	 * (si nécessaire).</li>
	 * <li>Déléguer la recherche des Entities enfants dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Retourner une liste vide si aucun enfant n'est trouvé.</li>
	 * <li>Fabriquer une liste réponse d'Entities sans null.</li>
	 * <li>Trier la liste réponse.</li>
	 * <li>Garantir l'unicité dans la liste réponse
	 * au moyen d'un {@code LinkedHashSet}.</li>
	 * <li>Convertir chaque Entity de la liste réponse en objet métier.</li>
	 * <li>Retourner la liste d'objets métier.</li>
	 * <li>Retourner toujours une {@link List} non {@code null}.
	 * La liste peut être vide.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Retourne toujours une {@link List} non {@code null}
	 * (éventuellement vide).</li>
	 * <li>Si {@code pParent == null} :
	 * jette une {@link ExceptionAppliParentNull}
	 * avec un message {@link #MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL}.</li>
	 * <li>Si {@code libelle de parent} est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message 
	 * {@link #MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK}.</li>
	 * <li>Si {@code parent} n'est pas persistant (ID {@code null}) :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message 
	 * {@link #MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
	 * + {@code pParent.getSousTypeProduit()}.</li>
	 * <li>Si le parent n'existe pas dans le stockage :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message 
	 * {@link #MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
	 * + {@code pParent.getSousTypeProduit()}.</li>
	 * <li>Si {@code DAO.findAllByParent(...)} retourne {@code null} :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_KO_STOCKAGE}.</li>
	 * <li>Si aucun résultat : 
	 * retourne une liste vide. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @param pParent : SousTypeProduit : parent.
	 * @return List&lt;Produit&gt; :
	 * liste des enfants attachés au parent pParent. Jamais {@code null}.
	 * 
	 * @throws ExceptionAppliParentNull 
	 * si {@code pParent == null}.
	 * @throws ExceptionAppliLibelleBlank 
	 * si libellé de pParent est blank.
	 * @throws ExceptionTechniqueGatewayNonPersistent 
	 * si pParent n'a pas d'id ou n'est pas persistant dans le stockage.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	List<Produit> findAllByParent(SousTypeProduit pParent) 
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">Retourne un {@link Produit}
	 * déterminé par son ID dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Déléguer la recherche de l'Entity dans le stockage
	 * au composant de persistance (DAO).</li>
	 * <li>Retourner {@code null} si le DAO ne trouve rien.</li>
	 * <li>Convertir l'Entity retournée en objet métier.</li>
	 * <li>Retourner l'objet métier trouvé.</li>
	 * <li>Peut retourner {@code null}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si {@code pId == null} :
	 * jette une {@link ExceptionAppliParamNull}
	 * avec un message {@link #MESSAGE_FINDBYID_KO_PARAM_NULL}.</li>
	 * <li>Si le {@link Produit} n'est pas trouvé :
	 * retourne {@code null}. Pas d'Exception.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>L'objet retourné correspond
	 * à l'état réellement persisté (si non {@code null}).</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long : ID dans le stockage.
	 * @return Produit :
	 * l'objet métier persistant qui possède pId, ou {@code null}.
	 * 
	 *  @throws ExceptionAppliParamNull 
	 * si {@code pId == null}.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	Produit findById(Long pId) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Modifie un {@link Produit}
	 * déjà existant dans le stockage.</p>
	 * <p>Cette méthode ne s'applique qu'à un objet déjà persistant.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Vérifier la persistance et l'existence du parent.</li>
	 * <li>Déléguer la recherche de l'Entity par ID 
	 * dans le stockage (DAO).</li>
	 * <li>Retourner {@code null} si l'objet n'existe pas 
	 * dans le stockage.</li>
	 * <li>Détecter les modifications (libellé et/ou parent).</li>
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
	 * <li>Si {@code libelle de l'objet métier} 
	 * est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_UPDATE_KO_LIBELLE_BLANK}.</li>
	 * <li>Si l'ID est {@code null} :
	 * jette une {@link ExceptionAppliParamNonPersistent}
	 * avec un message {@link #MESSAGE_UPDATE_KO_NON_PERSISTENT}
	 * + {@code pObject.getProduit()}.</li>
	 * <li>Si {@code parent} est {@code null} :
	 * jette une {@link ExceptionAppliParentNull}
	 * avec un message {@link #MESSAGE_UPDATE_KO_PARENT_NULL}.</li>
	 * <li>Si {@code libelle du parent} est {@code null} ou blank :
	 * jette une {@link ExceptionAppliLibelleBlank}
	 * avec un message {@link #MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK}.</li>
	 * <li>Si l'ID du parent est {@code null} :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message {@link #MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si le parent n'existe pas dans le stockage :
	 * jette une {@link ExceptionTechniqueGatewayNonPersistent}
	 * avec un message {@link #MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
	 * + {@code pObject.getSousTypeProduit().getSousTypeProduit()}.</li>
	 * <li>Si l'objet n'existe pas en stockage :
	 * retourne {@code null}. Pas d'Exception.</li>
	 * <li>Si une modification est effectuée :
	 * retourne l'objet persistant modifié.</li>
	 * <li>Si aucune modification n'est effectuée :
	 * retourne l'objet persistant inchangé.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
	 * @param pObject : Produit :
	 * Objet métier portant les modifications (hors ID).
	 * @return Produit :
	 * objet persistant de même ID que pObject, modifié ou inchangé,
	 * ou {@code null} si absent du stockage.
	 * 
	 * @throws ExceptionAppliParamNull 
	 * si {@code pObject == null}.
	 * @throws ExceptionAppliLibelleBlank 
	 * si le libellé de {@code pObject} est blank.
	 * @throws ExceptionAppliParamNonPersistent 
	 * si l'ID de {@code pObject} est {@code null}.
	 * @throws ExceptionAppliParentNull 
	 * si {@code parent == null}.
	 * @throws ExceptionAppliLibelleBlank
	 * si le libellé du parent de pObject est blank.
	 * @throws ExceptionTechniqueGatewayNonPersistent
	 * Si le parent de pObject n'est pas persistant.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	Produit update(Produit pObject) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">Détruit un {@link Produit}
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
	 * <li>Si {@code pObject.getIdProduit() == null} :
	 * jette une {@link ExceptionAppliParamNonPersistent}
	 * avec un message {@link #MESSAGE_DELETE_KO_ID_NULL}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
	 * et propage l'Exception technique cause.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>L'objet est retiré du stockage.</li>
	 * <li>Aucune information utilisateur n'est produite à ce niveau.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : Produit :
	 * Objet métier à détruire dans le stockage.
	 * 
	 * @throws ExceptionAppliParamNull si {@code pObject == null}.
	 *  @throws ExceptionAppliParamNonPersistent 
	 * si {@code pObject.getIdProduit() == null}.
	 * @throws ExceptionTechniqueGateway 
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	void delete(Produit pObject) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compte le nombre de {@link Produit}
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
	 * <li>fonctionnement nominal : Retourne un compteur {@code >= 0}.</li>
	 * <li>Si une erreur technique survient lors de l'accès au stockage :
	 * jette une {@link ExceptionTechniqueGateway}
	 * avec un message {@link #ERREUR_TECHNIQUE_STOCKAGE}
	 *  + {@code safeMessage(e)} 
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
