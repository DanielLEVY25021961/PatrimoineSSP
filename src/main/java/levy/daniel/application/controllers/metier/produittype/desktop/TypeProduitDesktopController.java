/* ********************************************************************* */
/* ********************** ADAPTER CONTROLLER *************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.desktop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitDesktopController.java :
 * </p>
 *
 * <p>Cette classe modélise :
 * <span style="font-weight:bold;">l' ADAPTER CONTROLLER DESKTOP</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link TypeProduit}</code>.</p>
 * </div>
 * 
 * <div>
 * <ul>
 * <li>Cette classe est appelée par les VUES Desktop.</li>
 * <li>Cette classe ne communique qu'avec le
 * <code style="font-weight:bold;">{@link TypeProduitICuService}</code>
 * injecté par SPRING via le constructeur.</li>
 * <li>Cette classe ne connait ni GATEWAY,
 * ni DAO, ni persistance.</li>
 * </ul>
 * </div>
 * 
 * <div>
 * <p>Dans l'état courant du chantier, elle implémente :</p>
 * <ul>
 * <li>la création d'un objet métier dans le stockage 
 * via {@link #creer(InputDTO)}.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
@Controller("TypeProduitDesktopController")
@Profile("desktop")
public class TypeProduitDesktopController implements TypeProduitIController {

	/* ************************* ATTRIBUTS ***************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">SERVICE USE CASE injecté par SPRING.</p>
	 * <p>Visibilité interface uniquement.</p>
	 * </div>
	 */
	private final TypeProduitICuService service;

	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger :</p>
	 * <p>Logger pour Log4j
	 * (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances :</p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG
		= LogManager.getLogger(TypeProduitDesktopController.class);

	
	
	/* ************************** METHODES ***************************** */

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>Indispensable pour l'injection par SPRING
	 * du {@link TypeProduitICuService} via le constructeur.</li>
	 * <li>Ne surtout pas créer de constructeur d'arité nulle
	 * dans cette classe (sinon, SPRING chercherait 
	 * à l'utiliser et cela casserait l'injection du SERVICE).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pService : TypeProduitICuService :
	 * le SERVICE UC utilisé par ce CONTROLLER Desktop.
	 */
	public TypeProduitDesktopController(
			@Qualifier("TypeProduitCuService")
			final TypeProduitICuService pService) {
		super();
		this.service = pService;
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO creer(final InputDTO pInputDTO) throws Exception {

		/*
		 * Délègue sans traitement métier local
		 * le scénario de création au SERVICE UC.
		 */
		return this.service.creer(pInputDTO);
	}

	
	
}