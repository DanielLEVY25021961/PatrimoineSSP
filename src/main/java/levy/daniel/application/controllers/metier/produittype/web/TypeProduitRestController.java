package levy.daniel.application.controllers.metier.produittype.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;



/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitRestController.java :
 * </p>
 * <p>ADAPTER CONTROLLER</p>
 * <p>
 * Cette classe modélise : 
 * un <span style="font-weight:bold;">CONTROLLER WEB REST</span> 
 * pour l'objet métier <code style="font-weight:bold;">TypeProduit</code>.
 * </p>
 * 
 * <p>
 * On y trouve :
 * <ul>
 * <li>exceptions.</li>
 * <li>xxxx.</li>
 * <li>yyy.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * xxx
 * </p>
 * <p>
 * zzzz
 * </p>
 * <p>
 * rrrr.
 * </p>
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
 * @since 29 décembre 2025
 */
@RestController
@RequestMapping("/typeproduit")
@Profile({"web", "dev", "prod"})
public class TypeProduitRestController {

	// ************************ATTRIBUTS************************************/

	/**
	 * <div>
	 * <p>SERVICE METIER (CU) - INTERFACE.</p>
	 * <p>Injecté par SPRING via le constructeur.</p>
	 * </div>
	 */
	private final TypeProduitICuService typeProduitService;
	
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
			.getLogger(TypeProduitRestController.class);

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
	public TypeProduitRestController(
			@Qualifier("typeProduitService") 
			final TypeProduitICuService pTypeProduitService) {
		super();
		this.typeProduitService = pTypeProduitService;
	}
	
	
	
	/**
	 *  .
	 *
	 * @return
	 */
//	@PostMapping
//	public String creerTypeProduit(@RequestBody final TypeProduitDTO.PostInput pPostInput) throws Exception {
//		
//		/* Instanciation d'un Objet métier à partir du DTO. */
//		final TypeProduit typeProduit 
//			= new TypeProduit(pPostInput.getTypeProduit());
//		
//		final boolean resultat = this.typeProduitService.creerTypeProduit(typeProduit);
//		
//		final String messageUtilisateur = this.typeProduitService.getMessage();
//		
//		if (resultat) {
//			if (messageUtilisateur != null) {
//				return messageUtilisateur;
//			}
//			return "OK";
//		} else {
//			
//			if (messageUtilisateur != null) {
//				return messageUtilisateur;
//			}
//			
//			return "KO";
//			
//		}		
//		
//	}
	

}
