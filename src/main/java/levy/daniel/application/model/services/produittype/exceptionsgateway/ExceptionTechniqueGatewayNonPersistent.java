package levy.daniel.application.model.services.produittype.exceptionsgateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ExceptionTechniqueGatewayNonPersistent.java :</p>
 * <p>Exception levée lorsqu'une erreur technique 
 * liée à la non-persistance d'une entité survient dans un gateway.</p>
 * <p>Utilisée pour encapsuler les exceptions techniques spécifiques 
 * à la persistance (ex : sauvegarde, mise à jour, suppression).</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li>Encapsulation d'une exception de sauvegarde :
 * <pre>
 * try {
 *     typeProduitRepository.save(typeProduitJPA);
 * } catch (DataAccessException e) {
 *     throw new ExceptionTechniqueGatewayNonPersistent(
 *         "TypeProduit",
 *         typeProduitJPA.getId(),
 *         "Impossible de sauvegarder le TypeProduit",
 *         e
 *     );
 * }
 * </pre>
 * </li>
 * <li>Gestion dans un contrôleur Spring :
 * <pre>
 * } catch (ExceptionTechniqueGatewayNonPersistent e) {
 *     LOG.error("Erreur de persistance pour l'entité " + e.getEntityType(), e);
 *     return ResponseEntity.internalServerError().body(
 *         "Erreur de persistance pour l'entité " + e.getEntityType() + " : " + e.getMessage()
 *     );
 * }
 * </pre>
 * </li>
 * </ul>
 * </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 24 janvier 2026
 */
public class ExceptionTechniqueGatewayNonPersistent extends Exception {

	// ************************ATTRIBUTS************************************/

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
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
			.getLogger(ExceptionTechniqueGatewayNonPersistent.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ExceptionTechniqueGatewayNonPersistent() {
		this(null, null);
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * <p>encapsule un message pour l'utilisateur</p>
	 * </div>
	 *
	 * @param pMessage : String : 
	 * message à faire transiter dans l'Exception.
	 */
	public ExceptionTechniqueGatewayNonPersistent(
			final String pMessage) {
		this(pMessage, null);
	}
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * <ul>
	 * <li>encapsule un message pour l'utilisateur.</li>
	 * <li>encapsule un Throwable cause.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pMessage : String : 
	 * message à faire transiter dans l'Exception.
	 * @param pE : Throwable : Exception cause à propager.
	 */
	public ExceptionTechniqueGatewayNonPersistent(
			final String pMessage, final Throwable pE) {
		super(pMessage, pE);
	}

}
