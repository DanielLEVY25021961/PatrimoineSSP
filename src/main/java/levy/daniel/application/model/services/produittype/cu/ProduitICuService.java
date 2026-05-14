/* ********************************************************************* */
/* *************** PORT SERVICE METIER USE CASE (CU) ******************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
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
 * <li>supprimer via {@link #delete(ProduitDTO.InputDTO)}</li>
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

	//* ---------------------- CONSTANTES ----------------------------- *//
	
	/**
	 * <div>
	 * <p>" - "</p>
	 * </div>
	 */
	String TIRET_ESPACE = " - ";


	//* ----------------- CONSTANTES DE MESSAGES ---------------------- *//


	/* --------------------------- Creer ------------------------------- */
	
	/**
	 * <div>
	 * <p>"KO - vous ne pouvez pas sauvegarder 
	 * un Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL_KO 
		= "KO - vous ne pouvez pas sauvegarder "
				+ "un Produit null.";

	/**
	 * <div>
	 * <p>"KO - vous ne pouvez pas sauvegarder un Produit 
	 * dont le libellé est blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_LIBELLE_BLANK_KO 
		= "KO - vous ne pouvez pas sauvegarder un Produit "
			+ "dont le libellé est blank (null ou que des espaces).";
	
	/**
	 * "KO - Le Produit doit posséder un parent (SousTypeProduit) 
	 * avec un libellé non blank."
	 */
	String MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO
	= "KO - Le Produit doit posséder un parent (SousTypeProduit) "
			+ "avec un libellé non blank.";
	
	/**
	 * "KO - Impossible de trouver le parent via 
	 * sousTypeProduitGateway.findByLibelle(...) 
	 * avec le libellé parent indiqué : "
	 */
	String PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO 
		= "KO - Impossible de trouver le parent via "
				+ "sousTypeProduitGateway.findByLibelle(...) "
				+ "avec le libellé parent indiqué : ";


	/**
	 * <div>
	 * <p>"KO - Le Produit doit posséder un parent 
	 * (SousTypeProduit) persistant".</p>
	 * </div>
	 */
	String MESSAGE_CREER_PARENT_NON_PERSISTANT_KO
		= "KO - Le Produit doit posséder un parent "
				+ "(SousTypeProduit) persistant";


	/**
	 * <div>
	 * <p>"KO - Impossible de vérifier l'unicité 
	 * du Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_DOUBLON_KO =
			"KO - Impossible de vérifier l'unicité "
			+ "du Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"KO - Vous ne pouvez pas sauvegarder un Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_CREER_DOUBLON_KO 
		= "KO - Vous ne pouvez pas sauvegarder un Produit "
			+ "déjà existant dans le stockage : ";


	/**
	 * <div>
	 * <p>"KO - Impossible de créer le Produit dans le stockage : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_GATEWAY_KO =
			"KO - Impossible de créer le Produit dans le stockage : ";
	
	/**
	 * <div>
	 * <p>"KO - Impossible de créer le Produit - 
	 * le stockage n'a retourné aucun objet métier créé."</p>
	 * </div>
	 */
	String MESSAGE_CREER_GATEWAY_KO =
			"KO - Impossible de créer le Produit - "
					+ "le stockage n'a retourné aucun objet métier créé.";
	
	/**
	 * <div>
	 * <p>"KO - Impossible de créer l'OutputDTO 
	 * après la création du Produit : "</p>
	 * </div>
	 */
	String PREFIX_MESSAGE_CREER_CONVERSION_KO =
			"KO - Impossible de créer l'OutputDTO "
					+ "après la création du Produit : ";
	
	/**
	 * <div>
	 * <p>"KO - OutputDTO null via la conversion  
	 * après la création du Produit."</p>
	 * </div>
	 */
	String MESSAGE_CREER_CONVERSION_KO =
			"KO - OutputDTO null via la conversion "
					+ "après la création du Produit.";

	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - La création de l'objet s'est bien déroulée.";

	/* ----------------------- rechercherTous -------------------------- */
	
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
	 * <p>"Objet Introuvable : ".</p>
	 * </div>
	 */
	String MESSAGE_OBJ_INTROUVABLE = "Objet Introuvable : ";

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
	 * <p>"Erreur non spécifiée".</p>
	 * </div>
	 */
	String MSG_ERREUR_NON_SPECIFIEE = "Erreur non spécifiée";

	/**
	 * "OK - findByLibelle(...) a retourné des enregistrements"
	 */
	String MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE 
		= "OK - findByLibelle(...) a retourné des enregistrements";
	
	/**
	 * <div>
	 * <p>"l'objet à rechercher ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OBJ_NULL 
		= "l'objet à rechercher ne doit pas être null.";
	
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
	 * <p>"Le SousTypeProduit parent ne doit pas être null".</p>
	 * </div>
	 */
	String RECHERCHE_PARENT_NULL 
		= "Le SousTypeProduit parent ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit ne doit pas être null".</p>
	 * </div>
	 */
	String MESSAGE_PARENT_NULL 
		= "Le SousTypeProduit ne doit pas être null";

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
	 * "méthode FindAllByParent(...)"
	 */
	String METHODE_FIND_ALL_BY_PARENT = "méthode FindAllByParent(...)";
	
	/**
	 * "méthode findByDTO(...)"
	 */
	String METHODE_FIND_BY_DTO = "méthode findByDTO(...)";
	
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

	

	// * ------------------------METHODES -------------------------------*//

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link ProduitDTO.InputDTO},
	 * puis retourne l'objet métier stocké sous forme de
	 * {@link ProduitDTO.OutputDTO}.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un {@link ProduitDTO.InputDTO}
	 * provenant de la couche de présentation ;</li>
	 * <li>vérifier préalablement que l'objet métier peut être stocké 
	 * ({@link ProduitDTO.InputDTO} ne peut être null
	 * , ne peut avoir un libellé blank, doit avoir un parent persistant, 
	 * ne peut créer de doublon) ;</li>
	 * <li>convertir l'InputDTO en objet métier {@link Produit} ;</li>
	 * <li>déléguer l'écriture de l'objet métier dans le stockage 
	 * au service technique GATEWAY ;</li>
	 * <li>récupérer l'objet métier persistant ;</li>
	 * <li>convertir l'objet métier persistant retourné par le GATEWAY en
	 * {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner le {@link ProduitDTO.OutputDTO} 
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
	 * {@code pInputDTO.getSousTypeProduit()} est blank : 
	 * positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_CREER_LIBELLE_BLANK_KO},
	 * LOG et jette une exception applicative 
	 * {@code ExceptionParametreBlank}.</li>
	 * <li>Si le libellé du parent est blank dans {@code pInputDTO},
	 * positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO},
	 * LOG et lève une {@code IllegalStateException}.</li>
	 * </ul>
	 * <li>Utilise la méthode private 
	 * {@code this.rechercherParentPersistant(pInputDTO)} 
	 * pour la récupération du parent persistant. 
	 * C'est obligatoire car le libellé d'un parent {@code SousTypeProduit} 
	 * n'est pas unique dans le stockage : </li>
	 * <ul>
	 * <li>Si {@code this.rechercherParentPersistant(pInputDTO)} 
	 * jette une Exception : 
	 * crée un message sécurisé basé sur 
	 * {@link #PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO},
	 * positionne {@link #getMessage()} sur le message sécurisé,  
	 * LOG et propage l'Exception.</li>
	 * <li>Si le parent {@link TypeProduit} n'existe pas dans le stockage
	 * ou n'est pas persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO},
	 * LOG et jette une {@link IllegalStateException}.</li>
	 * </ul>
	 * <li>Vérifie que creer(...) ne risque pas de créer un doublon 
	 * dans le stockage via la méthode private {@code isDoublon(pInputDTO)} : </li>
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
	 * rattache le parent persistant à l'objet métier,
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
	 * et jette une IllegalStateException</li>
	 * </ul>
	 * <li>Convertit l'objet métier persistant en 
	 * {@link ProduitDTO.OutputDTO} via 
	 * {@code ConvertisseurMetierToOutputDTOProduit.convert(...)} </li>
	 * <ul>
	 * <li>Si {@code ConvertisseurMetierToOutputDTOProduit.convert(...)} 
	 * jette Exception : crée un message sécurisé basé sur 
	 * {@link #PREFIX_MESSAGE_CREER_CONVERSION_KO}, 
	 * positionne {@link #getMessage()} sur le message sécurisé, 
	 * LOG, et propage l'Exception.</li>
	 * <li>Si {@code ConvertisseurMetierToOutputDTOProduit.convert(...)} 
	 * retourne null :  
	 * positionne {@link #getMessage()} sur 
	 * {@link #MESSAGE_CREER_CONVERSION_KO}, 
	 * LOG, et jette une {@code IllegalStateException}.</li>
	 * </ul>
	 * <li>Si tout se passe bien : positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_CREER_OK}, puis retourne le 
	 * {@link ProduitDTO.OutputDTO} correspondant 
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
	 * Retourne tous les {@link ProduitDTO.OutputDTO}
	 * dont le libellé correspond exactement au libellé demandé.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un libellé exact depuis la couche appelante ;</li>
	 * <li>valider le caractère exploitable (non null ou blank) 
	 * de ce libellé ;</li>
	 * <li>déléguer au composant GATEWAY
	 * la recherche exacte de tous les objets métier 
	 * {@link Produit}
	 * correspondant à ce libellé ;</li>
	 * <li>sécuriser la réponse technique retournée par le GATEWAY ;</li>
	 * <li>retirer les éventuels éléments {@code null},
	 * trier les objets métier et dédoublonner la réponse
	 * côté UC si nécessaire ;</li>
	 * <li>convertir la liste métier en 
	 * {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste non null à la couche appelante.</li>
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
	 * <li>délègue ensuite la recherche exacte au composant GATEWAY ;</li>
	 * <li>Si le GATEWAY retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL},
	 * émet un LOG de service et lève une exception.</li>
	 * <li>Si aucun résultat n'est trouvé,
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
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</p>
	 * <ul>
	 * <li>le message retourné par {@link #getMessage()}
	 * reflète l'issue de l'opération ;</li>
	 * <li>le message de succès n'est positionné
	 * qu'après préparation complète de la réponse utilisateur ;</li>
	 * <li>La liste retournée, si elle n'est pas vide,
	 * correspond aux objets métier ayant pour libellé pLibelle
	 * dans le stockage (liste de DTOs).</li>
	 * <li>Le libellé n'étant pas unique pour un objet métier
	 * {@link Produit},
	 * la méthode doit retourner une collection
	 * et non un DTO unitaire.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String :
	 * libellé exact des Produit recherchés.
	 * @return List<ProduitDTO.OutputDTO> :
	 *  liste des DTO trouvés ; jamais {@code null},
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
	List<ProduitDTO.OutputDTO> findByLibelle(String pLibelle) 
			throws Exception;

	
	
	/**
	 * <div>
	 * <p>Retourne tous les {@link ProduitDTO.OutputDTO}
	 * dont le libellé contient rapidement le contenu demandé.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>valider le contenu de recherche rapide ;</li>
	 * <li>si le contenu est blank, déléguer à {@link #rechercherTous()} ;</li>
	 * <li>sinon, déléguer la recherche rapide au GATEWAY Produit ;</li>
	 * <li>retirer les éventuels objets métier {@code null} ;</li>
	 * <li>trier les objets métier ;</li>
	 * <li>convertir les résultats métier en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>si {@code pContenu == null},
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}
	 * puis lève une exception ;</li>
	 * <li>si {@code pContenu} est blank,
	 * délègue à {@link #rechercherTous()}
	 * avec les mêmes messages et les mêmes erreurs ;</li>
	 * <li>si le GATEWAY retourne {@code null},
	 * positionne {@link #getMessage()} à
	 * {@link #KO_TECHNIQUE_RECHERCHE}
	 * puis propage une exception technique ;</li>
	 * <li>si aucun objet n'est trouvé,
	 * retourne une liste vide mais non {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>si au moins un objet est trouvé,
	 * retourne une liste de {@link ProduitDTO.OutputDTO}
	 * non {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</p>
	 * <ul>
	 * <li>le message retourné par {@link #getMessage()}
	 * reflète l'issue observable de l'opération ;</li>
	 * <li>le message de succès n'est positionné
	 * qu'après préparation complète de la réponse utilisateur ;</li>
	 * <li>la liste retournée, si elle n'est pas vide,
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY,
	 * exprimé sous forme de DTO ;</li>
	 * <li>aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String :
	 * contenu de recherche rapide sur le libellé Produit.
	 * @return List<ProduitDTO.OutputDTO> :
	 * liste des Produits correspondant à la recherche rapide ;
	 * jamais {@code null} si le traitement aboutit.
	 * @throws Exception
	 * si une erreur technique survient lors de la recherche rapide
	 * ou lors de la préparation de la réponse utilisateur.
	 */
	List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) 
			throws Exception;

	
	
	/**
	 * <div>
	 * <p>Retourne tous les {@link ProduitDTO.OutputDTO}
	 * rattachés au parent {@link SousTypeProduitDTO.InputDTO} demandé.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>valider le parent demandé ;</li>
	 * <li>retrouver le parent persistant correspondant ;</li>
	 * <li>déléguer au GATEWAY Produit la recherche de tous les
	 * {@link Produit} rattachés à ce parent ;</li>
	 * <li>retirer les éventuels objets métier {@code null} ;</li>
	 * <li>trier les objets métier ;</li>
	 * <li>convertir les résultats métier en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une liste exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>si {@code pSousTypeProduit == null},
	 * positionne {@link #getMessage()} à
	 * {@link #RECHERCHE_PARENT_NULL}
	 * puis lève une exception ;</li>
	 * <li>si le libellé du parent est blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}
	 * puis lève une exception ;</li>
	 * <li>si le parent n'est pas trouvé ou n'est pas persistant,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}
	 * puis lève une exception ;</li>
	 * <li>si le GATEWAY retourne {@code null},
	 * positionne {@link #getMessage()} à
	 * {@link #KO_TECHNIQUE_RECHERCHE}
	 * puis propage une exception technique ;</li>
	 * <li>si aucun objet n'est trouvé,
	 * retourne une liste vide mais non {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>si au moins un objet est trouvé,
	 * retourne une liste de {@link ProduitDTO.OutputDTO}
	 * non {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK}.</li>
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
	 * <li>la liste retournée, si elle n'est pas vide,
	 * correspond à l'état métier effectivement accessible
	 * dans le stockage via le GATEWAY,
	 * exprimé sous forme de DTO ;</li>
	 * <li>aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitDTO.InputDTO :
	 * parent demandé.
	 * @return List<ProduitDTO.OutputDTO> :
	 * liste des Produits rattachés au parent ;
	 * jamais {@code null} si le traitement aboutit.
	 * @throws Exception
	 * si une erreur survient lors de la recherche du parent,
	 * de la recherche des Produits
	 * ou de la préparation de la réponse utilisateur.
	 */
	List<ProduitDTO.OutputDTO> findAllByParent(
			SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;
	


	/**
	 * <div>
	 * <p>Recherche un {@link ProduitDTO.OutputDTO}
	 * à partir d'un {@link ProduitDTO.InputDTO}.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>valider le DTO de recherche ;</li>
	 * <li>valider les informations de parent nécessaires à la recherche ;</li>
	 * <li>retrouver le parent persistant correspondant ;</li>
	 * <li>demander au GATEWAY Produit tous les {@link Produit}
	 * rattachés à ce parent ;</li>
	 * <li>rechercher dans cette liste l'objet correspondant exactement
	 * au libellé demandé ;</li>
	 * <li>convertir l'objet métier trouvé en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>si {@code pInputDTO == null}, retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OBJ_NULL}
	 * sans lever d'exception ;</li>
	 * <li>si le parent porté par {@code pInputDTO}
	 * est blank, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}
	 * puis lève une exception ;</li>
	 * <li>si aucun parent persistant n'est trouvé,
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>si aucun {@link Produit} ne correspond,
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>sinon, retourne l'{@link ProduitDTO.OutputDTO} correspondant
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
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
	 * <li>l'objet retourné, s'il n'est pas {@code null},
	 * correspond à un {@link Produit} effectivement retrouvé
	 * dans le stockage et exprimé sous forme de DTO ;</li>
	 * <li>aucun résultat incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO :
	 * DTO de recherche.
	 * @return ProduitDTO.OutputDTO :
	 * DTO résultat ou {@code null}.
	 * @throws Exception
	 * si une erreur survient lors de la recherche
	 * ou lors de la préparation de la réponse utilisateur.
	 */
	ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) 
			throws Exception;

	
	
	/**
	 * <div>
	 * <p>Retourne un {@link ProduitDTO.OutputDTO}
	 * recherché par son identifiant technique.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC (scénario nominal) :</p>
	 * <ul>
	 * <li>recevoir un identifiant technique de {@link Produit} ;</li>
	 * <li>déléguer la recherche au composant GATEWAY ;</li>
	 * <li>convertir l'objet métier retrouvé en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>si {@code pId == null}, retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}
	 * et n'émet ni LOG ni exception ;</li>
	 * <li>si aucun objet n'est trouvé pour l'identifiant demandé,
	 * retourne {@code null} et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + id ;</li>
	 * <li>si un objet est trouvé, retourne l'{@link ProduitDTO.OutputDTO}
	 * correspondant et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_SUCCES_RECHERCHE} ;</li>
	 * <li>en cas d'échec technique remonté par le GATEWAY
	 * ou par la conversion finale,
	 * propage une exception conforme à l'implémentation.</li>
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
	 * <li>l'objet retourné, s'il n'est pas {@code null},
	 * correspond à un {@link Produit} effectivement retrouvé
	 * dans le stockage et exprimé sous forme de DTO ;</li>
	 * <li>aucun résultat incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long :
	 * identifiant technique du Produit recherché.
	 * @return ProduitDTO.OutputDTO :
	 * DTO résultat ou {@code null}.
	 * @throws Exception
	 * si une erreur survient lors de la recherche
	 * ou lors de la préparation de la réponse utilisateur.
	 */
	ProduitDTO.OutputDTO findById(Long pId) throws Exception;
	
	
	
	/**
	 * <div>
	 * <p>Modifie un {@link Produit} déjà persistant identifié
	 * par le couple [parent, libellé]
	 * et retourne le résultat sous forme
	 * de {@link ProduitDTO.OutputDTO}.</p>
	 * <p style="font-weight:bold;">INTENTION DE SERVICE UC 
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>recevoir un {@link ProduitDTO.InputDTO}
	 * transmis par la couche appelante ;</li>
	 * <li>valider les préconditions observables
	 * sur le DTO, sur le libellé Produit
	 * et sur le parent ;</li>
	 * <li>retrouver le {@link SousTypeProduit} parent persistant
	 * correspondant au DTO ;</li>
	 * <li>demander au composant GATEWAY
	 * tous les {@link Produit}
	 * rattachés à ce parent ;</li>
	 * <li>identifier, dans cette collection,
	 * le {@link Produit} effectivement persistant
	 * correspondant au couple [parent, libellé] ;</li>
	 * <li>reconstruire l'objet métier
	 * à partir du DTO de modification ;</li>
	 * <li>réinjecter l'identifiant persistant retrouvé
	 * et rattacher explicitement le parent persistant ;</li>
	 * <li>déléguer la modification technique
	 * au composant GATEWAY ;</li>
	 * <li>convertir l'objet métier modifié
	 * en {@link ProduitDTO.OutputDTO} ;</li>
	 * <li>retourner une réponse exploitable
	 * par la couche appelante.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL},
	 * émet un LOG de service
	 * et lève une {@link ExceptionParametreNull} ;</li>
	 * <li>Si {@code pInputDTO.getProduit()} est blank,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK},
	 * émet un LOG de service
	 * et lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>Si le parent porté par le DTO
	 * est blank, absent ou non persistant,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO},
	 * émet un LOG de service
	 * et lève une {@link IllegalStateException} ;</li>
	 * <li>Si aucun {@link Produit} persistant
	 * ne correspond au couple [parent, libellé],
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé ;</li>
	 * <li>Si l'objet retrouvé n'est pas persistant,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé,
	 * émet un LOG de service
	 * et lève une {@link ExceptionNonPersistant} ;</li>
	 * <li>Si la modification échoue techniquement,
	 * positionne un message utilisateur technique cohérent
	 * construit à partir de
	 * {@link #MESSAGE_MODIF_KO} + libellé
	 * et propage une exception circonstanciée
	 * conforme à l'implémentation ;</li>
	 * <li>Si le GATEWAY retourne {@code null},
	 * retourne {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_KO} + libellé ;</li>
	 * <li>Si la conversion finale
	 * en {@link ProduitDTO.OutputDTO}
	 * retourne {@code null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_KO}
	 * + libellé
	 * + {@link #TIRET_ESPACE}
	 * + {@link #MSG_ERREUR_NON_SPECIFIEE},
	 * émet un LOG
	 * et lève une {@link IllegalStateException} ;</li>
	 * <li>En cas de succès,
	 * retourne un {@link ProduitDTO.OutputDTO}
	 * non {@code null}
	 * et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_OK} + libellé
	 * uniquement après préparation complète
	 * de la réponse utilisateur.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES METIER, UTILISATEUR et TRAÇABILITE :</p>
	 * <ul>
	 * <li>La ré-identification de l'objet à modifier
	 * s'appuie sur le couple [parent, libellé]
	 * et jamais sur le seul libellé Produit ;</li>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue observable réelle de l'opération ;</li>
	 * <li>Le message de succès n'est positionné
	 * qu'après préparation complète
	 * de la réponse utilisateur finale ;</li>
	 * <li>L'identifiant persistant retrouvé
	 * est réinjecté dans l'objet envoyé au GATEWAY ;</li>
	 * <li>L'objet retourné, s'il n'est pas {@code null},
	 * correspond à un {@link Produit} effectivement modifié
	 * dans le stockage et exprimé sous forme de DTO.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO :
	 * DTO de modification.
	 * @return ProduitDTO.OutputDTO :
	 * DTO modifié ou {@code null}.
	 * @throws Exception
	 * toute exception levée par l'implémentation,
	 * notamment lors des recherches techniques,
	 * de la délégation de modification
	 * ou de la préparation de la réponse utilisateur.
	 */
	ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;
	

	
	/**
	 * <div>
	 * <p>Supprime un {@link Produit}
	 * à partir d'un {@link ProduitDTO.InputDTO}.</p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir un DTO de suppression
	 * transmis par la couche appelante ;</li>
	 * <li>valider les préconditions observables
	 * sur le DTO, sur le libellé Produit
	 * et sur le parent ;</li>
	 * <li>retrouver le {@link SousTypeProduit}
	 * parent persistant correspondant au DTO ;</li>
	 * <li>demander au composant GATEWAY
	 * tous les {@link Produit}
	 * rattachés à ce parent ;</li>
	 * <li>ré-identifier, dans cette collection,
	 * le {@link Produit} persistant exact
	 * correspondant au couple [parent, libellé] ;</li>
	 * <li>déléguer la destruction technique
	 * au composant GATEWAY ;</li>
	 * <li>positionner un message observable cohérent
	 * avec l'issue réelle de l'opération.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL}
	 * et lève une {@code ExceptionParametreNull}.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est blank,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK}
	 * et lève une {@code ExceptionParametreBlank}.</li>
	 * <li>Si le parent porté par le DTO
	 * est blank, absent ou non persistant,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}
	 * et lève une {@code IllegalStateException}.</li>
	 * <li>Si la recherche des enfants du parent
	 * retourne {@code null},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_STOCKAGE_NULL}
	 * et lève une {@code ExceptionStockageVide}.</li>
	 * <li>Si aucun {@link Produit} persistant
	 * ne correspond au couple [parent, libellé],
	 * ne supprime rien et positionne
	 * {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet retrouvé
	 * n'est pas persistant,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé
	 * et lève une {@code ExceptionNonPersistant}.</li>
	 * <li>Si une recherche technique échoue,
	 * positionne un message utilisateur technique cohérent
	 * construit à partir de {@link #KO_TECHNIQUE_RECHERCHE},
	 * de {@link #TIRET_ESPACE}
	 * et d'un détail technique sécurisé,
	 * puis propage l'exception circonstanciée.</li>
	 * <li>Si la suppression technique échoue,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_KO}
	 * + libellé
	 * + {@link #TIRET_ESPACE}
	 * + détail technique sécurisé,
	 * puis propage l'exception circonstanciée.</li>
	 * <li>En cas de succès,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_OK} + libellé
	 * uniquement après destruction effective
	 * de l'objet persistant.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES METIER, UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>La ré-identification de l'objet à détruire
	 * s'appuie sur le couple [parent, libellé]
	 * et jamais sur le seul libellé Produit.</li>
	 * <li>Aucune suppression ne doit viser
	 * un autre parent portant le même libellé Produit.</li>
	 * <li>Le message retourné par {@link #getMessage()}
	 * reflète l'issue observable réelle
	 * de l'opération.</li>
	 * <li>Le message de succès
	 * n'est positionné qu'après destruction effective
	 * de l'objet persistant.</li>
	 * <li>Aucun résultat partiel incohérent
	 * ne doit être exposé à l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO :
	 * DTO de suppression portant le parent
	 * et le libellé enfant
	 * de l'objet persistant à ré-identifier puis détruire.
	 * @throws ExceptionParametreNull
	 * si {@code pInputDTO == null}.
	 * @throws ExceptionParametreBlank
	 * si {@code pInputDTO.getProduit()} est blank.
	 * @throws ExceptionStockageVide
	 * si la recherche des enfants du parent
	 * retourne {@code null}.
	 * @throws ExceptionNonPersistant
	 * si l'objet retrouvé pour le couple [parent, libellé]
	 * n'est pas persistant.
	 * @throws IllegalStateException
	 * si le parent est blank, absent ou non persistant.
	 * @throws Exception
	 * toute autre exception levée par l'implémentation,
	 * notamment lors des recherches techniques
	 * ou de la délégation de destruction.
	 */
	void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p>Compte le nombre de {@link Produit}
	 * accessibles dans le stockage.</p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au composant GATEWAY
	 * le nombre total de {@link Produit}
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
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Délègue le comptage
	 * au composant GATEWAY.</li>
	 * <li>Retourne un {@code long}
	 * représentant le nombre total
	 * d'objets présents dans le stockage.</li>
	 * <li>Si le comptage retourné vaut {@code 0},
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Si le comptage retourné
	 * est strictement positif,
	 * positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * <li>Si le composant GATEWAY retourne
	 * une valeur négative,
	 * positionne un message utilisateur
	 * technique cohérent et lève une exception,
	 * car un tel résultat est incohérent
	 * pour un comptage observable.</li>
	 * <li>En cas d'échec technique
	 * lors du comptage via le GATEWAY,
	 * positionne un message utilisateur
	 * technique cohérent
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
	 * <li>Le message de succès
	 * ou d'absence de résultat
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
	 * <p>Retourne le dernier message observable
	 * actuellement porté par le SERVICE UC.</p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE SERVICE UC :
	 * </p>
	 * <ul>
	 * <li>exposer à la couche appelante
	 * le dernier message observable
	 * produit par une opération précédente
	 * du service ;</li>
	 * <li>permettre à la couche appelante
	 * de lire un message de succès,
	 * d'absence de résultat,
	 * d'erreur fonctionnelle
	 * ou d'erreur technique déjà mémorisé ;</li>
	 * <li>fournir une lecture pure,
	 * sans recalcul, sans délégation
	 * et sans effet de bord.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE SERVICE UC :</p>
	 * <ul>
	 * <li>Retourne le dernier message observable
	 * mémorisé par le service.</li>
	 * <li>Peut retourner {@code null}
	 * tant qu'aucune opération précédente
	 * n'a encore produit de message observable.</li>
	 * <li>Ne délègue à aucun composant GATEWAY.</li>
	 * <li>Ne déclenche aucun traitement métier,
	 * aucun recalcul,
	 * aucune lecture du stockage
	 * et aucune écriture.</li>
	 * <li>Ne modifie pas l'état du service.</li>
	 * <li>Ne lève aucune exception.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES UTILISATEUR et TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le message retourné reflète
	 * le dernier état observable
	 * réellement produit par le service.</li>
	 * <li>Le dernier message mémorisé
	 * remplace le précédent :
	 * le dernier message gagne.</li>
	 * <li>Une simple lecture via {@code getMessage()}
	 * ne doit jamais altérer
	 * le message courant.</li>
	 * <li>La méthode peut être appelée
	 * à tout moment,
	 * y compris avant toute opération métier.</li>
	 * </ul>
	 * </div>
	 *
	 * @return String :
	 * dernier message observable mémorisé
	 * par le service ;
	 * peut valoir {@code null}
	 * avant toute production de message.
	 */
	String getMessage();
	
	

}
