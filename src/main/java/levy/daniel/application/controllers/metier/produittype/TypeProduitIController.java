/* ********************************************************************* */
/* ************************ PORT CONTROLLER ***************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype;

import java.util.List;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
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
 * <p>Dans l'état courant du chantier, elle expose :</p>
 * <ul>
 * <li>la création d'un objet métier dans le stockage via
 * {@link #creer(TypeProduitDTO.InputDTO)}</li>
 * <li>la recherche exhaustive des objets métier dans le stockage via
 * {@link #rechercherTous()}</li>
 * <li>la récupération du message utilisateur courant via
 * {@link #getMessage()}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
public interface TypeProduitIController {

	/**
	 * "KO - la Vue a transmis un InputDTO null"
	 */
	String MESSAGE_CREER_VUE_NULL
		= "KO - la Vue a transmis un InputDTO null";

	/**
	 * "KO - la Vue a transmis un InputDTO
	 * avec un libellé blank (null ou espaces)"
	 */
	String MESSAGE_CREER_VUE_BLANK
		= "KO - la Vue a transmis un InputDTO "
				+ "avec un libellé blank (null ou espaces)";

	
	
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