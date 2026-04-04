package levy.daniel.application.controllers.metier.produittype.desktop;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import levy.daniel.application.controllers.metier.produittype.TypeProduitRechercheIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitRechercheDesktopController.java :
 * </p>
 * <p>ADAPTER CONTROLLER</p>
 * 
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 9 janv. 2026
 */
@Controller("TypeProduitRechercheDesktopController")
@Profile("desktop")
public class TypeProduitRechercheDesktopController
		implements TypeProduitRechercheIController {

	// ************************ATTRIBUTS************************************/
	
	/**
	 * <div>
	 * <p>SERVICE USE CASE injecté par SPRING via le constructeur</p>
	 * <p>Visibilité Interface.</p>
	 * </div>
	 */
	private final TypeProduitICuService service;

	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(TypeProduitRechercheDesktopController.class);

	// *************************METHODES************************************/

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * <p>Utilisé par SPRING pour injecter le SERVICE.</p>
	 * </div>
	 *
	 * @param pService : TypeProduitICuService
	 */
	public TypeProduitRechercheDesktopController(
			@Qualifier("TypeProduitCuService") 
				final TypeProduitICuService pService) {

		super();
		this.service = pService;

	} // Fin du CONSTRUCTEUR SPRING._______________________________________



	/**
	* {@inheritDoc}
	*/
	@Override
	public List<OutputDTO> rechercherTous() throws Exception {
		
//		final List<TypeProduit> list = this.service.rechercherTous();
		return null;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public List<String> rechercherTousString() {
		return null;
	}

	
}
