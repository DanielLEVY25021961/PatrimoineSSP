/* ********************************************************************* */
/* ******************** PORT SERVICE USE CASE ************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <p style="font-weight:bold;">INTERFACE SousTypeProduitICuService.java :</p>
 *
 * <p>Cette INTERFACE modélise :</p>
 * <p><span style="font-weight:bold;">les SERVICES METIER (CU)</span>
 * pour l'objet Métier <code style="font-weight:bold;">
 * SousTypeProduit</code>.</p>
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
 * @since 4 janvier 2026
 */
public interface SousTypeProduitICuService {

	//* -----------------------CONSTANTES ------------------------------*//
	/**
	 * <div>
	 * <p>" - "</p>
	 * </div>
	 */
	String TIRET_ESPACE = " - ";

	/* --------------------------- Creer(...) ---------------------------*/
	
	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Sous-Type de Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL
		= "vous ne pouvez pas sauvegarder un Sous-Type de Produit null.";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Sous-Type de Produit
	 * dont le libellé est blank."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NOM_BLANK
		= "vous ne pouvez pas sauvegarder un Sous-Type de Produit "
			+ "dont le libellé est blank.";

	/**
	 * <div>
	 * <p>"Impossible de vérifier l'unicité
	 * du Sous-Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER =
			"Impossible de vérifier l'unicité "
			+ "du Sous-Type de Produit dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de vérifier le Type de Produit parent
	 * dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER =
			"Impossible de vérifier le Type de Produit parent "
			+ "dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de créer le Sous-Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER =
			"Impossible de créer le Sous-Type de Produit dans le stockage : ";

	/**
	 * <div>
	 * <p>"Impossible de créer le Sous-Type de Produit -
	 * le stockage n'a retourné aucun objet créé."</p>
	 * </div>
	 */
	String MESSAGE_CREATION_TECHNIQUE_KO_CREER =
			"Impossible de créer le Sous-Type de Produit - "
					+ "le stockage n'a retourné aucun objet créé.";

	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur
	 * après la création du Sous-Type de Produit : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Sous-Type de Produit : ";

	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur
	 * après la création du Sous-Type de Produit."</p>
	 * </div>
	 */
	String MESSAGE_CONVERSION_TECHNIQUE_KO_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Sous-Type de Produit.";

	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK
		= "OK - La création de l'objet s'est bien déroulée.";

	/**
	 * <div>
	 * <p>"Vous ne pouvez pas sauvegarder un Sous-Type de Produit
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DOUBLON
		= "Vous ne pouvez pas sauvegarder un Sous-Type de Produit "
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
	 * <p>"Impossible de trouver dans le stockage l'objet : "</p>
	 * </div>
	 */
	String MESSAGE_OBJ_INTROUVABLE
		= "Impossible de trouver dans le stockage l'objet : ";

	/**
	 * <div>
	 * <p>"Impossible de modifier - 
	 * l'objet n'est pas persistant (pas d'ID dans le stockage) : "</p>
	 * </div>
	 */
	String MESSAGE_OBJ_NON_PERSISTE
		= "Impossible de modifier - "
			+ "l'objet n'est pas persistant (pas d'ID dans le stockage) : ";
	
	/**
	 * <div>
	 * <p>"Erreur non spécifiée"</p>
	 * </div>
	 */
	String MSG_ERREUR_NON_SPECIFIEE = "Erreur non spécifiée";

	/**
	 * <div>
	 * <p>"l'objet à rechercher ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OBJ_NULL 
		= "l'objet à rechercher ne doit pas être null.";

	/**
	 * <div>
	 * <p>"La recherche a bien retourné un objet."</p>
	 * </div>
	 */
	String MESSAGE_SUCCES_RECHERCHE 
		= "La recherche a bien retourné un objet.";

