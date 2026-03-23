package levy.daniel.application.model.services.produittype.exceptionsservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ExceptionNonPersistant.java :</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janv. 2026
 */
public class ExceptionNonPersistant extends Exception {

	// ************************ATTRIBUTS************************************/

	/**
	 * 1L
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
			.getLogger(ExceptionNonPersistant.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ExceptionNonPersistant() {
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
	public ExceptionNonPersistant(final String pMessage) {
		super(pMessage);
	}
	
	
}
