/* ********************************************************************* */
/* ********************* PORT SERVICE CU ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">INTERFACE TypeProduitICuService.java :</p>
 * 
 * <p>Cette INTERFACE modélise :</p> 
 * <p><span style="font-weight:bold;">les SERVICES METIER (CU)</span> 
 * pour l'objet Métier <code style="font-weight:bold;">
 * TypeProduit</code>.</p>
 * <p style="font-weight:bold;">PORT SERVICE USE CASE</p>
 * 
 * <p>
 * Utilisée par la couche de présentation CONTROLLER pour :
 * </p>
 * <ul>
 * <li>créer / modifier un Objet métier via {@link #creer(Object)}</li>
 * <li>lister toutes les Objets métier via {@link #rechercherTous()}</li>
 * <li>lister toutes les Objets métier par pages via 
 * {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par libellé exact via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant via {@link #findByLibelleRapide(String)}</li>
 * <li>rechercher par ID via {@link #findById(Object)}</li>
 * <li>supprimer via {@link #delete(Object)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>émettre des messages utilisateurs via {@link #getMessage()}</li>
 * </ul>
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 janvier 2026
 */
public interface TypeProduitICuService {

	//* ------------------------ATTRIBUTS -------------------------------*//
	/**
	 * <div>
	 * <p>" - "</p>
	 * </div>
	 */
	String TIRET_ESPACE = " - ";

	// * ---------------- CREER ---------------------- *//
	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Type de Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL 
		= "vous ne pouvez pas sauvegarder un Type de Produit null.";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Type de Produit 
	 * dont le libellé est blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NOM_BLANK 
		= "vous ne pouvez pas sauvegarder un Type de Produit "
			+ "dont le libellé est blank (null ou que des espaces).";
	
	/**
	 * <div>
	 * <p>"Impossible de vérifier l'unicité 
	 * du Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER =
			"Impossible de vérifier l'unicité "
			+ "du Type de Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"Impossible de créer le Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER =
			"Impossible de créer le Type de Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"Impossible de créer le Type de Produit - 
	 * le stockage n'a retourné aucun objet créé."</p>
	 * </div>
	 */
	String MESSAGE_CREATION_TECHNIQUE_KO_CREER =
			"Impossible de créer le Type de Produit - "
					+ "le stockage n'a retourné aucun objet créé.";
	
	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur 
	 * après la création du Type de Produit : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Type de Produit : ";
	
	/**
	 * <div>
	 * <p>"Impossible de préparer la réponse utilisateur 
	 * après la création du Type de Produit."</p>
	 * </div>
	 */
	String MESSAGE_CONVERSION_TECHNIQUE_KO_CREER =
			"Impossible de préparer la réponse utilisateur "
					+ "après la création du Type de Produit.";
	
	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - La création de l'objet s'est bien déroulée.";

	/**
	 * <div>
	 * <p>"Vous ne pouvez pas sauvegarder un Type de Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DOUBLON 
		= "Vous ne pouvez pas sauvegarder un Type de Produit "
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
	 * <p>"Vous avez passé null en paramètre."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_NULL 
		= "Vous avez passé null en paramètre.";
	
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
	 * <p>"Une recherche technique a échouée".</p>
	 * </div>
	 */
	String KO_TECHNIQUE_RECHERCHE
		= "Une recherche technique a échouée";


	

