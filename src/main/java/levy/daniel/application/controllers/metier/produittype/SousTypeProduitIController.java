/* ********************************************************************* */
/* ************************ PORT CONTROLLER ***************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype;

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE SousTypeProduitIController.java :</p>
 *
 * <p>Cette INTERFACE modélise :
 * <span style="font-weight:bold;">le PORT CONTROLLER</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link SousTypeProduit}</code>.</p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette INTERFACE (PORT) est appelée par la couche VUES.</li>
 * <li>Cette INTERFACE (PORT) communique exclusivement avec
 * la couche SERVICE METIER USE CASE via
 * <code style="font-weight:bold;">{@link SousTypeProduitICuService}</code>.
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
 * via <code>rechercherTous()</code>.</li>
 * <li>la recherche exhaustive des libellés dans le stockage
 * via <code>rechercherTousString()</code>.</li>
 * <li>la recherche paginée des objets métier dans le stockage
 * via <code>rechercherTousParPage(...)</code>.</li>
 * <li>la recherche exacte d'objets métier par libellé
 * via <code>findByLibelle(...)</code>.</li>
 * <li>la recherche rapide d'objets métier par contenu de libellé
 * via <code>findByLibelleRapide(...)</code>.</li>
 * <li>la recherche des objets métier rattachés à un
 * {@link TypeProduit} parent
 * via <code>findAllByParent(...)</code>.</li>
 * <li>la recherche d'un objet métier à partir d'un DTO d'entrée
 * via <code>findByDTO(...)</code>.</li>
 * <li>la recherche d'un objet métier par identifiant
 * via <code>findById(...)</code>.</li>
 * <li>la modification d'un objet métier dans le stockage
 * via <code>update(...)</code>.</li>
 * <li>la suppression d'un objet métier dans le stockage
 * via <code>delete(...)</code>.</li>
 * <li>le comptage des objets métier présents dans le stockage
 * via <code>count()</code>.</li>
 * <li>la récupération du message utilisateur courant
 * via <code>getMessage()</code>.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 7 avril 2026
 */
public interface SousTypeProduitIController {

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

	/**
	 * <div>
	 * <p>"KO - la Vue a transmis un InputDTO
	 * avec un TypeProduit parent blank (null ou espaces)"</p>
	 * </div>
	 */
	String MESSAGE_CREER_VUE_PARENT_BLANK
		= "KO - la Vue a transmis un InputDTO "
				+ "avec un TypeProduit parent blank (null ou espaces)";

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Reçoit depuis la couche VUES
	 * un {@link SousTypeProduitDTO.InputDTO}
	 * et crée un objet métier dans le stockage.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link SousTypeProduitDTO.InputDTO} ;</li>
	 * <li>exécuter les premiers contrôles de surface sur l'InputDTO,
	 * sur le libellé enfant
	 * et sur le libellé du parent ;</li>
	 * <li>rédiger le cas échéant
	 * un message utilisateur circonstancié
	 * sans solliciter le SERVICE UC ;</li>
	 * <li>si les contrôles de surface sont satisfaits,
	 * déléguer la création au
	 * {@link SousTypeProduitICuService#creer(SousTypeProduitDTO.InputDTO)} ;</li>
	 * <li>retourner à la VUE
	 * le {@link SousTypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC
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
	 * <li>Si {@code pInputDTO == null},
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_CREER_VUE_NULL}
	 * et retourne {@code null}.</li>
	 * <li>Si le libellé enfant porté par {@code pInputDTO}
	 * est blank ({@code null} ou espaces),
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_CREER_VUE_BLANK}
	 * et retourne {@code null}.</li>
	 * <li>Si le libellé du {@link TypeProduit} parent
	 * porté par {@code pInputDTO}
	 * est blank ({@code null} ou espaces),
	 * la méthode ne sollicite pas le SERVICE UC,
	 * positionne le message utilisateur
	 * {@link #MESSAGE_CREER_VUE_PARENT_BLANK}
	 * et retourne {@code null}.</li>
	 * <li>Si les contrôles de surface sont satisfaits,
	 * la méthode délègue la création au SERVICE UC,
	 * récupère le message utilisateur courant produit
	 * par ce service
	 * et retourne le {@link SousTypeProduitDTO.OutputDTO}
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
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO :
	 * le SousTypeProduit transmis par la couche VUES
	 * au scénario de création,
	 * avec son libellé enfant
	 * et le libellé du {@link TypeProduit} parent attendu.
	 * @return SousTypeProduitDTO.OutputDTO :
	 * le SousTypeProduit créé retourné à la couche VUES ;
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
	 * si le SERVICE UC lève une incohérence
	 * sur le parent
	 * ou sur son scénario de création.
	 * @throws Exception
	 * toute autre exception propagée par le SERVICE UC
	 * après délégation effective.
	 */
	SousTypeProduitDTO.OutputDTO creer(SousTypeProduitDTO.InputDTO pInputDTO)
			throws Exception;
	

	
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

	
	
} // FIN DE L'INTERFACE SousTypeProduitIController.------------------------