	/**
	 * <div>
	 * <p>"La recherche n'a retourné aucun résutat."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_VIDE 
		= "La recherche n'a retourné aucun résutat.";
	
	/**
	 * <div>
	 * <p>"OK - La recherche a retourné des résultats."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OK 
		= "OK - La recherche a retourné des résultats.";
	
	/**
	 * <div>
	 * <p>"KO - la recherche paginée a retourné null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_KO 
		= "KO - la recherche paginée a retourné null.";
	
	/**
	 * <div>
	 * <p>"OK - la recherche paginée a retourné des résultats."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_OK 
		= "OK - la recherche paginée a retourné des résultats.";
	
	/**
	 * <div>
	 * <p>"KO - la modification a retourné null : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_KO = "KO - la modification a retourné null : ";
	
	/**
	 * <div>
	 * <p>"OK - modification réussie de : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_OK = "OK - modification réussie de : ";
	
	/**
	 * <div>
	 * <p>"OK - destruction réussie de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_OK = "OK - destruction réussie de : ";
	
	/**
	 * <div>
	 * <p>"KO - échec de la destruction de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_KO = "KO - échec de la destruction de : ";

	/**
	 * <div>
	 * <p>"Le TypeProduit parent ne doit pas être null"</p>
	 * </div>
	 */
	String RECHERCHE_TYPEPRODUIT_NULL
		= "Le TypeProduit parent ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le TypeProduit parent ne doit pas être null".</p>
	 * </div>
	 */
	String MESSAGE_TYPEPRODUIT_NULL
		= "Le TypeProduit parent ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit doit posséder un parent (TypeProduit)".</p>
	 * </div>
	 */
	String MESSAGE_PAS_PARENT
		= "Le SousTypeProduit doit posséder un parent (TypeProduit)";

	/**
	 * <div>
	 * <p>"Une recherche technique a échouée".</p>
	 * </div>
	 */
	String KO_TECHNIQUE_RECHERCHE
		= "Une recherche technique a échouée";


	
	