	//* ------------------------ METHODES -------------------------------*//


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link TypeProduitDTO.InputDTO} en pilotant 
	 * un scénario complet de SERVICE UC,
	 * puis retourne la réponse sous forme de
	 * {@link TypeProduitDTO.OutputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link TypeProduitDTO.InputDTO} 
	 * provenant de la couche de présentation ;</li>
	 * <li>valider les préconditions applicatives observables 
	 * par l'utilisateur ;</li>
	 * <li>convertir l'InputDTO en objet métier {@link TypeProduit} ;</li>
	 * <li>déléguer l'écriture au composant technique GATEWAY ;</li>
	 * <li>récupérer l'objet métier effectivement stocké ;</li>
	 * <li>convertir l'objet métier retourné en
	 * {@link TypeProduitDTO.OutputDTO} ;</li>
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
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NOM_BLANK},
	 * émet un LOG de service et lève une exception applicative.</li>
	 * <li>Si le DTO correspond à un doublon fonctionnel, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_DOUBLON} + libellé,
	 * émet un LOG de service et lève une exception métier.</li>
	 * <li>Sinon, délègue la création au composant GATEWAY, puis retourne
	 * l'{@link TypeProduitDTO.OutputDTO} correspondant à l'objet réellement
	 * stocké.</li>
	 * <li>En cas d'échec remonté par le GATEWAY ou par une étape interne du
	 * SERVICE UC, propage une exception circonstanciée conforme à
	 * l'implémentation.</li>
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
	 * {@link #MESSAGE_CREER_OK}.</li>
	 * <li>En cas d'échec métier ou applicatif,
	 * le SERVICE UC produit un message utilisateur déterministe et traçable.</li>
	 * <li>Le résultat retourné, s'il est non {@code null},
	 * correspond à l'état métier effectivement créé dans le stockage
	 * via le GATEWAY.</li>
	 * <li>Le SERVICE UC conserve son rôle d'orchestration applicative entre
	 * couche de présentation, métier, GATEWAY et message utilisateur.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * le Type de Produit à créer via le SERVICE UC.
	 * @return TypeProduitDTO.OutputDTO :
	 * le Type de Produit créé et retourné à la couche appelante ;
	 * peut être {@code null} si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si le libellé de {@code pInputDTO} est blank.
	 * @throws ExceptionDoublon
	 * si {@code pInputDTO} correspond à un doublon fonctionnel.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors du contrôle d'unicité
	 * ou lors de la création via le GATEWAY.
	 * @throws IllegalStateException
	 * si le GATEWAY retourne {@code null}
	 * ou si la conversion finale en {@link TypeProduitDTO.OutputDTO}
	 * retourne {@code null}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;	


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link TypeProduitDTO.OutputDTO}
	 * disponibles en pilotant un scénario complet de SERVICE UC.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY la liste complète
	 * des {@link TypeProduit} présents dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>filtrer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse côté UC
	 * si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link TypeProduitDTO.OutputDTO} ;</li>
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
	 * {@link TypeProduitDTO.OutputDTO} jamais {@code null}
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
	 * @return List<TypeProduitDTO.OutputDTO> :
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
	List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les libellés des {@link TypeProduit}
	 * disponibles en pilotant un scénario complet de SERVICE UC.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY la liste complète
	 * des {@link TypeProduit} présents dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>filtrer les éventuels éléments {@code null},
	 * trier les objets métier,
	 * extraire les libellés non blank
	 * et dédoublonner la réponse côté UC si nécessaire ;</li>
	 * <li>retourner une liste de {@link String}
	 * exploitable par la couche appelante.</li>
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
	 * Retourne tous les {@link TypeProduitDTO.OutputDTO}
	 * disponibles sous forme paginée
	 * en pilotant un scénario complet de SERVICE UC.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>valider la requête de pagination transmise
	 * par la couche appelante ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche paginée des {@link TypeProduit}
	 * dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée
	 * par le GATEWAY ;</li>
	 * <li>filtrer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir le contenu métier paginé
	 * en {@link TypeProduitDTO.OutputDTO} ;</li>
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
	 * de {@link TypeProduitDTO.OutputDTO} jamais {@code null},
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
	 * @return ResultatPage&lt;TypeProduitDTO.OutputDTO&gt; :
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
	ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;
	

		
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduitDTO.OutputDTO}
	 * à partir de son libellé exact.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un libellé exact provenant de la couche appelante ;</li>
	 * <li>valider le caractère exploitable du libellé transmis ;</li>
	 * <li>déléguer au composant GATEWAY la recherche exacte
	 * du {@link TypeProduit} correspondant dans le stockage ;</li>
	 * <li>convertir l'objet métier trouvé
	 * en {@link TypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est blank, retourne {@code null},
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Sinon, délègue la recherche exacte au composant GATEWAY.</li>
	 * <li>Si aucun objet n'est trouvé en stockage,
	 * retourne {@code null} et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si un objet est trouvé, retourne l'{@link TypeProduitDTO.OutputDTO}
	 * correspondant.</li>
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
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String :
	 * libellé exact du TypeProduit recherché.
	 * @return TypeProduitDTO.OutputDTO :
	 * DTO correspondant à l'objet trouvé ;
	 * retourne {@code null} si le libellé est blank
	 * ou si aucun objet n'est trouvé.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * via le GATEWAY.
	 * @throws IllegalStateException
	 * si la conversion finale en {@link TypeProduitDTO.OutputDTO}
	 * retourne {@code null}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	TypeProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche rapidement tous les {@link TypeProduitDTO.OutputDTO}
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
	 * la recherche rapide des {@link TypeProduit}
	 * dans le stockage ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>filtrer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link TypeProduitDTO.OutputDTO} ;</li>
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
	 * {@link TypeProduitDTO.OutputDTO} jamais {@code null},
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
	 * @return List<TypeProduitDTO.OutputDTO> :
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
	List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu)
			throws Exception;
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduitDTO.OutputDTO}
	 * à partir d'un {@link TypeProduitDTO.InputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link TypeProduitDTO.InputDTO}
	 * provenant de la couche appelante ;</li>
	 * <li>vérifier que le DTO de recherche est exploitable ;</li>
	 * <li>extraire son libellé métier ;</li>
	 * <li>déléguer la recherche exacte
	 * à {@link #findByLibelle(String)} ;</li>
	 * <li>retourner une réponse exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OBJ_NULL}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Sinon, délègue la recherche exacte
	 * à {@link #findByLibelle(String)}
	 * avec {@code pInputDTO.getTypeProduit()}.</li>
	 * <li>Le comportement observable sur le libellé transmis
	 * (blank, introuvable, succès, erreur technique)
	 * est alors celui de {@link #findByLibelle(String)}.</li>
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
	 * de la réponse utilisateur déléguée.</li>
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via la recherche exacte par libellé.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * DTO de recherche.
	 * @return TypeProduitDTO.OutputDTO :
	 * DTO résultat ; retourne {@code null}
	 * si {@code pInputDTO == null}
	 * ou si la recherche exacte déléguée ne trouve aucun objet.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche exacte
	 * déléguée à {@link #findByLibelle(String)}.
	 * @throws IllegalStateException
	 * si la conversion finale en {@link TypeProduitDTO.OutputDTO}
	 * retourne {@code null}
	 * dans le scénario délégué à {@link #findByLibelle(String)}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * via {@link #findByLibelle(String)}.
	 */
	TypeProduitDTO.OutputDTO findByDTO(
			TypeProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduitDTO.OutputDTO}
	 * à partir de son identifiant persistant.</p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un identifiant persistant
	 * provenant de la couche appelante ;</li>
	 * <li>vérifier que cet identifiant est exploitable ;</li>
	 * <li>déléguer la recherche de l'objet métier
	 * au composant de stockage GATEWAY;</li>
	 * <li>préparer une réponse utilisateur complète
	 * à partir de l'objet effectivement trouvé ;</li>
	 * <li>retourner un {@link TypeProduitDTO.OutputDTO}
	 * exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :
	 * </p>
	 * <ul>
	 * <li>Si {@code pId == null}, retourne {@code null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Sinon, délègue la recherche de l'objet métier
	 * portant cet identifiant persistant
	 * au composant de stockage.</li>
	 * <li>Si aucun objet n'est trouvé en stockage,
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + pId.</li>
	 * <li>Si un objet est trouvé,
	 * prépare un {@link TypeProduitDTO.OutputDTO}
	 * correspondant à cet objet métier.</li>
	 * <li>En cas de succès,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_SUCCES_RECHERCHE}
	 * après préparation complète de la réponse utilisateur.</li>
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
	 * de la réponse utilisateur.</li>
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond à l'objet métier effectivement trouvé
	 * en stockage pour l'identifiant demandé.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long :
	 * identifiant persistant recherché dans le stockage.
	 * @return TypeProduitDTO.OutputDTO :
	 * DTO résultat ; retourne {@code null}
	 * si {@code pId == null}
	 * ou si aucun objet ne correspond à cet identifiant.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient
	 * lors de la recherche par identifiant
	 * via le composant de stockage.
	 * @throws IllegalStateException
	 * si la conversion finale en {@link TypeProduitDTO.OutputDTO}
	 * retourne {@code null}
	 * après récupération d'un objet métier trouvé.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors de la préparation
	 * de la réponse utilisateur.
	 */
	TypeProduitDTO.OutputDTO findById(Long pId) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p>Modifie un {@link TypeProduit} déjà persistant à partir d'un
	 * {@link TypeProduitDTO.InputDTO}.</p>
	 *
	 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
	 * <ul>
	 * <li>recevoir un {@link TypeProduitDTO.InputDTO}
	 * provenant de la couche appelante ;</li>
	 * <li>valider les préconditions applicatives observables
	 * sur le DTO et sur son libellé ;</li>
	 * <li>clarifier qu'un {@link TypeProduitDTO.InputDTO}
	 * ne portant aucun identifiant persistant
	 * ni aucune ancienne valeur métier,
	 * la méthode n'exprime pas un renommage
	 * ni un changement de clé métier ;</li>
	 * <li>ré-identifier l'objet déjà persistant
	 * par une recherche exacte
	 * sur ce même libellé ;</li>
	 * <li>réinjecter l'identifiant persistant retrouvé
	 * dans l'objet métier reconstruit
	 * à partir du DTO ;</li>
	 * <li>déléguer la modification technique
	 * au composant GATEWAY ;</li>
	 * <li>convertir l'objet métier modifié
	 * en {@link TypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL},
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK},
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Le {@link TypeProduitDTO.InputDTO}
	 * ne portant ni identifiant persistant
	 * ni ancienne valeur métier,
	 * cette méthode ne peut pas exprimer
	 * un renommage
	 * ni distinguer un "avant"
	 * d'un "après".</li>
	 * <li>En conséquence,
	 * cette méthode s'applique uniquement
	 * à un objet déjà persistant
	 * retrouvé par le même libellé exact
	 * que celui porté par {@code pInputDTO}.</li>
	 * <li>Si aucun objet n'est trouvé en stockage
	 * via cette recherche exacte,
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet retrouvé existe
	 * mais n'est pas persistant
	 * (identifiant {@code null}),
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé,
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Sinon, réinjecte l'identifiant persistant retrouvé
	 * dans l'objet métier reconstruit à partir du DTO,
	 * puis délègue la modification au GATEWAY.</li>
	 * <li>Si le GATEWAY retourne {@code null},
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_KO} + libellé.</li>
	 * <li>En cas de succès,
	 * retourne un {@link TypeProduitDTO.OutputDTO}
	 * correspondant à l'objet modifié
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_OK} + libellé
	 * seulement après préparation complète
	 * de la réponse utilisateur.</li>
	 * <li>En cas d'échec technique
	 * lors de la recherche de l'existant,
	 * de la délégation de modification
	 * ou de la préparation finale de la réponse,
	 * positionne un message utilisateur technique cohérent
	 * puis propage une exception circonstanciée
	 * conforme à l'implémentation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</strong></p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération.</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après préparation complète
	 * de la réponse utilisateur.</li>
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond à l'objet déjà persistant
	 * retrouvé par libellé exact,
	 * puis modifié via le GATEWAY.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * <li>L'absence d'identifiant
	 * dans le {@link TypeProduitDTO.InputDTO}
	 * est explicitement compensée
	 * par une phase préalable
	 * de ré-identification de l'objet persistant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * DTO portant le libellé métier
	 * de l'objet déjà persistant
	 * à ré-identifier puis à modifier ;
	 * ce DTO ne porte ni ID
	 * ni ancienne valeur métier.
	 * @return TypeProduitDTO.OutputDTO :
	 * DTO de l'objet persistant modifié ;
	 * retourne {@code null}
	 * si aucun objet n'est trouvé
	 * par libellé exact
	 * ou si le GATEWAY retourne {@code null}.
	 * @throws ExceptionParametreNull
	 * si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si {@code pInputDTO.getTypeProduit()} est blank.
	 * @throws ExceptionNonPersistant
	 * si l'objet retrouvé avant modification
	 * n'est pas persistant.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient
	 * lors de la recherche de l'existant,
	 * de la délégation de modification
	 * ou de la préparation finale
	 * de la réponse utilisateur.
	 * @throws IllegalStateException
	 * si l'objet retourné après modification
	 * n'est plus persistant
	 * ou si la conversion finale en
	 * {@link TypeProduitDTO.OutputDTO}
	 * retourne {@code null}.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	TypeProduitDTO.OutputDTO update(
			TypeProduitDTO.InputDTO pInputDTO) throws Exception;
	

	
	/**
	 * <div>
	 * <p>Détruit un {@link TypeProduit} déjà persistant
	 * à partir d'un {@link TypeProduitDTO.InputDTO}.</p>
	 *
	 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
	 * <ul>
	 * <li>recevoir un {@link TypeProduitDTO.InputDTO}
	 * provenant de la couche appelante ;</li>
	 * <li>valider les préconditions applicatives observables
	 * sur le DTO et sur son libellé ;</li>
	 * <li>clarifier que pour un {@link TypeProduitDTO.InputDTO}
	 * ne portant aucun identifiant persistant,
	 * la méthode ré-identifie l'objet à détruire
	 * par une recherche exacte sur le libellé métier ;</li>
	 * <li>vérifier que l'objet retrouvé
	 * est bien persistant ;</li>
	 * <li>déléguer la destruction technique
	 * au composant GATEWAY ;</li>
	 * <li>retourner un message de suppression
	 * observable et exploitable
	 * par la couche appelante si la suppression s'est bien déroulée.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL},
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK},
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Si {@link TypeProduitDTO.InputDTO}
	 * ne porte aucun identifiant persistant,
	 * la destruction cible l'objet déjà persistant
	 * retrouvé par le même libellé exact
	 * que celui porté par {@code pInputDTO}.</li>
	 * <li>Si aucun objet n'est trouvé en stockage
	 * via cette recherche exacte,
	 * ne détruit rien
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet retrouvé existe
	 * mais n'est pas persistant
	 * (identifiant {@code null}),
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé,
	 * émet un LOG de service
	 * et lève une exception.</li>
	 * <li>Sinon, délègue la destruction
	 * au composant GATEWAY.</li>
	 * <li>En cas de succès,
	 * ne retourne aucune valeur
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_OK} + libellé
	 * seulement après destruction effective
	 * de l'objet persistant.</li>
	 * <li>En cas d'échec technique
	 * lors de la recherche de l'existant
	 * ou lors de la destruction,
	 * positionne un message utilisateur technique cohérent
	 * puis propage une exception circonstanciée
	 * conforme à l'implémentation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</strong></p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération.</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après destruction effective
	 * de l'objet persistant ciblé.</li>
	 * <li>Si aucun objet ne correspond au libellé transmis,
	 * aucune suppression n'est exécutée.</li>
	 * <li>Aucune destruction incohérente
	 * d'objet non persistant
	 * ne doit être tentée.</li>
	 * <li>L'absence d'identifiant
	 * dans le {@link TypeProduitDTO.InputDTO}
	 * est explicitement compensée
	 * par une phase préalable
	 * de ré-identification par libellé exact.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * DTO portant le libellé métier
	 * de l'objet déjà persistant
	 * à ré-identifier puis à détruire ;
	 * ce DTO ne porte aucun identifiant persistant.
	 * @throws ExceptionParametreNull
	 * si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si {@code pInputDTO.getTypeProduit()} est blank.
	 * @throws ExceptionNonPersistant
	 * si l'objet retrouvé avant destruction
	 * n'est pas persistant.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient
	 * lors de la recherche de l'existant
	 * ou lors de la destruction via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	void delete(TypeProduitDTO.InputDTO pInputDTO) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compte le nombre de {@link TypeProduit}
	 * dans le stockage.
	 * </p>
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
	 * <p>ATTENTION : ne jamais finaliser cette méthode sinon SPRING 
	 * échoue à fournir le message dans le Proxy de TypeProduitCuService 
	 * injecté dans le CONTROLLER.</p>
	 * </div>
	 *
	 * @return String : 
	 * message à l'attention de l'Utilisateur (peut être null).
	 */
	String getMessage();

}
