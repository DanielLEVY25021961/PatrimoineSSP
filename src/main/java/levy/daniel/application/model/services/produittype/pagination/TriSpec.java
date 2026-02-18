/* ********************************************************************* */
/* *************************** PAGINATION ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.pagination;

import java.io.Serializable;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE TriSpec.java :</p>
 * <p>Spécification de tri neutre (propriété + direction).</p>
 * <p>Utilisée pour définir les critères de tri 
 * dans les requêtes paginées ou les listes.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li>Création d'un tri par nom ascendant :
 * <pre>
 * TriSpec triNom = new TriSpec("nom", DirectionTri.ASC);
 * </pre>
 * </li>
 * <li>Conversion en Sort Spring Data :
 * <pre>
 * Sort sort = Sort.by(tri.getDirection() == DirectionTri.ASC
 *     ? Sort.Direction.ASC : Sort.Direction.DESC, tri.getPropriete());
 * </pre>
 * </li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 19 janvier 2026
 */
public class TriSpec implements Serializable {

	// *************************** CONSTANTES ******************************/

	/** Serial UID. */
	private static final long serialVersionUID = 1L;

	/** Direction par défaut. */
	public static final DirectionTri DIRECTION_DEFAUT = DirectionTri.ASC;


	// *************************** ATTRIBUTS *******************************/

	/** Propriété (champ) à trier. */
	private String propriete;

	/** Direction de tri. */
	private DirectionTri direction;


	// ************************* METHODES **********************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pPropriete : String
	 * @param pDirection : DirectionTri
	 */
	public TriSpec(final String pPropriete, final DirectionTri pDirection) {
		super();
		this.propriete = pPropriete;
		this.direction = (pDirection != null) ? pDirection : DIRECTION_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * permet d'inverser la direction du tri.</p>
	 * </div>
	 *
	 * @return TriSpec
	 */
	public TriSpec inverserDirection() {
	    return new TriSpec(this.propriete,
	        this.direction == DirectionTri.ASC ? DirectionTri.DESC : DirectionTri.ASC);
	}
	
	
	
	/**
	 * @return propriete
	 */
	public String getPropriete() {
		return this.propriete;
	}

	/**
	 * @param pPropriete : String
	 */
	public void setPropriete(final String pPropriete) {
		this.propriete = pPropriete;
	}

	/**
	 * @return direction
	 */
	public DirectionTri getDirection() {
		return this.direction;
	}

	/**
	 * @param pDirection : DirectionTri
	 */
	public void setDirection(final DirectionTri pDirection) {
		this.direction = (pDirection != null) ? pDirection : DIRECTION_DEFAUT;
	}
}
