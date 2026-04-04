package levy.daniel.application.controllers.metier.produittype.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * CLASSE TypeProduitCreerWebController.java :
 * </p>
 * <p>ADAPTER CONTROLLER</p>
 * 
 * <p>
 * Cette classe modélise : 
 * <span style="font-weight:bold;">xxx</span> 
 * yyy.
 * </p>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janv. 2026
 */
@RestController
@RequestMapping("/typeproduit")
@Profile({"web", "dev", "prod"})
public class TypeProduitCreerWebController
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
			.getLogger(TypeProduitCreerWebController.class);

	// *************************METHODES************************************/



	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <p>Indispensable pour l'injection 
	 * de typeProduitService par SPRING.</p>
	 * <p>Ne SURTOUT PAS mettre de constructeur d'ARITE NULLE !!!</p>
	 * </div>
	 *
	 * @param pTypeProduitService
	 */
	public TypeProduitCreerWebController(
			@Qualifier("typeProduitService") 
			final TypeProduitICuService pTypeProduitService) {
		super();
		this.service = pTypeProduitService;
	}
	


	/**
	* {@inheritDoc}
	*/
	@Override
	@PostMapping
	public OutputDTO creer(InputDTO pInputDTO) throws Exception {

//		return this.service.creer(pInputDTO);
		return null;

	}

}