	//* ------------------------METHODES -------------------------------*//
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link SousTypeProduitDTO.InputDTO},
	 * puis retourne la réponse sous forme de
	 * {@link SousTypeProduitDTO.OutputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link SousTypeProduitDTO.InputDTO}
	 * provenant de la couche de présentation ;</li>
	 * <li>valider les préconditions applicatives observables
	 * par l'utilisateur ;</li>
	 * <li>vérifier l'absence de doublon fonctionnel ;</li>
	 * <li>récupérer le {@link TypeProduit} parent persistant
	 * nécessaire au rattachement métier ;</li>
	 * <li>convertir l'InputDTO en objet métier {@link SousTypeProduit} ;</li>
	 * <li>déléguer l'écriture au composant technique GATEWAY ;</li>
	 * <li>récupérer l'objet métier effectivement stocké ;</li>
	 * <li>convertir l'objet métier retourné en
	 * {@link SousTypeProduitDTO.OutputDTO} ;</li>
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
	 * <li>Si {@code pInputDTO.getSousTypeProduit()} est blank, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NOM_BLANK},
	 * émet un LOG de service et lève une exception de validation.</li>
	 * <li>Si le libellé du parent est blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Si le DTO correspond à un doublon fonctionnel, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_DOUBLON} + libellé,
	 * émet un LOG de service et lève une exception métier.</li>
	 * <li>Si le parent {@link TypeProduit} n'existe pas dans le stockage
	 * ou n'est pas persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Sinon, délègue la création au composant GATEWAY, puis retourne
	 * l'{@link SousTypeProduitDTO.OutputDTO} correspondant à l'objet réellement
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
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO :
	 * le Sous-Type de Produit à créer via le SERVICE UC.
	 * @return SousTypeProduitDTO.OutputDTO :
	 * le Sous-Type de Produit créé et retourné à la couche appelante ;
	 * peut être {@code null} si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si le libellé du Sous-Type de Produit porté par {@code pInputDTO} est blank.
	 * @throws ExceptionDoublon
	 * si {@code pInputDTO} correspond à un doublon fonctionnel.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors du contrôle d'unicité,
	 * lors de la vérification du parent
	 * ou lors de la création via le GATEWAY.
	 * @throws IllegalStateException
	 * si le parent est absent, non persistant,
	 * si le GATEWAY retourne {@code null}
	 * ou si la conversion finale en {@link SousTypeProduitDTO.OutputDTO}
	 * retourne {@code null}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	SousTypeProduitDTO.OutputDTO creer(
			SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link SousTypeProduitDTO.OutputDTO}
	 * accessibles dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY la liste complète
	 * des {@link SousTypeProduit} présents dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier
	 * puis dédoublonner la réponse côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link SousTypeProduitDTO.OutputDTO} ;</li>
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
	 * {@link SousTypeProduitDTO.OutputDTO} jamais {@code null}
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
	 * @return List<SousTypeProduitDTO.OutputDTO> :
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
	List<SousTypeProduitDTO.OutputDTO> rechercherTous() throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les libellés des {@link SousTypeProduit}
	 * accessibles dans le stockage sous forme de {@link String}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY la liste complète
	 * des {@link SousTypeProduit} présents dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier
	 * puis extraire les libellés exploitables ;</li>
	 * <li>dédoublonner les libellés côté UC si nécessaire ;</li>
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
	 * <li>Sinon, retourne une {@link List} de {@link String}
	 * jamais {@code null}, éventuellement vide.</li>
	 * <li>La liste retournée ne contient que des libellés non blank,
	 * dédoublonnés côté UC si nécessaire.</li>
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
	 * <li>La liste retournée correspond, si elle n'est pas vide,
	 * à l'état métier effectivement accessible dans le stockage
	 * via le GATEWAY, exprimé sous forme de libellés.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;String&gt; :
	 * liste des libellés des objets métier présents dans le stockage ;
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
	List<String> rechercherTousString() throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link SousTypeProduitDTO.OutputDTO}
	 * disponibles sous forme paginée.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>valider la requête de pagination transmise
	 * par la couche appelante ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche paginée des {@link SousTypeProduit}
	 * dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée
	 * par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir le contenu métier paginé
	 * en {@link SousTypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner un {@link ResultatPage} exploitable
	 * par la couche appelante,
	 * avec une pagination cohérente.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pRequetePage == null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PAGEABLE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Délègue ensuite la recherche paginée
	 * au composant GATEWAY.</li>
	 * <li>Si le résultat paginé retourné par le GATEWAY
	 * est {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_PAGINEE_KO},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Sinon, retourne un {@link ResultatPage}
	 * de {@link SousTypeProduitDTO.OutputDTO} jamais {@code null},
	 * en reprenant une pagination cohérente
	 * à partir du résultat technique sécurisé.</li>
	 * <li>En cas d'échec technique remonté par le GATEWAY
	 * ou par la préparation de la réponse paginée utilisateur,
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
	 * qu'après préparation complète
	 * de la réponse paginée utilisateur.</li>
	 * <li>Le contenu retourné dans le {@link ResultatPage},
	 * s'il n'est pas vide,
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY,
	 * exprimé sous forme de DTO paginés.</li>
	 * <li>Aucun résultat paginé partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : RequetePage :
	 * requête de pagination demandée par la couche appelante.
	 * @return ResultatPage&lt;SousTypeProduitDTO.OutputDTO&gt; :
	 * page de résultats DTO ; jamais {@code null}.
	 * @throws IllegalStateException
	 * si {@code pRequetePage == null}
	 * ou si le résultat paginé retourné par le GATEWAY
	 * est {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche paginée
	 * via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse paginée utilisateur.
	 */
	ResultatPage<SousTypeProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche tous les {@link SousTypeProduitDTO.OutputDTO}
	 * correspondant exactement au libellé transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un libellé exact depuis la couche appelante ;</li>
	 * <li>valider le caractère exploitable de ce libellé ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche exacte de tous les {@link SousTypeProduit}
	 * correspondant à ce libellé ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link SousTypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est blank, retourne une {@link List}
	 * vide mais non {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK},
	 * et n'émet ni LOG ni exception.</li>
	 * <li>Délègue ensuite la recherche exacte au composant GATEWAY.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Si aucun résultat exploitable n'est trouvé,
	 * retourne une {@link List} vide mais non {@code null},
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Sinon, retourne une {@link List} non vide de
	 * {@link SousTypeProduitDTO.OutputDTO}
	 * correspondant exactement au libellé recherché,
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
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
	 * <li>Le libellé exact n'étant pas unique pour un
	 * {@link SousTypeProduit},
	 * la méthode doit retourner une collection
	 * et non un DTO unitaire.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String :
	 * libellé exact des SousTypeProduit recherchés.
	 * @return List&lt;SousTypeProduitDTO.OutputDTO&gt; :
	 * liste des DTO trouvés ; jamais {@code null},
	 * éventuellement vide.
	 * @throws ExceptionStockageVide
	 * si le stockage retourne {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	List<SousTypeProduitDTO.OutputDTO> findByLibelle(
			String pLibelle) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche rapidement tous les {@link SousTypeProduitDTO.OutputDTO}
	 * dont le libellé contient un contenu donné.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un contenu partiel de recherche
	 * provenant de la couche appelante ;</li>
	 * <li>valider le caractère exploitable du contenu transmis ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche rapide des {@link SousTypeProduit}
	 * dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link SousTypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pContenu == null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Si {@code pContenu} est blank, délègue à
	 * {@link #rechercherTous()} et retourne tous les enregistrements
	 * selon le contrat observable de cette méthode.</li>
	 * <li>Sinon, délègue la recherche rapide au composant GATEWAY.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Sinon, retourne une {@link List} de
	 * {@link SousTypeProduitDTO.OutputDTO} jamais {@code null},
	 * éventuellement vide.</li>
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
	 * @param pContenu : String :
	 * contenu partiel du libellé recherché.
	 * @return List&lt;SousTypeProduitDTO.OutputDTO&gt; :
	 * liste des DTO dont le libellé contient {@code pContenu} ;
	 * jamais {@code null}, éventuellement vide.
	 * @throws IllegalStateException
	 * si {@code pContenu == null}.
	 * @throws ExceptionStockageVide
	 * si le stockage retourne {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	List<SousTypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu)
			throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link SousTypeProduitDTO.OutputDTO}
	 * rattachés au parent transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un parent
	 * {@link TypeProduitDTO.InputDTO} depuis la couche appelante ;</li>
	 * <li>valider que ce parent est exploitable ;</li>
	 * <li>retrouver le {@link TypeProduit} parent persistant
	 * dans le stockage ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche de tous les {@link SousTypeProduit}
	 * rattachés à ce parent ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link SousTypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pTypeProduit == null}, positionne
	 * {@link #getMessage()} à {@link #RECHERCHE_TYPEPRODUIT_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Si {@code pTypeProduit.getTypeProduit()} est blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Délègue ensuite la recherche du parent persistant
	 * au composant GATEWAY {@code TypeProduit}.</li>
	 * <li>Si le parent est absent du stockage
	 * ou non persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PAS_PARENT},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Délègue ensuite la recherche des enfants
	 * au composant GATEWAY {@code SousTypeProduit}.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Sinon, retourne une {@link List} de
	 * {@link SousTypeProduitDTO.OutputDTO}
	 * jamais {@code null}, éventuellement vide.</li>
	 * <li>Si la liste résultat est vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si la liste résultat n'est pas vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * <li>En cas d'échec technique remonté par la recherche
	 * du parent, par la recherche des enfants
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
	 * dans le stockage pour le parent demandé,
	 * exprimé sous forme de DTO.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitDTO.InputDTO :
	 * parent demandé par la couche appelante.
	 * @return List&lt;SousTypeProduitDTO.OutputDTO&gt; :
	 * liste des SousTypeProduit rattachés au parent ;
	 * jamais {@code null}, éventuellement vide.
	 * @throws IllegalStateException
	 * si {@code pTypeProduit == null},
	 * si son libellé parent est blank,
	 * ou si le parent est absent / non persistant.
	 * @throws ExceptionStockageVide
	 * si le stockage retourne {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * du parent ou des enfants via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	List<SousTypeProduitDTO.OutputDTO> findAllByParent(
			TypeProduitDTO.InputDTO pTypeProduit) throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link SousTypeProduitDTO.OutputDTO}
	 * à partir du couple [parent, libellé]
	 * porté par un {@link SousTypeProduitDTO.InputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link SousTypeProduitDTO.InputDTO}
	 * depuis la couche appelante ;</li>
	 * <li>vérifier que le parent de recherche est exploitable ;</li>
	 * <li>retrouver le {@link TypeProduit} parent persistant
	 * correspondant au libellé porté par le DTO ;</li>
	 * <li>déléguer au composant GATEWAY la recherche
	 * des {@link SousTypeProduit} rattachés à ce parent ;</li>
	 * <li>identifier, dans cette collection,
	 * le {@link SousTypeProduit} correspondant
	 * au libellé enfant porté par le DTO ;</li>
	 * <li>convertir l'objet métier trouvé
	 * en {@link SousTypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null},
	 * positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OBJ_NULL}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une {@link IllegalStateException}.</li>
	 * <li>Sinon, recherche d'abord le parent persistant
	 * correspondant au libellé porté par le DTO.</li>
	 * <li>Si le parent est absent du stockage
	 * ou non persistant, retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Sinon, délègue la recherche des enfants
	 * rattachés à ce parent au composant GATEWAY.</li>
	 * <li>Si aucun enfant n'est disponible pour ce parent
	 * ou si aucun enfant ne correspond au libellé demandé,
	 * retourne {@code null} et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si un objet est trouvé pour le couple
	 * [parent, libellé], retourne l'{@link SousTypeProduitDTO.OutputDTO}
	 * correspondant et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * <li>En cas d'échec technique remonté par la recherche
	 * du parent, par la recherche des enfants
	 * ou par la préparation de la réponse utilisateur,
	 * positionne {@link #getMessage()}
	 * à {@link #KO_TECHNIQUE_RECHERCHE}
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
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage pour le couple [parent, libellé]
	 * porté par le DTO de recherche.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * <li>Le libellé d'un {@link SousTypeProduit}
	 * n'étant pas unique seul,
	 * la recherche s'appuie sur le couple
	 * [parent, libellé].</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO :
	 * DTO de recherche portant le parent
	 * et le libellé enfant à retrouver.
	 * @return SousTypeProduitDTO.OutputDTO :
	 * DTO correspondant à l'objet trouvé pour le couple
	 * [parent, libellé] ; retourne {@code null}
	 * si {@code pInputDTO == null},
	 * si le parent est absent / non persistant
	 * ou si aucun enfant correspondant n'est trouvé.
	 * @throws IllegalStateException
	 * si {@code pInputDTO.getTypeProduit()} est blank.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * du parent ou des enfants via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	SousTypeProduitDTO.OutputDTO findByDTO(
			SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;


	/**
	 * <div>
	 * <p style="font-weight:bold;">Retourne un {@link SousTypeProduit}
	 * sous forme de OutputDTO déterminé par son ID dans le stockage.</p>
	 * <ul>
	 * <li>retourne null si pId == null.</li>
	 * <li>retourne null si l'objet métier n'est pas trouvé.</li>
	 * <li>convertit la réponse en DTO et le retourne.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pId == null}, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL} 
	 * (aucun LOG, aucune exception).</li>
 	 * <li>Si aucun objet n'est trouvé en stockage, positionne 
 	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + pId
 	 * , et retourne {@code null}.</li>
 	 * <li>Sinon, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_SUCCES_RECHERCHE} 
	 * et retourne l'OutputDTO correspondant à l'objet trouvé.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long : ID dans le stockage.
	 * @return SousTypeProduitDTO.OutputDTO : 
	 * l'objet métier qui possède pId, ou null.
	 * @throws Exception
	 */
	SousTypeProduitDTO.OutputDTO findById(Long pId) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">Modifie un {@link SousTypeProduit}
	 * déjà existant dans le stockage.</p>
	 * <p>Cette méthode ne s'applique qu'à un objet déjà persistant.</p>
	 * <ul>
	 * <li>doit jeter une Exception si pInputDTO == null.</li>
	 * <li>doit jeter une Exception 
	 * si le libellé est Blank.</li>
	 * <li>doit jeter une ExceptionNonPersistant si l'ID est null.</li>
	 * <li>retourne null si l'objet n'existe pas en stockage.</li>
	 * <li>retourne l'objet modifié 
	 * si une modification est effectuée.</li>
	 * <li>sinon retourne l'objet inchangé 
	 * et fournit un message utilisateur.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pTypeProduit == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL}, LOG et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getSousTypeProduit()} est Blank
	 * , positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * , LOG et lève une exception.</li>
	 * <li>Si l'objet n'existe pas dans le stockage 
	 * (recherche par libellé exact), retourne {@code null} et positionne
	 * {@link #getMessage()} à 
	 * {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet existe mais n'est pas persistant (ID {@code null})
	 * , positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé
	 * , LOG et lève une exception.</li>
	 * <li>Sinon, délègue la modification au Gateway.</li> 
	 * <li>retourne l'OutputDTO correspondant à l'objet modifié
	 * (ou {@code null} si le Gateway retourne {@code null}).</li>
	 * <ul>
	 * <li>positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_MODIF_KO} + pTypeProduit.getTypeProduit()
	 * , et retourne null si le Gateway retourne {@code null}.</li>
	 * <li>positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_MODIF_OK} + pTypeProduit.getTypeProduit()
	 * , et retourne l'OutputDTO modifié en cas de succès.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO : 
	 * Objet métier portant les modifications (hors ID).
	 * @return SousTypeProduitDTO.OutputDTO : 
	 * OutputDTO de même ID que pTypeProduit, modifié ou inchangé, ou null.
	 * @throws Exception
	 */
	SousTypeProduitDTO.OutputDTO update(
			SousTypeProduitDTO.InputDTO pInputDTO) throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détruit un {@link SousTypeProduit}
	 * déjà existant dans le stockage.</p>
	 * <ul>
	 * <li>doit jeter une Exception 
	 * si pInputDTO == null.</li>
	 * <li>doit jeter une Exception 
	 * si pInputDTO.getSousTypeProduit() est blank.</li>
	 * <li>doit déléguer la suppression à la couche technique.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne
	 *  {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}, LOG
	 *   et lève une exception (violation de contrat).</li>
	 * <li>Si {@code pInputDTO.getSousTypeProduit()} est Blank
	 * , positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * , LOG et lève une exception.</li>
	 * <li>Si l'objet n'existe pas en stockage 
	 * (recherche par libellé exact)
	 * , ne supprime rien
	 * , retourne, et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} 
	 * + libellé.</li>
	 * <li>Sinon, délègue la suppression au Gateway.</li>
	 * <ul>
	 * <li>positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_DELETE_OK} + libelle 
	 * si la destruction aboutit.</li>
	 * <li>alimente {@link #getMessage()} 
	 * à {@link #MESSAGE_DELETE_KO} + libelle 
	 * , LOG et jette une Exception si la destruction a échoué</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO : 
	 * l'objet métier à détruire.
	 * @throws Exception
	 */
	void delete(SousTypeProduitDTO.InputDTO pInputDTO) 
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compte le nombre d'enregistrements dans le stockage.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Délègue au Gateway le comptage et retourne le résultat.</li>
	 * </ul>
	 * </div>
	 *
	 * @return long : nombre d'enregistrements dans le stockage.
	 * @throws Exception
	 */
	long count() throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du message à l'attention de l'Utilisateur
	 * généré en cas de problème lors des opérations de stockage.</p>
	 * <p>ATTENTION : ne jamais finaliser la javadoc si non renseignée.</p>
	 * </div>
	 *
	 * @return String : message utilisateur.
	 */
	String getMessage();

} // Fin de l'INTERFACE SousTypeProduitICuService.
