package levy.daniel.application.model.services.produittype.exceptionsgateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ExceptionTechniqueGateway.java :</p>
 * <p>Exception levée lorsqu'une erreur 
 * technique survient dans un gateway.</p>
 * <p>Utilisée pour encapsuler les exceptions techniques 
 * (ex : persistance, conversion) et les propager.</p>
 * </div>
 * 
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li>Encapsulation d'une exception de persistance :
 * <pre>
 * try {
 *     typeProduitRepository.save(typeProduitJPA);
 * } catch (DataAccessException e) {
 *     throw new ExceptionTechniqueGateway(
 *         "Erreur technique lors de la persistance du TypeProduit",
 *         e
 *     );
 * }
 * </pre>
 * </li>
 * <li>Gestion dans un contrôleur Spring :
 * <pre>
 * } catch (ExceptionTechniqueGateway e) {
 *     LOG.error("Erreur technique dans le gateway", e);
 *     return ResponseEntity.internalServerError().body(
 *         "Une erreur technique est survenue : " + e.getMessage()
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
public class ExceptionTechniqueGateway extends Exception {

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
			.getLogger(ExceptionTechniqueGateway.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ExceptionTechniqueGateway() {
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
	public ExceptionTechniqueGateway(final String pMessage) {
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
	public ExceptionTechniqueGateway(
			final String pMessage, final Throwable pE) {
		super(pMessage, pE);
	}

}
