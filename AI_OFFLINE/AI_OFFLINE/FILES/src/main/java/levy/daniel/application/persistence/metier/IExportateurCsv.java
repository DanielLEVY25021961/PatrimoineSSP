package levy.daniel.application.persistence.metier;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;padding : 20px}</style>
 * <div>
 * <p>Interface <span style="font-weight : bold">IExportateurCsv</span> :</p>
 * <p>Interface qui garantit que tous les objets qui 
 * l'implémentent pourront être <span style="font-weight : bold">
 * exportés au format csv</span>.<p>
 * </div>
 *
 * @author dan Lévy
 * @version 1.0
 * @since 17 juin 2014
 */
public interface IExportateurCsv {
	

	
	/**
	 * <div>
	 * <ul>
	 * <li>
	 * Fournit l'<span style="font-weight : bold">en-tête</span> 
	 * des <span style="font-weight : bold">lignes csv</span> 
	 * de l'Objet métier.
	 * </li>
	 * <li>
	 * Pourrait être static dans l'absolu, 
	 * mais ne pourrait alors plus figurer dans l'interface.<br/> 
	 * L'idée est de pouvoir demander à n'importe quel objet métier 
	 * de fournir son en-tête de fichier csv.
	 * </li>
	 * <li>doit être <span style="font-weight : bold">transient</span> 
	 * (Java ou JPA) car n'est pas persisté.<br/>
	 * Or, commençant par <span style="font-style: italic;">"get"</span>
	 * , les frameworks (Hibernate, Spring, ...) 
	 * croiront qu'il s'agit d'une propriété persistante.</li>
	 * </ul>
	 * </div>
	 *
	 * @return : String : en-tête CSV de l'Objet métier.<br/>
	 */
	String getEnTeteCsv();

	
	
	/**
	 * <div>
	 * <ul>
	 * <li>Fournit l'Objet métier sous forme de <b>ligne Csv</b>.</li>
	 * <li>Java remplace automatiquement les valeurs null par 
	 * <b>"null"</b> comme dans "Robert;null" avec un nom null pour un 
	 * Objet métier [prenom;nom].</li>
	 * </ul>
	 * </div>
	 *
	 * @return : String : l'Objet métier sous forme de ligne CSV.<br/>
	 */
	String toStringCsv();



} // FIN DE L'INTERFACE IExportateurCsv.-------------------------------------
