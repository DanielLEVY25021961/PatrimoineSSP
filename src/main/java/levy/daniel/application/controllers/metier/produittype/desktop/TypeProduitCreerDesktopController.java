package levy.daniel.application.controllers.metier.produittype.desktop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import levy.daniel.application.controllers.metier.produittype.TypeProduitCreerIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitCreerDesktopController.java :
 * </p>
 * <p>ADAPTER CONTROLLER</p>
 * 
 * 
 * 
 * </div>
 *
 * <ul>
 * <li>
 * <p>Exemple d'utilisation :</p>
 * <p><code></code></p>
 * </li>
 *  
 * <li>
 * <p>Mots-clé :</p>
 * <p></p>
 * <p></p>
 * <p></p>
 * </li>
 * </ul>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janv. 2026
 */
@Controller("TypeProduitCreerDesktopController")
@Profile("desktop")
public class TypeProduitCreerDesktopController
		implements TypeProduitCreerIController {

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
			.getLogger(TypeProduitCreerDesktopController.class);

	// *************************METHODES************************************/


	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * <p>Utilisé par SPRING pour injecter le SERVICE.</p>
	 * </div>
	 *
	 * @param pService : TypeProduitICuService
	 */
	public TypeProduitCreerDesktopController(
			@Qualifier("TypeProduitCuService") 
				final TypeProduitICuService pService) {

		super();
		this.service = pService;

	} // Fin du CONSTRUCTEUR SPRING._______________________________________



	/**
	* {@inheritDoc}
	*/
	@Override
	public OutputDTO creer(InputDTO pInputDTO) {
//		return this.service.creer(pInputDTO);
		return null;
	}

}
