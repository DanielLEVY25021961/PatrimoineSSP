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
 * pour la pagination (par exemple tri ascendant, tri descendant, ...).</li>
 * <li>Cette CLASSE est un objet de transport
 * partagé entre les VUES, les CONTROLLERS
 * et les SERVICES UC.</li>
 * <li>Cette CLASSE est purement contractuelle
 * et ne dépend d'aucune technologie.</li>
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
	 * Direction par défaut.
	 */
	public static final DirectionTriDTO DIRECTION_DEFAUT
		= DirectionTriDTO.ASC;

	/* *************************** ATTRIBUTS **************************** */

	/**
	 * Propriété à trier.
	 */
	private String propriete;

	/**
	 * Direction de tri.
	 */
	private DirectionTriDTO direction;

	/* *************************** METHODES ***************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pPropriete : String :
	 * la propriété à trier.
	 * @param pDirection : DirectionTriDTO :
	 * la direction de tri.
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
	 * <p>Inverse la direction de tri.</p>
	 * </div>
	 *
	 * @return TriSpecDTO :
	 * une nouvelle spécification de tri
	 * avec la direction inversée.
	 */
	public TriSpecDTO inverserDirection() {

		return new TriSpecDTO(
				this.propriete,
				this.direction == DirectionTriDTO.ASC
					? DirectionTriDTO.DESC
					: DirectionTriDTO.ASC);

	}

	/**
	 * @return String :
	 * la propriété à trier.
	 */
	public String getPropriete() {
		return this.propriete;
	}

	/**
	 * @param pPropriete : String :
	 * la propriété à trier.
	 */
	public void setPropriete(final String pPropriete) {
		this.propriete = pPropriete;
	}

	/**
	 * @return DirectionTriDTO :
	 * la direction de tri.
	 */
	public DirectionTriDTO getDirection() {
		return this.direction;
	}

	/**
	 * @param pDirection : DirectionTriDTO :
	 * la direction de tri.
	 */
	public void setDirection(final DirectionTriDTO pDirection) {
		this.direction
			= (pDirection != null)
				? pDirection
				: DIRECTION_DEFAUT;
	}

}