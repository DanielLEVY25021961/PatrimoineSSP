/* ********************************************************************* */
/* ********************** TriSpecDTO DTO ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.dto.pagination;

import java.io.Serializable;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TriSpecDTO.java :
 * </p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette CLASSE modélise :
 * <span style="font-weight:bold;">une spécification de tri DTO</span>
 * (tri ascendant, tri descendant, ...) 
 * utilisée dans les objets de pagination transmis entre les VUES,
 * les CONTROLLERS et les SERVICES UC.</li>
 * <li>Cette CLASSE est un objet de transport partagé
 * entre les VUES, les CONTROLLERS et les SERVICES UC,
 * sans logique métier ni dépendance technologique.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 6 avril 2026
 */
public final class TriSpecDTO implements Serializable {

	/* *************************** CONSTANTES *************************** */

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Direction de tri par défaut.
	 */
	public static final DirectionTriDTO DIRECTION_DEFAUT
		= DirectionTriDTO.ASC;

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * Nom de la propriété à trier.
	 */
	private String propriete;

	/**
	 * Direction de tri portée par le DTO.
	 */
	private DirectionTriDTO direction;

	/* *************************** METHODES ***************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pPropriete : String :
	 * le nom de la propriété à trier.
	 * @param pDirection : DirectionTriDTO :
	 * la direction de tri portée par le DTO.
	 */
	public TriSpecDTO(
			final String pPropriete,
			final DirectionTriDTO pDirection) {
		super();
		this.propriete = pPropriete;
		this.direction
			= (pDirection != null)
				? pDirection
				: DIRECTION_DEFAUT;
	}

	
	
	/**
	 * <div>
	 * <p>Getter du nom de la propriété à trier.</p>
	 * </div>
	 *
	 * @return String : <code>this.propriete</code> :
	 * le nom de la propriété à trier.
	 */
	public String getPropriete() {
		return this.propriete;
	}

	
	
	/**
	 * <div>
	 * <p>Setter du nom de la propriété à trier.</p>
	 * </div>
	 *
	 * @param pPropriete : String :
	 * valeur à passer à <code>this.propriete</code> :
	 * le nom de la propriété à trier.
	 */
	public void setPropriete(final String pPropriete) {
		this.propriete = pPropriete;
	}

	
	
	/**
	 * <div>
	 * <p>Getter de la direction de tri portée par le DTO.</p>
	 * </div>
	 *
	 * @return DirectionTriDTO : <code>this.direction</code> :
	 * la direction de tri portée par le DTO.
	 */
	public DirectionTriDTO getDirection() {
		return this.direction;
	}

	
	
	/**
	 * <div>
	 * <p>Setter de la direction de tri portée par le DTO.</p>
	 * </div>
	 *
	 * @param pDirection : DirectionTriDTO :
	 * valeur à passer à <code>this.direction</code> :
	 * la direction de tri portée par le DTO.
	 */
	public void setDirection(final DirectionTriDTO pDirection) {
		this.direction
			= (pDirection != null)
				? pDirection
				: DIRECTION_DEFAUT;
	}

	
	
}