package levy.daniel.application.persistence.metier;

/**
 * class IExportateurJTable :<br/>
 * Interface qui garantit que tous les objets qui 
 * l'implémentent pourront être exportés dans des <b>JTable 
 * (Swing)</b>.<br/>
 * <br/>
 *
 * - Exemple d'utilisation :<br/>
 *<br/>
 * 
 * - Mots-clé :<br/>
 * <br/>
 *
 * - Dépendances :<br/>
 * <br/>
 *
 *
 * @author dan Lévy
 * @version 1.0
 * @since 17 juin 2014
 *
 */
public interface IExportateurJTable {
	
		
	/**
	* method getEnTeteColonne(
	* int pI) :<br/>
	* <div>
	* <ul>
	* <li>Fournit l'en-tête de la pIème colonne (0-based) 
	* pour affichage dans une JTable par exemple.</li>
	* <li>Suit l'ordre défini dans le csv.</li>
	* <li>doit être <b>transient</b> (Java ou JPA) 
	 * car n'est pas persisté.<br/>
	 * Or, commençant par <i>"get"</i>, les frameworks 
	 * (Hibernate, Spring, ...) 
	 * croiront qu'il s'agit d'une propriété persistante.</li>
	 * <li>retourne "invalide" si pI est hors valeur.</li>
	* </ul>
	* </div>
	*
	* @param pI : int : pIème colonne (0 - based).<br/>
	* 
	* @return : String : En-tête de la pIème colonne (0 - based).<br/>
	*/
	String getEnTeteColonne(int pI);
	
	
	
	/**
	* method getValeurColonne(
	* int pI) :<br/>
	* <div>
	* <ul>
	* <li>Fournit la valeur de la pIème colonne (0-based) 
	* pour affichage dans une JTable par exemple.</li>
	* <li>Suit l'ordre défini dans le csv.</li>
	* <li>Retourne toujours une <b>String</b> si l'objet n'est pas null.</li>
	* <li>Retourne null si la valeur dans l'objet est null.</li>
	* <li>doit être <b>transient</b> (Java ou JPA) 
	 * car n'est pas persisté.<br/>
	 * Or, commençant par <i>"get"</i>, les frameworks 
	 * (Hibernate, Spring, ...) 
	 * croiront qu'il s'agit d'une propriété persistante.</li>
	 * <li>retourne "invalide" si pI est hors valeur.</li>
	* </ul>
	* </div>
	*
	* @param pI : int : pIème colonne (0 - based).<br/>
	* 
	* @return : Object : valeur de la pIème colonne (0 - based).<br/>
	*/
	Object getValeurColonne(int pI);
	
	
	
} // FIN DE L'INTERFACE IExportateurJTable.----------------------------------
