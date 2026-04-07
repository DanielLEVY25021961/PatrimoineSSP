/* ********************************************************************* */
/* ************************ PORT CONTROLLER ***************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype;

import java.util.List;

import levy.daniel.application.model.dto.pagination.RequetePageDTO;
import levy.daniel.application.model.dto.pagination.ResultatPageDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE TypeProduitIController.java :</p>
 *
 * <p>Cette INTERFACE modélise :
 * <span style="font-weight:bold;">le PORT CONTROLLER</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link TypeProduit}</code>.</p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette INTERFACE (PORT) est appelée par la couche VUES.</li>
 * <li>Cette INTERFACE (PORT) communique exclusivement avec
 * la couche SERVICE METIER USE CASE via
 * <code style="font-weight:bold;">{@link TypeProduitICuService}</code>.
 * </li>
 * <li>Cette INTERFACE (PORT) ne connait ni le GATEWAY,
 * ni le DAO, ni la persistance.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p>Cette interface définit : </p>
 * <ul>
 * <li>la création d'un objet métier dans le stockage
 * via {@link #creer(InputDTO)}.</li>
 * <li>la recherche exhaustive des objets métier dans le stockage
 * via {@link #rechercherTous()}.</li>
 * <li>la recherche exhaustive des libellés dans le stockage
 * via {@link #rechercherTousString()}.</li>
 * <li>la recherche paginée des objets métier dans le stockage
 * via {@link #rechercherTousParPage(RequetePageDTO)}.</li>
 * <li>la recherche exacte d'un objet métier par libellé
 * via {@link #findByLibelle(String)}.</li>
 * <li>la recherche rapide d'objets métier par contenu de libellé
 * via {@link #findByLibelleRapide(String)}.</li>
 * <li>la recherche d'un objet métier à partir d'un DTO d'entrée
 * via {@link #findByDTO(InputDTO)}.</li>
 * <li>la recherche d'un objet métier par identifiant
 * via {@link #findById(Long)}.</li>
 * <li>la modification d'un objet métier dans le stockage
 * via {@link #update(InputDTO)}.</li>
 * <li>la suppression d'un objet métier dans le stockage
 * via {@link #delete(InputDTO)}.</li>
 * <li>le comptage des objets métier présents dans le stockage
 * via {@link #count()}.</li>
 * <li>la récupération du message utilisateur courant
 * via {@link #getMessage()}.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
public interface TypeProduitIController {

	// ----------------------- creer ------------------------------------//
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO null"</p>
	 * </div>
	 */
	String MESSAGE_CREER_VUE_NULL
		= "KO - la Vue a transmis un InputDTO null";

	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO
	 * avec un libellé blank (null ou espaces)"</p>
	 * </div>
	 */
	String MESSAGE_CREER_VUE_BLANK
		= "KO - la Vue a transmis un InputDTO "
				+ "avec un libellé blank (null ou espaces)";

	// ----------------------- rechercherTousParPage --------------------//

	/**
	 * <div>
	 * <p>"La requête paginée transmise à la VUE ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL
	        = "La requête paginée transmise à la VUE ne doit pas être null.";

	/**
	 * <div>
	 * <p>Numéro de page "humain" par défaut utilisé lorsque la VUE 
	 * n'a rien renseigné ou a fourni une valeur incohérente 
	 * (1 signifie "page 1" qui aura pour index 0-based la valeur 0).</p>
	 * </div>
	 */
	int NUMERO_PAGE_HUMAIN_PAR_DEFAUT = 1;

	/**
	 * <div>
	 * <p>Nombre d'enregistrements par page par défaut utilisé 
	 * lorsque la VUE n'a rien renseigné ou a fourni 
	 * une valeur incohérente.</p>
	 * </div>
	 */
	int NOMBRE_ENREGISTREMENTS_PAR_PAGE_PAR_DEFAUT = 10;
	
	// ----------------------- findByLibelle -----------------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un libellé null"</p>
	 * </div>
	 */
	String MESSAGE_FIND_BY_LIBELLE_VUE_NULL
			= "KO - la Vue a transmis un libellé null";
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un libellé blank (null ou espaces)"</p>
	 * </div>
	 */
	String MESSAGE_FIND_BY_LIBELLE_VUE_BLANK
			= "KO - la Vue a transmis un libellé blank (null ou espaces)";
	
	// ----------------------- findByLibelleRapide -----------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un contenu de recherche rapide null"</p>
	 * </div>
	 */
	String MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL
			= "KO - la Vue a transmis un contenu de recherche rapide null";
	
	// ----------------------- findByDTO --------------------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO de recherche null"</p>
	 * </div>
	 */
	String MESSAGE_FIND_BY_DTO_VUE_NULL
			= "KO - la Vue a transmis un InputDTO de recherche null";	

	// ----------------------- findById ---------------------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un identifiant persistant null"</p>
	 * </div>
	 */
	String MESSAGE_FIND_BY_ID_VUE_NULL
			= "KO - la Vue a transmis un identifiant persistant null";
	
	// ----------------------- update -----------------------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO de modification null"</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_VUE_NULL
			= "KO - la Vue a transmis un InputDTO de modification null";

	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un libellé 
	 * de modification blank (null ou espaces)"</p>
	 * </div>
	 */
	String MESSAGE_UPDATE_VUE_BLANK
			= "KO - la Vue a transmis un libellé "
					+ "de modification blank (null ou espaces)";	

	// ----------------------- delete -----------------------------------//
	
	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO de suppression null"</p>
	 * </div>
	 */
	String MESSAGE_DELETE_VUE_NULL
			= "KO - la Vue a transmis un InputDTO de suppression null";

	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un libellé 
	 * de suppression blank (null ou espaces)"</p>
	 * </div>
	 */
	String MESSAGE_DELETE_VUE_BLANK
			= "KO - la Vue a transmis un libellé "
					+ "de suppression blank (null ou espaces)";

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Reçoit depuis la couche VUES
	 * un {@link TypeProduitDTO.InputDTO}
	 * et crée un objet métier dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link TypeProduitDTO.InputDTO} ;</li>
	 * <li>exécuter les premiers contrôles de surface sur l'InputDTO
	 * et rédiger le cas échéant un message utilisateur circonstancié ;</li>
	 * <li>si les contrôles de surface sont satisfaits,
	 * déléguer la création au
	 * {@link TypeProduitICuService#creer(TypeProduitDTO.InputDTO)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
	 * ou {@code null} si le CONTROLLER bloque la saisie
	 * avant délégation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pInputDTO == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_CREER_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Si le libellé porté par {@code pInputDTO}
	 * est blank ({@code null} ou espaces),
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_CREER_VUE_BLANK}
	 * et retourne {@code null}.</li>
	 * <li>Si les contrôles de surface sont satisfaits,
	 * la méthode délègue la création au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * et retourne le {@link TypeProduitDTO.OutputDTO}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC après délégation,
	 * la méthode propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors des contrôles de surface d'entrée.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * le TypeProduit transmis par la couche VUES
	 * au scénario de création.
	 * @return TypeProduitDTO.OutputDTO :
	 * le TypeProduit créé retourné à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC retourne {@code null}.
	 * @throws ExceptionParametreBlank
	 * si le SERVICE UC détecte une incohérence applicative
	 * lors de la délégation effective.
	 * @throws ExceptionDoublon
	 * si le SERVICE UC détecte un doublon fonctionnel
	 * lors de la délégation effective.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de création.
	 * @throws Exception
	 * toute autre exception propagée par le SERVICE UC
	 * après délégation effective.
	 */
	TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * tous les objets métier présents dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au SERVICE UC
	 * la liste exhaustive des {@link TypeProduit}
	 * présents dans le stockage ;</li>
	 * <li>récupérer le message utilisateur courant
	 * produit par le SERVICE UC ;</li>
	 * <li>retourner à la VUE
	 * la liste des {@link TypeProduitDTO.OutputDTO}
	 * fournie par le SERVICE UC.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>La méthode délègue la recherche exhaustive
	 * au SERVICE UC.</li>
	 * <li>En cas de succès, la méthode récupère
	 * le message utilisateur courant du SERVICE UC
	 * puis retourne la liste de
	 * {@link TypeProduitDTO.OutputDTO}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le message utilisateur porté
	 * par le CONTROLLER après l'appel
	 * est celui du SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List<TypeProduitDTO.OutputDTO> :
	 * la liste exhaustive des objets métier
	 * présents dans le stockage,
	 * retournée par le SERVICE UC ;
	 * jamais {@code null}, éventuellement vide,
	 * sauf comportement exceptionnel de l'implémentation appelée.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche exhaustive.
	 */
	List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * tous les libellés des objets métier présents dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au SERVICE UC
	 * la liste exhaustive des libellés des {@link TypeProduit}
	 * présents dans le stockage ;</li>
	 * <li>récupérer le message utilisateur courant
	 * produit par le SERVICE UC ;</li>
	 * <li>retourner à la VUE
	 * la liste des {@link String}
	 * fournie par le SERVICE UC.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>La méthode délègue la recherche exhaustive
	 * des libellés au SERVICE UC.</li>
	 * <li>En cas de succès, la méthode récupère
	 * le message utilisateur courant du SERVICE UC
	 * puis retourne la liste de {@link String}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le message utilisateur porté
	 * par le CONTROLLER après l'appel
	 * est celui du SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List<String> :
	 * la liste exhaustive des libellés des objets métier
	 * présents dans le stockage,
	 * retournée par le SERVICE UC ;
	 * jamais {@code null}, éventuellement vide,
	 * sauf comportement exceptionnel de l'implémentation appelée.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche exhaustive.
	 */
	List<String> rechercherTousString() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * tous les objets métier présents dans le stockage
	 * sous forme paginée.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * une requête paginée DTO ;</li>
	 * <li>applique les contrôles de surface propres à la 
	 * pagination de VUE avant d'appeler le SERVICE UC.</li>
	 * <li>convertir le numéro de page "humain" 
	 * en numéro de page 0-based.</li>
	 * <li>laisser l'ADAPTER convertir cette requête DTO
	 * en pagination interne (du package services.pagination) 
	 * avant l'appel au SERVICE UC ;</li>
	 * <li>retourner à la VUE
	 * un résultat paginé DTO.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>Si la requête paginée est null : le CONTROLLER mémorise 
	 * un message utilisateur local, n'appelle pas le SERVICE UC 
	 * et retourne null.</li>
	 * <li>Si le numéro de page humain est absent ou inférieur à 1 : 
	 * le CONTROLLER applique le numéro de page humain par défaut 
	 * {@code NUMERO_PAGE_HUMAIN_PAR_DEFAUT}.</li>
	 * <li>Si la taille de page (nombre d'enregistrements par page) 
	 * est absent ou inférieur à 1 : 
	 * le CONTROLLER applique la taille de page par défaut 
	 * {@code NOMBRE_ENREGISTREMENTS_PAR_PAGE_PAR_DEFAUT}.</li>
	 * <li>La méthode convertit le numéro de page humain 
	 * en numéro de page 0-based.</li>
	 * <li>La méthode délègue la recherche paginée
	 * au SERVICE UC après conversion de la pagination DTO
	 * en pagination interne (du package services.pagination).</li>
	 * <li>En cas de succès, la méthode récupère
	 * le message utilisateur courant du SERVICE UC
	 * puis retourne le résultat paginé DTO.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePageDTO :
	 * la requête de pagination DTO transmise par la VUE.
	 * @return ResultatPageDTO<TypeProduitDTO.OutputDTO> :
	 * le résultat paginé DTO retourné à la VUE.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche paginée.
	 */
	ResultatPageDTO<TypeProduitDTO.OutputDTO> rechercherTousParPage(
					RequetePageDTO pRequetePageDTO)
							throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * correspondant à un libellé exact transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un libellé exact de {@link TypeProduit} ;</li>
	 * <li>exécuter les premiers contrôles de surface
	 * sur ce libellé
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si les contrôles de surface sont satisfaits,
	 * déléguer la recherche exacte au
	 * {@link TypeProduitICuService#findByLibelle(String)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
	 * ou {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pLibelle == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_FIND_BY_LIBELLE_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Si {@code pLibelle} est blank ({@code null} ou espaces),
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}
	 * et retourne {@code null}.</li>
	 * <li>Si les contrôles de surface sont satisfaits,
	 * la méthode délègue la recherche exacte au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * puis retourne le {@link TypeProduitDTO.OutputDTO}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors des contrôles de surface d'entrée.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String :
	 * le libellé exact transmis par la couche VUES
	 * au scénario de recherche.
	 * @return TypeProduitDTO.OutputDTO :
	 * le TypeProduit trouvé retourné à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de recherche exacte.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche exacte.
	 */
	TypeProduitDTO.OutputDTO findByLibelle(String pLibelle)
			throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * la liste des {@link TypeProduitDTO.OutputDTO}
	 * correspondant à un contenu de libellé transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un contenu de recherche rapide sur le libellé
	 * d'un {@link TypeProduit} ;</li>
	 * <li>exécuter le contrôle de surface
	 * sur ce contenu lorsqu'il est {@code null}
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si le contrôle de surface est satisfait,
	 * déléguer la recherche rapide au
	 * {@link TypeProduitICuService#findByLibelleRapide(String)} ;</li>
	 * <li>retourner à la VUE
	 * la liste des {@link TypeProduitDTO.OutputDTO}
	 * fournie par le SERVICE UC
	 * ou {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pContenu == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Si {@code pContenu} est blank ({@code null} ou espaces),
	 * la méthode ne bloque pas localement la saisie
	 * et délègue au SERVICE UC,
	 * qui applique alors son propre contrat 
	 * de recherche rapide (retourne tous les enregistrements).</li>
	 * <li>Si le contrôle de surface est satisfait,
	 * la méthode délègue la recherche rapide au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * puis retourne la liste de
	 * {@link TypeProduitDTO.OutputDTO}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors du contrôle de surface d'entrée
	 * sur un contenu {@code null}.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String :
	 * le contenu partiel de libellé transmis par la couche VUES
	 * au scénario de recherche rapide.
	 * @return List<TypeProduitDTO.OutputDTO> :
	 * la liste des TypeProduit trouvés retournée à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation ;
	 * sinon, la liste retournée par le SERVICE UC,
	 * éventuellement vide.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de recherche rapide.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche rapide.
	 */
	List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu)
			throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * correspondant à un {@link TypeProduitDTO.InputDTO}
	 * transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link TypeProduitDTO.InputDTO}
	 * destiné à une recherche ;</li>
	 * <li>exécuter le premier contrôle de surface
	 * sur cet InputDTO lorsqu'il est {@code null}
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si ce contrôle de surface est satisfait,
	 * déléguer la recherche au
	 * {@link TypeProduitICuService#findByDTO(TypeProduitDTO.InputDTO)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
	 * ou {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pInputDTO == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_FIND_BY_DTO_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Sinon, la méthode délègue la recherche
	 * au SERVICE UC
	 * via
	 * {@link TypeProduitICuService#findByDTO(TypeProduitDTO.InputDTO)}.</li>
	 * <li>Le comportement observable sur le contenu métier
	 * porté par l'InputDTO
	 * (libellé null, blank, introuvable, succès, erreur technique)
	 * est alors celui du SERVICE UC délégué.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors du contrôle de surface d'entrée
	 * sur un InputDTO {@code null}.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * l'InputDTO transmis par la couche VUES
	 * au scénario de recherche.
	 * @return TypeProduitDTO.OutputDTO :
	 * le TypeProduit trouvé retourné à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de recherche.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche.
	 */
	TypeProduitDTO.OutputDTO findByDTO(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * correspondant à un identifiant persistant transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un identifiant persistant de {@link TypeProduit} ;</li>
	 * <li>exécuter le premier contrôle de surface
	 * sur cet identifiant lorsqu'il est {@code null}
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si ce contrôle de surface est satisfait,
	 * déléguer la recherche au
	 * {@link TypeProduitICuService#findById(Long)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
	 * ou {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pId == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_FIND_BY_ID_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Sinon, la méthode délègue la recherche
	 * au SERVICE UC
	 * via
	 * {@link TypeProduitICuService#findById(Long)}.</li>
	 * <li>Le comportement porté par l'identifiant transmis
	 * (introuvable, succès, erreur technique)
	 * est alors celui du SERVICE UC délégué.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors du contrôle de surface d'entrée
	 * sur un identifiant {@code null}.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long :
	 * l'identifiant persistant transmis par la couche VUES
	 * au scénario de recherche.
	 * @return TypeProduitDTO.OutputDTO :
	 * le TypeProduit trouvé retourné à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de recherche par identifiant.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la recherche par identifiant.
	 */
	TypeProduitDTO.OutputDTO findById(Long pId)
			throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * correspondant à un {@link TypeProduitDTO.InputDTO}
	 * de modification transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link TypeProduitDTO.InputDTO}
	 * destiné à une modification ;</li>
	 * <li>exécuter les premiers contrôles de surface
	 * sur cet InputDTO
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si les contrôles de surface sont satisfaits,
	 * déléguer la modification au
	 * {@link TypeProduitICuService#update(TypeProduitDTO.InputDTO)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
	 * ou {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet
	 * ou retourne {@code null}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pInputDTO == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_UPDATE_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank,
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_UPDATE_VUE_BLANK}
	 * et retourne {@code null}.</li>
	 * <li>Si les contrôles de surface sont satisfaits,
	 * la méthode délègue la modification au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * puis retourne le {@link TypeProduitDTO.OutputDTO}
	 * qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors des contrôles de surface d'entrée.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * l'InputDTO transmis par la couche VUES
	 * au scénario de modification.
	 * @return TypeProduitDTO.OutputDTO :
	 * le TypeProduit modifié retourné à la couche VUES ;
	 * peut être {@code null}
	 * si le CONTROLLER bloque la saisie
	 * avant délégation
	 * ou si le SERVICE UC ne trouve aucun objet
	 * ou retourne {@code null}.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de modification.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la modification.
	 */
	TypeProduitDTO.OutputDTO update(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détruit depuis la VUE
	 * un {@link TypeProduit}
	 * à partir d'un {@link TypeProduitDTO.InputDTO}
	 * transmis.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link TypeProduitDTO.InputDTO}
	 * destiné à une suppression ;</li>
	 * <li>exécuter les premiers contrôles de surface
	 * sur cet InputDTO
	 * et rédiger le cas échéant un message utilisateur
	 * circonstancié ;</li>
	 * <li>si les contrôles de surface sont satisfaits,
	 * déléguer la suppression au
	 * {@link TypeProduitICuService#delete(TypeProduitDTO.InputDTO)} ;</li>
	 * <li>ne retourner aucune valeur à la VUE
	 * et laisser le message utilisateur courant
	 * refléter l'issue de la suppression.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>Si {@code pInputDTO == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_DELETE_VUE_NULL}
	 * et retourne immédiatement.</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est blank,
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_DELETE_VUE_BLANK}
	 * et retourne immédiatement.</li>
	 * <li>Si les contrôles de surface sont satisfaits,
	 * la méthode délègue la suppression au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * puis termine sans retourner de valeur.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le CONTROLLER peut produire un message utilisateur
	 * propre lors des contrôles de surface d'entrée.</li>
	 * <li>Après délégation, le message utilisateur porté
	 * par le CONTROLLER devient celui produit
	 * par le SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO :
	 * l'InputDTO transmis par la couche VUES
	 * au scénario de suppression.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de suppression.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors de la suppression.
	 */
	void delete(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE
	 * le nombre total de {@link TypeProduit}
	 * présents dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>demander au SERVICE UC
	 * le nombre total de {@link TypeProduit}
	 * présents dans le stockage ;</li>
	 * <li>récupérer le message utilisateur courant
	 * produit par le SERVICE UC ;</li>
	 * <li>retourner à la VUE
	 * le comptage fourni par le SERVICE UC.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais directement au stockage.</li>
	 * <li>La méthode délègue le comptage
	 * au SERVICE UC
	 * via {@link TypeProduitICuService#count()}.</li>
	 * <li>En cas de succès, la méthode récupère
	 * le message utilisateur courant du SERVICE UC
	 * puis retourne le comptage qu'il fournit.</li>
	 * <li>En cas d'erreur applicative, métier ou technique
	 * levée par le SERVICE UC,
	 * la méthode récupère le message utilisateur courant
	 * du SERVICE UC
	 * puis propage l'exception sans remappage local.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le message utilisateur porté
	 * par le CONTROLLER après l'appel
	 * est celui du SERVICE UC.</li>
	 * <li>Les éventuelles exceptions traversent le CONTROLLER
	 * et remontent à la VUE.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @return long :
	 * le nombre total d'objets métier
	 * présents dans le stockage,
	 * retourné par le SERVICE UC ;
	 * peut valoir {@code 0},
	 * mais ne doit jamais être négatif.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de comptage.
	 * @throws Exception
	 * toute exception propagée par le SERVICE UC
	 * lors du comptage.
	 */
	long count() throws Exception;	
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne à la VUE le message utilisateur courant
	 * porté par le présent CONTROLLER.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>retourner à la VUE le dernier message utilisateur
	 * actuellement mémorisé par le CONTROLLER ;</li>
	 * <li>fournir ainsi soit un message produit
	 * par les contrôles de surface du CONTROLLER,
	 * soit un message récupéré auprès du SERVICE UC
	 * après délégation.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais au stockage.</li>
	 * <li>La méthode retourne le message utilisateur courant
	 * mémorisé dans le CONTROLLER.</li>
	 * <li>Ce message peut provenir soit d'un contrôle
	 * de surface exécuté par le CONTROLLER,
	 * soit du SERVICE UC après une délégation effective.</li>
	 * <li>La méthode peut retourner {@code null}
	 * si aucun message utilisateur n'a encore été mémorisé
	 * dans le CONTROLLER.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">
	 * GARANTIES ARCHITECTURALES ET DE TRAÇABILITE :
	 * </p>
	 * <ul>
	 * <li>Le CONTROLLER reste sur sa frontière :
	 * VUES <span style="font-weight:bold;">→</span>
	 * SERVICE UC.</li>
	 * <li>Le message utilisateur retourné
	 * est celui actuellement porté
	 * par le présent CONTROLLER.</li>
	 * <li>La méthode ne connaît ni GATEWAY,
	 * ni DAO, ni entité JPA.</li>
	 * </ul>
	 * </div>
	 *
	 * @return String :
	 * le message utilisateur courant mémorisé
	 * par le présent CONTROLLER ;
	 * peut être {@code null}.
	 */
	String getMessage();

	
	
}