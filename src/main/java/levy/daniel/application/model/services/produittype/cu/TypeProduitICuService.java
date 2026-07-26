/* ********************************************************************* */
/* *************** PORT SERVICE METIER USE CASE (CU) ******************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
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

	//* ---------------------- CONSTANTES ----------------------------- *//
	
	/**
	 * <div>
	 * <p>" - "</p>
	 * </div>
	 */
	String TIRET_ESPACE = " - ";


	//* ----------------- CONSTANTES DE MESSAGES ---------------------- *//

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

	/* --------------------------- Creer ------------------------------- */
	
	/**
	 * <div>
	 * <p>"KO - creer() - vous ne pouvez pas sauvegarder un Type de Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL_KO 
		= "KO - creer() - vous ne pouvez pas sauvegarder un Type de Produit null.";

	/**
	 * <div>
	 * <p>"KO - creer() - vous ne pouvez pas sauvegarder un Type de Produit 
	 * dont le libellé est blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_LIBELLE_BLANK_KO 
		= "KO - creer() - vous ne pouvez pas sauvegarder un Type de Produit "
			+ "dont le libellé est blank (null ou que des espaces).";
	
	/**
	 * <div>
	 * <p>"KO - creer() - Impossible de vérifier l'unicité 
	 * du Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_DOUBLON_KO =
			"KO - creer() - Impossible de vérifier l'unicité "
			+ "du Type de Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"KO - creer() - Vous ne pouvez pas sauvegarder un Type de Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_CREER_DOUBLON_KO 
		= "KO - creer() - Vous ne pouvez pas sauvegarder un Type de Produit "
			+ "déjà existant dans le stockage : ";

	/**
	 * <div>
	 * <p>"KO - creer() - Impossible de créer le Type de Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_GATEWAY_KO =
			"KO - creer() - Impossible de créer le Type de Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"KO - creer() - Impossible de créer le Type de Produit - 
	 * le stockage n'a retourné aucun objet créé."</p>
	 * </div>
	 */
	String MESSAGE_CREER_GATEWAY_KO =
			"KO - creer() - Impossible de créer le Type de Produit - "
					+ "le stockage n'a retourné aucun objet créé.";
	
	/**
	 * <div>
	 * <p>"KO - creer() - Impossible de créer l'OutputDTO 
	 * après la création du Type de Produit : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_CONVERSION_KO =
			"KO - creer() - Impossible de créer l'OutputDTO "
					+ "après la création du Type de Produit : ";
	
	/**
	 * <div>
	 * <p>"KO - creer() - OutputDTO null via la conversion  
	 * après la création du Type de Produit."</p>
	 * </div>
	 */
	String MESSAGE_CREER_CONVERSION_KO =
			"KO - creer() - OutputDTO null via la conversion "
					+ "après la création du Type de Produit.";

	/**
	 * <div>
	 * <p>"OK - creer() - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - creer() - La création de l'objet s'est bien déroulée.";

	/* ----------------------- rechercherTous -------------------------- */

	/**
	 * <div>
	 * <p>"KO - rechercherTous() - le Gateway a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
		= "KO - rechercherTous() - le Gateway a jeté Exception";
	
	/**
	 * <div>
	 * <p>"KO - rechercherTous() - le Gateway a retourné Null".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO
		= "KO - rechercherTous() - le Gateway a retourné Null";
	
	/**
	 * <div>
	 * <p>"KO - rechercherTous() - convertirEtDedoublonner(...) a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_CONVERSION_KO
		= "KO - rechercherTous() - convertirEtDedoublonner(...) a jeté Exception";
	
	/**
	 * "KO - rechercherTous() - convertirEtDedoublonner(...) a retourné null"
	 */
	String MESSAGE_RECHERCHER_TOUS_CONVERSION_NULL_KO 
		= "KO - rechercherTous() - convertirEtDedoublonner(...) a retourné null";
	
	/**
	 * "OK - rechercherTous() - La recherche n'a retourné aucun résutat."
	 */
	String MESSAGE_RECHERCHER_TOUS_VIDE 
		= "OK - rechercherTous() - La recherche n'a retourné aucun résutat.";
	
	/**
	 * "OK - rechercherTous() - La recherche a retourné des résultats."
	 */
	String MESSAGE_RECHERCHER_TOUS_OK 
		= "OK - rechercherTous() - La recherche a retourné des résultats.";

	/* ------------------ rechercherTousString ------------------------- */
	
	/**
	 * <div>
	 * <p>"KO - rechercherTousString()
	 * - le Gateway a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_STRING_GATEWAY_KO
		= "KO - rechercherTousString() - le Gateway a jeté Exception";

	/**
	 * <div>
	 * <p>"KO - rechercherTousString()
	 * - la préparation de la réponse utilisateur a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_STRING_PREPARATION_KO
		= "KO - rechercherTousString() "
				+ "- la préparation de la réponse utilisateur a jeté Exception";


	/* ---------------- rechercherTousParPage -------------------------- */

	/**
	 * <div>
	 * <p>"KO - rechercherTousParPage(...)
	 * - le Gateway a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO
		= "KO - rechercherTousParPage(...) "
				+ "- le Gateway a jeté Exception";

	/**
	 * <div>
	 * <p>"KO - rechercherTousParPage(...)
	 * - la préparation de la page DTO a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHER_TOUS_PAR_PAGE_PREPARATION_KO
		= "KO - rechercherTousParPage(...) "
				+ "- la préparation de la page DTO a jeté Exception";

	/* ----------------------- findByLibelle --------------------------- */

	/**
	 * <div>
	 * <p>"KO - findByLibelle(...)
	 * - le Gateway a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_GATEWAY_KO
		= "KO - findByLibelle(...) "
				+ "- le Gateway a jeté Exception";

	/**
	 * <div>
	 * <p>"KO - findByLibelle(...)
	 * - la préparation de la réponse utilisateur a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_PREPARATION_KO
		= "KO - findByLibelle(...) "
				+ "- la préparation de la réponse utilisateur a jeté Exception";

	/**
	 * <div>
	 * <p>"KO - findByLibelle(...)
	 * - la conversion en OutputDTO a retourné null".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_CONVERSION_NULL_KO
		= "KO - findByLibelle(...) "
				+ "- la conversion en OutputDTO a retourné null";

	/**
	 * <div>
	 * <p>"OK - findByLibelle(...) a retourné un enregistrement".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE
		= "OK - findByLibelle(...) a retourné un enregistrement";

	/* -------------------- findByLibelleRapide ------------------------ */

	/**
	 * <div>
	 * <p>"KO - findByLibelleRapide(...)
	 * - le Gateway a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLERAPIDE_GATEWAY_KO
		= "KO - findByLibelleRapide(...) "
				+ "- le Gateway a jeté Exception";

	/**
	 * <div>
	 * <p>"KO - findByLibelleRapide(...)
	 * - le filtrage, le tri ou la conversion en OutputDTO
	 * a jeté Exception".</p>
	 * </div>
	 */
	String MESSAGE_FINDBYLIBELLERAPIDE_PREPARATION_KO
		= "KO - findByLibelleRapide(...) "
				+ "- le filtrage, le tri ou la conversion en OutputDTO "
				+ "a jeté Exception";

	/* -------------------------- findByDTO ---------------------------- */
	
	
	/* --------------------------- findById ---------------------------- */
	

	/* ---------------------------- update ----------------------------- */
	
	/**
	 * <div>
	 * <p>"KO - update() - la modification a retourné null : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_KO = "KO - update() - la modification a retourné null : ";
	
	/**
	 * <div>
	 * <p>"OK - update() - modification réussie de : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_OK = "OK - update() - modification réussie de : ";

	/* ---------------------------- delete ----------------------------- */
	
	/**
	 * <div>
	 * <p>"OK - delete() - destruction réussie de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_OK = "OK - delete() - destruction réussie de : ";
		
	/**
	 * <div>
	 * <p>"KO - delete() - échec de la destruction de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_KO = "KO - delete() - échec de la destruction de : ";
	
	/* ----------------------------- count ----------------------------- */
	
	/* --------------------------- getMessage -------------------------- */

	
	// -------------------Constantes Méthodes ---------------------------//
	
	/**
	 * "méthode Creer(...)"
	 */
	String METHODE_CREER = "méthode Creer(...)";
	
	/**
	 * "méthode rechercherTous()"
	 */
	String METHODE_RECHERCHER_TOUS = "méthode rechercherTous()";
	
	/**
	 * "méthode rechercherTousString()"
	 */
	String METHODE_RECHERCHER_TOUS_STRING 
		= "méthode rechercherTousString()";
	
	/**
	 * "méthode rechercherTousParPage(...)"
	 */
	String METHODE_RECHERCHER_TOUS_PAGE 
		= "méthode rechercherTousParPage(...)";
	
	/**
	 * "méthode findByLibelle(...)"
	 */
	String METHODE_FIND_BY_LIBELLE = "méthode findByLibelle(...)";
	
	/**
	 * "méthode findByLibelleRapide()"
	 */
	String METHODE_FIND_BY_LIBELLE_RAPIDE = "méthode findByLibelleRapide()";
	
	/**
	 * "méthode findById(...)"
	 */
	String METHODE_FIND_BY_ID = "méthode findById(...)";
	
	/**
	 * "méthode update(...)"
	 */
	String METHODE_UPDATE = "méthode update(...)";
	
	/**
	 * "méthode delete(...)"
	 */
	String METHODE_DELETE = "méthode delete(...)";
	
	/**
	 * "méthode count()"
	 */
	String METHODE_COUNT = "méthode count()";

	//* ------------------------ METHODES -------------------------------*//


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Reçoit un {@link TypeProduitDTO.InputDTO},
	 * le transforme en objet métier à stocker,
	 * puis retourne l'objet métier stocké sous forme de
	 * {@link TypeProduitDTO.OutputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link TypeProduitDTO.InputDTO} 
	 * provenant de la couche de présentation ;</li>
	 * <li>vérifier préalablement que l'objet métier peut être stocké 
	 * ({@link TypeProduitDTO.InputDTO} ne peut être null
	 * , ne peut avoir un libellé blank, ne peut créer de doublon) ;</li>
	 * <li>convertir l'InputDTO en objet métier {@link TypeProduit} ;</li>
	 * <li>déléguer l'écriture de l'objet métier dans le stockage 
	 * au service technique GATEWAY ;</li>
	 * <li>récupérer l'objet métier persistant ;</li>
	 * <li>convertir l'objet métier persistant retourné par le GATEWAY en
	 * {@link TypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner le {@link TypeProduitDTO.OutputDTO} 
	 * correspondant à l'objet métier persistant 
	 * au CONTROLLER appelant (peut être {@code null}) avec 
	 * un message utilisateur de succès de la création dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Elimine les paramètres invalides :</li>
	 * <ul>
	 * <li>Si {@code pInputDTO == null} : retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NULL_KO}
	 * et n'émet ni LOG ni Exception.</li>
	 * <li>Si le libellé de l'objet métier 
	 * {@code pInputDTO.getTypeProduit()} est blank : 
	 * positionne {@link #getMessage()} à {@link #MESSAGE_CREER_LIBELLE_BLANK_KO},
	 * émet un LOG et jette une exception applicative 
	 * {@code ExceptionParametreBlank}.</li>
	 * </ul>
	 * <li>Vérifie que creer(...) ne risque pas de créer un doublon 
	 * dans le stockage via la méthode private {@code isDoublon(pInputDTO)} : 
	 * </li>
	 * <ul>
	 * <li>Si la méthode private {@code isDoublon(pInputDTO)} 
	 * jette Exception : 
	 * crée un message sécurisé basé sur 
	 * {@link #PREFIX_MESSAGE_CREER_DOUBLON_KO}, 
	 * positionne {@link #getMessage()} sur le message sécurisé, LOG, 
	 * et propage l'Exception.</li>
	 * <li>Si {@code pInputDTO} correspond à un doublon, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_DOUBLON_KO} + libellé,
	 * émet un LOG de service et lève une exception métier 
	 * {@code ExceptionDoublon}.</li>
	 * </ul>
	 * <li>Convertit l'InputDTO en objet métier, 
	 * et tente la création de l'objet métier dans le stockage en déléguant 
	 * au service GATEWAY via {@code gateway.creer(...)}.</li>
	 * <ul>
	 * <li>Si {@code gateway.creer(...)} jette Exception : 
	 * crée un message sécurisé basé sur 
	 * {@link #PREFIX_MESSAGE_CREER_GATEWAY_KO}, 
	 * positionne {@link #getMessage()} sur le message sécurisé, 
	 * LOG, et propage l'Exception.</li>
	 * <li>Si {@code gateway.creer(...)} retourne null : 
	 * positionne {@link #getMessage()} sur 
	 * {@link #MESSAGE_CREER_GATEWAY_KO}, LOG, 
	 * et jette une {@code IllegalStateException}.</li>
	 * </ul>
	 * <li>Convertit l'objet métier persistant en 
	 * {@link TypeProduitDTO.OutputDTO} via 
	 * {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} : </li>
	 * <ul>
	 * <li>Si {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} 
	 * jette Exception : crée un message sécurisé basé sur 
	 * {@link #PREFIX_MESSAGE_CREER_CONVERSION_KO}, 
	 * positionne {@link #getMessage()} sur le message sécurisé, 
	 * LOG, et propage l'Exception.</li>
	 * <li>Si {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} 
	 * retourne null :  
	 * positionne {@link #getMessage()} sur 
	 * {@link #MESSAGE_CREER_CONVERSION_KO}, 
	 * LOG, et jette une {@code IllegalStateException}.</li>
	 * </ul>
	 * <li>Si tout se passe bien : positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_CREER_OK}, puis retourne le 
	 * {@link TypeProduitDTO.OutputDTO} correspondant 
	 * à l'objet métier persistant.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()} reflète l'issue
	 * de l'opération pour l'appelant.</li>
	 * <li>En cas de succès, {@link #getMessage()} est positionné à
	 * {@link #MESSAGE_CREER_OK}.</li>
	 * <li>En cas d'échec métier (doublon) ou applicatif,
	 * le SERVICE UC produit un message utilisateur explicite.</li>
	 * <li>Le résultat retourné, s'il est non {@code null},
	 * correspond à l'objet métier effectivement créé dans le stockage
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
	 * présents dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au service GATEWAY la liste complète
	 * des {@link TypeProduit} présents dans le stockage ;</li>
	 * <li>filtrer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse côté UC
	 * si nécessaire ;</li>
	 * <li>convertir la liste métier en
	 * {@link TypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste non {@code null} (éventuellement vide) 
	 * à la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Délègue la recherche exhaustive au composant GATEWAY 
	 * via {@code gateway.rechercherTous()}.</li>
	 * <ul>
	 * <li>Si le GATEWAY jette une Exception, positionne
	 * {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO} + message sécurisé,
	 * émet un LOG et propage l'Exception provenant du Gateway.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO},
	 * émet un LOG et lève une {@code ExceptionStockageVide}.</li>
	 * </ul>
	 * <li>Filtre les null et trie la liste d'objets métier 
	 * retournée par le GATEWAY.</li>
	 * <li>Convertit la liste d'objets métier en liste d'OutputDTO 
	 * en dédoublonnant via la méthode private 
	 * {@code convertirEtDedoublonner(...)}.</li>
	 * <ul>
	 * <li>Si convertirEtDedoublonner(...) jette Exception : 
	 * alimente message avec un message sécurisé basé sur 
	 * {@link #MESSAGE_RECHERCHER_TOUS_CONVERSION_KO}, 
	 * LOG, propage l'Exception</li>
	 * <li>Si convertirEtDedoublonner(...) retourne null : 
	 * positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_RECHERCHER_TOUS_CONVERSION_NULL_KO}, LOG, 
	 * jette une {@code IllegalStateException}</li>
	 * </ul>
	 * <li>Si la liste résultat est vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHER_TOUS_VIDE}.</li>
	 * <li>Si la liste résultat n'est pas vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHER_TOUS_OK}.</li>
	 * <li>Retourne une {@link List} de
	 * {@link TypeProduitDTO.OutputDTO} jamais {@code null}
	 * (éventuellement vide).</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue de l'opération.</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après préparation complète de la réponse utilisateur.</li>
	 * <li>La liste retournée, si elle n'est pas vide,
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY,
	 * exprimé sous forme de DTO.</li>
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
	 * <li>Délègue la recherche exhaustive au composant GATEWAY
	 * via {@code gateway.rechercherTous()}.</li>
	 * <li>Si le GATEWAY jette une Exception, positionne
	 * {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHER_TOUS_STRING_GATEWAY_KO}
	 * + message sécurisé, émet un LOG et propage
	 * l'Exception provenant du GATEWAY.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Filtre les objets métier {@code null}, trie les objets métier,
	 * extrait uniquement les libellés non blank
	 * et les dédoublonne en conservant leur ordre.</li>
	 * <li>Si la préparation de la réponse utilisateur jette une Exception,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHER_TOUS_STRING_PREPARATION_KO}
	 * + message sécurisé, émet un LOG et propage l'Exception.</li>
	 * <li>Si la liste résultat est vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si la liste résultat n'est pas vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * <li>Retourne une {@link List} de {@link String}
	 * jamais {@code null}, éventuellement vide.</li>
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
	 * <li>sécuriser la page retournée par le GATEWAY ;</li>
	 * <li>filtrer les éléments {@code null}
	 * et trier les objets métier ;</li>
	 * <li>convertir les objets métier
	 * en {@link TypeProduitDTO.OutputDTO}
	 * et dédoublonner le contenu DTO ;</li>
	 * <li>reconstruire un {@link ResultatPage} DTO non {@code null}
	 * avec le numéro de page, la taille de page
	 * et le total d'éléments sécurisés.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pRequetePage == null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PAGEABLE_NULL},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Si le GATEWAY jette une exception, positionne
	 * {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO}
	 * + {@link #TIRET_ESPACE} + un message technique sûr,
	 * puis propage l'exception d'origine.</li>
	 * <li>Si le résultat paginé retourné par le GATEWAY
	 * est {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_PAGINEE_KO},
	 * émet un LOG de service et lève une {@link IllegalStateException}.</li>
	 * <li>Si le filtrage, le tri, la conversion
	 * ou la reconstruction de la page DTO jette une exception,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHER_TOUS_PAR_PAGE_PREPARATION_KO}
	 * + {@link #TIRET_ESPACE} + un message technique sûr,
	 * puis propage l'exception d'origine.</li>
	 * <li>Après reconstruction complète de la page DTO, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_PAGINEE_OK}
	 * et retourne le {@link ResultatPage} DTO.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète la branche réellement exécutée.</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après reconstruction complète de la page DTO.</li>
	 * <li>Le contenu DTO est non {@code null},
	 * filtré, trié et dédoublonné.</li>
	 * <li>Le numéro de page, la taille de page
	 * et le total d'éléments proviennent de la page Gateway
	 * après sécurisation par le SERVICE UC.</li>
	 * <li>La lecture paginée ne modifie pas le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : RequetePage :
	 * requête de pagination demandée par la couche appelante.
	 * @return ResultatPage&lt;TypeProduitDTO.OutputDTO&gt; :
	 * page DTO non {@code null}.
	 * @throws IllegalStateException
	 * si {@code pRequetePage == null}
	 * ou si le résultat paginé retourné par le GATEWAY
	 * est {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche paginée
	 * via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée lors du filtrage, du tri,
	 * de la conversion ou de la reconstruction de la page DTO.
	 */
	ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;
	

		
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduitDTO.OutputDTO}
	 * à partir de son libellé.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un libellé provenant de la couche appelante ;</li>
	 * <li>refuser localement un paramètre blank ;</li>
	 * <li>déléguer au composant GATEWAY la recherche du
	 * {@link TypeProduit} correspondant dans le stockage ;</li>
	 * <li>convertir l'objet métier trouvé
	 * en {@link TypeProduitDTO.OutputDTO} ;</li>
	 * <li>retourner le DTO obtenu à la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est blank, retourne {@code null},
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK},
	 * n'émet aucun LOG, ne lève aucune exception
	 * et n'appelle pas le GATEWAY.</li>
	 * <li>Si le GATEWAY lève une exception avec message,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_GATEWAY_KO}
	 * + {@link #TIRET_ESPACE} + message technique,
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si le GATEWAY lève une exception sans message,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_GATEWAY_KO}
	 * + {@link #TIRET_ESPACE} + {@link #MSG_ERREUR_NON_SPECIFIEE},
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si aucun objet n'est trouvé dans le stockage,
	 * retourne {@code null} et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si la conversion lève une exception avec message,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_PREPARATION_KO}
	 * + {@link #TIRET_ESPACE} + message technique,
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si la conversion lève une exception sans message,
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_PREPARATION_KO}
	 * + {@link #TIRET_ESPACE} + {@link #MSG_ERREUR_NON_SPECIFIEE},
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si la conversion retourne {@code null},
	 * positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_CONVERSION_NULL_KO},
	 * émet un LOG et lève une {@link IllegalStateException}.</li>
	 * <li>En cas de succès, retourne le
	 * {@link TypeProduitDTO.OutputDTO} correspondant
	 * et positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE}
	 * uniquement après conversion complète.</li>
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
	 * qu'après conversion complète du DTO.</li>
	 * <li>Le DTO retourné, s'il n'est pas {@code null},
	 * correspond au {@link TypeProduit} fourni par le GATEWAY.</li>
	 * <li>La méthode n'écrit rien dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String :
	 * libellé du TypeProduit recherché.
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
	 * notamment lors de la conversion de la réponse utilisateur.
	 */
	TypeProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche tous les {@link TypeProduitDTO.OutputDTO}
	 * dont le libellé contient un contenu donné.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un contenu partiel de recherche
	 * provenant de la couche appelante ;</li>
	 * <li>refuser localement un contenu {@code null} ;</li>
	 * <li>déléguer un contenu blank au scénario
	 * {@link #rechercherTous()} ;</li>
	 * <li>pour un contenu non blank, appeler le GATEWAY
	 * une seule fois afin de rechercher les {@link TypeProduit}
	 * dont le libellé contient ce contenu ;</li>
	 * <li>refuser une liste {@code null} retournée par le GATEWAY ;</li>
	 * <li>retirer les éléments {@code null}, trier les objets métier,
	 * les convertir en {@link TypeProduitDTO.OutputDTO}
	 * et supprimer les doublons ;</li>
	 * <li>positionner le message observable
	 * uniquement après obtention de la liste DTO finale.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pContenu == null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL},
	 * émet un LOG, lève une {@link IllegalStateException}
	 * portant exactement ce message et n'appelle jamais le GATEWAY.</li>
	 * <li>Si {@code pContenu} est blank, retourne directement
	 * le résultat de {@link #rechercherTous()} avec le message,
	 * les exceptions et les garanties de cette méthode.</li>
	 * <li>Si {@code gateway.findByLibelleRapide(pContenu)}
	 * lève une exception avec message, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_GATEWAY_KO}
	 * + {@link #TIRET_ESPACE} + message technique,
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si {@code gateway.findByLibelleRapide(pContenu)}
	 * lève une exception sans message, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_GATEWAY_KO}
	 * + {@link #TIRET_ESPACE} + {@link #MSG_ERREUR_NON_SPECIFIEE},
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG et lève une {@link ExceptionStockageVide}
	 * portant exactement ce message.</li>
	 * <li>Si le filtrage, le tri ou la conversion en DTO
	 * lève une exception avec message, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_PREPARATION_KO}
	 * + {@link #TIRET_ESPACE} + message technique,
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si le filtrage, le tri ou la conversion en DTO
	 * lève une exception sans message, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_FINDBYLIBELLERAPIDE_PREPARATION_KO}
	 * + {@link #TIRET_ESPACE} + {@link #MSG_ERREUR_NON_SPECIFIEE},
	 * émet un LOG et propage la même exception.</li>
	 * <li>Si la liste DTO finale est vide, retourne une liste vide
	 * mais non {@code null} et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si la liste DTO finale n'est pas vide, retourne les DTO
	 * triés et sans doublon, puis positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>La méthode ne retourne jamais {@code null}
	 * lorsqu'elle aboutit.</li>
	 * <li>La liste retournée ne contient aucun élément {@code null}
	 * et aucun doublon.</li>
	 * <li>Les DTO sont ordonnés selon l'ordre métier
	 * des {@link TypeProduit}.</li>
	 * <li>Le message de succès ou d'absence de résultat
	 * n'est positionné qu'après filtrage, tri et conversion complets.</li>
	 * <li>Un échec de filtrage, de tri ou de conversion côté UC
	 * n'est jamais attribué au GATEWAY.</li>
	 * <li>La méthode n'écrit rien dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String :
	 * contenu partiel du libellé recherché.
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt; :
	 * liste non {@code null}, éventuellement vide,
	 * des DTO dont le libellé contient {@code pContenu} ;
	 * pour un contenu blank, résultat de {@link #rechercherTous()}.
	 * @throws IllegalStateException
	 * si {@code pContenu == null}.
	 * @throws ExceptionStockageVide
	 * si le GATEWAY retourne {@code null}.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de la recherche
	 * via le GATEWAY.
	 * @throws Exception
	 * toute autre exception levée lors du filtrage,
	 * du tri ou de la conversion en DTO.
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
	 * <p>Compte le nombre de {@link TypeProduit}
	 * accessibles dans le stockage
	 * en pilotant un scénario complet de SERVICE UC.</p>
	 *
	 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
	 * <ul>
	 * <li>demander au composant GATEWAY
	 * le nombre total de {@link TypeProduit}
	 * présents dans le stockage ;</li>
	 * <li>sécuriser la valeur numérique
	 * retournée par le GATEWAY ;</li>
	 * <li>retourner un résultat de comptage
	 * exploitable par la couche appelante ;</li>
	 * <li>positionner un message utilisateur
	 * cohérent avec l'issue observable du comptage.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
	 * <ul>
	 * <li>Délègue le comptage
	 * au composant GATEWAY.</li>
	 * <li>Retourne un {@code long}
	 * représentant le nombre total
	 * d'objets présents dans le stockage.</li>
	 * <li>Si le comptage retourné vaut {@code 0},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si le comptage retourné est strictement positif,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * <li>Si le composant GATEWAY retourne
	 * une valeur négative,
	 * positionne un message utilisateur technique cohérent
	 * et lève une exception,
	 * car un tel résultat est incohérent
	 * pour un comptage observable.</li>
	 * <li>En cas d'échec technique
	 * lors du comptage via le GATEWAY,
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
	 * <li>Le message de succès ou d'absence de résultat
	 * n'est positionné qu'après récupération effective
	 * du comptage retourné par le GATEWAY.</li>
	 * <li>Le résultat retourné correspond
	 * au nombre total d'objets
	 * effectivement accessibles dans le stockage
	 * via le GATEWAY.</li>
	 * <li>Aucune valeur de comptage incohérente
	 * ne doit être exposée à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @return long :
	 * nombre total d'enregistrements présents
	 * dans le stockage ;
	 * peut valoir {@code 0},
	 * mais ne doit jamais être négatif.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient
	 * lors du comptage via le GATEWAY.
	 * @throws IllegalStateException
	 * si le comptage retourné
	 * est incohérent
	 * (par exemple strictement négatif).
	 * @throws Exception
	 * toute autre exception levée par l'implémentation.
	 */
	long count() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p>Retourne le message utilisateur courant
	 * porté localement par le SERVICE METIER UC.</p>
	 *
	 * <p><strong>INTENTION DE SERVICE UC (scénario nominal) :</strong></p>
	 * <ul>
	 * <li>exposer à la couche appelante
	 * le message observable le plus récent
	 * produit par le SERVICE UC ;</li>
	 * <li>permettre la consultation de ce message
	 * après une opération nominale,
	 * une absence de résultat,
	 * une erreur bénigne,
	 * une erreur métier
	 * ou une erreur technique ;</li>
	 * <li>retourner ce message
	 * sans recalcul,
	 * sans délégation technique
	 * et sans altération de l'état courant du service.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>CONTRAT DE SERVICE UC :</strong></p>
	 * <ul>
	 * <li>Peut retourner {@code null}
	 * tant qu'aucune opération UC
	 * n'a encore positionné de message observable.</li>
	 * <li>Retourne ensuite le dernier message local
	 * effectivement positionné
	 * par l'opération UC la plus récente.</li>
	 * <li>Le message retourné peut correspondre
	 * à un succès,
	 * à une absence de résultat,
	 * à une erreur bénigne,
	 * à une erreur métier
	 * ou à une erreur technique.</li>
	 * <li>Ne délègue jamais au composant GATEWAY.</li>
	 * <li>Ne modifie aucun état métier
	 * ni aucun état technique du service.</li>
	 * <li>N'émet aucun LOG
	 * et ne lève aucune exception.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><strong>GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</strong></p>
	 * <ul>
	 * <li>Le message retourné reflète l'issue observable
	 * de l'opération UC la plus récente.</li>
	 * <li>Le getter retourne un état local déjà établi ;
	 * il ne recalcule pas le message.</li>
	 * <li>Le getter reste appelable
	 * avant comme après les opérations UC.</li>
	 * <li>Aucune interaction technique supplémentaire
	 * n'est déclenchée par sa consultation.</li>
	 * </ul>
	 * </div>
	 *
	 * @return String :
	 * message utilisateur courant du SERVICE UC ;
	 * peut valoir {@code null}
	 * avant toute opération ayant positionné un message.
	 */
	String getMessage();
	
	
	
}
