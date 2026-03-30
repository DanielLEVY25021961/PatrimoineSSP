/* ********************************************************************* */
/* ********************* PORT SERVICE CU ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <p style="font-weight:bold;">INTERFACE ProduitICuService.java :</p>
 *
 * <p>Cette INTERFACE modélise :</p>
 * <p><span style="font-weight:bold;">les SERVICES METIER (CU)</span>
 * pour l'objet Métier <code style="font-weight:bold;">
 * Produit</code>.</p>
 * <p style="font-weight:bold;">PORT SERVICE USE CASE</p>
 *
 * <p>
 * Utilisée par la couche de présentation CONTROLLER pour :
 * </p>
 * <ul>
 * <li>créer / modifier un Objet métier 
 * via {@link #creer(Object)}</li>
 * <li>lister toutes les Objets métier 
 * via {@link #rechercherTous()}</li>
 * <li>lister toutes les Objets métier par pages via
 * {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par libellé exact 
 * via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant 
 * via {@link #findByLibelleRapide(String)}</li>
 * <li>rechercher par DTO 
 * via {@link #findByDTO(SousTypeProduitDTO.InputDTO)}</li>
 * <li>rechercher par ID 
 * via {@link #findById(Long)}</li>
 * <li>supprimer via {@link #delete(SousTypeProduitDTO.InputDTO)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>émettre des messages utilisateurs via {@link #getMessage()}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
public interface ProduitICuService {

	//* -----------------------CONSTANTES ------------------------------*//

	/**
	 * Chaîne " - ".
	 */
	String TIRET_ESPACE = " - ";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL 
		= "vous ne pouvez pas sauvegarder un Produit null.";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Produit 
	 * dont le libellé est blank."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NOM_BLANK 
		= "vous ne pouvez pas sauvegarder un Produit "
				+ "dont le libellé est blank.";

	/**
	 * <div>
	 * <p>"Impossible de vérifier l'unicité 
	 * du Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER =
			"Impossible de vérifier l'unicité "
					+ "du Produit dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de vérifier le Sous-Type de Produit parent 
	 * dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER =
			"Impossible de vérifier le Sous-Type de Produit parent "
					+ "dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de créer le Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER =
			"Impossible de créer le Produit dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de créer le Produit - 
	 * le stockage n'a retourné aucun objet créé."</p>
	 * </div>
	 */
	String MESSAGE_CREATION_TECHNIQUE_KO_CREER =
			"Impossible de créer le Produit - "
					+ "le stockage n'a retourné aucun objet créé.";

	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur 
	 * après la création du Produit : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Produit : ";

	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur 
	 * après la création du Produit."</p>
	 * </div>
	 */
	String MESSAGE_CONVERSION_TECHNIQUE_KO_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Produit.";

	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - La création de l'objet s'est bien déroulée.";

	/**
	 * <div>
	 * <p>"Vous ne pouvez pas sauvegarder un Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DOUBLON 
		= "Vous ne pouvez pas sauvegarder un Produit "
				+ "déjà existant dans le stockage : ";

	/**
	 * <div>
	 * <p>"Le stockage n'a pas retourné d'enregistrements (null)."</p>
	 * </div>
	 */
	String MESSAGE_STOCKAGE_NULL
		= "Le stockage n'a pas retourné d'enregistrements (null).";
	
	/**
	 * <div>
	 * <p>"l'indication de page demandée ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_PAGEABLE_NULL
		= "l'indication de page demandée ne doit pas être null.";

	/**
	 * <div>
	 * <p>"Le paramètre ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_NULL
		= "Le paramètre ne doit pas être null.";

	/**
	 * <div>
	 * <p>"Vous avez passé une chaine de caractères blank
	 * (null ou que des espaces) en paramètre."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_BLANK = "Vous avez passé une chaine "
			+ "de caractères blank (null ou que des espaces) en paramètre.";

	/**
	 * <div>
	 * <p>"Erreur non spécifiée".</p>
	 * </div>
	 */
	String MSG_ERREUR_NON_SPECIFIEE = "Erreur non spécifiée";

	/**
	 * <div>
	 * <p>"Erreur lors de la création de l'objet".</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO = "Erreur lors de la création de l'objet";

	/**
	 * <div>
	 * <p>"Aucun enregistrement ne correspond à la recherche".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_VIDE = "Aucun enregistrement ne correspond à la recherche";

	/**
	 * <div>
	 * <p>"La recherche a retourné des enregistrements".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OK = "La recherche a retourné des enregistrements";

	/**
	 * <div>
	 * <p>"Le Produit est null".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OBJ_NULL = "Le Produit est null";

	/**
	 * <div>
	 * <p>"La recherche a abouti".</p>
	 * </div>
	 */
	String MESSAGE_SUCCES_RECHERCHE = "La recherche a abouti";

	/**
	 * <div>
	 * <p>"Objet Introuvable : ".</p>
	 * </div>
	 */
	String MESSAGE_OBJ_INTROUVABLE = "Objet Introuvable : ";

	/**
	 * <div>
	 * <p>"Objet non persisté en base : ".</p>
	 * </div>
	 */
	String MESSAGE_OBJ_NON_PERSISTE = "Objet non persisté en base : ";

	/**
	 * <div>
	 * <p>"La recherche paginée a abouti".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_OK = "La recherche paginée a abouti";

	/**
	 * <div>
	 * <p>"la recherche paginée a échoué".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_KO = "la recherche paginée a échoué";

	/**
	 * <div>
	 * <p>"La modification a été effectuée".</p>
	 * </div>
	 */
	String MESSAGE_MODIF_OK = "La modification a été effectuée";

	/**
	 * <div>
	 * <p>"La modification a échouée".</p>
	 * </div>
	 */
	String MESSAGE_MODIF_KO = "La modification a échouée";

	/**
	 * <div>
	 * <p>"La suppression a été effectuée".</p>
	 * </div>
	 */
	String MESSAGE_DELETE_OK = "La suppression a été effectuée";

	/**
	 * <div>
	 * <p>"La suppression a échouée".</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO = "La suppression a échouée";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit parent ne doit pas être null".</p>
	 * </div>
	 */
	String RECHERCHE_SOUSTYPEPRODUIT_NULL = "Le SousTypeProduit parent ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit ne doit pas être null".</p>
	 * </div>
	 */
	String MESSAGE_SOUSTYPEPRODUIT_NULL = "Le SousTypeProduit ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le Produit doit posséder un parent (SousTypeProduit)".</p>
	 * </div>
	 */
	String MESSAGE_PAS_PARENT = "Le Produit doit posséder un parent (SousTypeProduit)";

	/**
	 * <div>
	 * <p>"Une recherche technique a échouée".</p>
	 * </div>
	 */
	String KO_TECHNIQUE_RECHERCHE = "Une recherche technique a échouée";

	// * ------------------------METHODES -------------------------------*//

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link ProduitDTO.InputDTO},
	 * puis retourne la réponse sous forme de
	 * {@link ProduitDTO.OutputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link ProduitDTO.InputDTO}
	 * provenant de la couche de présentation ;</li>
	 * <li>valider les préconditions applicatives observables
	 * par l'utilisateur ;</li>
	 * <li>vérifier l'absence de doublon fonctionnel ;</li>
	 * <li>récupérer le {@link SousTypeProduit} parent persistant
	 * nécessaire au rattachement métier ;</li>
	 * <li>convertir l'InputDTO en objet métier {@link Produit} ;</li>
	 * <li>déléguer l'écriture au composant technique GATEWAY ;</li>
	 * <li>récupérer l'objet métier effectivement stocké ;</li>
	 * <li>convertir l'objet métier retourné en
	 * {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable par le CONTROLLER appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NULL}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est blank, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NOM_BLANK},
	 * émet un LOG de service et lève une exception de validation.</li>
	 * <li>Si le libellé du parent est blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Si le DTO correspond à un doublon fonctionnel, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_DOUBLON} + libellé,
	 * émet un LOG de service et lève une exception métier.</li>
	 * <li>Si le parent {@link SousTypeProduit} n'existe pas dans le stockage
	 * ou n'est pas persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Sinon, délègue la création au composant GATEWAY, puis retourne
	 * l'{@link ProduitDTO.OutputDTO} correspondant à l'objet réellement
	 * stocké et rattaché à son parent persistant.</li>
	 * <li>En cas d'échec remonté par la vérification d'unicité,
	 * par la vérification du parent,
	 * par le GATEWAY ou par une étape interne du SERVICE UC,
	 * propage une exception circonstanciée conforme à l'implémentation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()} reflète l'issue
	 * observable de l'opération pour l'appelant.</li>
	 * <li>En cas de succès, {@link #getMessage()} est positionné à
	 * {@link #MESSAGE_CREER_OK} uniquement après préparation complète
	 * de la réponse utilisateur.</li>
	 * <li>En cas d'échec métier, applicatif ou technique,
	 * le SERVICE UC produit un message utilisateur déterministe et traçable.</li>
	 * <li>Le résultat retourné, s'il est non {@code null},
	 * correspond à l'état métier effectivement créé dans le stockage
	 * avec rattachement à un parent persistant.</li>
	 * <li>Le SERVICE UC conserve son rôle d'orchestration applicative entre
	 * couche de présentation, métier, GATEWAY et message utilisateur.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO :
	 * le Produit à créer via le SERVICE UC.
	 * @return ProduitDTO.OutputDTO :
	 * le Produit créé et retourné à la couche appelante ;
	 * peut être {@code null} si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si le libellé du Produit porté par {@code pInputDTO} est blank.
	 * @throws ExceptionDoublon
	 * si {@code pInputDTO} correspond à un doublon fonctionnel.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors du contrôle d'unicité,
	 * lors de la vérification du parent
	 * ou lors de la création via le GATEWAY.
	 * @throws IllegalStateException
	 * si le parent est absent, non persistant,
	 * si le GATEWAY retourne {@code null}
	 * ou si la conversion finale en {@link ProduitDTO.OutputDTO}
	 * retourne {@code null}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	ProduitDTO.OutputDTO creer(
			ProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link ProduitDTO.OutputDTO}
	 * accessibles dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY la liste complète
	 * des {@link Produit} présents dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier
	 * puis dédoublonner la réponse côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Délègue la recherche exhaustive au composant GATEWAY.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Sinon, retourne une {@link List} de
	 * {@link ProduitDTO.OutputDTO} jamais {@code null}
	 * (éventuellement vide).</li>
	 * <li>Si la liste résultat est vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si la liste résultat n'est pas vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * <li>En cas d'échec technique remonté par le GATEWAY
	 * ou par la préparation de la réponse utilisateur,
	 * positionne un message utilisateur technique cohérent
	 * puis propage une exception circonstanciée
	 * conforme à l'implémentation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération.</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après préparation complète de la réponse utilisateur.</li>
	 * <li>La liste retournée, si elle n'est pas vide,
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY,
	 * exprimé sous forme de DTO.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List<ProduitDTO.OutputDTO> :
	 * liste de tous les objets métier présents dans le stockage ;
	 * jamais {@code null}, éventuellement vide.
	 * @throws ExceptionStockageVide
	 * si le stockage retourne {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * exhaustive via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation de la réponse utilisateur.
	 */
	List<ProduitDTO.OutputDTO> rechercherTous() throws Exception;

	
	
	/**
	 * <div>
	 * <p>Retourne tous les libellés de {@link Produit}
	 * accessibles dans le stockage.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>déléguer la recherche exhaustive à {@link #rechercherTous()} ;</li>
	 * <li>extraire de la réponse les libellés Produit exploitables ;</li>
	 * <li>retirer les éventuels libellés {@code null} ou blank ;</li>
	 * <li>retourner une liste de {@link String} exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>délègue la recherche exhaustive à {@link #rechercherTous()} ;</li>
	 * <li>si {@link #rechercherTous()} échoue, propage l'exception
	 * et conserve le message déjà positionné par cette méthode ;</li>
	 * <li>si aucun libellé exploitable n'est disponible en sortie,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}
	 * et retourne une liste vide mais non {@code null} ;</li>
	 * <li>si au moins un libellé exploitable est disponible,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}
	 * et retourne la liste des libellés ;</li>
	 * <li>ne retourne jamais {@code null} lorsque la recherche aboutit.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</p>
	 * <ul>
	 * <li>le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération ;</li>
	 * <li>le message de succès n'est positionné
	 * qu'après préparation complète de la réponse utilisateur ;</li>
	 * <li>les libellés retournés correspondent aux
	 * {@link ProduitDTO.OutputDTO} réellement préparés par
	 * {@link #rechercherTous()} ;</li>
	 * <li>aucun libellé {@code null} ou blank
	 * n'est exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List<String> :
	 * liste de tous les libellés Produit accessibles ;
	 * jamais {@code null}, éventuellement vide.
	 * @throws Exception
	 * si une erreur survient lors de la recherche exhaustive
	 * ou lors de la préparation finale de la réponse.
	 */
	List<String> rechercherTousString() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p>Retourne une page de {@link ProduitDTO.OutputDTO}
	 * correspondant à la requête de pagination fournie.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>valider la requête de pagination reçue ;</li>
	 * <li>déléguer la recherche paginée au GATEWAY Produit ;</li>
	 * <li>convertir le contenu métier de la page
	 * en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse paginée exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>si {@code pRequetePage == null},
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAGEABLE_NULL}
	 * puis lève une {@link IllegalStateException} ;</li>
	 * <li>si la recherche paginée du GATEWAY échoue,
	 * positionne un message technique construit à partir de
	 * {@link #MESSAGE_RECHERCHE_PAGINEE_KO}
	 * puis propage une exception circonstanciée ;</li>
	 * <li>si le GATEWAY retourne {@code null},
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHE_PAGINEE_KO}
	 * puis lève une {@link IllegalStateException} ;</li>
	 * <li>sinon, retourne un {@link ResultatPage}
	 * de {@link ProduitDTO.OutputDTO} non {@code null} ;</li>
	 * <li>le message de succès
	 * {@link #MESSAGE_RECHERCHE_PAGINEE_OK}
	 * n'est positionné qu'après préparation complète
	 * de la réponse paginée.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</p>
	 * <ul>
	 * <li>le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération ;</li>
	 * <li>la réponse paginée retournée correspond
	 * à l'état métier effectivement accessible
	 * via le GATEWAY ;</li>
	 * <li>aucune réponse paginée partielle incohérente
	 * n'est exposée à l'appelant ;</li>
	 * <li>le message de succès n'est positionné
	 * qu'après conversion complète de la page résultat.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : {@link RequetePage}
	 * décrivant la pagination demandée.
	 * @return ResultatPage<ProduitDTO.OutputDTO> :
	 * la page de résultat correspondant à la requête ;
	 * jamais {@code null} si le traitement aboutit.
	 * @throws Exception
	 * si une erreur survient lors de la recherche paginée
	 * ou lors de la préparation finale de la réponse.
	 */
	ResultatPage<ProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir de son libellé.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est Blank, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle String : libellé du Produit.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> findByLibelle(String pLibelle) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche "rapide" des {@link Produit} dans le stockage à partir d'un préfixe de libellé.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pContenu} est {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pContenu} est Blank, délègue à {@link #rechercherTous()}
	 * (mêmes messages/erreurs que {@code rechercherTous}).</li>
	 * <li>Si la recherche retourne une liste vide, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} et retourne la liste vide.</li>
	 * <li>Sinon, positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}
	 * et retourne la liste des résultats.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu String : contenu (préfixe) de recherche rapide.
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : liste de DTOs résultats (non null).
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit} attachés à un parent {@link SousTypeProduit},
	 * sous forme d'OutputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pSousTypeProduit == null}, positionne {@link #getMessage()}
	 * à {@link #RECHERCHE_SOUSTYPEPRODUIT_NULL} et lève une exception.</li>
	 * <li>Si le parent {@link SousTypeProduit} n'est pas trouvé/persistant,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une exception.</li>
	 * <li>Si aucun {@link Produit} n'est trouvé, retourne une liste vide et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Sinon, retourne la liste des OutputDTO et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit SousTypeProduitDTO.InputDTO : parent.
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : liste des Produits (non null).
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> findAllByParent(
			SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null}
	 * et positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OBJ_NULL}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si {@code pInputDTO.getSousTypeProduit()} est Blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une exception.</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null}
	 * et positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de recherche.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir de son identifiant.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pId == null}, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + id.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId Long : identifiant.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO findById(Long pId) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Met à jour un {@link Produit} existant à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est Blank, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK} et lève une exception.</li>
	 * <li>Si l'objet est introuvable, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet est non persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé et lève une exception.</li>
	 * <li>Si la modification échoue, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_MODIF_KO} + libellé.</li>
	 * <li>Sinon, retourne l'OutputDTO modifié et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_OK} + parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de modification.
	 * @return ProduitDTO.OutputDTO : DTO modifié ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Supprime un {@link Produit} à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est Blank, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK} et lève une exception.</li>
	 * <li>Si l'objet est introuvable, ne supprime rien et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si la suppression échoue, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_KO} + libellé et lève une exception.</li>
	 * <li>Sinon, supprime et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_OK} + libellé.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de suppression.
	 * @throws Exception
	 */
	void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne le nombre total de {@link Produit} présents en stockage.
	 * </p>
	 * </div>
	 *
	 * @return long : nombre de Produits.
	 * @throws Exception
	 */
	long count() throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne le dernier message métier/observable produit par le service.
	 * </p>
	 * </div>
	 *
	 * @return String : message courant (peut être null selon le contexte).
	 */
	String getMessage();
	
	

}
