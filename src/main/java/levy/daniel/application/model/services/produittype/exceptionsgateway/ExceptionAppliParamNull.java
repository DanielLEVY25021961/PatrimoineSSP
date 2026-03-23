package levy.daniel.application.model.services.produittype.exceptionsgateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ExceptionAppliParamNull.java :</p>
 * <p>Exception levée lorsqu'un paramètre est <code>null</code>.</p>
 * <p>Utilisée pour valider les entrées dans 
 * les services métier ou les gateways.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li>Validation d'un paramètre :
 * <pre>
 * if (typeProduit == null) {
 *     throw new ExceptionAppliParamNull("typeProduit",
 *         "Le paramètre 'typeProduit' ne peut pas être null.");
 * }
 * </pre>
 * </li>
 * <li>Gestion dans un contrôleur Spring :
 * <pre>
 * } catch (ExceptionAppliParamNull e) {
 *     return ResponseEntity.badRequest().body(
 *         String.format("Erreur sur le paramètre '%s' : %s", e.getParamName(), e.getMessage())
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
public class ExceptionAppliParamNull extends Exception {

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
			.getLogger(ExceptionAppliParamNull.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ExceptionAppliParamNull() {
		this(null);
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
	public ExceptionAppliParamNull(final String pMessage) {
		super(pMessage);
	}

}
