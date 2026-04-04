/* ********************************************************************* */
/* ************************ PORT CONTROLLER ***************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype;

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
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
public interface TypeProduitIController {

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Reçoit depuis la couche VUES
	 * un {@link TypeProduitDTO.InputDTO}
	 * et crée un objet métier dans le stockage
	 * en déléguant exclusivement au SERVICE UC.
	 * </p>
	 * <p style="font-weight:bold;">
	 * INTENTION DE CONTROLLER (scénario nominal) :
	 * </p>
	 * <ul>
	 * <li>recevoir depuis la VUE
	 * un {@link TypeProduitDTO.InputDTO} ;</li>
	 * <li>déléguer la création au
	 * {@link TypeProduitICuService#creer(TypeProduitDTO.InputDTO)} ;</li>
	 * <li>retourner tel quel à la VUE
	 * le {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT DE CONTROLLER :</p>
	 * <ul>
	 * <li>La méthode ne porte aucune logique métier locale.</li>
	 * <li>La méthode ne construit aucun objet métier
	 * et ne parle jamais au stockage.</li>
	 * <li>Si {@code pInputDTO == null},
	 * la méthode délègue quand même au SERVICE UC,
	 * qui reste seul responsable du message utilisateur
	 * et du résultat retourné.</li>
	 * <li>Si le libellé est blank,
	 * la méthode propage l'exception applicative
	 * levée par le SERVICE UC et fournit un message utilisateur.</li>
	 * <li>Si le DTO correspond à un doublon fonctionnel,
	 * la méthode propage l'exception métier
	 * levée par le SERVICE UC et fournit un message utilisateur.</li>
	 * <li>En cas de succès,
	 * la méthode retourne le
	 * {@link TypeProduitDTO.OutputDTO}
	 * fourni par le SERVICE UC + message utilisateur de succès.</li>
	 * <li>En cas d'erreur technique ou applicative,
	 * la méthode propage l'exception levée
	 * par le SERVICE UC sans remappage local.</li>
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
	 * <li>Le message utilisateur reste celui produit
	 * par le SERVICE UC via
	 * {@link TypeProduitICuService#getMessage()}.</li>
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
	 * peut être {@code null} si le SERVICE UC
	 * retourne {@code null}.
	 * @throws ExceptionParametreBlank
	 * si le SERVICE UC détecte un libellé blank.
	 * @throws ExceptionDoublon
	 * si le SERVICE UC détecte un doublon fonctionnel.
	 * @throws IllegalStateException
	 * si le SERVICE UC lève une incohérence technique
	 * sur son scénario de création.
	 * @throws Exception
	 * toute autre exception propagée par le SERVICE UC.
	 */
	TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO)
			throws Exception;

}